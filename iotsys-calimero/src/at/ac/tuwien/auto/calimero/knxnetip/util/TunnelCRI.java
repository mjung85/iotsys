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

import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel;

/**
 * Connection request information used for KNX tunneling connection.
 * <p>
 * 
 * @author B. Malinowsky
 */
public class TunnelCRI extends CRI
{
	/**
	 * Creates a new CRI for tunnel connection type out of a byte array.
	 * <p>
	 * The CRI structure has a length of 4 bytes.<br>
	 * 
	 * @param data byte array containing a CRI structure,
	 *        <code>data.length - offset = 4</code>
	 * @param offset start offset of CRI in <code>data</code>
	 * @throws KNXFormatException if no CRI found or invalid structure
	 */
	public TunnelCRI(byte[] data, int offset) throws KNXFormatException
	{
		super(data, offset);
		if (getConnectionType() != KNXnetIPTunnel.TUNNEL_CONNECTION)
			throw new KNXFormatException("not a tunneling CRI", getConnectionType());
		if (getStructLength() != 4)
			throw new KNXFormatException("wrong length for tunneling CRI");
	}

	/**
	 * Creates a new CRI for tunnel connection type on the given KNX layer.
	 * <p>
	 * 
	 * @param KNXLayer KNX layer specifying the kind of tunnel (e.g. link layer tunnel)
	 */
	public TunnelCRI(short KNXLayer)
	{
		super(KNXnetIPTunnel.TUNNEL_CONNECTION, new byte[] { (byte) KNXLayer, 0 });
		if (KNXLayer < 0 || KNXLayer > 0xff)
			throw new KNXIllegalArgumentException("KNX layer out of range [0..255]");
	}

	/**
	 * Creates a CRI for tunnel connection type containing optional data.
	 * <p>
	 * Note, the optional data field contains the KNX tunnel layer.<br>
	 * 
	 * @param optionalData byte array containing tunneling host protocol data, this
	 *        information is located starting at offset 2 in the CRI structure,
	 *        <code>optionalData.length</code> = 2
	 */
	TunnelCRI(byte[] optionalData)
	{
		super(KNXnetIPTunnel.TUNNEL_CONNECTION, (byte[]) optionalData.clone());
		if (getStructLength() != 4)
			throw new KNXIllegalArgumentException("wrong length for tunneling CRI");
	}

	/**
	 * Returns the KNX tunneling layer.
	 * <p>
	 * 
	 * @return layer value as unsigned byte
	 */
	public final short getKNXLayer()
	{
		return (short) (opt[0] & 0xFF);
	}

	/**
	 * Returns a textual representation of this tunnel CRI.
	 * <p>
	 * 
	 * @return a string representation of the object
	 */
	public String toString()
	{
		return "tunneling CRI, KNX layer " + getKNXLayer();
	}
}
