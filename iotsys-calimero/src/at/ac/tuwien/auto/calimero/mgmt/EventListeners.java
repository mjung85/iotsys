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

package at.ac.tuwien.auto.calimero.mgmt;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * Internal container for keeping event listeners.
 * <p>
 * 
 * @author B. Malinowsky
 */
final class EventListeners
{
	private final List listeners = new ArrayList();
	private List listenersCopy = new ArrayList();
	private final LogService logger;

	EventListeners(LogService logger)
	{
		this.logger = logger;
	}

	void add(EventListener l)
	{
		if (l == null)
			return;
		synchronized (listeners) {
			if (!listeners.contains(l)) {
				listeners.add(l);
				listenersCopy = new ArrayList(listeners);
			}
			else
				logger.warn("event listener already registered");
		}
	}

	void remove(EventListener l)
	{
		synchronized (listeners) {
			if (listeners.remove(l))
				listenersCopy = new ArrayList(listeners);
		}
	}

	Iterator iterator()
	{
		return listenersCopy.iterator();
	}
}
