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

import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;


/**
 * Represents an indication for lost routing messages for routing services.
 * <p>
 * The routing lost message is used to inform about the fact, that routing messages were
 * lost due to an overflow in the LAN-to-KNX queue, i.e. a router receives more IP packets
 * than it is able to deliver to the KNX network.<br>
 * Additionally, the router device state is supplied.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author B. Malinowsky
 */
public class RoutingLostMessage extends ServiceType
{
	private final int lost;
	private final short state;

	/**
	 * Creates a new routing lost message indication out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing a lost message indication structure
	 * @param offset start offset of indication in <code>data</code>
	 * @throws KNXFormatException if buffer is too short for routing lost message
	 *         indication or lost message info has wrong size
	 */
	public RoutingLostMessage(byte[] data, int offset) throws KNXFormatException
	{
		super(KNXnetIPHeader.ROUTING_LOST_MSG);
		if (data.length - offset < 4)
			throw new KNXFormatException("buffer too short for lost messages info");
		final int size = data[offset] & 0xFF;
		if (size != 4)
			throw new KNXFormatException("wrong size for lost messages info", size);
		state = (short) (data[offset + 1] & 0xFF);
		lost = (data[offset + 2] & 0xFF) << 8 | (data[offset + 3] & 0xFF);
	}

	/**
	 * Creates a new routing lost message indication.
	 * <p>
	 * 
	 * @param lostMessages number of KNXnet/IP routing messages lost
	 * @param deviceState router device state, the router KNX property in object type 11,
	 *        PID 69
	 */
	public RoutingLostMessage(int lostMessages, short deviceState)
	{
		super(KNXnetIPHeader.ROUTING_LOST_MSG);
		if (lostMessages < 0 || lostMessages > 0xFFFF)
			throw new KNXIllegalArgumentException(
				"lost message count out of range [0..0xFFFF]");
		if (deviceState < 0 || deviceState > 0xFF)
			throw new KNXIllegalArgumentException(
				"device state field out of range [0..0xFF]");
		// bits 2 to 7 are reserved for now...
		if (deviceState > 0x03)
			ServiceType.logger.info("Bits 2..7 not supported in device state");
		lost = lostMessages;
		state = deviceState;
	}

	/**
	 * The number of lost KNXnet/IP routing messages.
	 * <p>
	 * 
	 * @return lost messages as unsigned short
	 */
	public final int getLostMessages()
	{
		return lost;
	}

	/**
	 * Returns the router device state, i.e. the property referred to by PID 69 of object
	 * type 11 in the KNX property table.
	 * <p>
	 * 
	 * @return device state as unsigned byte
	 */
	public final short getDeviceState()
	{
		return state;
	}

	/**
	 * Returns whether the KNX network cannot be accessed, causing the message loss.
	 * <p>
	 * The KNX fault mode is part of the device state.
	 * 
	 * @return <code>true</code> on KNX access fault, <code>false</code> otherwise
	 * @see #getDeviceState()
	 */
	public final boolean isKNXFault()
	{
		return (state & 0x01) != 0;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#getStructLength()
	 */
	short getStructLength()
	{
		return 4;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#toByteArray
	 * (java.io.ByteArrayOutputStream)
	 */
	byte[] toByteArray(ByteArrayOutputStream os)
	{
		os.write(4);
		os.write(state);
		os.write(lost >> 8);
		os.write(lost);
		return os.toByteArray();
	}
}
