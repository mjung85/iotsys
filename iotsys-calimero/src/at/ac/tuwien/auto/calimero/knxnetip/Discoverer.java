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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.exception.KNXInvalidResponseException;
import at.ac.tuwien.auto.calimero.exception.KNXTimeoutException;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.DescriptionRequest;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.DescriptionResponse;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.KNXnetIPHeader;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.PacketHelper;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.SearchRequest;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.SearchResponse;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * Does KNXnet/IP discovery and retrieval of self description from other devices.
 * <p>
 * Discovery searches and description requests can be run in blocking mode or asynchronous
 * in the background.<br>
 * Supports networks with routers doing network address translation.<br>
 * Requests for self description are sent using the UDP transport protocol.<br>
 * Due to protocol limitations, only IPv4 addresses are supported when network address
 * translation is <b>not</b> used. With NAT enabled, IPv6 addresses can be used as well.
 * <p>
 * A note on (not) using network address translation (NAT):<br>
 * If discovery or description attempts fail indicating a timeout limit, it might be
 * possible that NAT is used on routers while traversing the network, so the solution
 * would be to enable the use of NAT.<br>
 * On the other hand, if NAT is used but not supported by the answering device, no
 * response is received and a timeout will occur nevertheless. That's life.
 * 
 * @author B. Malinowsky
 */
public class Discoverer
{
	/**
	 * Name of the log service used by a discoverer.
	 * <p>
	 */
	public static final String LOG_SERVICE = "Discoverer";

	/**
	 * Multicast IP address used for discovery, multicast group is {@value}.
	 */
	public static final String SEARCH_MULTICAST = "224.0.23.12";

	/**
	 * Port number used for discovery, port is {@value}.
	 */
	public static final int SEARCH_PORT = KNXnetIPConnection.IP_PORT;

	// 512 bytes is a common minimum, but 256 should be large enough for all purposes
	private static final int bufferSize = 256;
	private static final InetAddress multicast;

	private static final LogService logger =
		LogManager.getManager().getLogService(LOG_SERVICE);

	private final InetAddress host;
	private final int port;
	// is our discovery/description aware of network address translation
	private final boolean isNatAware;
	private final List receiver = Collections.synchronizedList(new ArrayList());
	private final List responses = Collections.synchronizedList(new ArrayList());

	static {
		InetAddress a = null;
		try {
			a = InetAddress.getByName(SEARCH_MULTICAST);
		}
		catch (final UnknownHostException e) {}
		multicast = a;
	}

	/**
	 * Creates a new Discoverer.
	 * <p>
	 * Network address translation:<br>
	 * If subsequent discovery or description attempts fail indicating a timeout limit, it
	 * might be possible that network address translation (NAT) is used on routers while
	 * traversing the network (besides the other reason that timeouts are too short). This
	 * would effectively stop any communication done in the standard way, due to the way
	 * the HPAI structure is used by default.<br>
	 * Setting the parameter for indicating use of NAT to <code>true</code> takes
	 * account of such routers, leading to the desired behavior.
	 * 
	 * @param localPort the port number used to bind a socket, a valid port is in the
	 *        range of 1 to 65535, or use 0 to pick an arbitrary unused (ephemeral) port.
	 *        Note that a specified valid port does not ensure a successful bind in
	 *        subsequent discoverer operations due to operating system dependencies.
	 * @param useNAT <code>true</code> to use a NAT (network address translation) aware
	 *        discovery/description mechanism, <code>false</code> to use the default way
	 * @throws KNXException on error getting usable local host
	 */
	public Discoverer(int localPort, boolean useNAT) throws KNXException
	{
		if (localPort < 0 || localPort > 0xFFFF)
			throw new KNXIllegalArgumentException("port out of range [0..0xFFFF]");
		port = localPort;
		isNatAware = useNAT;
		try {
			host = InetAddress.getLocalHost();
			checkHost();
		}
		catch (final UnknownHostException e) {
			logger.error("can't get local host", e);
			throw new KNXException("can't get local host");
		}
	}

