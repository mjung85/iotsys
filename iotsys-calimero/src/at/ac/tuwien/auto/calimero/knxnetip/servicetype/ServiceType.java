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

import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * Common base for the different service type structures.
 * <p>
 * 
 * @author B. Malinowsky
 */
abstract class ServiceType
{
	static final LogService logger =
		LogManager.getManager().getLogService("KNXnet/IP service");

	final int svcType;

	ServiceType(int serviceType)
	{
		svcType = serviceType;
	}

	/**
	 * Returns the service type structure formatted into a byte array.
	 * <p>
	 * 
	 * @return service type structure as byte array
	 * @see PacketHelper
	 */
	public final byte[] toByteArray()
	{
		return toByteArray(new ByteArrayOutputStream(50));
	}

	/**
	 * Returns the service type name of this service type.
	 * <p>
	 * 
	 * @return service type as string
	 */
	public String toString()
	{
		return KNXnetIPHeader.getSvcName(svcType);
	}

	abstract byte[] toByteArray(ByteArrayOutputStream os);

	abstract short getStructLength();
}
