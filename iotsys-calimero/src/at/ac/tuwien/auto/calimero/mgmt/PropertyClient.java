/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package at.ac.tuwien.auto.calimero.mgmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import at.ac.tuwien.auto.calimero.Settings;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator2ByteUnsigned;
import at.ac.tuwien.auto.calimero.dptxlator.PropertyTypes;
import at.ac.tuwien.auto.calimero.dptxlator.TranslatorTypes;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalStateException;
import at.ac.tuwien.auto.calimero.exception.KNXRemoteException;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;
import at.ac.tuwien.auto.calimero.xml.Attribute;
import at.ac.tuwien.auto.calimero.xml.Element;
import at.ac.tuwien.auto.calimero.xml.KNXMLException;
import at.ac.tuwien.auto.calimero.xml.XMLFactory;
import at.ac.tuwien.auto.calimero.xml.XMLReader;
import at.ac.tuwien.auto.calimero.xml.XMLWriter;


/**
 * A client to access properties in interface objects of a device.
 * <p>
 * This can be done in different ways, to specify the kind of access a property adapter is
 * supplied on creation of the property client. The implementation of the
 * {@link PropertyAdapter} interface methods don't need to be synchronized for use by this
 * property client.
 * <p>
 * Properties can be retrieved or set in the device, property descriptions can be read and
 * scans of properties can be done.<br>
 * If desired, the property data type of a property element is used to return an
 * appropriate DPT translator or the ready formatted string representation.<br>
 * The DPT translators used are requested from {@link PropertyTypes} by default, or, if
 * property definitions were loaded, there will be a lookup in these data at first (i.e.
 * loaded property definitions take priority over the default PDT to DPT mapping).
 * <p>
 * It is possible to load property definitions with information about KNX properties from
 * a resource to be used by the property client for lookup requests and property type
 * translation. Also, definitions can be saved to a resource. A global resource handler
 * takes care of working with the resource where these definitions are stored. Loaded
 * definitions are shared with all property clients.<br>
 * Loading of such definitions is not required for a client to work, i.e. might be done
 * optionally by the user.
 * <p>
 * By default, the resource handler uses a xml property file structure.<br>
 * XML file layout: <br>
 * &lt;propertyDefinitions&gt;<br>
 * &lt;object type=(object-type:number | "global")&gt;<br>
 * &lt;property pid=PID:number pidName=PID-name:string name=friendly-name:string
 * pdt=PDT:number [dpt=DPT-ID:string] rw=("R"|"W"|"R/W"|"R(/W"):string
 * writeEnabled=("0"|"1"|"0/1"):string&gt;<br>
 * &lt;usage&gt;<br>
 * usage description and additional information<br>
 * &lt;/usage&gt;<br>
 * &lt;/property&gt;<br>
 * ...next property<br>
 * &lt;/object&gt;<br>
 * ...next object<br>
 * &lt;/propertyDefinitions&gt;<br>
 * <br>
 * Attribute values of type number might be written in hexadecimal form by prepending
 * "0x". The optional attribute "dpt" is to specify a DPT to use for the property, it will
 * be used in preference before the default DPT assigned to the PDT.<br>
 * The attribute "pdt" might have a value of "&lt;tbd&gt;", standing for "to be defined",
 * in this case the PDT value used by the property client is -1.
 * <p>
 * Reduced Property Interfaces:<br>
 * When working with reduced property interfaces, the user has to be aware of the
 * limitations and act accordingly.
 * <p>
 * Some KNX devices only support a 5 Bit field for storing the Property Data Type (PDT),
 * i.e. they only use a PDT identifier up to value 0x1F. When accessing property
 * descriptions in the interface object (for example, using
 * {@link PropertyClient#getDescription(int, int)}), not all existing PDTs can be
 * transmitted. Consequently, for properties that are formatted according any of the
 * higher PDTs, "alternative PDTs" get used. Following implementations are known to
 * support only a 5 Bit PDT in the description:
 * <ul>
 * <li>mask 0x0020</li>
 * <li>mask 0x0021</li>
 * <li>mask 0x0701</li>
 * </ul>
 * In general, it is not possible for the property client to deduce the actual type to use
 * for encoding/decoding values from such an "alternative PDT". For these cases, the
 * property client has to rely on property definitions supplied by the user through
 * {@link PropertyClient#loadDefinitions(String)}.
 * <p>
 * A note on property descriptions:<br>
 * With a local device management adapter, not all information is supported when reading a
 * description, or is not supported by the protocol at all. In particular, no access
 * levels for read/write (i.e. access is always done with maximum rights) and no property
 * data type (PDT) are available. Also, the maximum number of elements allowed in the
 * property is not available (only the current number of elements).<br>
 * All methods for property access invoked after a close of the property client will throw
 * a {@link KNXIllegalStateException}.
 * 
 * @author B. Malinowsky
 * @see PropertyAdapter
 * @see PropertyTypes
 */
