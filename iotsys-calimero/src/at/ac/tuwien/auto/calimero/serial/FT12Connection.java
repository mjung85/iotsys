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

package at.ac.tuwien.auto.calimero.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.DataUnitBuilder;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.KNXListener;
import at.ac.tuwien.auto.calimero.exception.KNXAckTimeoutException;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * Connection based on the FT1.2 protocol for communication with a BCU2 device.
 * <p>
 * Currently, one log service is provided for all connection instances, with the log
 * service named "FT1.2".
 * 
 * @author B. Malinowsky
 */
public class FT12Connection
{
	/**
	 * State of communication: in idle state, no error, ready to send.
	 * <p>
	 */
	public static final int OK = 0;

	/**
	 * State of communication: in closed state, no send possible.
	 * <p>
	 */
	public static final int CLOSED = 1;

	/**
	 * Status code of communication: waiting for acknowledge after send, no error, not
	 * ready to send.
	 * <p>
	 */
	public static final int ACK_PENDING = 2;

	// default used baud rate for BAU
	private static final int DEFAULT_BAUDRATE = 19200;

	// primary station control field
	private static final int DIR_FROM_BAU = 0x80;
	private static final int INITIATOR = 0x40;
	private static final int FRAMECOUNT_BIT = 0x20;

	// frame count valid is always set on sending user data
	private static final int FRAMECOUNT_VALID = 0x10;
	private static final int ACK = 0xE5;
	// primary station service codes
	private static final int RESET = 0x00;
	private static final int USER_DATA = 0x03;
	private static final int REQ_STATUS = 0x09;
	// secondary station control field (used only for response to REQ_STATUS)
	// reserved bit, always 0
	// private static final int RESERVED = 0x20;
	// secondary station service codes
	// private static final int CONFIRM_ACK = 0x00;
	// private static final int CONFIRM_NACK = 0x01;
	// private static final int RESPONSE_STATUS = 0x0B;

	// timeout for end of frame exchange [Bits]
	private static final int EXCHANGE_TIMEOUT = 512;
	// limit for retransmissions of discarded frames
	private static final int REPEAT_LIMIT = 3;
	// maximum time between two frame characters, minimum time to indicate error [Bits]
	private static final int IDLE_TIMEOUT = 33;

	// frame delimiter characters
	private static final int START = 0x68;
	private static final int START_FIXED = 0x10;
	private static final int END = 0x16;

	// for now, a static log service should be sufficient, since we assume
	// usage of physical ports for peripheral devices only, which are limited
	// to at most 2 interfaces on common devices
	private static final LogService logger =
		LogManager.getManager().getLogService("FT1.2");
	
	// adapter for used serial I/O library
	// - on ME CDC platforms with CommConnection available, holds an
	// instance of type CommConnection
	// - on internal serial port access, SerialCom adapter is used
	// - or some external serial I/O library adapter (e.g. rxtx)
	private LibraryAdapter adapter;
	
	private String port;
	private InputStream is;
	private OutputStream os;
	private volatile int state = CLOSED;

	private Receiver receiver;
	private final Object lock = new Object();
	private int sendFrameCount;
	private int rcvFrameCount;
	private int exchangeTimeout;
	private int idleTimeout;

	// event listener lists
	private final List listeners = new ArrayList();
	private List listenersCopy = new ArrayList();

	private static class CommConnectionAdapter extends LibraryAdapter
	{
		private Object conn;
		private InputStream is;
		private OutputStream os;
		
		CommConnectionAdapter(String portID, int baudrate) throws KNXException
		{
			open(portID, baudrate);
		}

		public void close() throws IOException
		{
			try {
				invoke(conn, "close", null);
			}
			catch (final InvocationTargetException e) {
				if (e.getCause() instanceof IOException)
					throw (IOException) e.getCause();
			}
			catch (final Exception e) {}
		}

		public InputStream getInputStream()
		{
			return is;
		}

		public OutputStream getOutputStream()
		{
			return os;
		}

		private void open(String portID, int baudrate) throws KNXException
		{
			Object cc = null;
			try {
				final Class connector = Class.forName("javax.microedition.io.Connector");
				cc = invoke(connector, "open", new String[] { "comm:" + portID
					+ ";baudrate=" + baudrate
					+ ";bitsperchar=8;stopbits=1;parity=even;autocts=off;autorts=off" });
				is = (InputStream) invoke(cc, "openInputStream", null);
				os = (OutputStream) invoke(cc, "openOutputStream", null);
				conn = cc;
				return;
			}
			catch (final ClassNotFoundException e) {
				throw new KNXException("Connector factory not found, " +
					"no ME CDC environment");
			}
			catch (final SecurityException e) {
				logger.error("CommConnection access denied", e);
			}
			catch (final InvocationTargetException e) {
				logger.error("CommConnection: " + e.getCause().getMessage());
			}
			// NoSuchMethodException, IllegalAccessException, IllegalArgumentException
			catch (final Exception e) {}
			try {
				invoke(cc, "close", null);
				is.close();
				os.close();
			}
			catch (final Exception mainlyNPE) {}
			throw new KNXException("failed to open CommConnection");
		}
	}
	
