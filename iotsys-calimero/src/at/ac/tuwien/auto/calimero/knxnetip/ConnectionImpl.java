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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.KNXListener;
import at.ac.tuwien.auto.calimero.cemi.CEMI;
import at.ac.tuwien.auto.calimero.exception.KNXAckTimeoutException;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalStateException;
import at.ac.tuwien.auto.calimero.exception.KNXInvalidResponseException;
import at.ac.tuwien.auto.calimero.exception.KNXRemoteException;
import at.ac.tuwien.auto.calimero.exception.KNXTimeoutException;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.ConnectRequest;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.ConnectResponse;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.ConnectionstateRequest;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.ConnectionstateResponse;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.DisconnectRequest;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.DisconnectResponse;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.ErrorCodes;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.KNXnetIPHeader;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.PacketHelper;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.RoutingIndication;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.ServiceAck;
import at.ac.tuwien.auto.calimero.knxnetip.servicetype.ServiceRequest;
import at.ac.tuwien.auto.calimero.knxnetip.util.CRI;
import at.ac.tuwien.auto.calimero.knxnetip.util.HPAI;
import at.ac.tuwien.auto.calimero.log.LogLevel;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * Base implementation for tunneling, device management and routing.
 * <p>
 * The communication on OSI layer 4 is done with UDP.<br>
 * Implements a communication heartbeat monitor.
 * 
 * @author B. Malinowsky
 */
abstract class ConnectionImpl implements KNXnetIPConnection
{
	// IMPLEMENTATION NOTE: on MS Windows platforms, interruptible I/O is not supported,
	// i.e. a blocked I/O method remains blocked after interrupt of the thread.
	// To bypass this, we use the rude way to close I/O to force
	// these methods to interrupt and throw.

	/**
	 * Status code of communication: waiting for acknowledge after send, no error, not
	 * ready to send.
	 * <p>
	 */
	public static final int ACK_PENDING = 2;

	/**
	 * Status code of communication: in idle state, received an acknowledge error in
	 * response, ready to send.
	 * <p>
	 */
	public static final int ACK_ERROR = 3;

	/**
	 * Status code of communication: waiting for confirmation after acknowledge, no error,
	 * not ready to send.
	 * <p>
	 */
	public static final int CEMI_CON_PENDING = 4;

	/**
	 * Status code of communication: unknown error, no send possible.
	 * <p>
	 */
	public static final int UNKNOWN_ERROR = -1;

	// KNXnet/IP client SHALL wait 10 seconds for a connect response frame from server
	private static final int CONNECT_REQ_TIMEOUT = 10;

	// request to confirmation timeout
	private static final int CONFIRMATION_TIMEOUT = 3;

	// constants identifying the source which initiated a connection close
	private static final int CLIENT = ConnectionCloseEvent.CLIENT;
	private static final int SERVER = ConnectionCloseEvent.SERVER;
	private static final int INTERNAL = ConnectionCloseEvent.INTERNAL;

	// initialized in connect, when name of connection is available
	LogService logger;

	// service codes used for this connection type
	int serviceRequest;
	int serviceAck;
	// number of maximum sends (= retries + 1)
	int maxSendAttempts;
	// timeout for response message in seconds
	int responseTimeout;

	DatagramSocket socket;
	// lock object to do wait() on for protocol timeouts
	Object lock = new Object();

	InetSocketAddress ctrlEP;
	InetSocketAddress dataEP;

	// current state visible to the user
	private volatile int state = CLOSED;
	// internal state, so we can use states not visible to the user
	private volatile int internalState = CLOSED;
	// should an internal state change update the state member
	private volatile boolean updateState = true;

	private volatile int closing;

	private int seqNoRcv;
	private int seqNoSend;
	private short channelID;
	private boolean isNatAware;

	private Receiver receiver;
	private HeartbeatMonitor heartbeat;

	// event listener lists
	private final List listeners = new ArrayList();
	private List listenersCopy = new ArrayList();
	private final Semaphore sendWaitQueue = new Semaphore();