public class PropertyClient
{
	/**
	 * Contains constants definitions of the property IDs for global properties (server
	 * object type = global) and PIDs of the KNXnet/IP parameter object (server object
	 * type 11).
	 * <p>
	 * It is encouraged for users to use these more descriptive name constants instead of
	 * plain PID integer values for better readability.<br>
	 * The PIDs are put into its own interface to create an encapsulation besides the
	 * property client interface, since these constants are solely for the user's sake,
	 * the property client itself does not depend on them. <br>
	 * For this reason, the "interface is type" idiom is not strictly followed here.
	 * 
	 * @author B. Malinowsky
	 */
	public static interface PID
	{
		//
		// global properties (server object type = global)
		//

		/**
		 * Global property "Interface Object Type".
		 * <p>
		 * Object Type Device Object.
		 */
		// PDT,DPT: PDT_UNSIGNED_INT,DPT_PropDataType
		int OBJECT_TYPE = 1;

		/**
		 * Global property "Interface Object Name".
		 * <p>
		 * Name of the Interface Object.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR[]
		int OBJECT_NAME = 2;

		/**
		 * ! to be defined ! Property name:
		 * <p>
		 */
		// PDT,DPT: <tbd>
		// int SEMAPHOR = 3;
		/**
		 * ! to be defined ! Property name:
		 * <p>
		 */
		// PDT,DPT: <tbd>
		// int GROUP_OBJECT_REFERENCE = 4;
		/**
		 * Global property "Load Control".
		 * <p>
		 * Access to load state machines.
		 */
		// PDT,DPT: PDT_CONTROL
		int LOAD_STATE_CONTROL = 5;

		/**
		 * Global property "Run Control".
		 * <p>
		 * Access to run state machines.
		 */
		// PDT,DPT: PDT_CONTROL
		int RUN_STATE_CONTROL = 6;

		/**
		 * Global property "Table Reference".
		 * <p>
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int TABLE_REFERENCE = 7;

		/**
		 * Global property "Service Control".
		 * <p>
		 * Service Control, Permanent Control field for the Device.
		 */
		// PDT,DPT: PDT_UNSIGNED_INT
		int SERVICE_CONTROL = 8;

		/**
		 * Global property "Firmware Revision".
		 * <p>
		 * Revision number of the Firmware.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR
		int FIRMWARE_REVISION = 9;

		/**
		 * ! to be defined ! Property name:
		 * <p>
		 * Services Supported.
		 */
		// PDT,DPT: <tbd>
		// int SERVICES_SUPPORTED = 10;
		/**
		 * Global property "KNX Serial Number".
		 * <p>
		 * KNX Serial Number of the device.
		 */
		// PDT,DPT: PDT_GENERIC_06
		int SERIAL_NUMBER = 11;

		/**
		 * Global property "Manufacturer Identifier".
		 * <p>
		 * Manufacturer code.
		 */
		// PDT,DPT: PDT_UNSIGNED_INT
		int MANUFACTURER_ID = 12;

		/**
		 * Global property "Application Version".
		 * <p>
		 * Version of the Application Program.
		 */
		// PDT,DPT: PDT_GENERIC_05
		int PROGRAM_VERSION = 13;

		/**
		 * Global property "Device Control".
		 * <p>
		 * Device Control, Temporary Control field for the Device.
		 */
		// PDT,DPT: PDT_BISET8,DPT_Device_Control
		int DEVICE_CONTROL = 14;

		/**
		 * Global property "Order Info".
		 * <p>
		 * OrderInfo, Manufacturer specific Order ID.
		 */
		// PDT,DPT: PDT_GENERIC_10
		int ORDER_INFO = 15;

		/**
		 * Global property "PEI Type".
		 * <p>
		 * Connected or required PEI-type.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR
		int PEI_TYPE = 16;

		/**
		 * Global property "PortADDR".
		 * <p>
		 * PortAddr, Direction bits for Port A.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR
		int PORT_CONFIGURATION = 17;

		/**
		 * Global property "Pollgroup Settings".
		 * <p>
		 * Pollgroup settings (polling group and slot number).
		 */
		// PDT,DPT: PDT_POLL_GROUP_SETTINGS
		int POLL_GROUP_SETTINGS = 18;

		/**
		 * Global property "Manufacturer Data".
		 * <p>
		 * Manufacturer Data.
		 */
		// PDT,DPT: PDT_GENERIC_04
		int MANUFACTURER_DATA = 19;

		/**
		 * ! to be defined ! Property name:
		 * <p>
		 */
		// PDT,DPT: <tbd>
		// int ENABLE = 20;
		/**
		 * Global property "Description".
		 * <p>
		 * Description, Description of the device.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR[ ]
		int DESCRIPTION = 21;

		/**
		 * ! to be defined ! Property name:
		 * <p>
		 */
		// PDT,DPT: <tbd>
		// int FILE = 22;
		/**
		 * Global property "Address Table Format 0".
		 * <p>
		 * Group Address Table, Association Table.
		 */
		// PDT,DPT: PDT_UNSIGNED_INT[]
		int TABLE = 23;

		/**
		 * Global property "Interface Object Link".
		 * <p>
		 * enrollment status, A-Mode enrollment.
		 */
		// PDT,DPT: PDT_FUNCTION
		// this property is specified uses British spelling
		int ENROL = 24;

