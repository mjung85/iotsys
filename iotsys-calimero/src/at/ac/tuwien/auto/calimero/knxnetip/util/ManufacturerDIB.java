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

import at.ac.tuwien.auto.calimero.DataUnitBuilder;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;

/**
 * Represents a manufacturer data description information block.
 * <p>
 * Since the data in this DIB is dependent on the manufacturer and might contain any
 * information, no specific content parsing is done.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author B. Malinowsky
 */
public class ManufacturerDIB extends DIB
{
	private final int id;
	private final byte[] mfrData;

	/**
	 * Creates a manufacturer data DIB out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing manufacturer data DIB structure
	 * @param offset start offset of DIB in <code>data</code>
	 * @throws KNXFormatException if no DIB found or invalid structure
	 */
	public ManufacturerDIB(byte[] data, int offset) throws KNXFormatException
	{
		super(data, offset);
		if (type != MFR_DATA)
			throw new KNXFormatException("DIB is not of type manufacturer data", type);
		if (size < 4)
			throw new KNXFormatException("MFR DIB too short");
		id = (data[2] & 0xFF) << 8 | data[3] & 0xFF;
		mfrData = new byte[data.length - offset - 4];
		for (int i = 0; i < mfrData.length; ++i)
			mfrData[i] = data[4 + offset + i];
	}

	/**
	 * Returns the KNX manufacturer ID.
	 * <p>
	 * The ID clearly identifies the manufacturer who created this DIB structure.
	 * 
	 * @return ID as unsigned short
	 */
	public final int getID()
	{
		return id;
	}

	/**
	 * Returns the manufacturer specific description data.
	 * <p>
	 * This data block starts at byte offset 4 in the DIB structure.
	 * 
	 * @return byte array with manufacturer data
	 */
	public final byte[] getData()
	{
		return (byte[]) mfrData.clone();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.util.DIB#toByteArray()
	 */
	public byte[] toByteArray()
	{
		final byte[] buf = super.toByteArray();
		buf[2] = (byte) (id >> 8);
		buf[3] = (byte) id;
		for (int i = 0; i < mfrData.length; ++i)
			buf[4 + i] = mfrData[i];
		return buf;
	}

	/**
	 * Returns a textual representation of this manufacturer DIB.
	 * <p>
	 * 
	 * @return a string representation of the DIB object
	 */
	public String toString()
	{
		return "KNX manufacturer ID 0x" + Integer.toHexString(id) + ", data 0x"
			+ DataUnitBuilder.toHex(mfrData, null);
	}
}