	/**
	 * Creates a new connection to a BCU2 using the FT1.2 protocol.
	 * <p>
	 * If the port to use can not be told just by the number, use
	 * {@link FT12Connection#FT12Connection(String)}.<br>
	 * The baud rate is set to 19200.
	 * 
	 * @param portNumber port number of the serial communication port to use; mapped to
	 *        the default port identifier using this number (device and platform specific)
	 * @throws KNXException on port not found or access error, initializing port settings
	 *         failed, if reset of BCU2 failed
	 */
	public FT12Connection(int portNumber) throws KNXException
	{
		this(defaultPortPrefix() + portNumber, DEFAULT_BAUDRATE);
	}

	/**
	 * Creates a new connection to a BCU2 using the FT1.2 protocol.
	 * <p>
	 * The baud rate is set to 19200.
	 * 
	 * @param portID port identifier of the serial communication port to use
	 * @throws KNXException on port not found or access error, initializing port settings
	 *         failed, if reset of BCU2 failed
	 */
	public FT12Connection(String portID) throws KNXException
	{
		this(portID, DEFAULT_BAUDRATE);
	}

	/**
	 * Creates a new connection to a BCU2 using the FT1.2 protocol, and set the baud rate
	 * for communication.
	 * <p>
	 * If the requested baud rate is not supported, it may get substituted with a valid
	 * baud rate by default.
	 * 
	 * @param portID port identifier of the serial communication port to use
	 * @param baudrate baud rate to use for communication, 0 &lt; baud rate
	 * @throws KNXException on port not found or access error, initializing port settings
	 *         failed, if reset of BCU2 failed
	 */
	public FT12Connection(String portID, int baudrate) throws KNXException
	{
		open(portID, baudrate);
		try {
			sendReset();
		}
		catch (final KNXAckTimeoutException e) {
			close(false, "acknowledge timeout on sending reset");
			throw e;
		}
	}

	/**
	 * Attempts to gets the available serial communication ports on the host.
	 * <p>
	 * At first, the Java system property "microedition.commports" is queried. If there is
	 * no property with that key, and Calimero itself has access to serial ports,
	 * the lowest 10 ports numbers are enumerated and checked if present.<br>
	 * The empty array is returned if no ports are discovered.
	 * 
	 * @return array of strings with found port IDs
	 */
	public static String[] getPortIdentifiers()
	{
		String ports = null;
		try {
			ports = System.getProperty("microedition.commports");
		}
		catch (final SecurityException e) {}
		if (ports != null) {
			final StringTokenizer st = new StringTokenizer(ports, ",");
			final String[] portIDs = new String[st.countTokens()];
			for (int i = 0; i < portIDs.length; ++i)
				portIDs[i] = st.nextToken();
			return portIDs;
		}
		if (SerialCom.isLoaded()) {
			final String prefix = defaultPortPrefix();
			final List l = new ArrayList(10);
			for (int i = 0; i < 10; ++i)
				if (SerialCom.portExists(prefix + i))
					l.add(prefix + i);
			return (String[]) l.toArray(new String[l.size()]);
		}
		// skip other possible adapters for now, and return empty list...
		return new String[0];
	}

	/**
	 * Adds the specified event listener <code>l</code> to receive events from this
	 * connection.
	 * <p>
	 * If <code>l</code> was already added as listener, no action is performed.
	 * 
	 * @param l the listener to add
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

	/**
	 * Removes the specified event listener <code>l</code>, so it does no longer
	 * receive events from this connection.
	 * <p>
	 * If <code>l</code> was not added in the first place, no action is performed.
	 * 
	 * @param l the listener to remove
	 */
	public void removeConnectionListener(KNXListener l)
	{
		synchronized (listeners) {
			if (listeners.remove(l))
				listenersCopy = new ArrayList(listeners);
		}
	}

	/**
	 * Returns the port identifier used in this connection.
	 * <p>
	 * After the connection is closed, the returned ID will always be the empty string.
	 * 
	 * @return port ID as string, or empty string
	 */
	public final String getPortID()
	{
		return state == CLOSED ? "" : port;
	}