		/**
		 * Global property "Version".
		 * <p>
		 * Generic version information.
		 */
		// PDT,DPT: PDT_VERSION (U6U6U6)
		int VERSION = 25;

		/**
		 * ! to be defined ! Property name: Group Address Assignment.
		 * <p>
		 */
		// PDT,DPT: PDT_FUNCTION
		// int GROUP_OBJECT_LINK = 26;
		/**
		 * Global property "Memory Control Table".
		 * <p>
		 * Sub-segmentation of memory space and checksum.
		 */
		// PDT,DPT: PDT_GENERIC_07[]
		int MCB_TABLE = 27;

		/**
		 * Global property "Error code".
		 * <p>
		 * Error code when load state machine indicates "error".
		 */
		// PDT,DPT: PDT_GENERIC_01
		int ERROR_CODE = 28;

		//
		// properties of object type 11, KNXnet/IP parameter object
		//

		/**
		 * Object type 11 property "Project Installation Identification".
		 * <p>
		 * Identification of the project and the installation in which this KNXnet/IP
		 * device resides.
		 */
		// PDT,DPT: PDT_UNSIGNED_INT
		int PROJECT_INSTALLATION_ID = 51;

		/**
		 * Object type 11 property "KNX Individual Address".
		 * <p>
		 * Individual Address of the KNXnet/IP server.
		 */
		// PDT,DPT: PDT_UNSIGNED_INT
		int KNX_INDIVIDUAL_ADDRESS = 52;

		/**
		 * Object type 11 property "Additional Individual Addresses".
		 * <p>
		 * Sorted list of additional KNX Individual Addresses for KNXnet/IP routers and
		 * tunneling servers.
		 */
		// PDT,DPT: PDT_UNSIGNED_INT[]
		int ADDITIONAL_INDIVIDUAL_ADDRESSES = 53;

		/**
		 * Object type 11 property "Current IP Assignment Method".
		 * <p>
		 * Used IP address assignment method.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR
		int CURRENT_IP_ASSIGNMENT_METHOD = 54;

		/**
		 * Object type 11 property "IP Assignment Method".
		 * <p>
		 * Enabled IP assignment methods that can be used.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR
		int IP_ASSIGNMENT_METHOD = 55;

		/**
		 * Object type 11 property "IP Capabilities".
		 * <p>
		 * Capabilities of the KNXnet/IP device for obtaining an IP address.
		 */
		// PDT,DPT: PDT_BITSET_8
		int IP_CAPABILITIES = 56;

		/**
		 * Object type 11 property "Current IP Address".
		 * <p>
		 * Currently used IP address.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int CURRENT_IP_ADDRESS = 57;

		/**
		 * Object type 11 property "Current Subnet Mask".
		 * <p>
		 * Currently used IP subnet mask.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int CURRENT_SUBNET_MASK = 58;

		/**
		 * Object type 11 property "Current Default Gateway".
		 * <p>
		 * IP Address of the KNXnet/IP device's default gateway.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int CURRENT_DEFAULT_GATEWAY = 59;

		/**
		 * Object type 11 property "IP Address".
		 * <p>
		 * Configured fixed IP Address.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int IP_ADDRESS = 60;

		/**
		 * Object type 11 property "Subnet Mask".
		 * <p>
		 * Configured IP subnet mask.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int SUBNET_MASK = 61;

		/**
		 * Object type 11 property "Default Gateway".
		 * <p>
		 * Configured IP address of the default gateway.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int DEFAULT_GATEWAY = 62;

		/**
		 * Object type 11 property "DHCP/BootP Server".
		 * <p>
		 * IP address of the DHCP or BootP server from which the KNXnet/IP device received
		 * its IP address.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int DHCP_BOOTP_SERVER = 63;

		/**
		 * Object type 11 property "MAC Address".
		 * <p>
		 * MAC address of the KNXnet/IP device.
		 */
		// PDT,DPT: PDT_GENERIC_06
		int MAC_ADDRESS = 64;

		/**
		 * Object type 11 property "System Setup Multicast Address".
		 * <p>
		 * KNXnet/IP system set-up multicast address. Value is fixed to 224.0.23.12.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int SYSTEM_SETUP_MULTICAST_ADDRESS = 65;

		/**
		 * Object type 11 property "Routing Multicast Address".
		 * <p>
		 * Routing multicast address.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int ROUTING_MULTICAST_ADDRESS = 66;

		/**
		 * Object type 11 property "Time To Live".
		 * <p>
		 * TTL value to be used by KNXnet/IP devices.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR
		int TTL = 67;

		/**
		 * Object type 11 property "KNXnet/IP Device Capabilities".
		 * <p>
		 * KNXnet/IP protocols supported by the KNXnet/IP device.<br>
		 * Compatibility note: the identifier is synonymous to "EIBnet/IP Device
		 * Capabilities" in the KNX specification.
		 */
		// PDT,DPT: PDT_BITSET_16
		int KNXNETIP_DEVICE_CAPABILITIES = 68;

