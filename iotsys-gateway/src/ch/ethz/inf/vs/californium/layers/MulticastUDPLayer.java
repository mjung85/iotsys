package ch.ethz.inf.vs.californium.layers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Arrays;

import ch.ethz.inf.vs.californium.coap.EndpointAddress;
import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.util.Properties;

public class MulticastUDPLayer extends Layer {

	// Members
	// /////////////////////////////////////////////////////////////////////

	// The UDP socket used to send and receive datagrams
	// TODO Use MulticastSocket
	private MulticastSocket socket;

	// The thread that listens on the socket for incoming datagrams
	private ReceiverThread receiverThread;

	// explicitly bound server address
	private InetAddress inetAddress;

	private Inet6Address group;

	private static ThreadLocal<REQUEST_TYPE> uniqueRequestType = new ThreadLocal<REQUEST_TYPE>() {
		@Override
		protected REQUEST_TYPE initialValue() {
			return REQUEST_TYPE.NORMAL_REQUEST;
		}

	};

	private static ThreadLocal<Inet6Address> multicastAddress = new ThreadLocal<Inet6Address>() {
		@Override
		protected Inet6Address initialValue() {
			return null;
		}

	};

	public synchronized static void setMulticastAddress(Inet6Address mAddr) {
		multicastAddress.set(mAddr);
	}

	public synchronized static Inet6Address getMulticastAddress() {
		return multicastAddress.get();
	}

	public synchronized static void setRequestType(REQUEST_TYPE reqType) {
		uniqueRequestType.set(reqType);
	}

	public synchronized static REQUEST_TYPE getRequestType() {
		return uniqueRequestType.get();
	}

	public enum REQUEST_TYPE {
		MULTICAST_REQUEST, NORMAL_REQUEST
	}

	// Inner Classes
	// ///////////////////////////////////////////////////////////////

	class ReceiverThread extends Thread {
		private volatile boolean stop = false;

		public ReceiverThread() {
			super("ReceiverThread");
		}

		@Override
		public void run() {
			setMulticastAddress(MulticastUDPLayer.this.group);
			setRequestType(REQUEST_TYPE.MULTICAST_REQUEST);
			// always listen for incoming datagrams

			while (!stop) {

				// allocate buffer
				byte[] buffer = new byte[Properties.std
						.getInt("RX_BUFFER_SIZE") + 1]; // +1 to check for >
														// RX_BUFFER_SIZE

				// initialize new datagram
				DatagramPacket datagram = new DatagramPacket(buffer,
						buffer.length);

				// receive datagram
				try {
					socket.receive(datagram);
				} catch (IOException e) {
					LOG.severe("Could not receive datagram: " + e.getMessage());
					e.printStackTrace();
					continue;
				}

				// TODO: Dispatch to worker thread
				datagramReceived(datagram);
			}
		}

		public void stopReceiver() {
			stop = true;
		}
	}

	// Constructors
	// ////////////////////////////////////////////////////////////////