	/**
	 * Sets a new baud rate for this connection.
	 * <p>
	 * 
	 * @param baud requested baud rate [Bit/s], 0 &lt; baud rate
	 */
	public void setBaudrate(int baud)
	{
		adapter.setBaudRate(baud);
	}

	/**
	 * Returns the currently used baud rate.
	 * <p>
	 * After closing the connection, the returned baud rate is 0 by default.
	 * 
	 * @return baud rate in Bit/s
	 */
	public final int getBaudRate()
	{
		return adapter.getBaudRate();
	}

	/**
	 * Returns information about the current FT1.2 communication state.
	 * 
	 * @return state enumeration
	 */
	public final int getState()
	{
		return state;
	}

	/**
	 * Sends an EMI frame to the BCU2 connected with this endpoint.
	 * <p>
	 * In blocking mode, all necessary retransmissions of the sent frame will be done
	 * automatically according to the protocol specification (i.e. in case of timeout).
	 * <br>
	 * If a communication failure occurs on the port, {@link #close()} is called. A send
	 * timeout does not lead to closing of this connection.<br>
	 * In blocking send mode, on successfully receiving a confirmation, all listeners are
	 * guaranteed to get notified before this method returns. The communication state (see
	 * {@link #getState()}) is reset to {@link #OK} when the notification completed, so
	 * to prevent another send call from a listener.
	 * 
	 * @param frame EMI message to send, length of frame &lt; 256 bytes
	 * @param blocking <code>true</code> to block for confirmation (ACK),
	 *        <code>false</code> to immediately return after send
	 * @throws KNXAckTimeoutException in <code>blocking</code> mode, if a timeout
	 *         regarding the acknowledge message was encountered
	 * @throws KNXPortClosedException if no communication was established in the
	 *         first place or communication was closed
	 */
	public void send(byte[] frame, boolean blocking) throws KNXAckTimeoutException,
		KNXPortClosedException
	{
		boolean ack = false;
		try {
			for (int i = 0; i <= REPEAT_LIMIT; ++i) {
				logger.trace("sending FT1.2 frame, " + (blocking ? "" : "non-")
					+ "blocking, attempt " + (i + 1));
				sendData(frame);
				if (!blocking || waitForAck()) {
					ack = true;
					break;
				}
			}
			sendFrameCount ^= FRAMECOUNT_BIT;
			if (state == ACK_PENDING)
				state = OK;
			if (!ack)
				throw new KNXAckTimeoutException("no acknowledge reply received");
		}
		catch (final IOException e) {
			close(false, e.getMessage());
			throw new KNXPortClosedException(e.getMessage());
		}
	}

	/**
	 * Ends communication with the BCU2 as specified by the FT1.2 protocol.
	 * <p>
	 * The BCU is always switched back into normal mode.<br>
	 * All registered event listeners get notified. The close event is the last event the
	 * listeners receive. <br>
	 * If this connection endpoint is already closed, no action is performed.
	 */
	public void close()
	{
		close(true, "requested by client");
	}

	private void close(boolean user, String reason)
	{
		if (state == CLOSED)
			return;
		logger.info("close serial port " + port + " - " + reason);
		state = CLOSED;
		if (receiver != null)
			receiver.quit();
		try {
			is.close();
			os.close();
			adapter.close();
		}
		catch (final Exception e) {
			logger.warn("failed to close all serial I/O resources", e);
		}
		fireConnectionClosed(user, reason);
	}
	
	private void open(String portID, int baudrate) throws KNXException
	{
		adapter = createAdapter(portID, baudrate);
		port = portID;
		is = adapter.getInputStream();
		os = adapter.getOutputStream();
		calcTimeouts(adapter.getBaudRate());
		(receiver = new Receiver()).start();
		state = OK;
		logger.info("access supported, opened serial port " + portID);
	}