		/**
		 * Object type 11 property "KNXnet/IP Device State".
		 * <p>
		 * Various KNXnet/IP device status info, like KNX or IP network connection
		 * failure.<br>
		 * Compatibility note: the identifier is synonymous to "EIBnet/IP Device State" in
		 * the KNX specification.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR
		int KNXNETIP_DEVICE_STATE = 69;

		/**
		 * Object type 11 property "KNXnet/IP Routing Capabilities".
		 * <p>
		 * Supported features by the KNXnet/IP Router.<br>
		 * Compatibility note: the identifier is synonymous to "EIBnet/IP Routing
		 * Capabilities" in the KNX specification.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR
		int KNXNETIP_ROUTING_CAPABILITIES = 70;

		/**
		 * Object type 11 property "Priority FIFO Enabled".
		 * <p>
		 * Indication of whether the priority FIFO is enabled of disabled.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR
		int PRIORITY_FIFO_ENABLED = 71;

		/**
		 * Object type 11 property "Queue Overflow to IP".
		 * <p>
		 * Number of telegrams lost due to overflow of queue to IP.
		 */
		// PDT,DPT: PDT_UNSIGNED_INT
		int QUEUE_OVERFLOW_TO_IP = 72;

		/**
		 * Object type 11 property "Queue overflow to KNX".
		 * <p>
		 * Number of telegrams lost due to overflow of queue to KNX.
		 */
		// PDT,DPT: PDT_UNSIGNED_INT
		int QUEUE_OVERFLOW_TO_KNX = 73;

		/**
		 * Object type 11 property "Telegrams Transmitted to IP".
		 * <p>
		 * Number of telegrams successfully transmitted to IP.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int MSG_TRANSMIT_TO_IP = 74;

		/**
		 * Object type 11 property "Telegrams Transmitted to KNX".
		 * <p>
		 * Number of telegrams successfully transmitted to KNX.
		 */
		// PDT,DPT: PDT_UNSIGNED_LONG
		int MSG_TRANSMIT_TO_KNX = 75;

		/**
		 * Object type 11 property "Friendly Name".
		 * <p>
		 * Human Readable Friendly Name.
		 */
		// PDT,DPT: PDT_UNSIGNED_CHAR[30]
		int FRIENDLY_NAME = 76;
	}

	/**
	 * Provides an interface to load property definitions from a resource, and store
	 * property definitions into a resource.
	 * <p>
	 * It is used by the property client when loading or saving of property definitions is
	 * requested.
	 * <p>
	 * To allow the property client handling a user defined property resource, set a new
	 * property resource handler with {@link PropertyClient#setResourceHandler
	 * (tuwien.auto.calimero.mgmt.PropertyClient.ResourceHandler)}.<br>
	 * It is not necessary for subtypes implementing this interface to synchronize the
	 * methods for concurrent access.
	 * 
	 * @author B. Malinowsky
	 */
	public static interface ResourceHandler
	{
		/**
		 * Loads the properties from the resource.
		 * <p>
		 * 
		 * @param resource the identifier of the resource used for loading the properties
		 * @return a collection containing the property definitions of type
		 *         {@link PropertyClient.Property}
		 * @throws KNXException on error reading from the resource
		 */
		Collection load(String resource) throws KNXException;

		/**
		 * Saves the properties to the resource.
		 * 
		 * @param resource the identifier of the resource used for saving the properties
		 * @param properties the property definitions in a collection holding
		 *        {@link PropertyClient.Property}-type values
		 * @throws KNXException on error writing to the resource
		 */
		void save(String resource, Collection properties) throws KNXException;
	}

	/**
	 * Key value in the map returned by {@link PropertyClient#getDefinitions()}.
	 * <p>
	 * A key consists of the interface object type and the property identifier of the
	 * associated property. If the property is defined globally, the global object type
	 * {@link PropertyClient.PropertyKey#GLOBAL_OBJTYPE} is used.
	 * 
	 * @author B. Malinowsky
	 */
	public static final class PropertyKey implements Comparable
	{
		/** Identifier for a property defined with global object type. */
		public static final int GLOBAL_OBJTYPE = -1;

		private final int ot;
		private final int id;

		/**
		 * Creates a new key for a global defined property.
		 * <p>
		 * 
		 * @param pid property identifier
		 */
		public PropertyKey(int pid)
		{
			ot = GLOBAL_OBJTYPE;
			id = pid;
		}

		/**
		 * Creates a new key for a property.
		 * <p>
		 * 
		 * @param objType object type of the property
		 * @param pid property identifier
		 */
		public PropertyKey(int objType, int pid)
		{
			ot = objType;
			id = pid;
		}

		/**
		 * Returns whether the property is defined with global object type.
		 * <p>
		 * 
		 * @return <code>true</code> if property has global object type,
		 *         <code>false</code> if property has a specific object type
		 */
		public boolean isGlobal()
		{
			return ot == GLOBAL_OBJTYPE;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
			return ot << 16 | id;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			if (obj instanceof PropertyKey) {
				final PropertyKey key = (PropertyKey) obj;
				return ot == key.ot && id == key.id;
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o)
		{
			final int rhs = o.hashCode();
			return hashCode() < rhs ? -1 : hashCode() > rhs ? 1 : 0;
		}
	}

