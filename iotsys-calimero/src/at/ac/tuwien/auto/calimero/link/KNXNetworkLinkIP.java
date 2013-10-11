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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.IndividualAddress;
import at.ac.tuwien.auto.calimero.KNXAddress;
import at.ac.tuwien.auto.calimero.Priority;
import at.ac.tuwien.auto.calimero.cemi.CEMIFactory;
import at.ac.tuwien.auto.calimero.cemi.CEMILData;
import at.ac.tuwien.auto.calimero.cemi.CEMILDataEx;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.exception.KNXTimeoutException;
import at.ac.tuwien.auto.calimero.knxnetip.KNXConnectionClosedException;
import at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPRouter;
import at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel;
import at.ac.tuwien.auto.calimero.link.event.NetworkLinkListener;
import at.ac.tuwien.auto.calimero.link.medium.KNXMediumSettings;
import at.ac.tuwien.auto.calimero.link.medium.PLSettings;
import at.ac.tuwien.auto.calimero.link.medium.RFSettings;
import at.ac.tuwien.auto.calimero.link.medium.TPSettings;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * Implementation of the KNX network link based on the KNXnet/IP protocol, using a
 * {@link KNXnetIPConnection}.
 * <p>
 * Once a link has been closed, it is not available for further link communication, i.e.
 * it can't be reopened.
 * <p>
 * If KNXnet/IP routing is used as base protocol, the send methods with wait for
 * confirmation behave equally like without wait specified, since routing is an
 * unconfirmed protocol. This implies that no confirmation frames are generated, thus
 * {@link NetworkLinkListener#confirmation(FrameEvent)} is not used.
 * <p>
 * IP address considerations:<br>
 * On more IP addresses assigned to the local host (on possibly several local network
 * interfaces), the default chosen local host address can differ from the expected. In
 * this situation, the local endpoint has to be specified manually during instantiation.
 * <br>
 * Network Address Translation (NAT) aware communication can only be used, if the
 * KNXnet/IP server of the remote endpoint supports it. Otherwise, connection timeouts
 * will occur. With NAT enabled, KNXnet/IP accepts IPv6 addresses. By default, the
 * KNXnet/IP protocol only works with IPv4 addresses.<br>
 * 
 * @author B. Malinowsky
 */
public class KNXNetworkLinkIP implements KNXNetworkLink
{
	/**
	 * Service mode for link layer tunneling.
	 * <p>
	 */
	public static final int TUNNEL = 1;

	/**
	 * Service mode for routing.
	 * <p>
	 */
	public static final int ROUTER = 2;

	private static final class LinkNotifier extends EventNotifier
	{
		LinkNotifier(Object source, LogService logger)
		{
			super(source, logger);
		}

		public void frameReceived(FrameEvent e)
		{
			final int mc = e.getFrame().getMessageCode();
			if (mc == CEMILData.MC_LDATA_IND) {
				addEvent(new Indication(new FrameEvent(source, e.getFrame())));
				logger.info("indication from " + ((CEMILData) e.getFrame()).getSource());
			}
			else if (mc == CEMILData.MC_LDATA_CON) {
				addEvent(new Confirmation(new FrameEvent(source, e.getFrame())));
				logger.info("confirmation of "
					+ ((CEMILData) e.getFrame()).getDestination());
			}
			else
				logger.warn("unspecified frame event - ignored, msg code = 0x"
					+ Integer.toHexString(mc));
		}

		public void connectionClosed(CloseEvent e)
		{
			((KNXNetworkLinkIP) source).closed = true;
			super.connectionClosed(e);
			logger.info("link closed");
			LogManager.getManager().removeLogService(logger.getName());
		}
	};

	private final int mode;
	private volatile boolean closed;
	private final KNXnetIPConnection conn;
	private volatile byte hopCount = 6;
	private KNXMediumSettings medium;

	private final LogService logger;
	// our link connection event notifier
	private final EventNotifier notifier;

	/**
	 * Creates a new network link based on the KNXnet/IP protocol, using a
	 * {@link KNXnetIPConnection}.
	 * <p>
	 * For more details on KNXnet/IP connections, refer to the various KNXnet/IP
	 * implementations.<br>
	 * 
	 * @param serviceMode mode of communication to open, <code>serviceMode</code> is one
	 *        of the service mode constants (e.g. {@link #TUNNEL}); depending on the mode
	 *        set, the expected local / remote endpoints might differ
	 * @param localEP the local endpoint of the link to use;<br> - in tunneling mode
	 *        (point-to-point), this is the client control endpoint, use <code>null</code>
	 *        for the default local host and an ephemeral port number<br> - in
	 *        {@link #ROUTER} mode, specifies the multicast interface, i.e. the local
	 *        network interface is taken that has the IP address bound to it (if IP
	 *        address is bound more than once, it's undefined which interface is
	 *        returned), the port is not used; use <code>null</code> for
	 *        <code>localEP</code> or an unresolved IP address to take the host's
	 *        default multicast interface
	 * @param remoteEP the remote endpoint of the link to communicate with;<br> - in
	 *        tunneling mode (point-to-point), this is the server control endpoint <br> -
	 *        in {@link #ROUTER} mode, the IP address specifies the multicast group to
	 *        join, the port is not used; use <code>null</code> for
	 *        <code>remoteEP</code> or an unresolved IP address to take the default
	 *        multicast group
	 * @param useNAT <code>true</code> to use network address translation in tunneling
	 *        service mode, <code>false</code> to use the default (non aware) mode;
	 *        parameter is ignored for routing
	 * @param settings medium settings defining device and medium specifics needed for
	 *        communication
	 * @throws KNXException on failure establishing link using the KNXnet/IP connection
	 */
	public KNXNetworkLinkIP(int serviceMode, InetSocketAddress localEP,
		InetSocketAddress remoteEP, boolean useNAT, KNXMediumSettings settings)
		throws KNXException
	{
		switch (serviceMode) {
		case TUNNEL:
			InetSocketAddress local = localEP;
			if (local == null)
				try {
					local = new InetSocketAddress(InetAddress.getLocalHost(), 0);
				}
				catch (final UnknownHostException e) {
					throw new KNXException("no local host available");
				}
			conn = new KNXnetIPTunnel(KNXnetIPTunnel.LINK_LAYER, local, remoteEP, useNAT);
			break;
		case ROUTER:
			NetworkInterface netIf = null;
			if (localEP != null && !localEP.isUnresolved())
				try {
					netIf = NetworkInterface.getByInetAddress(localEP.getAddress());
				}
				catch (final SocketException e) {
					throw new KNXException("error getting network interface: "
						+ e.getMessage());
				}
			final InetAddress mcast = remoteEP != null ? remoteEP.getAddress() : null;
			conn = new KNXnetIPRouter(netIf, mcast);
			break;
		default:
			throw new KNXIllegalArgumentException("unknown service mode");
		}
		// initialize our link with opened connection
		mode = serviceMode;
		logger = LogManager.getManager().getLogService(getName());
		notifier = new LinkNotifier(this, logger);
		conn.addConnectionListener(notifier);
		// configure KNX medium stuff
		setKNXMedium(settings);
	}

	/**
	 * Creates a new network link based on the KNXnet/IP tunneling protocol, using a
	 * {@link KNXnetIPTunnel} with default communication settings.
	 * <p>
	 * The link is established using a KNXnet/IP tunnel, the local endpoint is the default
	 * local host, the remote endpoint uses the default KNXnet/IP port number, network
	 * address translation (NAT) is disabled.
	 * 
	 * @param remoteHost remote host name
	 * @param settings medium settings defining device and medium specifics needed for
	 *        communication
	 * @throws KNXException on failure establishing link using the KNXnet/IP connection
	 */
	public KNXNetworkLinkIP(String remoteHost, KNXMediumSettings settings)
		throws KNXException
	{
		this(TUNNEL, null, new InetSocketAddress(remoteHost, KNXnetIPConnection.IP_PORT),
			false, settings);
	}

	/**
	 * Creates a new network link based on the KNXnet/IP routing protocol, using a
	 * {@link KNXnetIPRouter}.
	 * <p>
	 * 
	 * @param netIf local network interface used to join the multicast group and for
	 *        sending, use <code>null</code> for the host's default multicast interface
	 * @param mcGroup address of the multicast group to join, use <code>null</code> for
	 *        the default KNXnet/IP multicast address
	 * @param settings medium settings defining device and medium specifics needed for
	 *        communication
	 * @throws KNXException on failure establishing link using the KNXnet/IP connection
	 */
	public KNXNetworkLinkIP(NetworkInterface netIf, InetAddress mcGroup,
		KNXMediumSettings settings) throws KNXException
	{
		conn = new KNXnetIPRouter(netIf, mcGroup);
		// initialize our link
		mode = ROUTER;
		logger = LogManager.getManager().getLogService(getName());
		notifier = new LinkNotifier(this, logger);
		conn.addConnectionListener(notifier);
		// configure KNX medium stuff
		setKNXMedium(settings);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#setKNXMedium
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
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#getKNXMedium()
	 */
	public KNXMediumSettings getKNXMedium()
	{
		return medium;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#addLinkListener
	 * (tuwien.auto.calimero.link.event.NetworkLinkListener)
	 */
	public void addLinkListener(NetworkLinkListener l)
	{
		notifier.addListener(l);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#removeLinkListener
	 * (tuwien.auto.calimero.link.event.NetworkLinkListener)
	 */
	public void removeLinkListener(NetworkLinkListener l)
	{
		notifier.removeListener(l);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#setHopCount(int)
	 */
	public final void setHopCount(int count)
	{
		if (count < 0 || count > 7)
			throw new KNXIllegalArgumentException("hop count out of range [0..7]");
		hopCount = (byte) count;
		logger.info("hop count set to " + count);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#getHopCount()
	 */
	public final byte getHopCount()
	{
		return hopCount;
	}

	/**
	 * {@inheritDoc} When communicating with a KNX network which uses open medium,
	 * messages are broadcasted within domain (as opposite to system broadcast) by
	 * default. Specify <code>dst null</code> for system broadcast.
	 */
	public void sendRequest(KNXAddress dst, Priority p, byte[] nsdu)
		throws KNXLinkClosedException, KNXTimeoutException
	{
		send(dst, p, nsdu, false);
	}

	/**
	 * {@inheritDoc} When communicating with a KNX network which uses open medium,
	 * messages are broadcasted within domain (as opposite to system broadcast) by
	 * default. Specify <code>dst null</code> for system broadcast.
	 */
	public void sendRequestWait(KNXAddress dst, Priority p, byte[] nsdu)
		throws KNXTimeoutException, KNXLinkClosedException
	{
		send(dst, p, nsdu, true);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#send
	 * (tuwien.auto.calimero.cemi.CEMILData, boolean)
	 */
	public void send(CEMILData msg, boolean waitForCon) throws KNXTimeoutException,
		KNXLinkClosedException
	{
		doSend(adjustMsgType(msg), waitForCon);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#getName()
	 */
	public String getName()
	{
		// do our own IP:port string, since InetAddress.toString() always prepends a '/'
		final InetSocketAddress a = conn.getRemoteAddress();
		return "link " + a.getAddress().getHostAddress() + ":" + a.getPort();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#isOpen()
	 */
	public boolean isOpen()
	{
		return !closed;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.KNXNetworkLink#close()
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
		return getName() + (mode == TUNNEL ? " tunnel" : " routing") + " mode"
			+ (closed ? " (closed), " : ", ") + medium.getMediumString()
			+ " medium hopcount " + hopCount;
	}

	private CEMILData adjustMsgType(CEMILData msg)
	{
		final boolean srcOk = msg.getSource().getRawAddress() != 0;
		// just return if we don't need to adjust source address and don't need LDataEx
		if ((srcOk || medium.getDeviceAddress().getRawAddress() == 0)
			&& (medium instanceof TPSettings || msg instanceof CEMILDataEx))
			return msg;
		return (CEMILDataEx) CEMIFactory.create(srcOk ? null : medium.getDeviceAddress(),
			null, msg, true);
	}

	private void send(KNXAddress dst, Priority p, byte[] nsdu, boolean confirm)
		throws KNXTimeoutException, KNXLinkClosedException
	{
		final CEMILData f;
		final short mc = mode == TUNNEL ? CEMILData.MC_LDATA_REQ : CEMILData.MC_LDATA_IND;
		final IndividualAddress src = medium.getDeviceAddress();
		// use default address 0 in system broadcast
		final KNXAddress d = dst == null ? new GroupAddress(0) : dst;
		final boolean tp = medium.getMedium() == KNXMediumSettings.MEDIUM_TP0
			|| medium.getMedium() == KNXMediumSettings.MEDIUM_TP1;
		if (nsdu.length <= 16 && tp)
			f = new CEMILData(mc, src, d, nsdu, p, true, hopCount);
		else
			f = new CEMILDataEx(mc, src, d, nsdu, p, true, dst != null, false, hopCount);
		doSend(f, confirm);
	}

	private void doSend(CEMILData msg, boolean waitForCon) throws KNXTimeoutException,
		KNXLinkClosedException
	{
		if (closed)
			throw new KNXLinkClosedException("link closed");
		if (medium instanceof PLSettings) {
			final CEMILDataEx f = (CEMILDataEx) msg;
			if (f.getAdditionalInfo(CEMILDataEx.ADDINFO_PLMEDIUM) == null) {
				f.addAdditionalInfo(CEMILDataEx.ADDINFO_PLMEDIUM, ((PLSettings) medium)
					.getDomainAddress());
				logger.trace("send - added PL additional info to message");
			}
		}
		else if (medium.getMedium() == KNXMediumSettings.MEDIUM_RF) {
			final CEMILDataEx f = (CEMILDataEx) msg;
			final RFSettings rf = (RFSettings) medium;
			if (f.getAdditionalInfo(CEMILDataEx.ADDINFO_RFMEDIUM) == null) {
				final byte[] sn =
					f.isDomainBroadcast() ? rf.getDomainAddress() : rf.getSerialNumber();
				// add-info: rf-info (0 ignores a lot), sn (6 bytes), lfn (=255:void)
				f.addAdditionalInfo(CEMILDataEx.ADDINFO_RFMEDIUM, new byte[] { 0, sn[0],
					sn[1], sn[2], sn[3], sn[4], sn[5], (byte) 0xff });
				logger.trace("send - added RF additional info to message "
					+ (f.isDomainBroadcast() ? "(domain address)" : "(s/n)"));
			}
		}
		try {
			logger.info("send message to " + msg.getDestination()
				+ (waitForCon ? ", wait for confirmation" : ""));
			logger.trace("cEMI " + msg);
			conn.send(msg, waitForCon ? KNXnetIPConnection.WAIT_FOR_CON
				: KNXnetIPConnection.WAIT_FOR_ACK);
			logger.trace("send to " + msg.getDestination() + " succeeded");
		}
		catch (final KNXConnectionClosedException e) {
			logger.error("send error, closing link", e);
			close();
			throw new KNXLinkClosedException("link closed, " + e.getMessage());
		}
	}
}
