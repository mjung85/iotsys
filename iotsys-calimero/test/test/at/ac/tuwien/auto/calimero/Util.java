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

package test.at.ac.tuwien.auto.calimero;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import at.ac.tuwien.auto.calimero.IndividualAddress;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.knxnetip.Discoverer;
import at.ac.tuwien.auto.calimero.log.LogLevel;
import at.ac.tuwien.auto.calimero.log.LogStreamWriter;
import at.ac.tuwien.auto.calimero.log.LogWriter;
import at.ac.tuwien.auto.calimero.serial.FT12Connection;


/**
 * @author B. Malinowsky
 */
public final class Util
{
	/**
	 * Sets whether NAT functionality should be tested (false for devices without NAT
	 * support).
	 */
	public static final boolean TEST_NAT = false;
	
	// supply address here to prevent automatic router discovery
	private static InetSocketAddress server;
	private static IndividualAddress device;
	
	private static final LogWriter w =
		new LogStreamWriter(LogLevel.ALL, System.out, true)
		{
			public void close()
			{}
		};

	private Util()
	{}

	/**
	 * Standard out desc and toHexDec(bytes).
	 * <p>
	 * 
	 * @param desc description
	 * @param bytes bytes to print hex and decimal
	 */
	public static void out(String desc, byte[] bytes)
	{
		System.out.println(desc + ": " + toHexDec(bytes));
	}

	/**
	 * Format first 200 bytes into hex, followed by decimal presentation.
	 * <p>
	 * 
	 * @param bytes bytes to format
	 * @return formatted bytes as string
	 */
	public static String toHexDec(byte[] bytes)
	{
		final StringBuffer buf = new StringBuffer();
		final int max = Math.min(200, bytes.length);
		for (int i = 0; i < max; ++i) {
			final String hex = Integer.toHexString(bytes[i] & 0xff);
			if (hex.length() == 1)
				buf.append("0");
			buf.append(hex);
			buf.append(" ");
		}
		if (max < bytes.length)
			buf.append("...");
		buf.append("(");
		for (int i = 0; i < max; ++i) {
			final String no = Integer.toString(bytes[i] & 0xff);
			buf.append(no);
			if (i < bytes.length - 1)
				buf.append(" ");
		}
		if (max < bytes.length)
			buf.append("...");
		buf.append(")");
		return buf.toString();
	}

	/**
	 * Returns a log writer for standard out.
	 * <p>
	 * 
	 * @return LogWriter
	 */
	public static LogWriter getLogWriter()
	{
		return w;
	}

	/**
	 * Returns KNXnet/IP router address used for testing.
	 * <p>
	 * 
	 * @return router individual address
	 */
	public static IndividualAddress getRouterAddress()
	{
		if (device == null) {
			Discoverer d;
			try {
				d = new Discoverer(getLocalHost().getAddress(), getLocalHost().getPort(),
					false);
				d.startSearch(2, true);
				device = d.getSearchResponses()[0].getDevice().getAddress();
			}
			catch (final KNXException e) {
				e.printStackTrace();
			}
		}
		return device;
	}

	public static InetSocketAddress getLocalHost()
	{
		// don't trust default local host resolving of Java
		try {
			return new InetSocketAddress(InetAddress.getByName("192.168.0.10"), 0);
		}
		catch (final UnknownHostException e) {}
		return null;
	}

	/**
	 * Returns the socket address of the KNXnet/IP router to use for testing.
	 * <p>
	 * 
	 * @return socket address
	 * @throws KNXException if KNXnet/IP discovery failed
	 */
	public static InetSocketAddress getServer() throws KNXException
	{
		if (server == null) {
			Discoverer d;
			d = new Discoverer(getLocalHost().getAddress(), getLocalHost().getPort(),
				false);
			d.startSearch(2, true);
			server =
				new InetSocketAddress(d.getSearchResponses()[0].getControlEndpoint()
					.getAddress(), d.getSearchResponses()[0].getControlEndpoint()
					.getPort());
			device = d.getSearchResponses()[0].getDevice().getAddress();
		}
		return server;
	}

	/**
	 * Returns the serial port number to use for testing the FT1.2 protocol.
	 * <p>
	 * The returned port has to correspond with the port identifier returned by
	 * {@link #getSerialPortID()}.
	 * 
	 * @return port number
	 */
	public static int getSerialPort()
	{
		// on windows platforms, it is port 1 most of the time
		return 0;
	}

	/**
	 * Returns the serial port identifier to use for testing the FT1.2 protocol.
	 * <p>
	 * 
	 * @return port ID, <code>null</code> if no ID found
	 */
	public static String getSerialPortID()
	{
		final String[] ids = FT12Connection.getPortIdentifiers();
		return ids.length > 0 ? ids[0] : null;
	}
}
