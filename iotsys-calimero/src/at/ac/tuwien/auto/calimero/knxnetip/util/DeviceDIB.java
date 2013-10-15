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

package at.ac.tuwien.auto.calimero.knxnetip.util;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import at.ac.tuwien.auto.calimero.DataUnitBuilder;
import at.ac.tuwien.auto.calimero.IndividualAddress;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;


/**
 * Represents a device description information block.
 * <p>
 * <p>
 * Objects of this type are immutable.
 * 
 * @author B. Malinowsky
 * @see at.ac.tuwien.auto.calimero.knxnetip.servicetype.DescriptionResponse
 */
public class DeviceDIB extends DIB
{
	/**
	 * KNX medium code for twisted pair 0 (2400 bit/s), inherited from BatiBUS.
	 * <p>
	 */
	public static final short MEDIUM_TP0 = 0x01;

	/**
	 * KNX medium code for twisted pair 1 (9600 bit/s).
	 * <p>
	 */
	public static final short MEDIUM_TP1 = 0x02;

	/**
	 * KNX medium code for power line 110 kHz (1200 bit/s).
	 * <p>
	 */
	public static final short MEDIUM_PL110 = 0x04;

	/**
	 * KNX medium code for power line 132 kHz (2400 bit/s), inherited from EHS.
	 * <p>
	 */
	public static final short MEDIUM_PL132 = 0x08;

	/**
	 * KNX medium code for radio frequency (868 MHz).
	 * <p>
	 */
	public static final short MEDIUM_RF = 0x10;

	private static final short DIB_SIZE = 54;

	private final short devicestatus;
	private final short knxmedium;
	private final byte[] serial = new byte[6];
	private final int projectInstallID;
	private final IndividualAddress address;
	private final byte[] mcaddress = new byte[4];
	private final byte[] mac = new byte[6];
	private final String name;

	/**
	 * Creates a device DIB out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing device DIB structure
	 * @param offset start offset of DIB in <code>data</code>
	 * @throws KNXFormatException if no DIB found or invalid structure
	 */
	public DeviceDIB(byte[] data, int offset) throws KNXFormatException
	{
		super(data, offset);
		if (type != DEVICE_INFO)
			throw new KNXFormatException("DIB is not of type device info", type);
		if (size < DIB_SIZE)
			throw new KNXFormatException("device info DIB too short", size);
		final ByteArrayInputStream is =
			new ByteArrayInputStream(data, offset + 2, data.length - offset - 2);
		knxmedium = (short) is.read();
		devicestatus = (short) is.read();
		address =
			new IndividualAddress(new byte[] { (byte) is.read(), (byte) is.read(), });
		projectInstallID = (is.read() << 8) | is.read();
		is.read(serial, 0, serial.length);
		is.read(mcaddress, 0, mcaddress.length);
		is.read(mac, 0, mac.length);

		// device friendly name is optional
		final StringBuffer sbuf = new StringBuffer(30);
		int i = 30;
		for (int c = is.read(); i > 0 && c > 0; --i, c = is.read())
			sbuf.append((char) c);
		name = sbuf.toString();
	}

	/**
	 * Returns the device individual address.
	 * <p>
	 * 
	 * @return individual address as {@link IndividualAddress}
	 */
	public final IndividualAddress getAddress()
	{
		return address;
	}

	/**
	 * Returns the device status byte.
	 * <p>
	 * Bit 0 is programming mode flag.<br>
	 * 
	 * @return status as unsigned byte
	 */
	public final short getDeviceStatus()
	{
		return devicestatus;
	}

	/**
	 * Returns the KNX medium code.
	 * <p>
	 * 
	 * @return KNX medium as unsigned byte
	 */
	public final short getKNXMedium()
	{
		return knxmedium;
	}

	/**
	 * Returns a textual representation of the KNX medium code.
	 * <p>
	 * 
	 * @return KNX medium as string format
	 * @see #getKNXMedium()
	 */
	public String getKNXMediumString()
	{
		switch (knxmedium) {
		case MEDIUM_TP0:
			return "TP0";
		case MEDIUM_TP1:
			return "TP1";
		case MEDIUM_PL110:
			return "PL110";
		case MEDIUM_PL132:
			return "PL132";
		case MEDIUM_RF:
			return "RF";
		default:
			return "unknown";
		}
	}

