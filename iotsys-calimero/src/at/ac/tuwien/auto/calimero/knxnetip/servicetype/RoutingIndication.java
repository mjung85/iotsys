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

package at.ac.tuwien.auto.calimero.knxnetip.servicetype;

import java.io.ByteArrayOutputStream;

import at.ac.tuwien.auto.calimero.cemi.CEMI;
import at.ac.tuwien.auto.calimero.cemi.CEMIFactory;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;


/**
 * Represents a routing indication for routing services.
 * <p>
 * The data format contained in routing messages is cEMI. It is used to send a cEMI
 * message over IP networks. The routing indication service is unconfirmed.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author B. Malinowsky
 */
public class RoutingIndication extends ServiceType
{
	private final CEMI cemi;

	/**
	 * Creates a routing indication carrying the given cEMI frame.
	 * <p>
	 * 
	 * @param frame cEMI frame to be routed over IP networks
	 */
	public RoutingIndication(CEMI frame)
	{
		super(KNXnetIPHeader.ROUTING_IND);
		cemi = CEMIFactory.copy(frame);
	}

	/**
	 * Creates a routing indication out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing a indication structure
	 * @param offset start offset of indication in <code>data</code>
	 * @param length length in bytes of whole indication structure
	 * @throws KNXFormatException if the contained cEMI frame could not be created
	 */
	public RoutingIndication(byte[] data, int offset, int length)
		throws KNXFormatException
	{
		super(KNXnetIPHeader.ROUTING_IND);
		cemi = CEMIFactory.create(data, offset, length);
	}

	/**
	 * Returns the cEMI frame contained in the indication.
	 * <p>
	 * 
	 * @return a cEMI type
	 */
	public final CEMI getCEMI()
	{
		return CEMIFactory.copy(cemi);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#getStructLength()
	 */
	short getStructLength()
	{
		return cemi.getStructLength();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#toByteArray
	 *      (java.io.ByteArrayOutputStream)
	 */
	byte[] toByteArray(ByteArrayOutputStream os)
	{
		final byte[] buf = cemi.toByteArray();
		os.write(buf, 0, buf.length);
		return os.toByteArray();
	}
}