	ConnectionImpl()
	{}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.KNXnetIPConnection#addConnectionListener
	 * (tuwien.auto.calimero.KNXListener)
	 */
	public void addConnectionListener(KNXListener l)
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

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.KNXnetIPConnection#removeConnectionListener
	 * (tuwien.auto.calimero.KNXListener)
	 */
	public void removeConnectionListener(KNXListener l)
	{
		synchronized (listeners) {
			if (listeners.remove(l))
				listenersCopy = new ArrayList(listeners);
		}
	}

	/**
	 * <p>
	 * If <code>mode</code> is {@link KNXnetIPConnection#WAIT_FOR_CON} or
	 * {@link KNXnetIPConnection#WAIT_FOR_ACK}, the sequence order of more
	 * {@link #send(CEMI, at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPConnection.BlockingMode)}
	 * calls from different threads is being maintained according to invocation order
	 * (FIFO).<br>
	 * A call of this method blocks until (possible) previous invocations are done, then
	 * does communication according to the protocol and waits for response (either ACK or
	 * cEMI confirmation), timeout or an error condition.<br>
	 * Note that, for now, when using blocking mode any ongoing nonblocking invocation is
	 * not detected or considered for waiting until completion.
	 * <p>
	 * If mode is {@link KNXnetIPConnection#NONBLOCKING}, sending is only permitted if no
	 * other send is currently being done, otherwise throws a
	 * {@link KNXIllegalStateException}. A user has to check the state ({@link #getState()}
	 * on its own.
	 */
	public void send(CEMI frame, BlockingMode mode) throws KNXTimeoutException,
		KNXConnectionClosedException
	{
		// send state | blocking | nonblocking
		// -----------------------------------
		// OK         |send+wait | send+return
		// WAIT       |wait+s.+w.| throw
		// ACK_ERROR  |send+wait | send+return
		// ERROR      |throw     | throw
	
		if (state == CLOSED) {
			logger.warn("send invoked on closed connection - aborted");
			throw new KNXConnectionClosedException("connection closed");
		}
		if (state < 0) {
			logger.error("send invoked in error state " + state + " - aborted");
			throw new KNXIllegalStateException("in error state, send aborted");
		}
		// arrange into line for blocking mode
		if (mode != NONBLOCKING)
			sendWaitQueue.acquire();
		synchronized (lock) {
			if (mode == NONBLOCKING && state != OK && state != ACK_ERROR) {
				logger.warn("nonblocking send invoked while waiting for data response "
					+ "in state " + state + " - aborted");
				throw new KNXIllegalStateException("waiting for data response");
			}
			try {
				if (state == CLOSED) {
					logger.warn("send invoked on closed connection - aborted");
					throw new KNXConnectionClosedException("connection closed");
				}
				updateState = mode == NONBLOCKING;
				final byte[] buf;
				if (serviceRequest == KNXnetIPHeader.ROUTING_IND)
					buf = PacketHelper.toPacket(new RoutingIndication(frame));
				else
					buf = PacketHelper.toPacket(new ServiceRequest(serviceRequest,
						channelID, getSeqNoSend(), frame));
				final DatagramPacket p =
					new DatagramPacket(buf, buf.length, dataEP.getAddress(), dataEP
						.getPort());
				int attempt = 0;
				for (; attempt < maxSendAttempts; ++attempt) {
					logger.trace("sending cEMI frame, " + mode + ", attempt "
						+ (attempt + 1));
					socket.send(p);
					// shortcut for routing, don't switch into 'ack-pending'
					if (serviceRequest == KNXnetIPHeader.ROUTING_IND)
						return;
					internalState = ACK_PENDING;
					// always forward this state to user
					state = ACK_PENDING;
					if (mode == NONBLOCKING)
						return;
					waitForStateChange(ACK_PENDING, responseTimeout);
					if (internalState == CEMI_CON_PENDING || internalState == OK)
						break;
				}
				// close connection on no service ack from server
				if (attempt == maxSendAttempts) {
					final KNXAckTimeoutException e =
						new KNXAckTimeoutException("no acknowledge reply received");
					close(INTERNAL, "maximum send attempts", LogLevel.ERROR, e);
					throw e;
				}
				// always forward this state to user
				state = internalState;
				if (mode == WAIT_FOR_ACK)
					return;
				// wait for incoming request
				waitForStateChange(CEMI_CON_PENDING, CONFIRMATION_TIMEOUT);
				// throw on no answer
				if (internalState == CEMI_CON_PENDING) {
					final KNXTimeoutException e =
						new KNXTimeoutException("no confirmation reply received");
					logger.log(LogLevel.INFO, "send response timeout", e);
					internalState = OK;
					throw e;
				}
			}
			catch (final IOException e) {
				close(INTERNAL, "communication failure", LogLevel.ERROR, e);
				throw new KNXConnectionClosedException("connection closed");
			}
			finally {
				updateState = true;
				setState(internalState);
				if (mode != NONBLOCKING)
					sendWaitQueue.release();
			}
		}
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.KNXnetIPConnection#getRemoteAddress()
	 */
	public final InetSocketAddress getRemoteAddress()
	{
		if (state == CLOSED)
			return new InetSocketAddress(0);
		return ctrlEP;
	}

	public final int getState()
	{
		return state;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.KNXnetIPConnection#close()
	 */
	public final void close()
	{
		close(CLIENT, "requested by client", LogLevel.INFO, null);
	}

	/**
	 * Opens a new IP communication channel to a remote server.
	 * <p>
	 * The communication state of this object is assumed to be closed state. This method
	 * is designed to be called only once during the objects lifetime!
	 * 
	 * @param localEP the local endpoint to use for communication channel
	 * @param serverCtrlEP the remote server control endpoint used for connect request
	 * @param cri connect request information used to configure the communication
	 *        attributes
	 * @param useNAT <code>true</code> to use a NAT (network address translation) aware
	 *        communication mechanism, <code>false</code> to use the default way
	 * @throws KNXException on socket communication error
	 * @throws KNXTimeoutException on no connect response before connect timeout
	 * @throws KNXRemoteException if response indicates an error condition at the server
	 *         concerning the request
	 * @throws KNXInvalidResponseException if connect response is in wrong format
	 */
	protected void connect(InetSocketAddress localEP, InetSocketAddress serverCtrlEP,
		CRI cri, boolean useNAT) throws KNXException
	{
		ctrlEP = serverCtrlEP;
		isNatAware = useNAT;
		logger = LogManager.getManager().getLogService(getName());
		try {
			// if we allow localEP to be null, we would create an unbound socket
			if (localEP == null)
				throw new KNXIllegalArgumentException("no local endpoint specified");
			logger.info("establish link from " + localEP + " to " + ctrlEP);
			socket = new DatagramSocket(localEP);
			// HPAI throws if wildcard local address (0.0.0.0) is supplied
			final HPAI hpai =
				new HPAI(HPAI.IPV4_UDP, isNatAware ? null : (InetSocketAddress) socket
					.getLocalSocketAddress());
			final byte[] buf = PacketHelper.toPacket(new ConnectRequest(cri, hpai, hpai));
			final DatagramPacket p =
				new DatagramPacket(buf, buf.length, ctrlEP.getAddress(), ctrlEP.getPort());
			socket.send(p);
		}
		catch (final IOException e) {
			if (socket != null)
				socket.close();
			logger.error("communication failure on connect", e);
			if (localEP.getAddress().isLoopbackAddress())
				logger.warn("try to specify the actual IP address of the local host");
			LogManager.getManager().removeLogService(getName());
			throw new KNXException(e.getMessage());
		}
		logger.info("wait for connect response from " + ctrlEP + " ...");
		startReceiver();
		final boolean changed = waitForStateChange(CLOSED, CONNECT_REQ_TIMEOUT);
		if (state == OK) {
			(heartbeat = new HeartbeatMonitor()).start();
			logger.info("link established");
			return;
		}
		// quit, cleanup and notify client
		receiver.quit();
		socket.close();
		KNXException e;
		if (!changed)
			e = new KNXTimeoutException("timeout connecting to control endpoint "
					+ ctrlEP);
		else if (state == ACK_ERROR)
			e = new KNXRemoteException(
				"acknowledge error, failed to connect to control endpoint " + ctrlEP);
		else
			e = new KNXInvalidResponseException("invalid connect response from " + ctrlEP);
		setState(CLOSED);
		logger.error("establishing connection failed", e);
		LogManager.getManager().removeLogService(getName());
		throw e;
	}

	/**
	 * Returns the protocol's current receive sequence number.
	 * <p>
	 * 
	 * @return receive sequence number as int
	 */
	protected synchronized int getSeqNoRcv()
	{
		return seqNoRcv;
	}

	/**
	 * Increments the protocol's receive sequence number, with increment on sequence
	 * number 255 resulting in 0.
	 * <p>
	 */
	protected synchronized void incSeqNoRcv()
	{
		seqNoRcv = ++seqNoRcv & 0xFF;
	}

	/**
	 * Returns the protocol's current send sequence number.
	 * <p>
	 * 
	 * @return send sequence number as int
	 */
	protected synchronized int getSeqNoSend()
	{
		return seqNoSend;
	}

	/**
	 * Increments the protocol's send sequence number, with increment on sequence number
	 * 255 resulting in 0.
	 * <p>
	 */
	protected synchronized void incSeqNoSend()
	{
		seqNoSend = ++seqNoSend & 0xFF;
	}

	/**
	 * Fires a frame received event ({@link KNXListener#frameReceived(FrameEvent)}) for
	 * the supplied cEMI <code>frame</code>.
	 * 
	 * @param frame the cEMI to generate the event for
	 */
	protected void fireFrameReceived(CEMI frame)
	{
		final FrameEvent fe = new FrameEvent(this, frame);
		for (final Iterator i = listenersCopy.iterator(); i.hasNext();) {
			final KNXListener l = (KNXListener) i.next();
			try {
				l.frameReceived(fe);
			}
			catch (final RuntimeException e) {
				removeConnectionListener(l);
				logger.error("removed event listener", e);
			}
		}
	}

	abstract void handleService(KNXnetIPHeader h, byte[] data, int offset)
		throws KNXFormatException, IOException;

	final short getChannelID()
	{
		return channelID;
	}

	final List getListeners()
	{
		return listenersCopy;
	}

	final void startReceiver()
	{
		if (receiver == null)
			(receiver = new Receiver()).start();
	}

	final void setState(int newState)
	{
		if (closing < 2 || state != CLOSED) {
			// ignore confirmation state change during ACK_PENDING
			if (internalState == ACK_PENDING && newState == OK)
				return;
			internalState = newState;
			if (updateState)
				state = newState;
		}
	}

	final void setStateNotify(int newState)
	{
		synchronized (lock) {
			setState(newState);
			// worst case: we notify 2 threads, the closing one and 1 sending
			lock.notifyAll();
		}
	}

	void close(int initiator, String reason, LogLevel level, Throwable t)
	{
		synchronized (this) {
			if (closing > 0)
				return;
			closing = 1;
		}
		try {
			synchronized (lock) {
				final byte[] buf =
					PacketHelper.toPacket(new DisconnectRequest(channelID, new HPAI(
						HPAI.IPV4_UDP, isNatAware ? null : (InetSocketAddress) socket
							.getLocalSocketAddress())));
				final DatagramPacket p = new DatagramPacket(buf, buf.length,
					ctrlEP.getAddress(), ctrlEP.getPort());
				socket.send(p);
				long remaining = CONNECT_REQ_TIMEOUT * 1000;
				final long end = System.currentTimeMillis() + remaining;
				while (closing == 1 && remaining > 0) {
					try {
						lock.wait(remaining);
					}
					catch (final InterruptedException e) {}
					remaining = end - System.currentTimeMillis();
				}
			}
		}
		catch (final IOException e) {
			logger.error("send disconnect failed", e);
		}
		catch (final RuntimeException e) {
			// we have to do this strange catch here, since if socket already failed
			// before close(), getLocalSocketAddress() might throw illegal argument
			// exception or return  the wildcard address, indicating a messed up socket
			logger.error("send disconnect failed, socket problem");
		}
		finally {
			shutdown(initiator, reason, level, t);
		}
	}

	void shutdown(int initiator, String reason, LogLevel level, Throwable t)
	{
		if (t != null)
			logger.log(level, "close KNXnet/IP connection - " + reason, t);
		else
			logger.log(level, "close KNXnet/IP connection - " + reason);
		// heartbeat was not necessarily used at all
		if (heartbeat != null)
			heartbeat.quit();
		receiver.quit();
		socket.close();
		// ensure user sees final state CLOSED
		updateState = true;
		setState(CLOSED);
		fireConnectionClosed(initiator, reason);
		synchronized (listeners) {
			listeners.clear();
			listenersCopy = Collections.EMPTY_LIST;
		}
		LogManager.getManager().removeLogService(getName());
	}

	private boolean waitForStateChange(int initialState, int timeout)
	{
		long remaining = timeout * 1000L;
		final long end = System.currentTimeMillis() + remaining;
		synchronized (lock) {
			while (internalState == initialState && remaining > 0) {
				try {
					lock.wait(remaining);
				}
				catch (final InterruptedException e) {}
				remaining = end - System.currentTimeMillis();
			}
		}
		return remaining > 0;
	}

	private void disconnectRequested(DisconnectRequest req)
	{
		// requests with wrong channel ID are ignored (conforming to spec)
		if (req.getChannelID() == channelID) {
			final byte[] buf =
				PacketHelper.toPacket(new DisconnectResponse(channelID,
					ErrorCodes.NO_ERROR));
			final DatagramPacket p =
				new DatagramPacket(buf, buf.length, ctrlEP.getAddress(), ctrlEP.getPort());
			try {
				socket.send(p);
			}
			catch (final IOException e) {
				logger.error("communication failure", e);
			}
			finally {
				shutdown(SERVER, "requested by server", LogLevel.INFO, null);
			}
		}
	}

	private void fireConnectionClosed(int initiator, String reason)
	{
		final ConnectionCloseEvent ce = new ConnectionCloseEvent(this, initiator, reason);
		for (final Iterator i = listenersCopy.iterator(); i.hasNext();) {
			final KNXListener l = (KNXListener) i.next();
			try {
				l.connectionClosed(ce);
			}
			catch (final RuntimeException e) {
				removeConnectionListener(l);
				logger.error("removed event listener", e);
			}
		}
	}
	
	// a semaphore with fair use behavior (FIFO)
	// acquire and its associated release don't have to be invoked by same thread
	private static final class Semaphore
	{
		private static final class Node
		{
			Node next;
			boolean blocked;
	
			Node(Node n)
			{
				next = n;
				blocked = true;
			}
		}
	
		private Node head;
		private Node tail;
		private int cnt;
	
		Semaphore()
		{
			cnt = 1;
		}
	
		Semaphore(int count)
		{
			cnt = count;
		}
	
		void acquire()
		{
			Node n;
			synchronized (this) {
				if (cnt > 0 && tail == null) {
					--cnt;
					return;
				}
				n = enqueue();
			}
			synchronized (n) {
				while (n.blocked)
					try {
						n.wait();
					}
					catch (final InterruptedException e) {}
			}
			synchronized (this) {
				dequeue();
				--cnt;
			}
		}
	
		synchronized void release()
		{
			if (++cnt > 0)
				notifyNext();
		}
	
		private Node enqueue()
		{
			final Node n = new Node(null);
			if (tail == null)
				tail = n;
			else
				head.next = n;
			return head = n;
		}
	
		private void notifyNext()
		{
			if (tail != null)
				synchronized (tail) {
					tail.blocked = false;
					tail.notify();
				}
		}
	
		private void dequeue()
		{
			tail = tail.next;
			if (tail == null)
				head = null;
		}
	}

	private final class HeartbeatMonitor extends Thread
	{
		// client SHALL wait 10 seconds for a connection state response from server
		private static final int CONNECTIONSTATE_REQ_TIMEOUT = 10;
		private static final int HEARTBEAT_INTERVAL = 60;
		private static final int MAX_REQUEST_ATTEMPTS = 4;
		private boolean received;

		HeartbeatMonitor()
		{
			super("KNXnet/IP heartbeat monitor");
			setDaemon(true);
		}

		public void run()
		{
			final byte[] buf =
				PacketHelper.toPacket(new ConnectionstateRequest(channelID, new HPAI(
					HPAI.IPV4_UDP, isNatAware ? null : (InetSocketAddress) socket
						.getLocalSocketAddress())));
			final DatagramPacket p =
				new DatagramPacket(buf, buf.length, ctrlEP.getAddress(), ctrlEP.getPort());
			try {
				while (true) {
					Thread.sleep(HEARTBEAT_INTERVAL * 1000);
					int i = 0;
					for (; i < MAX_REQUEST_ATTEMPTS; i++) {
						logger.trace("sending connection state request, attempt "
							+ (i + 1));
						synchronized (this) {
							received = false;
							socket.send(p);

							long remaining = CONNECTIONSTATE_REQ_TIMEOUT * 1000;
							final long end = System.currentTimeMillis() + remaining;
							while (!received && remaining > 0) {
								wait(remaining);
								remaining = end - System.currentTimeMillis();
							}
							if (received)
								break;
						}
					}
					// disconnect on no reply
					if (i == MAX_REQUEST_ATTEMPTS) {
						close(INTERNAL, "no heartbeat response", LogLevel.WARN, null);
						break;
					}
				}
			}
			catch (final InterruptedException e) {}
			catch (final IOException e) {
				close(INTERNAL, "heartbeat communication failure", LogLevel.ERROR, e);
			}
		}

		void quit()
		{
			interrupt();
			if (currentThread() == this)
				return;
			try {
				join();
			}
			catch (final InterruptedException e) {}
		}

		void setResponse(ConnectionstateResponse res)
		{
			final boolean ok = res.getStatus() == ErrorCodes.NO_ERROR;
			synchronized (this) {
				if (ok)
					received = true;
				notify();
			}
			if (!ok)
				logger.warn("connection state response status: " + res.getStatusString());
		}
	}

	private final class Receiver extends Thread
	{
		private static final int RCV_MAXBUF = 0x1000;
		private volatile boolean quit;

		Receiver()
		{
			super("KNXnet/IP receiver");
			setDaemon(true);
		}

		public void run()
		{
			final byte[] buf = new byte[RCV_MAXBUF];
			try {
				while (!quit) {
					final DatagramPacket p = new DatagramPacket(buf, buf.length);
					socket.receive(p);
					final byte[] data = p.getData();
					try {
						final KNXnetIPHeader h = new KNXnetIPHeader(data, p.getOffset());
						if (h.getTotalLength() > p.getLength())
							logger.warn("received frame length does not match - ignored");
						else if (h.getServiceType() == 0)
							// check service type for 0 (invalid type),
							// so unused service types of us can stay 0 by default
							logger.warn("received frame with service type 0 - ignored");
						else
							parseService(h, data, p.getOffset() + h.getStructLength(), p
								.getAddress(), p.getPort());
					}
					catch (final KNXFormatException e) {
						// if available log bad item, too
						if (e.getItem() != null)
							logger.warn("received invalid frame, item " + e.getItem(), e);
						else
							logger.warn("received invalid frame", e);
					}
				}
			}
			catch (final InterruptedIOException e) {}
			catch (final IOException e) {
				if (!quit)
					close(INTERNAL, "receiver communication failure", LogLevel.ERROR, e);
			}
		}

		void quit()
		{
			// since receiver might not get interrupted, we additionally set quit flag
			// and only give it a try with restricted timeout for a graceful end
			quit = true;
			interrupt();
			if (currentThread() == this)
				return;
			try {
				join(50);
			}
			catch (final InterruptedException e) {}
		}

		private void parseService(KNXnetIPHeader h, byte[] data, int offset,
			InetAddress src, int port) throws KNXFormatException, IOException
		{
			final int svc = h.getServiceType();
			if (svc == KNXnetIPHeader.CONNECT_REQ)
				logger.warn("received connect request - ignored");
			else if (svc == KNXnetIPHeader.CONNECT_RES) {
				int newState = OK;
				final ConnectResponse res = new ConnectResponse(data, offset);
				final HPAI ep = res.getDataEndpoint();
				// we do an additional check for UDP to be on the safe side
				if (res.getStatus() == ErrorCodes.NO_ERROR
					&& ep.getHostProtocol() == HPAI.IPV4_UDP) {
					channelID = res.getChannelID();
					final InetAddress ip = ep.getAddress();
					// in NAT aware mode, if the data EP is incomplete or left 
					// empty, we fall back to the IP address and port of the sender 
					if (isNatAware && (ip == null || ip.isAnyLocalAddress() 
						|| ep.getPort() == 0)) {
						dataEP = new InetSocketAddress(src, port);
						logger.info("NAT aware mode: using data endpoint " + dataEP);						
					}
					else {
						dataEP = new InetSocketAddress(ip, ep.getPort());
						logger.info("using assigned data endpoint " + dataEP);						
					}
					checkVersion(h);
				}
				else {
					newState = ACK_ERROR;
					if (ep != null && ep.getHostProtocol() != HPAI.IPV4_UDP)
						logger.error("only connection support for UDP/IP");
					else
						logger.error(res.getStatusString());
				}
				// notify to resume with connect
				setStateNotify(newState);
			}
			else if (svc == KNXnetIPHeader.CONNECTIONSTATE_REQ)
				logger.warn("received connection state request - ignored");
			else if (svc == KNXnetIPHeader.CONNECTIONSTATE_RES) {
				if (checkVersion(h))
					heartbeat.setResponse(new ConnectionstateResponse(data, offset));
			}
			else if (svc == KNXnetIPHeader.DISCONNECT_REQ) {
				if (ctrlEP.getAddress().equals(src) && ctrlEP.getPort() == port)
					disconnectRequested(new DisconnectRequest(data, offset));
			}
			else if (svc == KNXnetIPHeader.DISCONNECT_RES) {
				final DisconnectResponse res = new DisconnectResponse(data, offset);
				if (res.getStatus() != ErrorCodes.NO_ERROR)
					logger.warn("received disconnect response status 0x"
						+ Integer.toHexString(res.getStatus()) + " ("
						+ ErrorCodes.getErrorMessage(res.getStatus()) + ")");
				// finalize closing
				closing = 2;
				setStateNotify(CLOSED);
			}
			else if (svc == serviceRequest)
				handleService(h, data, offset);
			else if (svc == serviceAck) {
				final ServiceAck res = new ServiceAck(svc, data, offset);
				if (res.getChannelID() != channelID)
					logger.warn("received wrong acknowledge channel-ID " +
						res.getChannelID() + ", expected " + channelID + " - ignored");
				else if (res.getSequenceNumber() != getSeqNoSend())
					logger.warn("received invalid acknowledge send-sequence " +
						+ res.getSequenceNumber() + ", expected " + getSeqNoSend() +
						" - ignored");
				else {
					if (!checkVersion(h))
						return;
					incSeqNoSend();
					// update state and notify our lock
					setStateNotify(res.getStatus() == ErrorCodes.NO_ERROR
						? CEMI_CON_PENDING : ACK_ERROR);
					if (internalState == ACK_ERROR)
						logger.warn("received acknowledge status: "
							+ res.getStatusString());
				}
			}
			else
				handleService(h, data, offset);
		}

		/**
		 * Checks for supported protocol version in KNX header.
		 * <p>
		 * On unsupported version,
		 * {@link ConnectionImpl#close(int, String, LogLevel, Throwable)} is invoked.
		 * 
		 * @param h KNX header to check
		 * @return <code>true</code> on supported version, <code>false</code>
		 *         otherwise
		 */
		private boolean checkVersion(KNXnetIPHeader h)
		{
			if (h.getVersion() != KNXNETIP_VERSION_10) {
				close(INTERNAL, "protocol version changed", LogLevel.ERROR, null);
				return false;
			}
			return true;
		}
	}
}