	/**
	 * Stores property definition information of one property, used for type translation
	 * and property lookup by a property client.
	 * <p>
	 * 
	 * @author B. Malinowsky
	 */
	public static class Property
	{
		final int id;
		final String name;
		final int objType;
		final int pdt;
		final String dpt;
		final String propName;

		/**
		 * Creates a new property object of the supplied information.
		 * <p>
		 * 
		 * @param pid property identifier
		 * @param pidName name of the property ID
		 * @param propertyName property name, a friendly readable name for the property
		 * @param objectType object type the property belongs to
		 * @param pdt property data type
		 * @param dpt datapoint type, use <code>null</code> if no DPT specified or to
		 *        indicate default DPT usage
		 */
		public Property(final int pid, final String pidName, final String propertyName,
			final int objectType, final int pdt, final String dpt)
		{
			id = pid;
			name = pidName;
			propName = propertyName;
			objType = objectType;
			this.pdt = pdt;
			this.dpt = dpt;
		}

		/**
		 * Returns the property identifier.
		 * <p>
		 * 
		 * @return the PID
		 */
		public final int getPID()
		{
			return id;
		}

		/**
		 * Returns the PID name as string representation.
		 * 
		 * @return the PID name as string
		 */
		public final String getPIDName()
		{
			return name;
		}

		/**
		 * Returns the interface object type the property belongs to.
		 * <p>
		 * 
		 * @return the interface object type
		 */
		public final int getObjectType()
		{
			return objType;
		}

		/**
		 * Returns the property data type used for the property elements.
		 * <p>
		 * 
		 * @return the PDT
		 */
		public final int getPDT()
		{
			return pdt;
		}

		/**
		 * Returns the datapoint type ID used for the property elements.
		 * <p>
		 * 
		 * @return the DPT ID as string, or <code>null</code> if no DPT was set
		 */
		public final String getDPT()
		{
			return dpt;
		}

		/**
		 * Returns the property friendly name, a more readable name of the property.
		 * <p>
		 * 
		 * @return the property name
		 */
		public final String getName()
		{
			return propName;
		}
	}

	// mapping of object type numbers to the associated object type names
	// the offset of a name in the array corresponds to the object type number
	private static final String[] OBJECT_TYPE_NAMES = {
		"Device Object", "Addresstable Object", "Associationtable Object",
		"Applicationprogram Object", "Interfaceprogram Object",
		"EIB-Object Associationtable Object", "Router Object",
		"LTE Address Filter Table Object", "cEMI Server Object",
		"Group Object Table Object", "Polling Master", "KNXnet/IP Parameter Object",
		"Application Controller", "File Server Object", };

	private static Map properties;
	private static ResourceHandler rh;

	private final PropertyAdapter pa;
	// helper flag to determine local DM mode, mainly for detecting absence of PDT
	// detection is currently done by querying PropertyAdapter.getName()
	private final boolean local;
	private final LogService logger;

	// maps object index to object type
	private final List objectTypes = new ArrayList();
	private final DPTXlator2ByteUnsigned tObjType;

	/**
	 * Creates a new property client using the specified adapter for accessing device
	 * properties.
	 * <p>
	 * The property client obtains ownership of the adapter.<br>
	 * The log service used by this property client is named "PC " + adapter.getName().
	 * 
	 * @param adapter property adapter object
	 * @throws KNXFormatException on missing DPT for translating interface object types (
	 *         DPTXlator2ByteUnsigned.DPT_PROP_DATATYPE)
	 */
	public PropertyClient(PropertyAdapter adapter) throws KNXFormatException
	{
		pa = adapter;
		local = pa.getName().startsWith("local");
		try {
			tObjType =
				new DPTXlator2ByteUnsigned(DPTXlator2ByteUnsigned.DPT_PROP_DATATYPE);
		}
		catch (final KNXFormatException e) {
			pa.close();
			throw e;
		}
		logger = LogManager.getManager().getLogService("PC " + pa.getName());
	}

	/**
	 * Returns the object type name associated to the requested object type.
	 * <p>
	 * 
	 * @param objType object type to get name for
	 * @return object type name as string
	 */
	public static String getObjectTypeName(int objType)
	{
		if (objType < OBJECT_TYPE_NAMES.length)
			return OBJECT_TYPE_NAMES[objType];
		return "";
	}

	/**
	 * Sets a new default property resource handler used in the load / save methods by all
	 * property clients.
	 * <p>
	 * By default, a xml property file handler is used.
	 * 
	 * @param handler the new property storage handler, or <code>null</code> to use the
	 *        client default handler
	 */
	public static synchronized void setResourceHandler(ResourceHandler handler)
	{
		rh = handler;
	}

