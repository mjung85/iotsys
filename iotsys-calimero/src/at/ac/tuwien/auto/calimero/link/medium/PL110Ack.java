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

package at.ac.tuwien.auto.calimero.link.medium;

import at.ac.tuwien.auto.calimero.exception.KNXFormatException;

/**
 * Raw acknowledge frame on PL110 communication medium.
 * <p>
 * 
 * @author B. Malinowsky
 */
public class PL110Ack extends RawAckBase
{
	/**
	 * Creates a new PL110 acknowledge frame out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing the acknowledge frame
	 * @param offset start offset of frame structure in <code>data</code>, offset &gt;=
	 *        0
	 * @throws KNXFormatException if no valid acknowledge frame was found
	 */
	public PL110Ack(byte[] data, int offset) throws KNXFormatException
	{
		final int ctrl = data[offset] & 0xff;
		if (ctrl == ACK)
			ack = ACK;
		else if (ctrl == NAK)
			ack = NAK;
		else if ((ctrl & 0xD3) == 0x90)
			// filter L-Data.req ID
			throw new KNXFormatException("no PL110 ACK frame, L-Data.req control field");
		else
			// everything else is interpreted as NAK
			ack = NAK;
	}
}