	/**
	 * Creates a new Discoverer and allows to specify a local host.
	 * <p>
	 * See {@link Discoverer#Discoverer(int, boolean)} for additional description.<br>
	 * The <code>localHost</code> is used to specify a particular local host address,
	 * used as response destination address when doing discovery / description. By
	 * default, the local host is used as obtained by {@link InetAddress#getLocalHost()}.
	 * The returned address is quite system dependent and might not always be useful in
	 * some situations. So it can be overruled specifying a local host address using this
	 * constructor.
	 * 
	 * @param localHost local host address used for discovery / description responses
	 * @param localPort the port number used to bind a socket, a valid port is in the
	 *        range of 1 to 65535, or use 0 to pick an arbitrary unused (ephemeral) port.
	 *        Note that a specified valid port does not ensure a successful bind in
	 *        subsequent discoverer operations due to operating system dependencies.
	 * @param useNAT <code>true</code> to use a NAT (network address translation) aware
	 *        discovery/description mechanism, <code>false</code> to use the default way
	 * @throws KNXException if local host can't be used
	 */
	public Discoverer(InetAddress localHost, int localPort, boolean useNAT)
		throws KNXException
	{
		if (localPort < 0 || localPort > 0xFFFF)
			throw new KNXIllegalArgumentException("port out of range [0..0xFFFF]");
		host = localHost;
		checkHost();
		port = localPort;
		isNatAware = useNAT;
	}

	/**
	 * Starts a new discovery, the <code>localPort</code> and network interface can be
	 * specified.
	 * <p>
	 * The search will continue for <code>timeout</code> seconds, or infinite if timeout
	 * value is zero. During this time, search responses will get collected asynchronous
	 * in the background by this {@link Discoverer}.<br>
	 * With <code>wait</code> you can force this method into blocking mode to wait until
	 * the search finished, otherwise the method returns with the search running in the
	 * background.<br>
	 * A search is finished if either the <code>timeout</code> was reached or the
	 * background receiver stopped.<br>
	 * The reason the <code>localPort</code> parameter is specified here, in addition to
	 * the port queried at {@link #Discoverer(int, boolean)}, is to distinguish between
	 * search responses if more searches are running concurrently.<br>
	 * 
	 * @param localPort the port used to bind the socket, a valid port is 0 to 65535, if
	 *        localPort is zero an arbitrary unused (ephemeral) port is picked
	 * @param ni the {@link NetworkInterface} used for sending outgoing multicast
	 *        messages, or <code>null</code> to use the default multicast interface
	 * @param timeout time window in seconds during which search response messages will
	 *        get collected, timeout >= 0. If timeout is zero, no timeout is set, the
	 *        search has to be stopped with {@link #stopSearch()}.
	 * @param wait <code>true</code> to block until end of search before return
	 * @throws KNXException on network I/O error
	 * @see MulticastSocket
	 * @see NetworkInterface
	 */
	public void startSearch(int localPort, NetworkInterface ni, int timeout, boolean wait)
		throws KNXException
	{
		if (timeout < 0)
			throw new KNXIllegalArgumentException("timeout has to be >= 0");
		if (localPort < 0 || localPort > 65535)
			throw new KNXIllegalArgumentException("port out of range [0..0xFFFF]");
		final Receiver r = search(new InetSocketAddress(host, localPort), ni, timeout);
		if (wait)
			join(r);
	}