	/**
	 * Loads property definitions from a resource using the global set
	 * {@link ResourceHandler}.
	 * <p>
	 * The loaded definitions are used by all property client objects.
	 * 
	 * @param resource the identifier of a resource to load
	 * @throws KNXException on errors in the property resource handler
	 */
	public static synchronized void loadDefinitions(String resource) throws KNXException
	{
		if (rh == null)
			rh = new XMLPropertyHandler();
		final Collection coll = rh.load(resource);
		if (properties == null)
			properties = Collections.synchronizedMap(
				new HashMap((int) ((coll.size() + 10) / 0.75)));
		for (final Iterator i = coll.iterator(); i.hasNext();) {
			final Property p = (Property) i.next();
			properties.put(new PropertyKey(p.objType, p.id), p);
		}
	}

	/**
	 * Saves all loaded property definitions in the property client to a resource using
	 * the global set {@link ResourceHandler}.
	 * <p>
	 * 
	 * @param resource the identifier of a resource for saving
	 * @throws KNXException on errors in the property resource handler
	 */
	public static synchronized void saveDefinitions(String resource) throws KNXException
	{
		if (properties != null) {
			if (rh == null)
				rh = new XMLPropertyHandler();
			final Map map = new TreeMap(properties);
			rh.save(resource, map.values());
		}
	}

	/**
	 * Returns the property definitions used by property clients, if definitions were
	 * loaded.
	 * <p>
	 * The returned map is synchronized and a reference to the one used by all property
	 * clients. Property definitions might be added or removed as required. Note that
	 * modifications will influence lookup behavior of all property client instances.<br>
	 * A map key is of type {@link PropertyKey}, a map value is of type {@link Property}.
	 * 
	 * @return property map, or <code>null</code> if no definitions loaded
	 */
	public static synchronized Map getDefinitions()
	{
		return properties;
	}

	/**
	 * Sets one element of a property, with the value given as string representation.
	 * <p>
	 * The value is translated according the associated property data type.
	 * 
	 * @param objIndex interface object index in the device
	 * @param pid property identifier
	 * @param start index of the array element to set
	 * @param value string representation of the element value
	 * @throws KNXException on adapter errors while setting the property elements or
	 *         translation problems
	 */
	public void setProperty(int objIndex, int pid, int start, String value)
		throws KNXException
	{
		final DPTXlator t = createTranslator(objIndex, pid);
		t.setValue(value);
		setProperty(objIndex, pid, start, t.getItems(), t.getData());
	}

	/**
	 * Gets the first property element using the associated property data type of the
	 * requested property.
	 * <p>
	 * 
	 * @param objIndex interface object index in the device
	 * @param pid property identifier
	 * @return property element value represented as string
	 * @throws KNXException on adapter errors while querying the property element or data
	 *         type translation problems
	 */
	public String getProperty(int objIndex, int pid) throws KNXException
	{
		return getPropertyTranslated(objIndex, pid, 1, 1).getValue();
	}

	/**
	 * Sets one or more elements of a property.
	 * <p>
	 * 
	 * @param objIndex interface object index in the device
	 * @param pid property identifier
	 * @param start index of the first array element to set
	 * @param elements number of elements to set in the property
	 * @param data byte array holding the element data
	 * @throws KNXException on adapter errors while setting the property elements
	 */
	public void setProperty(int objIndex, int pid, int start, int elements, byte[] data)
		throws KNXException
	{
		try {
			pa.setProperty(objIndex, pid, start, elements, data);
		}
		catch (final KNXException e) {
			logger.error("set property failed", e);
			throw e;
		}
	}

	/**
	 * Gets one or more elements of a property.
	 * <p>
	 * 
	 * @param objIndex interface object index in the device
	 * @param pid property identifier
	 * @param start index of the first array element to get
	 * @param elements number of elements to get in the property
	 * @return byte array holding the retrieved element data
	 * @throws KNXException on adapter errors while querying the property element
	 */
	public byte[] getProperty(int objIndex, int pid, int start, int elements)
		throws KNXException
	{
		try {
			return pa.getProperty(objIndex, pid, start, elements);
		}
		catch (final KNXException e) {
			logger.error("get property failed", e);
			throw e;
		}
	}

	/**
	 * Gets one or more elements of a property with the returned data set in a DPT
	 * translator of the associated data type.
	 * <p>
	 * 
	 * @param objIndex interface object index in the device
	 * @param pid property identifier
	 * @param start index of the first array element to get
	 * @param elements number of elements to get in the property
	 * @return a DPT translator containing the returned the element data
	 * @throws KNXException on adapter errors while querying the property element or data
	 *         type translation problems
	 */
	public DPTXlator getPropertyTranslated(int objIndex, int pid, int start, int elements)
		throws KNXException
	{
		final DPTXlator t = createTranslator(objIndex, pid);
		t.setData(getProperty(objIndex, pid, start, elements));
		return t;
	}

	/**
	 * Gets the property description based on the property ID.
	 * <p>
	 * It is not always possible to supply all description information:<br>
	 * If a description read is done using a property identifier (PID) like in this
	 * method, the description response is not required to contain the correct property
	 * index associated with the PID, even though recommended. The default index is 0
	 * then.
	 * 
	 * @param objIndex interface object index in the device
	 * @param pid property identifier, pid &gt; 0
	 * @return the property description
	 * @throws KNXException on adapter errors while querying the description
	 */
	public Description getDescription(int objIndex, int pid) throws KNXException
	{
		if (pid == 0)
			throw new KNXIllegalArgumentException("pid has to be > 0");
		try {
			return createDesc(objIndex, pa.getDescription(objIndex, pid, 0));
		}
		catch (final KNXException e) {
			logger.error("get description failed", e);
			throw e;
		}
	}

