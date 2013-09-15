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

package at.ac.tuwien.auto.calimero;

import java.util.EventObject;

import at.ac.tuwien.auto.calimero.link.event.LinkListener;


/**
 * Informs about the closing of a previously established communication with the KNX
 * network.
 * <p>
 * In general, the source of the event is the connection object or network link to be
 * closed.
 * 
 * @author B. Malinowsky
 * @see LinkListener
 * @see KNXListener
 */
public class CloseEvent extends EventObject
{
	private static final long serialVersionUID = 1L;

	private final boolean user;
	private final String msg;

	/**
	 * Creates a new close event object.
	 * <p>
	 * 
	 * @param source the communication object to be closed
	 * @param userRequest <code>true</code> if the closing was requested by the user of
	 *        the object, <code>false</code> otherwise (for example, a close initiated by
	 *        a remote server)
	 * @param reason brief description of the reason leading to the close event
	 */
	public CloseEvent(Object source, boolean userRequest, String reason)
	{
		super(source);
		user = userRequest;
		msg = reason;
	}

	/**
	 * Returns whether the close event was initiated by the user of the communication
	 * object.
	 * <p>
	 * 
	 * @return <code>true</code> if close is user requested, <code>false</code>
	 *         otherwise
	 */
	public final boolean isUserRequest()
	{
		return user;
	}

	/**
	 * Returns a brief textual description of the closing reason.
	 * <p>
	 * 
	 * @return reason as string
	 */
	public final String getReason()
	{
		return msg;
	}
}