	/**
	 * Starts a new discovery from all found network interfaces.
	 * <p>
	 * The search will continue for <code>timeout</code> seconds, or infinite if timeout
	 * value is zero. During this time, search responses will get collected asynchronous
	 * in the background by this Discoverer.<br>
	 * With <code>wait</code> you can force this method into blocking mode to wait until
	 * the search finished, otherwise the method returns with the search running in the
	 * background.<br>
	 * A search has finished if either the <code>timeout</code> was reached, all
	 * background receiver stopped (all responses received) or {@link #stopSearch()} was
	 * invoked.
	 * 
	 * @param timeout time window in seconds during which search response messages will
	 *        get collected, timeout >= 0. If timeout is 0, no timeout is set, the search
	 *        has to be stopped with <code>stopSearch</code>.
	 * @param wait <code>true</code> to block until end of search before return
	 * @throws KNXException on network I/O error
	 */
	public void startSearch(int timeout, boolean wait) throws KNXException
	{
		if (timeout < 0)
			throw new KNXIllegalArgumentException("timeout has to be >= 0");
		final Enumeration eni;
		try {
			eni = NetworkInterface.getNetworkInterfaces();
		}
		catch (final SocketException e) {
			logger.error("failed to get network interfaces", e);
			throw new KNXException("network interface error: " + e.getMessage());
		}
		if (eni == null) {
			logger.error("no network interfaces found");
			throw new KNXException("no network interfaces found");
		}
		
		final List rcv = new ArrayList();
		boolean lo = false;
		while (eni.hasMoreElements()) {
			final NetworkInterface ni = (NetworkInterface) eni.nextElement();
			for (final Enumeration ea = ni.getInetAddresses(); ea.hasMoreElements();) {
				final InetAddress a = (InetAddress) ea.nextElement();
				if (!isNatAware && a.getAddress().length != 4)
					logger.info("skipped " + a + ", not an IPv4 address");
				else
					try {
						if (!(lo && a.isLoopbackAddress()))
							rcv.add(search(new InetSocketAddress(a, port), ni, timeout));
						if (a.isLoopbackAddress())
							lo = true;
						else
							break;
					}
					catch (final KNXException e) {}
			}
		}
		if (rcv.size() == 0)
			throw new KNXException("search couldn't be started on any network interface");
		if (wait)
			for (final Iterator i = rcv.iterator(); i.hasNext();)
				join((Thread) i.next());
	}

	/**
	 * Stops every search currently running within this Discoverer.
	 * <p>
	 * Already gathered search responses from a search will not be removed.
	 */
	public final void stopSearch()
	{
		synchronized (receiver) {
			for (final Iterator i = receiver.iterator(); i.hasNext();)
				((Receiver) i.next()).quit();
			receiver.clear();
		}
	}

	/**
	 * Returns <code>true</code> if a search is currently running.
	 * <p>
	 * 
	 * @return a <code>boolean</code> showing the search state
	 */
	public final boolean isSearching()
	{
		return receiver.size() != 0;
	}

	/**
	 * Returns all collected search responses received by searches so far.
	 * <p>
	 * As long as searches are running, new responses might be added to the list of
	 * responses.
	 * 
	 * @return array of {@link SearchResponse}s
	 * @see #stopSearch()
	 */
	public final SearchResponse[] getSearchResponses()
	{
		return (SearchResponse[]) responses.toArray(new SearchResponse[responses.size()]);
	}

	/**
	 * Removes all search responses collected so far.
	 * <p>
	 */
	public final void clearSearchResponses()
	{
		responses.clear();
	}

	/**
	 * Sends a description request to <code>server</code> and waits at most
	 * <code>timeout</code> seconds for the answer message to arrive.
	 * <p>
	 * 
	 * @param server the InetSocketAddress of the server the description is requested from
	 * @param timeout time window in seconds to wait for answer message, 0 &lt; timeout
	 *        &lt; ({@link Integer#MAX_VALUE} / 1000)
	 * @return the description response message
	 * @throws KNXException on network I/O error
	 * @throws KNXTimeoutException if the timeout was reached before the description
	 *         response arrived
	 * @throws KNXInvalidResponseException if a received message from <code>server</code>
	 *         does not match the expected response
	 */
	public DescriptionResponse getDescription(InetSocketAddress server, int timeout)
		throws KNXException
	{
		if (timeout <= 0 || timeout >= Integer.MAX_VALUE / 1000)
			throw new KNXIllegalArgumentException("timeout out of range");
		DatagramSocket s = null;
		try {
			s = new DatagramSocket(port, host);
			final byte[] buf = PacketHelper.toPacket(new DescriptionRequest(
				isNatAware ? null : (InetSocketAddress) s.getLocalSocketAddress()));
			s.send(new DatagramPacket(buf, buf.length, server));
			final long end = System.currentTimeMillis() + timeout * 1000L;
			DatagramPacket p = null;
			while ((p = receive(s, end)) != null) {
				if (p.getSocketAddress().equals(server)) {
					final KNXnetIPHeader h =
						new KNXnetIPHeader(p.getData(), p.getOffset());
					if (h.getServiceType() == KNXnetIPHeader.DESCRIPTION_RES)
						return new DescriptionResponse(p.getData(), p.getOffset()
							+ h.getStructLength());
				}
			}
		}
		catch (final IOException e) {
			final String msg = "network failure on getting description";
			logger.error(msg, e);
			throw new KNXException(msg);
		}
		catch (final KNXFormatException e) {
			logger.error("invalid description response", e);
			throw new KNXInvalidResponseException(e.getMessage());
		}
		finally {
			if (s != null)
				s.close();
		}
		final String msg = "timeout, no description response received";
		logger.warn(msg);
		throw new KNXTimeoutException(msg);
	}

