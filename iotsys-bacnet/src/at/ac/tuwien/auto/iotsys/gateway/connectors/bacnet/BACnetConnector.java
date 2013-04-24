/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2013 
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.Connector;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.event.DefaultDeviceEventListener;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.PropertyReference;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;

public class BACnetConnector implements Connector{
	private static final Logger log = Logger.getLogger(BACnetConnector.class.getName());
	public static void main(String[] args) {
		new BACnetConnector();
	}

	private LocalDevice localDevice;
	private static final int BACNET_PORT = 0xBAC0;

	private final Hashtable<Integer, RemoteDevice> remoteDevices = new Hashtable<Integer, RemoteDevice>();
	private final List<DeviceDiscoveryListener> discoveryListeners = new ArrayList<DeviceDiscoveryListener>();
	private final Obj root = new Obj();

	private int localDeviceID = (int) ((Math.random() * 10000) + 10000);
	private String broadCastIP = "128.130.56.255";
	private int localDevicePort = BACNET_PORT;
	
	public static final UnsignedInteger BACNET_PRIORITY = new UnsignedInteger(10);

	public BACnetConnector() {
		this(12345, "128.130.56.255", 0xBAC0);
	}

	public BACnetConnector(int localDeviceID, String broadCastIP,
			int localDevicePort) {
		this.localDeviceID = localDeviceID;
		this.broadCastIP = broadCastIP;
		this.localDevicePort = localDevicePort;
		log.info("Creating BACnet connector - localDeviceID: " + localDeviceID + ", broadCastIP: "+ broadCastIP + ", localDevicePort: " + localDevicePort);
		localDevice = new LocalDevice(this.localDeviceID, this.broadCastIP);
		localDevice.setPort(this.localDevicePort);

		localDevice.getEventHandler().addListener(new DeviceListener());
	}
	
	public void connect() {
		try {
			log.info("Initializing BACnet connector.");
			localDevice.initialize();

		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}

	public void disconnect() {
		log.info("Disconnecting BACnet.");
		localDevice.terminate();
	}

	class DeviceListener extends DefaultDeviceEventListener {
		public void iAmReceived(RemoteDevice d) {
			deviceDiscovered(d);
		}

		public void iHaveReceived(RemoteDevice d, RemoteObject o) {
			deviceDiscovered(d);
		}
	}

	public Encodable readProperty(int deviceInstanceNumber,
			ObjectIdentifier objectIdentifier,
			PropertyIdentifier propertyIdentifier) throws BACnetException,
			PropertyValueException {
		synchronized (remoteDevices) {
			RemoteDevice remoteDev = remoteDevices.get(deviceInstanceNumber);

			if (remoteDev == null) {
				return null;
			}
			PropertyReferences refs = new PropertyReferences();

			PropertyReference propRef = new PropertyReference(
					propertyIdentifier);

			// refs.add(obj, eventDeadline);
			refs.add(objectIdentifier, propRef);

			remoteDev.setMaxAPDULengthAccepted(1476);
			remoteDev.setSegmentationSupported(Segmentation.segmentedBoth);

			PropertyValues pvs = localDevice.readProperties(remoteDev, refs);
			Encodable property = pvs.get(objectIdentifier, propertyIdentifier);

			return property;
		}
	}

	public void writeProperty(int deviceInstanceNumber,
			ObjectIdentifier objectIdentifier,
			PropertyIdentifier propertyIdentifier, Encodable property,
			UnsignedInteger priority) throws BACnetException,
			PropertyValueException {
		
		synchronized (remoteDevices) {
			RemoteDevice remoteDev = remoteDevices.get(deviceInstanceNumber);

			if (remoteDev == null) {
				return;
			}

			PropertyReferences refs = new PropertyReferences();

			PropertyReference propRef = new PropertyReference(
					propertyIdentifier);

			refs.add(objectIdentifier, propRef);

			remoteDev.setMaxAPDULengthAccepted(1476);
			remoteDev.setSegmentationSupported(Segmentation.segmentedBoth);

			AcknowledgementService ack = localDevice.setProperty(remoteDev,
					objectIdentifier, propertyIdentifier, property, priority);
			
		}
	}
	
	private List<RemoteObject> fetchObjects(RemoteDevice device) throws BACnetException, PropertyValueException {
		Encodable objlist = readProperty(device.getInstanceNumber(), new ObjectIdentifier(new ObjectType(8), device.getInstanceNumber()), new PropertyIdentifier(76));
		device.clearObjects();
		
		if (objlist instanceof SequenceOf<?>) {
			List<?> objects = ((SequenceOf<?>) objlist).getValues();
			for (int i = 0; i < objects.size(); i++) {
				if (!(objects.get(i) instanceof ObjectIdentifier)) continue;
				device.setObject(new RemoteObject((ObjectIdentifier) objects.get(i)));
			}
		}
		
		return device.getObjects();
	}
	
	public interface DeviceDiscoveryListener {
		public void deviceDiscovered(Obj device);
	}

	public void discover(DeviceDiscoveryListener discoveryListener) {
		synchronized (discoveryListeners) {
			if (discoveryListener != null && !discoveryListeners.contains(discoveryListener))
				discoveryListeners.add(discoveryListener);
		}
		
		// discover all devices
		try {
			localDevice.sendBroadcast(BACNET_PORT, null, new WhoIsRequest());
		} catch (BACnetException e) {
			e.printStackTrace();
		}
	}
	
	private void deviceDiscovered(RemoteDevice device) {
		log.fine("BACnet device discovered - instance number "
				+ device.getInstanceNumber());
		
		synchronized (remoteDevices) {
			remoteDevices.put(device.getInstanceNumber(), device);
		}
		
		try {
			List<RemoteObject> objects = fetchObjects(device);
			for (RemoteObject object : objects) {
				ObjectIdentifier objIdentifier = object.getObjectIdentifier();
				
				String href = "bacnet_" + root.getName() + "_" + device.getInstanceNumber() + "_" + objIdentifier.toString().replaceAll(" ", "");
				if (objIdentifier.getObjectType().intValue() > 5) continue;
				
				Obj bacnetDevice = BacnetDeviceFactory.createDevice(this, device, objIdentifier);
				if (bacnetDevice == null) continue;
				bacnetDevice.setHref(new Uri(href));
				
				synchronized (discoveryListeners) {
					for (DeviceDiscoveryListener listener : discoveryListeners) {
						listener.deviceDiscovered(bacnetDevice);
					}
				}
				
				root.add(bacnetDevice);
			}
		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
	}

	public Obj getRootObj() {
		return root;
	}
}