	private LibraryAdapter createAdapter(String portID, int baudrate) throws KNXException
	{
		// check for ME CDC platform and available serial communication port
		// protocol support for communication ports is optional in CDC
		logger.info("try ME CDC support for serial ports (CommConnection)");
		try {
			return new CommConnectionAdapter(portID, baudrate);
		}
		catch (final KNXException e) {
			logger.warn("ME CDC access to serial port failed", e);
		}

		// check internal support for serial port access
		// protocol support available for Win 32/64 platforms
		// (so we provide serial port access at least on platforms with ETS)
		logger.info("try Calimero native support for serial ports (SerialCom)");
		SerialCom conn = null;
		try {
			conn = new SerialCom(portID);
			conn.setBaudRate(baudrate);
			calcTimeouts(conn.getBaudRate());
			// In Windows Embedded CE, the read interval timeout starts immediately
			conn.setTimeouts(new SerialCom.Timeouts(idleTimeout, 0, 0, 0, 0));
			conn.setParity(SerialCom.PARITY_EVEN);
			conn.setControl(SerialCom.STOPBITS, SerialCom.ONE_STOPBIT);
			conn.setControl(SerialCom.DATABITS, 8);
			conn.setControl(SerialCom.FLOWCTRL, SerialCom.FLOWCTRL_NONE);
			logger.info("setup serial port: baudrate " + conn.getBaudRate()
				+ ", parity even, databits " + conn.getControl(SerialCom.DATABITS)
				+ ", stopbits " + conn.getControl(SerialCom.STOPBITS) + ", timeouts "
				+ conn.getTimeouts());
			return conn;
		}
		catch (final IOException e) {
			if (conn != null)
				try {
					conn.close();
				}
				catch (final IOException ignore) {}
			logger.warn("native access to serial port failed", e);
		}

		// check whether a rxtx library is hanging around somewhere
		logger.info("try rxtx library support for serial ports");
		try {
			final Class c = Class.forName("tuwien.auto.calimero.serial.RxtxAdapter");
			return (LibraryAdapter) c.getConstructors()[0].newInstance(new Object[] {
				portID, new Integer(baudrate) });
		}
		catch (final ClassNotFoundException e) {
			logger.warn("rxtx library adapter not found");
		}
		catch (final SecurityException e) {
			logger.error("rxtx library adapter access denied", e);
		}
		catch (final InvocationTargetException e) {
			logger.error("initalizing rxtx serial port", e.getCause());
		}
		catch (final Exception e) {
			// InstantiationException, NoSuchMethodException,
			// IllegalAccessException, IllegalArgumentException
			logger.warn("rxtx access to serial port failed", e);
		}
		catch (final NoClassDefFoundError e) {
			logger.error("no rxtx library classes found", e);
		}
		throw new KNXException("can not open serial port " + portID);
	}
	
	private void sendReset() throws KNXPortClosedException, KNXAckTimeoutException
	{
		try {
			final byte[] reset =
				new byte[] { START_FIXED, INITIATOR | RESET, INITIATOR | RESET, END };
			for (int i = 0; i <= REPEAT_LIMIT; ++i) {
				logger.trace("send reset to BCU");
				state = ACK_PENDING;
				os.write(reset);
				os.flush();
				if (waitForAck())
					return;
			}
			throw new KNXAckTimeoutException(
				"resetting BCU failed (no acknowledge reply received)");
		}
		catch (final IOException e) {
			close(false, e.getMessage());
			throw new KNXPortClosedException(e.getMessage());
		}
		finally {
			sendFrameCount = FRAMECOUNT_BIT;
			rcvFrameCount = FRAMECOUNT_BIT;
		}
	}

	private void sendData(byte[] data) throws IOException, KNXPortClosedException
	{
		if (data.length > 255)
			throw new KNXIllegalArgumentException("data length > 255 bytes");
		if (state == CLOSED)
			throw new KNXPortClosedException("connection closed");
		final byte[] buf = new byte[data.length + 7];
		int i = 0;
		buf[i++] = START;
		buf[i++] = (byte) (data.length + 1);
		buf[i++] = (byte) (data.length + 1);
		buf[i++] = START;
		buf[i++] = (byte) (INITIATOR | sendFrameCount | FRAMECOUNT_VALID | USER_DATA);
		for (int k = 0; k < data.length; ++k)
			buf[i++] = data[k];
		buf[i++] = checksum(buf, 4, data.length + 1);
		buf[i++] = END;
		
		state = ACK_PENDING;
		os.write(buf);
		os.flush();
	}

	private void sendAck() throws IOException
	{
		os.write(ACK);
		os.flush();
	}

	private boolean waitForAck()
	{
		long remaining = exchangeTimeout;
		final long now = System.currentTimeMillis();
		final long end = now + remaining;
		synchronized (lock) {
			while (state == ACK_PENDING && remaining > 0) {
				try {
					lock.wait(remaining);
				}
				catch (final InterruptedException e) {}
				remaining = end - System.currentTimeMillis();
			}
		}
		return remaining > 0;
	}

