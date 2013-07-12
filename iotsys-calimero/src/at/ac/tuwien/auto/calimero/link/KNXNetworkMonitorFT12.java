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

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.cemi.CEMIBusMon;
import at.ac.tuwien.auto.calimero.cemi.CEMIFactory;
import at.ac.tuwien.auto.calimero.exception.KNXAckTimeoutException;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.link.event.LinkListener;
import at.ac.tuwien.auto.calimero.link.event.MonitorFrameEvent;
import at.ac.tuwien.auto.calimero.link.medium.KNXMediumSettings;
import at.ac.tuwien.auto.calimero.link.medium.RawFrame;
import at.ac.tuwien.auto.calimero.link.medium.RawFrameFactory;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;
import at.ac.tuwien.auto.calimero.serial.FT12Connection;
import at.ac.tuwien.auto.calimero.serial.KNXPortClosedException;

/**
 * Implementation of the KNX network monitor link based on the FT1.2 protocol, using a
 * {@link FT12Connection}.
 * <p>
 * Once a monitor has been closed, it is not available for further link communication,
 * i.e. it can't be reopened.
 * 
 * @author B. Malinowsky
 */
public class KNXNetworkMonitorFT12 implements KNXNetworkMonitor
{
	private static final class MonitorNotifier extends EventNotifier
	{
		volatile boolean decode;

		MonitorNotifier(Object source, LogService logger)
		{
			super(source, logger);
		}

		/* (non-Javadoc)
		 * @see tuwien.auto.calimero.link.EventNotifier#frameReceived
		 * (tuwien.auto.calimero.FrameEvent)
		 */
		public void frameReceived(FrameEvent e)
		{
			try {
				final CEMIBusMon mon =
					(CEMIBusMon) CEMIFactory.createFromEMI(e.getFrameBytes());
				logger.info("received monitor indication");
				RawFrame raw = null;
				if (decode)
					try {
						raw = RawFrameFactory.create(((KNXNetworkMonitorFT12) source)
							.medium.getMedium(), mon.getPayload(), 0);
					}
					catch (final KNXFormatException ex) {
						logger.error("decoding raw frame", ex);
					}
				addEvent(new Indication(new MonitorFrameEvent(source, mon, raw)));
			}
			catch (final KNXFormatException ex) {
				logger.warn("unspecified frame event - ignored", ex);
			}
		}

		public void connectionClosed(CloseEvent e)
		{
			((KNXNetworkMonitorFT12) source).closed = true;
			super.connectionClosed(e);
			logger.info("monitor closed");
			LogManager.getManager().removeLogService(logger.getName());
		}
	}

	private static final int PEI_SWITCH = 0xA9;

	private volatile boolean closed;
	private final FT12Connection conn;
	private KNXMediumSettings medium;

	private final LogService logger;
	// our link connection event notifier
	private final MonitorNotifier notifier;

	/**
	 * Creates a new network monitor based on the FT1.2 protocol for accessing the KNX
	 * network.
	 * <p>
	 * The port identifier is used to choose the serial port for communication. These
	 * identifiers are usually device and platform specific.
	 * 
	 * @param portID identifier of the serial communication port to use
	 * @param settings medium settings defining the specific KNX medium needed for
	 *        decoding raw frames received from the KNX network
	 * @throws KNXException
	 */
	public KNXNetworkMonitorFT12(String portID, KNXMediumSettings settings)
		throws KNXException
	{
		conn = new FT12Connection(portID);
		try {
			enterBusmonitor();
		}
		catch (final KNXAckTimeoutException e) {
			conn.close();
			throw e;
		}
		logger = LogManager.getManager().getLogService(getName());
		logger.info("in busmonitor mode - ready to receive");
		notifier = new MonitorNotifier(this, logger);
		conn.addConnectionListener(notifier);
		// configure KNX medium stuff
		setKNXMedium(settings);
	}

	/**
	 * Creates a new network monitor based on the FT1.2 protocol for accessing the KNX
	 * network.
	 * <p>
	 * The port number is used to choose the serial port for communication. It is mapped
	 * to the default port identifier using that number on the platform.
	 * 
	 * @param portNumber port number of the serial communication port to use
	 * @param settings medium settings defining the specific KNX medium needed for
	 *        decoding raw frames received from the KNX network
	 * @throws KNXException
	 */
	public KNXNetworkMonitorFT12(int portNumber, KNXMediumSettings settings)
		throws KNXException
	{
		conn = new FT12Connection(portNumber);
		enterBusmonitor();
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
	 * The returned name is "monitor " + port identifier.
	 */
	public String getName()
	{
		return "monitor " + conn.getPortID();
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
		try {
			leaveBusmonitor();
		}
		catch (final KNXException e) {
			logger.error("could not switch BCU back to normal mode", e);
		}
		conn.close();
		notifier.quit();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getName() + (closed ? "(closed), " : ", ") + medium.getMediumString()
			+ " medium" + (notifier.decode ? ", decode raw frames" : "");
	}

	private void enterBusmonitor() throws KNXAckTimeoutException, KNXPortClosedException
	{
		final byte[] switchBusmon =
			{ (byte) PEI_SWITCH, (byte) 0x90, 0x18, 0x34, 0x56, 0x78, 0x0A, };
		conn.send(switchBusmon, true);
	}

	private void leaveBusmonitor() throws KNXAckTimeoutException, KNXPortClosedException
	{
		normalMode();
	}

	private void normalMode() throws KNXAckTimeoutException, KNXPortClosedException
	{
		final byte[] switchNormal =
			{ (byte) PEI_SWITCH, 0x1E, 0x12, 0x34, 0x56, 0x78, (byte) 0x9A, };
		conn.send(switchNormal, true);
	}
}
