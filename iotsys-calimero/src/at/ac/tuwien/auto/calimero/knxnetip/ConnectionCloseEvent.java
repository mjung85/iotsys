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

package at.ac.tuwien.auto.calimero.knxnetip;

import at.ac.tuwien.auto.calimero.CloseEvent;

/**
 * An event providing information regarding the closing of an established KNXnet/IP
 * communication connection.
 * <p>
 */
public class ConnectionCloseEvent extends CloseEvent
{
	/**
	 * Identifier stating the close event was initiated by the client.
	 * <p>
	 */
	public static final int CLIENT = 0;

	/**
	 * Identifier stating the close event was initiated by the remote endpoint/server.
	 * <p>
	 */
	public static final int SERVER = 1;

	/**
	 * Identifier stating the close event was initiated internal (for example due to an
	 * unsupported protocol version).
	 * <p>
	 */
	public static final int INTERNAL = 2;

	private static final long serialVersionUID = 1L;

	private final int initiator;

	/**
	 * Creates a new close event.
	 * <p>
	 * 
	 * @param source the object on which the Event initially occurred
	 * @param initiator initiator of the close event, one of {@link #CLIENT},
	 *        {@link #SERVER} or {@link #INTERNAL}
	 * @param reason brief textual description
	 */
	public ConnectionCloseEvent(Object source, int initiator, String reason)
	{
		super(source, initiator == CLIENT, reason);
		this.initiator = initiator;
	}

	/**
	 * Returns the initiator of the close event, one of {@link #CLIENT}, {@link #SERVER}
	 * or {@link #INTERNAL}.
	 * <p>
	 * 
	 * @return identifier of the initiator
	 */
	public final int getInitiator()
	{
		return initiator;
	}
}