	/**
	 * Gets the property description based on the property index.
	 * <p>
	 * 
	 * @param objIndex interface object index in the device
	 * @param propIndex property index in the object
	 * @return a property description object
	 * @throws KNXException on adapter errors while querying the description
	 */
	public Description getDescriptionByIndex(int objIndex, int propIndex)
		throws KNXException
	{
		try {
			return createDesc(objIndex, pa.getDescription(objIndex, 0, propIndex));
		}
		catch (final KNXException e) {
			logger.error("get description failed", e);
			throw e;
		}
	}

	/**
	 * Does a property description scan of the properties in all interface objects.
	 * <p>
	 * 
	 * @param allProperties <code>true</code> to scan all property descriptions in the
	 *        interface objects, <code>false</code> to only scan the object type
	 *        descriptions, i.e. ({@link PID#OBJECT_TYPE})
	 * @return a list containing the property descriptions of type {@link Description}
	 * @throws KNXException on adapter errors while querying the descriptions
	 */
	public List scanProperties(boolean allProperties) throws KNXException
	{
		final List scan = new ArrayList();
		for (int index = 0;; ++index) {
			final List l = scanProperties(index, allProperties);
			if (l.size() == 0)
				break;
			scan.addAll(l);
		}
		return scan;
	}

	/**
	 * Does a property description scan of the properties of one interface object.
	 * <p>
	 * 
	 * @param objIndex interface object index in the device
	 * @param allProperties <code>true</code> to scan all property descriptions in the
	 *        interface objects, <code>false</code> to only scan the object type
	 *        descriptions, i.e. ({@link PID#OBJECT_TYPE})
	 * @return a list containing the property descriptions of type {@link Description}
	 * @throws KNXException on adapter errors while querying the descriptions
	 */
	public List scanProperties(int objIndex, boolean allProperties) throws KNXException
	{
		final List scan = new ArrayList();
		// property with index 0 is description of object type
		// rest are ordinary properties of the object
		try {
			for (int i = 0; i < 1 || allProperties; ++i)
				scan.add(createDesc(objIndex, pa.getDescription(objIndex, 0, i)));
		}
		catch (final KNXException e) {
			if (!KNXRemoteException.class.equals(e.getClass())) {
				logger.error("scan properties failed", e);
				throw e;
			}
		}
		return scan;
	}

	/**
	 * Returns whether the adapter used for property access is opened.
	 * <p>
	 * 
	 * @return <code>true</code> on open adapter, <code>false</code> on closed adapter
	 */
	public final boolean isOpen()
	{
		return pa.isOpen();
	}

	/**
	 * Closes the property client and the used adapter.
	 * <p>
	 */
	public void close()
	{
		if (pa.isOpen()) {
			pa.close();
			logger.info("closed property client");
			LogManager.getManager().removeLogService(logger.getName());
		}
	}

	private Description createDesc(int oi, byte[] desc) throws KNXException
	{
		final Description d = new Description(getObjectType(oi, true), desc);
		d.setCurrentElements(pa.getProperty(oi, d.getPID(), 0, 1));
		// workaround for PDT on local DM
		if (local)
			d.setPDT(-1);
		return d;
	}

	private int getObjectType(int objIndex, boolean queryObject) throws KNXException
	{
		for (final Iterator i = objectTypes.iterator(); i.hasNext();) {
			final Pair p = (Pair) i.next();
			if (p.oindex == objIndex)
				return p.otype;
		}
		if (queryObject)
			return queryObjectType(objIndex);
		throw new KNXException("couldn't deduce object type");
	}

	private int queryObjectType(int objIndex) throws KNXException
	{
		tObjType.setData(pa.getProperty(objIndex, 1, 1, 1));
		objectTypes.add(new Pair(objIndex, tObjType.getValueUnsigned()));
		return tObjType.getValueUnsigned();
	}

	private DPTXlator createTranslator(int objIndex, int pid) throws KNXException
	{
		final int ot = getObjectType(objIndex, true);
		int pdt = -1;
		if (properties != null) {
			Property p = (Property) properties.get(new PropertyKey(ot, pid));
			// if no property found, lookup global pid
			if (p == null && pid < 50)
				p = (Property) properties.get(new PropertyKey(pid));
			if (p != null) {
				if (p.dpt != null)
					try {
						return TranslatorTypes.createTranslator(0, p.dpt);
					}
					catch (final KNXException e) {
						logger.warn("fallback to default translator", e);
					}
				pdt = p.pdt;
			}
		}
		// if we didn't get pdt from definitions, query property description,
		// in local dev.mgmt, no pdt description is available
		if (pdt == -1 && !local)
			pdt = (byte) (pa.getDescription(objIndex, pid, 0)[3] & 0x3f);
		if (PropertyTypes.hasTranslator(pdt))
			return PropertyTypes.createTranslator(pdt);
		final KNXException e = new KNXException("no translator available for PID 0x"
			+ Integer.toHexString(pid) + ", " + getObjectTypeName(ot));
		logger.warn("translator missing", e);
		throw e;
	}

