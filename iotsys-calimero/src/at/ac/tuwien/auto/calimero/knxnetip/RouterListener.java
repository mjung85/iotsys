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

import at.ac.tuwien.auto.calimero.KNXListener;

/**
 * A listener for use with a {@link KNXnetIPRouter}.
 * <p>
 * In routing mode, it is possible that indications sent with KNXnetIPRouter might be
 * looped back by the network stack and received again through a registered listener in
 * {@link KNXListener#frameReceived(at.ac.tuwien.auto.calimero2.FrameEvent)}.<br>
 * Background: a platform's network interface has the option to send multicast
 * datagrams back to the local socket of the sender; setting the socket's loopback mode is
 * only considered as request and does not have to be followed rigorously.<br>
 * 
 * @author B. Malinowsky
 * @see KNXnetIPRouter
 */
public interface RouterListener extends KNXListener
{
	/**
	 * Informs about the loss of messages in the KNXnet/IP router.
	 * <p>
	 * 
	 * @param e event with lost message information
	 */
	void lostMessage(LostMessageEvent e);
}