	/*
	 * Constructor for a new UDP layer
	 * 
	 * @param port The local UDP port to listen for incoming messages
	 * 
	 * @param daemon True if receiver thread should terminate with main thread
	 */
	public MulticastUDPLayer(int port, boolean daemon,
			Inet6Address ipv6MulticastAddress) throws SocketException {
		// initialize members

		try {
			System.out.println("###### New multicast socket! " + port);

			// join IPv6 Multicast on all interfaces

			// List<InetSocketAddress> multicastSockets = new
			// ArrayList<InetSocketAddress>();

			// InetSocketAddress socketAddress = new InetSocketAddress(group,
			// 5683);
			this.group = ipv6MulticastAddress;

			
			this.socket = new MulticastSocket(5683);
			this.socket.setReuseAddress(true);
			this.socket.joinGroup(group);

			// Enumeration<NetworkInterface> networkInterfaces =
			// NetworkInterface.getNetworkInterfaces();
			// while(networkInterfaces.hasMoreElements()){
			// NetworkInterface nextElement = networkInterfaces.nextElement();
			// try{
			// if(!nextElement.isLoopback() && !nextElement.isPointToPoint() &&
			// nextElement.isUp() && !nextElement.isVirtual())
			// this.socket.joinGroup(socketAddress, nextElement);
			//
			// }
			// catch(Exception e){
			// // fail silently
			// }
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.inetAddress = socket.getLocalAddress();
		this.receiverThread = new ReceiverThread();

		// decide if receiver thread terminates with main thread
		receiverThread.setDaemon(daemon);

		// start listening right from the beginning
		this.receiverThread.start();

	}

	/*
	 * Constructor for a new UDP layer
	 */
	public MulticastUDPLayer() throws SocketException {
		this(0, true, null); // use any available port on the local host machine
	}

	public MulticastUDPLayer(Inet6Address ipv6MulticastAddress)
			throws SocketException {
		this(0, true, ipv6MulticastAddress);
	}

	// Commands
	// ////////////////////////////////////////////////////////////////////

	/*
	 * Decides if the listener thread persists after the main thread terminates
	 * 
	 * @param on True if the listener thread should stay alive after the main
	 * thread terminates. This is useful for e.g. server applications
	 */
	public void setDaemon(boolean on) {
		receiverThread.setDaemon(on);
	}

	// I/O implementation
	// //////////////////////////////////////////////////////////

	@Override
	protected void doSendMessage(Message msg) throws IOException {

		// retrieve payload
		byte[] payload = msg.toByteArray();

		// create datagram
		DatagramPacket datagram = new DatagramPacket(payload, payload.length,
				msg.getPeerAddress().getAddress(), msg.getPeerAddress()
						.getPort());

		// remember when this message was sent for the first time
		// set timestamp only once in order
		// to handle retransmissions correctly
		if (msg.getTimestamp() == -1) {
			msg.setTimestamp(System.nanoTime());
		}

		// send it over the UDP socket
		socket.send(datagram);
	}

	@Override
	protected void doReceiveMessage(Message msg) {
		deliverMessage(msg);
	}

	// Internal
	// ////////////////////////////////////////////////////////////////////

	private void datagramReceived(DatagramPacket datagram) {
		System.out
				.println("DataGram Received!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		if (datagram.getLength() > 0) {

			// get current time
			long timestamp = System.nanoTime();

			// extract message data from datagram
			byte[] data = Arrays.copyOfRange(datagram.getData(),
					datagram.getOffset(), datagram.getLength());

			// create new message from the received data
			Message msg = Message.fromByteArray(data);

			if (msg != null) {
				msg.setNetworkInterface(inetAddress);

				// remember when this message was received
				msg.setTimestamp(timestamp);

				msg.setPeerAddress(new EndpointAddress(datagram.getAddress(),
						datagram.getPort()));

				if (datagram.getLength() > Properties.std
						.getInt("RX_BUFFER_SIZE")) {
					LOG.info(String
							.format("Marking large datagram for blockwise transfer: %s",
									msg.key()));
					msg.requiresBlockwise(true);
				}

				// protect against unknown exceptions
				try {

					receiveMessage(msg);

				} catch (Exception e) {
					StringBuilder builder = new StringBuilder();
					builder.append("Crash: ");
					builder.append(e.getMessage());
					builder.append('\n');
					builder.append("                    ");
					builder.append("Stacktrace for ");
					builder.append(e.getClass().getName());
					builder.append(":\n");
					for (StackTraceElement elem : e.getStackTrace()) {
						builder.append("                    ");
						builder.append(elem.getClassName());
						builder.append('.');
						builder.append(elem.getMethodName());
						builder.append('(');
						builder.append(elem.getFileName());
						builder.append(':');
						builder.append(elem.getLineNumber());
						builder.append(")\n");
					}

					LOG.severe(builder.toString());
				}
			} else {
				LOG.severe("Illeagal datagram received:\n" + data.toString());
			}

		} else {

			LOG.info(String.format("Dropped empty datagram from: %s:%d",
					datagram.getAddress().getHostName(), datagram.getPort()));
		}
	}

	// Queries
	// /////////////////////////////////////////////////////////////////////

	/*
	 * Checks whether the listener thread persists after the main thread
	 * terminates
	 * 
	 * @return True if the listener thread stays alive after the main thread
	 * terminates. This is useful for e.g. server applications
	 */
	public boolean isDaemon() {
		return receiverThread.isDaemon();
	}

	public int getPort() {
		return socket.getLocalPort();
	}

	public String getStats() {
		StringBuilder stats = new StringBuilder();

		stats.append("UDP port: ");
		stats.append(getPort());
		stats.append('\n');
		stats.append("Messages sent:     ");
		stats.append(numMessagesSent);
		stats.append('\n');
		stats.append("Messages received: ");
		stats.append(numMessagesReceived);

		return stats.toString();
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public void close() {
		try {
			this.receiverThread.stopReceiver();

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