	/**
	 * Returns the device Ethernet MAC address.
	 * <p>
	 * 
	 * @return byte array containing MAC address
	 */
	public final byte[] getMACAddress()
	{
		return (byte[]) mac.clone();
	}

	/**
	 * Returns a textual representation of the device Ethernet MAC address.
	 * <p>
	 * 
	 * @return MAC address as string format
	 */
	public final String getMACAddressString()
	{
		return DataUnitBuilder.toHex(mac, "-");
	}

	/**
	 * Returns the device routing multicast address.
	 * <p>
	 * For devices which don't implement routing, the multicast address is 0.
	 * 
	 * @return multicast address as byte array
	 */
	public final byte[] getMulticastAddress()
	{
		return (byte[]) mcaddress.clone();
	}

	/**
	 * Returns the project-installation identifier of this device.
	 * <p>
	 * This ID uniquely identifies a device in a project with more than one installation.
	 * The lowest 4 bits specify the installation number, bit 4 to 15 (MSB) contain the
	 * project number.
	 * 
	 * @return project installation identifier as unsigned short
	 */
	public final int getProjectInstallID()
	{
		return projectInstallID;
	}

	/**
	 * Returns the project number for the device.
	 * <p>
	 * The project number is the upper 12 bits of the project-installation identifier.
	 * 
	 * @return project number as 12 bit unsigned value
	 * @see #getProjectInstallID()
	 */
	public final short getProject()
	{
		return (short) (projectInstallID >> 4);
	}

	/**
	 * Returns the installation number for the device.
	 * <p>
	 * The installation number is the lower 4 bits of the project-installation identifier.
	 * 
	 * @return installation number as 4 bit unsigned value
	 * @see #getProjectInstallID()
	 */
	public final byte getInstallation()
	{
		return (byte) (projectInstallID & 0x0F);
	}

	/**
	 * Returns the KNX serial number of the device.
	 * <p>
	 * The serial number uniquely identifies a device.
	 * 
	 * @return byte array with serial number
	 */
	public final byte[] getSerialNumber()
	{
		return (byte[]) serial.clone();
	}

	/**
	 * Returns a textual representation of the device KNX serial number.
	 * <p>
	 * 
	 * @return serial number as string
	 */
	public final String getSerialNumberString()
	{
		return DataUnitBuilder.toHex(serial, null);
	}

	/**
	 * Returns the device friendly name.
	 * <p>
	 * This name is used to display a device in textual format. The maximum name length is
	 * 30 characters.
	 * 
	 * @return device name as string
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 * Returns a textual representation of this device DIB.
	 * <p>
	 * 
	 * @return a string representation of the object
	 */
	public String toString()
	{
		InetAddress mc = null;
		try {
			mc = InetAddress.getByAddress(getMulticastAddress());
		}
		catch (final UnknownHostException ignore) {}
		return "device " + address + " \"" + name + "\" KNX medium "
			+ getKNXMediumString() + ", installation " + getInstallation() + " project "
			+ getProject() + " (project-installation-ID " + projectInstallID + ")"
			+ ", routing multicast address " + mc + ", MAC address "
			+ getMACAddressString() + ", S/N 0x" + getSerialNumberString();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.util.DIB#toByteArray()
	 */
	public byte[] toByteArray()
	{
		final byte[] buf = super.toByteArray();
		int i = 2;
		buf[i++] = (byte) knxmedium;
		buf[i++] = (byte) devicestatus;
		final byte[] addr = address.toByteArray();
		buf[i++] = addr[0];
		buf[i++] = addr[1];
		buf[i++] = (byte) (projectInstallID >> 8);
		buf[i++] = (byte) projectInstallID;
		for (int k = 0; k < 6; ++k)
			buf[i++] = serial[k];
		for (int k = 0; k < 4; ++k)
			buf[i++] = mcaddress[k];
		for (int k = 0; k < 6; ++k)
			buf[i++] = mac[k];
		for (int k = 0; k < name.length(); ++k)
			buf[i++] = (byte) name.charAt(k);
		return buf;
	}
}
