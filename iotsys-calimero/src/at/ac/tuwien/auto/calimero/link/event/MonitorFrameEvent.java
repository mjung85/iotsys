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

package at.ac.tuwien.auto.calimero.link.event;

import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.cemi.CEMI;
import at.ac.tuwien.auto.calimero.link.KNXNetworkMonitor;
import at.ac.tuwien.auto.calimero.link.medium.RawFrame;

/**
 * Informs about a new monitor indication received from the KNX network and contains the
 * received indication frame.
 * <p>
 * The source of the event is a {@link KNXNetworkMonitor}.
 * 
 * @author B. Malinowsky
 */
public class MonitorFrameEvent extends FrameEvent
{
	// ??? ctor which takes error information if raw frame creation failed

	private static final long serialVersionUID = 1L;

	private final RawFrame raw;

	/**
	 * Creates a new monitor frame event with the indication frame.
	 * 
	 * @param source the network monitor which received the frame
	 * @param frame monitor indication frame encapsulated in cEMI type
	 */
	public MonitorFrameEvent(Object source, CEMI frame)
	{
		super(source, frame);
		raw = null;
	}

	/**
	 * Creates a new monitor frame event with the indication frame and the decoded raw
	 * frame.
	 * 
	 * @param source the network monitor which received the frame
	 * @param frame monitor indication frame encapsulated in cEMI type
	 * @param rawFrame the decoded raw frame on medium encapsulated in type RawFrame, use
	 *        <code>null</code> if no decoded raw frame is available
	 */
	public MonitorFrameEvent(Object source, CEMI frame, RawFrame rawFrame)
	{
		super(source, frame);
		raw = rawFrame;
	}

	/**
	 * Returns the decoded raw frame on medium.
	 * <p>
	 * 
	 * @return the frame of type RawFrame or <code>null</code> on no decoded raw frame
	 */
	public final RawFrame getRawFrame()
	{
		return raw;
	}
}