	/**
	 * Starts a search sending a search request message.
	 * <p>
	 * 
	 * @param a local SocketAddress to send search request from
	 * @param ni {@link NetworkInterface} used to send outgoing multicast, or
	 *        <code>null</code> to use the default multicast interface
	 * @param timeout timeout in seconds, timeout >= 0, 0 for an infinite time window
	 * @return the receiver thread for the search started
	 * @throws KNXException
	 */
	private Receiver search(InetSocketAddress a, NetworkInterface ni, int timeout)
		throws KNXException
	{
		logger.info("search on " + a);
		MulticastSocket s = null;
		try {
			s = new MulticastSocket(a);
			//if (ni != null)
			//	s.setNetworkInterface(ni);
			final byte[] buf = PacketHelper.toPacket(new SearchRequest(isNatAware ? null
				: (InetSocketAddress) s.getLocalSocketAddress()));
			s.send(new DatagramPacket(buf, buf.length, multicast, SEARCH_PORT));
			final Receiver r = new Receiver(s, timeout);
			receiver.add(r);
			return r;
		}
		catch (final IOException e) {
			if (s != null)
				s.close();
			logger.warn("I/O failure sending search request on " + a, e);
			throw new KNXException("search request failed, " + e.getMessage());
		}
	}

	// timeEnd = 0 for infinite timeout
	private DatagramPacket receive(DatagramSocket s, long timeEnd) throws IOException
	{
		final long timeout = timeEnd == 0 ? 0 : timeEnd - System.currentTimeMillis();
		if (timeout > 0 || timeEnd == 0) {
			final byte[] buf = new byte[bufferSize];
			final DatagramPacket p = new DatagramPacket(buf, bufferSize);
			try {
				s.setSoTimeout((int) timeout);
				s.receive(p);
				return p;
			}
			catch (final InterruptedIOException ignore) {}
		}
		return null;
	}

	private void join(Thread t)
	{
		while (t.isAlive())
			try {
				t.join();
			}
			catch (final InterruptedException ignore) {}
	}

	private void checkHost() throws KNXException
	{
		if (isNatAware || host.getAddress().length == 4)
			return;
		final KNXException e =
			new KNXException(host.getHostAddress() + " is not an IPv4 address");
		logger.error("NAT not used, only IPv4 address support", e);
		throw e;
	}

	private final class Receiver extends Thread
	{
		private volatile boolean quit;
		private final MulticastSocket s;
		private final int timeout;

		/**
		 * Creates a new Receiver.
		 * <p>
		 * 
		 * @param socket socket to receive from
		 * @param timeout live time of this Receiver, timeout >= 0, 0 is infinite timeout
		 */
		Receiver(MulticastSocket socket, int timeout)
		{
			super("Discoverer receiver " + socket.getLocalAddress().getHostAddress());
			s = socket;
			this.timeout = timeout;
			setDaemon(true);
			start();
		}

		void quit()
		{
			quit = true;
			s.close();
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run()
		{
			final long end = System.currentTimeMillis() + timeout * 1000;
			DatagramPacket p;
			try {
				while (!quit && (p = receive(s, timeout == 0 ? 0 : end)) != null)
					checkForResponse(p);
			}
			catch (final IOException e) {
				if (!quit)
					logger.error("while waiting for response", e);
			}
			finally {
				s.close();
				receiver.remove(this);
			}
		}

		private void checkForResponse(final DatagramPacket p)
		{
			try {
				final KNXnetIPHeader h = new KNXnetIPHeader(p.getData(), p.getOffset());
				if (h.getServiceType() == KNXnetIPHeader.SEARCH_RES)
					// sync with receiver queue: check if our search was stopped
					synchronized (receiver) {
						if (receiver.contains(this))
							responses.add(new SearchResponse(p.getData(), p.getOffset()
								+ h.getStructLength()));
					}
			}
			catch (final KNXFormatException ignore) {}
		}
	}
}