	private static class XMLPropertyHandler implements ResourceHandler
	{
		private static final String PROPDEFS_TAG = "propertyDefinitions";
		private static final String OBJECT_TAG = "object";
		private static final String OBJECTTYPE_ATTR = "type";
		private static final String PROPERTY_TAG = "property";
		private static final String PID_ATTR = "pid";
		private static final String PIDNAME_ATTR = "pidName";
		private static final String NAME_ATTR = "name";
		private static final String PDT_ATTR = "pdt";
		private static final String DPT_ATTR = "dpt";
		private static final String RW_ATTR = "rw";
		private static final String WRITE_ATTR = "writeEnabled";
		private static final String USAGE_TAG = "usage";

		XMLPropertyHandler()
		{}

		/* (non-Javadoc)
		 * @see tuwien.auto.calimero.mgmt.PropertyClient.ResourceHandler#load
		 * (java.lang.String)
		 */
		public Collection load(String resource) throws KNXException
		{
			final XMLReader r = XMLFactory.getInstance().createXMLReader(resource);
			final List list = new ArrayList(30);
			int objType = -1;
			try {
				if (r.read() != XMLReader.START_TAG
					|| !r.getCurrent().getName().equals(PROPDEFS_TAG))
					throw new KNXMLException("no property defintions");
				while (r.read() != XMLReader.END_DOC) {
					final Element e = r.getCurrent();
					if (r.getPosition() == XMLReader.START_TAG) {
						if (e.getName().equals(OBJECT_TAG)) {
							// on no type attribute, toInt() throws, that's ok
							final String type = e.getAttribute(OBJECTTYPE_ATTR);
							objType = "global".equals(type) ? -1 : toInt(type);
						}
						else if (e.getName().equals(PROPERTY_TAG)) {
							r.complete(e);
							list.add(new Property(toInt(e.getAttribute(PID_ATTR)), e
								.getAttribute(PIDNAME_ATTR), e.getAttribute(NAME_ATTR),
								objType, toInt(e.getAttribute(PDT_ATTR)), e
									.getAttribute(DPT_ATTR)));
						}
					}
					else if (r.getPosition() == XMLReader.END_TAG
						&& e.getName().equals(PROPDEFS_TAG))
						break;
				}
				return list;
			}
			catch (final KNXFormatException e) {
				throw new KNXException("loading property definitions, " + e.getMessage());
			}
			finally {
				r.close();
			}
		}

		/* (non-Javadoc)
		 * @see tuwien.auto.calimero.mgmt.PropertyClient.ResourceHandler#save
		 * (java.lang.String, java.util.Collection)
		 */
		public void save(String resource, Collection properties) throws KNXException
		{
			final XMLWriter w = XMLFactory.getInstance().createXMLWriter(resource);
			try {
				w.writeDeclaration(true, "UTF-8");
				w.writeComment("Calimero " + Settings.getLibraryVersion() +
					" KNX property definitions, saved on " + new Date().toString());
				w.writeElement(PROPDEFS_TAG, null, null);
				final int noType = -2;
				int objType = noType;
				for (final Iterator i = properties.iterator(); i.hasNext();) {
					final Property p = (Property) i.next();
					if (p.objType != objType) {
						if (objType != noType)
							w.endElement();
						objType = p.objType;
						final List att = new ArrayList();
						att.add(new Attribute(OBJECTTYPE_ATTR, objType == -1 ? "global"
							: Integer.toString(objType)));
						w.writeElement(OBJECT_TAG, att, null);
					}
					// property attributes
					final List att = new ArrayList();
					att.add(new Attribute(PID_ATTR, Integer.toString(p.id)));
					att.add(new Attribute(PIDNAME_ATTR, p.name));
					att.add(new Attribute(NAME_ATTR, p.propName));
					att.add(new Attribute(PDT_ATTR, p.pdt == -1 ? "<tbd>" : Integer
						.toString(p.pdt)));
					if (p.dpt != null && p.dpt.length() > 0)
						att.add(new Attribute(DPT_ATTR, p.dpt));
					att.add(new Attribute(RW_ATTR, ""));
					att.add(new Attribute(WRITE_ATTR, ""));
					// write property
					w.writeElement(PROPERTY_TAG, att, null);
					w.writeElement(USAGE_TAG, null, null);
					w.endElement();
					w.endElement();
				}
			}
			finally {
				w.close();
			}
		}

		private int toInt(String s) throws KNXFormatException
		{
			try {
				if (s != null) {
					if (s.equals("<tbd>"))
						return -1;
					return s.length() == 0 ? 0 : Integer.decode(s).intValue();
				}
			}
			catch (final NumberFormatException e) {}
			throw new KNXFormatException("can't convert to number " + s, s);
		}
	}

	private static final class Pair
	{
		final int oindex;
		final int otype;

		Pair(int objIndex, int objType)
		{
			oindex = objIndex;
			otype = objType;
		}
	}
}