	private void fireConnectionClosed(boolean user, String reason)
	{
		final CloseEvent ce = new CloseEvent(this, user, reason);
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

	private void calcTimeouts(int baudrate)
	{
		// with some serial driver/BCU/OS combinations, the calculated
		// timeouts are just too short, so add some milliseconds just as it fits
		// this little extra time usually doesn't hurt
		final int xTolerance = 5;
		final int iTolerance = 15;
		exchangeTimeout = Math.round((1000f * EXCHANGE_TIMEOUT) / baudrate) + xTolerance;
		idleTimeout = Math.round((1000f * IDLE_TIMEOUT) / baudrate) + iTolerance;
	}
	
	private static byte checksum(byte[] data, int offset, int length)
	{
		byte chk = 0;
		for (int i = 0; i < length; ++i)
			chk += data[offset + i];
		return chk;
	}

	private static String defaultPortPrefix()
	{
		return System.getProperty("os.name").toLowerCase().indexOf("windows") > -1
			? "\\\\.\\COM" : "/dev/ttyS";
	}

	private final class Receiver extends Thread
	{
		private volatile boolean quit;
		private int lastChecksum;
		
		Receiver()
		{
			super("FT1.2 receiver");
			setDaemon(true);
		}
	
		public void run()
		{
			try {
				while (!quit) {
					final int c = is.read();
					if (c > -1) {
						if (c == ACK) {
							if (state == ACK_PENDING)
								synchronized (lock) {
									state = OK;
									lock.notify();
								}
						}
						else if (c == START)
							readFrame();
						else if (c == START_FIXED)
							readShortFrame();
						else
							logger.trace("received unexpected start byte 0x" +
								Integer.toHexString(c) + " - ignored");
					}
				}
			}
			catch (final IOException e) {
				if (!quit)
					close(false, "receiver communication failure");
			}
		}
	
		void quit()
		{
			quit = true;
			interrupt();
			if (currentThread() == this)
				return;
			try {
				join(50);
			}
			catch (final InterruptedException e) {}
		}
	
		private boolean readShortFrame() throws IOException
		{
			final byte[] buf = new byte[3];
			if (is.read(buf) == 3 && buf[0] == buf[1] && (buf[2] & 0xff) == END) {
				// for our purposes (reset and status), FRAMECOUNT_VALID is never set
				if ((buf[0] & 0x30) == 0) {
					sendAck();
					final int fc = buf[0] & 0x0f;
					logger.trace("received " + (fc == RESET ? "reset" : fc == REQ_STATUS
						? "status" : "unknown function code "));
					return true;
				}
			}
			return false;
		}
		
		private boolean readFrame() throws IOException
		{
			final int len = is.read();
			final byte[] buf = new byte[len + 4];
			// read rest of frame, check header, ctrl, and end tag
			final int read = is.read(buf);
			if (read == (len + 4) && (buf[0] & 0xff) == len
				&& (buf[1] & 0xff) == START && (buf[len + 3] & 0xff) == END) {
				final byte chk = buf[buf.length - 2];
				if (!checkCtrlField(buf[2] & 0xff, chk))
					;
				else if (checksum(buf, 2, len) != chk)
					logger.warn("invalid checksum in frame "
						+ DataUnitBuilder.toHex(buf, " "));
				else {
					sendAck();
					lastChecksum = chk;
					rcvFrameCount ^= FRAMECOUNT_BIT;
					final byte[] ldata = new byte[len - 1];
					for (int i = 0; i < ldata.length; ++i)
						ldata[i] = buf[3 + i];

					fireFrameReceived(ldata);
					return true;
				}
			}
			else
				logger.warn("invalid frame, discarded " + read + " bytes: "
					+ DataUnitBuilder.toHex(buf, " "));
			return false;
		}
		
		private boolean checkCtrlField(int c, byte chk)
		{
			if ((c & (DIR_FROM_BAU | INITIATOR)) != (DIR_FROM_BAU | INITIATOR)) {
				logger.warn("unexpected ctrl field 0x" + Integer.toHexString(c));
				return false;
			}
			if ((c & FRAMECOUNT_VALID) == FRAMECOUNT_VALID) {
				if ((c & FRAMECOUNT_BIT) != rcvFrameCount) {
					// ignore repeated frame
					if (chk == lastChecksum) {
						logger.trace("framecount and checksum indicate a repeated " +
							"frame - ignored");
						return false;
					}
					// protocol discrepancy (merten instabus coupler)
					logger.warn("toggle frame count bit");
					rcvFrameCount ^= FRAMECOUNT_BIT;
				}
			}
			if ((c & 0x0f) == USER_DATA && (c & FRAMECOUNT_VALID) == 0)
				return false;
			return true;
		}
	
		/**
		 * Fires a frame received event ({@link KNXListener#frameReceived(FrameEvent)})
		 * for the supplied EMI2 <code>frame</code>.
		 * 
		 * @param frame the EMI2 L-data frame to generate the event for
		 */
		private void fireFrameReceived(byte[] frame)
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
	}
}
