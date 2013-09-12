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

package at.ac.tuwien.auto.calimero.link;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.KNXListener;
import at.ac.tuwien.auto.calimero.link.event.LinkListener;
import at.ac.tuwien.auto.calimero.link.event.NetworkLinkListener;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * Threaded event notifier for network link and monitor.
 * <p>
 * 
 * @author B. Malinowsky
 */
abstract class EventNotifier extends Thread implements KNXListener
{
	// TODO extract event listener (throughout Calimero) and put it into its own class
	
	static interface EventCallback
	{
		/**
		 * Invokes the appropriate listener method with the event contained in this event
		 * callback.
		 * <p>
		 * 
		 * @param l the listener to notify
		 */
		void invoke(LinkListener l);
	}

	static final class Indication implements EventCallback
	{
		private final FrameEvent event;

		Indication(FrameEvent e)
		{
			event = e;
		}

		public void invoke(LinkListener l)
		{
			l.indication(event);
		}
	}

	static final class Confirmation implements EventCallback
	{
		private final FrameEvent event;

		Confirmation(FrameEvent e)
		{
			event = e;
		}

		public void invoke(LinkListener l)
		{
			((NetworkLinkListener) l).confirmation(event);
		}
	}

	static final class Closed implements EventCallback
	{
		private final CloseEvent event;

		Closed(CloseEvent e)
		{
			event = e;
		}

		public void invoke(LinkListener l)
		{
			l.linkClosed(event);
		}
	}

	final LogService logger;
	final Object source;

	// event listeners
	private final List listeners = new ArrayList();
	private List listenersCopy = new ArrayList();

	private final List events = new LinkedList();
	private volatile boolean stop;

	EventNotifier(Object source, LogService logger)
	{
		super("Link notifier");
		this.logger = logger;
		this.source = source;
		setDaemon(true);
		start();
	}

	public final void run()
	{
		while (!stop) {
			try {
				EventCallback ec;
				synchronized (events) {
					while (events.isEmpty())
						events.wait();
					ec = (EventCallback) events.remove(0);
				}
				fire(ec);
			}
			catch (final InterruptedException ignore) {}
		}
		// empty event queue
		synchronized (events) {
			while (!events.isEmpty())
				fire((EventCallback) events.remove(0));
		}
	}

	public abstract void frameReceived(FrameEvent e);

	public void connectionClosed(CloseEvent e)
	{
		addEvent(new Closed(new CloseEvent(source, e.isUserRequest(), e.getReason())));
		quit();
	}

	final void addEvent(EventCallback ec)
	{
		if (!stop) {
			synchronized (events) {
				events.add(ec);
				events.notify();
			}
		}
	}

	final void addListener(LinkListener l)
	{
		if (stop || l == null)
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

	final void removeListener(LinkListener l)
	{
		synchronized (listeners) {
			if (listeners.remove(l))
				listenersCopy = new ArrayList(listeners);
		}
	}

	final void quit()
	{
		if (stop)
			return;
		stop = true;
		interrupt();
		if (currentThread() != this) {
			try {
				join();
			}
			catch (final InterruptedException e) {}
		}
	}

	private void fire(EventCallback ec)
	{
		for (final Iterator i = listenersCopy.iterator(); i.hasNext();) {
			final LinkListener l = (LinkListener) i.next();
			try {
				ec.invoke(l);
			}
			catch (final RuntimeException rte) {
				removeListener(l);
				logger.error("removed event listener", rte);
			}
		}
	}
}
