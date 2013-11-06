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
import java.net.InetAddress;
import java.net.InetSocketAddress;

import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.knxnetip.util.HPAI;


/**
 * Represents a KNXnet/IP search request.
 * <p>
 * Such request is sent during KNXnet/IP device discovery destined to the discovery
 * endpoint of listening servers. The communication is done multicast, i.e. using the UDP
 * transport protocol.<br>
 * The counterpart sent in reply to the request are search responses.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author Bernhard Erb
 * @author B. Malinowsky
 * @see at.ac.tuwien.auto.calimero.knxnetip.servicetype.SearchResponse
 * @see at.ac.tuwien.auto.calimero.knxnetip.Discoverer
 */
public class SearchRequest extends ServiceType
{
	private final HPAI endpoint;

	/**
	 * Creates a search request out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing a search request structure
	 * @param offset start offset of request in <code>data</code>
	 * @throws KNXFormatException if no valid host protocol address information was found
	 */
	public SearchRequest(byte[] data, int offset) throws KNXFormatException
	{
		super(KNXnetIPHeader.SEARCH_REQ);
		endpoint = new HPAI(data, offset);
	}

	/**
	 * Creates a new search request with the given client response address.
	 * <p>
	 * 
	 * @param responseAddr address of the client discovery endpoint used for the response,
	 *        use <code>null</code> to create a NAT aware search request
	 */
	public SearchRequest(InetSocketAddress responseAddr)
	{
		super(KNXnetIPHeader.SEARCH_REQ);
		endpoint = new HPAI(HPAI.IPV4_UDP, responseAddr);
	}

	/**
	 * Convenience constructor to create a new search request using the system default
	 * local host with the given client port.
	 * <p>
	 * 
	 * @param responsePort port number of the client control endpoint used for the
	 *        response, 0 &lt;= port &lt;= 0xFFFF
	 */
	public SearchRequest(int responsePort)
	{
		super(KNXnetIPHeader.SEARCH_REQ);
		endpoint = new HPAI((InetAddress) null, responsePort);
	}

	/**
	 * Returns the client discovery endpoint.
	 * <p>
	 * 
	 * @return discovery endpoint in a HPAI
	 */
	public final HPAI getEndpoint()
	{
		return endpoint;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#getStructLength()
	 */
	short getStructLength()
	{
		return endpoint.getStructLength();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#toByteArray
	 *      (java.io.ByteArrayOutputStream)
	 */
	byte[] toByteArray(ByteArrayOutputStream os)
	{
		final byte[] buf = endpoint.toByteArray();
		os.write(buf, 0, buf.length);
		return os.toByteArray();
	}
}
