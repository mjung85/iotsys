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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.cemi.CEMIBusMon;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel;
import at.ac.tuwien.auto.calimero.link.event.LinkListener;
import at.ac.tuwien.auto.calimero.link.event.MonitorFrameEvent;
import at.ac.tuwien.auto.calimero.link.medium.KNXMediumSettings;
import at.ac.tuwien.auto.calimero.link.medium.RawFrame;
import at.ac.tuwien.auto.calimero.link.medium.RawFrameFactory;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * Implementation of the KNX network monitor link based on the KNXnet/IP protocol, using a
 * {@link KNXnetIPConnection}.
 * <p>
 * Once a monitor has been closed, it is not available for further link communication,
 * i.e. it can't be reopened.
 * <p>
 * Pay attention to the IP address consideration stated in the documentation comments of
 * class {@link KNXNetworkLinkIP}.
 * 
 * @author B. Malinowsky
 */
public class KNXNetworkMonitorIP implements KNXNetworkMonitor
{
	private static final class MonitorNotifier extends EventNotifier
	{
		volatile boolean decode;

		MonitorNotifier(Object source, LogService logger)
		{
			super(source, logger);
		}

		public void frameReceived(FrameEvent e)
		{
			final int mc = e.getFrame().getMessageCode();
			if (mc == CEMIBusMon.MC_BUSMON_IND) {
				RawFrame raw = null;
				logger.info("received monitor indication");
				if (decode)
					try {
						final short m = ((KNXNetworkMonitorIP) source).medium.getMedium();
						raw = RawFrameFactory.create(m, e.getFrame().getPayload(), 0);
					}
					catch (final KNXFormatException ex) {
						logger.error("decoding raw frame", ex);
					}
				addEvent(new Indication(new MonitorFrameEvent(source, e.getFrame(), raw)));
			}
			else
				logger.warn("unspecified frame event - ignored, msg code = 0x"
					+ Integer.toHexString(mc));
		}

		public void connectionClosed(CloseEvent e)
		{
			((KNXNetworkMonitorIP) source).closed = true;
			super.connectionClosed(e);
			logger.info("monitor closed");
			LogManager.getManager().removeLogService(logger.getName());
		}
	};

	private volatile boolean closed;
	private final KNXnetIPConnection conn;
	private KNXMediumSettings medium;

	private final LogService logger;
	// our link connection event notifier
	private final MonitorNotifier notifier;

	/**
	 * Creates a new network monitor based on the KNXnet/IP protocol for accessing the KNX
	 * network.
	 * <p>
	 * 
	 * @param localEP the local endpoint to use for the link, this is the client control
	 *        endpoint, use <code>null</code> for the default local host and an
	 *        ephemeral port number
	 * @param remoteEP the remote endpoint of the link; this is the server control
	 *        endpoint
	 * @param useNAT <code>true</code> to use network address translation in the
	 *        KNXnet/IP protocol, <code>false</code> to use the default (non aware) mode
	 * @param settings medium settings defining the specific KNX medium needed for
	 *        decoding raw frames received from the KNX network
	 * @throws KNXException on failure establishing the link
	 */
	public KNXNetworkMonitorIP(InetSocketAddress localEP, InetSocketAddress remoteEP,
		boolean useNAT, KNXMediumSettings settings) throws KNXException
	{
		InetSocketAddress ep = localEP;
		if (ep == null)
			try {
				ep = new InetSocketAddress(InetAddress.getLocalHost(), 0);
			}
			catch (final UnknownHostException e) {
				throw new KNXException("no local host available");
			}
		conn = new KNXnetIPTunnel(KNXnetIPTunnel.BUSMONITOR_LAYER, ep, remoteEP, useNAT);
		logger = LogManager.getManager().getLogService(getName());
		logger.info("in busmonitor mode - ready to receive");
		notifier = new MonitorNotifier(this, logger);
		conn.addConnectionListener(notifier);
		// configure KNX medium stuff
		setKNXMedium(settings);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkMonitor#setKNXMedium
	 * (tuwien.auto.calimero.link.medium.KNXMediumSettings)
	 */
	public void setKNXMedium(KNXMediumSettings settings)
	{
		if (settings == null)
			throw new KNXIllegalArgumentException("medium settings are mandatory");
		if (medium != null && !settings.getClass().isAssignableFrom(medium.getClass())
			&& !medium.getClass().isAssignableFrom(settings.getClass()))
			throw new KNXIllegalArgumentException("medium differs");
		medium = settings;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkMonitor#getKNXMedium()
	 */
	public KNXMediumSettings getKNXMedium()
	{
		return medium;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkMonitor#addMonitorListener
	 * (tuwien.auto.calimero.link.event.LinkListener)
	 */
	public void addMonitorListener(LinkListener l)
	{
		notifier.addListener(l);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkMonitor#removeMonitorListener
	 * (tuwien.auto.calimero.link.event.LinkListener)
	 */
	public void removeMonitorListener(LinkListener l)
	{
		notifier.removeListener(l);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkMonitor#setDecodeRawFrames(boolean)
	 */
	public void setDecodeRawFrames(boolean decode)
	{
		notifier.decode = decode;
		logger.info((decode ? "enable" : "disable") + " decoding of raw frames");
	}

	/**
	 * {@inheritDoc}<br>
	 * The returned name is "monitor " + remote IP address of the control endpoint + ":" +
	 * remote port used by the monitor.
	 */
	public String getName()
	{
		// do our own IP:port string, since InetAddress.toString() always prepends a '/'
		final InetSocketAddress a = conn.getRemoteAddress();
		return "monitor " + a.getAddress().getHostAddress() + ":" + a.getPort();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkMonitor#isOpen()
	 */
	public boolean isOpen()
	{
		return !closed;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkMonitor#close()
	 */
	public void close()
	{
		synchronized (this) {
			if (closed)
				return;
			closed = true;
		}
		conn.close();
		notifier.quit();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getName() + (closed ? " (closed), " : ", ") + medium.getMediumString()
			+ " medium" + (notifier.decode ? ", decode raw frames" : "");
	}
}
