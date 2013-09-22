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

/**
 * Raw acknowledge frame on PL132 communication medium.
 * <p>
 * The acknowledge on PL132 medium is handled using the frame checksum in the associated
 * frame request.<br>
 * A positive acknowledgment frame contains the sequence of bits received in the FCS field
 * of the associated L-data request. A negative acknowledgment contains the bitwise
 * complement of the checksum received with the L-data request.
 * 
 * @author B. Malinowsky
 */
public class PL132Ack extends RawAckBase
{
	/**
	 * Negative acknowledge type, reception buffer is full.
	 * <p>
	 * 
	 * @see #getAckType()
	 */
	public static final int FULL = 0x100;

	/**
	 * The kind of acknowledge is unknown, because no checksum from the frame request is
	 * available for comparison, or the delivered checksum in the ack-frame is invalid.
	 * <p>
	 * 
	 * @see #getAckType()
	 */
	public static final int UNKNOWN_ACK = 0x200;

	private final int fcs;

	/**
	 * Creates a new PL132 acknowledge frame out of a byte array.
	 * <p>
	 * The type of acknowledge returned for the created frame is always
	 * {@link PL132Ack#UNKNOWN_ACK}, since no checksum of the associated frame request is
	 * supplied. Use {@link #getChecksum()} to determine the type of acknowledge.
	 * 
	 * @param data byte array containing the acknowledge frame
	 * @param offset start offset of frame structure in <code>data</code>, offset &gt;=
	 *        0
	 */
	public PL132Ack(byte[] data, int offset)
	{
		final int hi = data[offset] & 0xff;
		fcs = hi << 8 | (data[offset + 1] & 0xff);
		ack = UNKNOWN_ACK;
	}

	/**
	 * Creates a new PL132 acknowledge frame out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing the acknowledge frame
	 * @param offset start offset of frame structure in <code>data</code>, offset &gt;=
	 *        0
	 * @param requestChecksum checksum of the associated frame request
	 */
	public PL132Ack(byte[] data, int offset, int requestChecksum)
	{
		final int hi = data[offset] & 0xff;
		fcs = hi << 8 | data[offset + 1] & 0xff;
		if (fcs == requestChecksum)
			ack = ACK;
		else if (((~fcs) & 0xffff) == requestChecksum)
			ack = FULL;
		else
			ack = UNKNOWN_ACK;
	}

	/**
	 * Returns the checksum contained in this acknowledge frame.
	 * <p>
	 * 
	 * @return checksum as unsigned short
	 */
	public final int getChecksum()
	{
		return fcs;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.medium.RawAckBase#toString()
	 */
	public String toString()
	{
		return ack == ACK ? "ACK" : ack == FULL ? "FULL" : "unknown ack";
	}
}
