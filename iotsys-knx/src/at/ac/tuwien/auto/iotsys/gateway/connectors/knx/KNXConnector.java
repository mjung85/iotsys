/*
  	Copyright (c) 2013 - IotSyS KNX Connector
 	Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
  	All rights reserved.

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

package at.ac.tuwien.auto.iotsys.gateway.connectors.knx;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.cemi.CEMILData;
import at.ac.tuwien.auto.calimero.datapoint.Datapoint;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.link.KNXNetworkLinkIP;
import at.ac.tuwien.auto.calimero.link.event.NetworkLinkListener;
import at.ac.tuwien.auto.calimero.link.medium.TPSettings;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class KNXConnector extends Connector {
	private String routerHostname;
	private int routerPort;
	private String localIP;

	public String getRouterHostname() {
		return routerHostname;
	}

	public void setRouterHostname(String routerHostname) {
		this.routerHostname = routerHostname;
	}

	public int getRouterPort() {
		return routerPort;
	}

	public void setRouterPort(int routerPort) {
		this.routerPort = routerPort;
	}

	public String getLocalIP() {
		return localIP;
	}

	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}

	// Calimero NG
	private KNXNetworkLinkIP nl;
	private ProcessCommunicator pc;

	private boolean connected = false;

	public static final Logger knxBus = Logger.getLogger("knxbus");

	private final Hashtable<Integer, ArrayList<KNXWatchDog>> watchDogs = new Hashtable<Integer, ArrayList<KNXWatchDog>>();

	private static final Logger log = Logger.getLogger(KNXConnector.class
			.getName());

	public KNXConnector(String routerHostname, int routerPort, String localIP) {
		this.routerHostname = routerHostname;
		this.routerPort = routerPort;
		this.localIP = localIP;
	}

	public void connect() throws UnknownHostException, KNXException {
		synchronized (this) {
			log.info("Connecting KNX tunnel - Tunnel, " + localIP + ", "
					+ routerHostname + ", " + routerPort);

			if ("auto".equals(localIP)) {
				log.finest("auto detetecting local IP.");
				String detectedLocalIP = "";
				int curSimilarity = 0;
				InetAddress routerAddress = InetAddress
						.getByName(routerHostname);

				try {
					Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
							.getNetworkInterfaces();

					while (networkInterfaces.hasMoreElements()) {
						NetworkInterface ni = networkInterfaces.nextElement();
						Enumeration<InetAddress> inetAddresses = ni
								.getInetAddresses();
						while (inetAddresses.hasMoreElements()) {
							InetAddress inetAddress = inetAddresses
									.nextElement();
							String hostAddress = inetAddress.getHostAddress();
							int sim = similarity(
									routerAddress.getHostAddress(), hostAddress);
							if (sim >= curSimilarity) {
								curSimilarity = sim;
								detectedLocalIP = hostAddress;
							}

						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}
				localIP = detectedLocalIP;
				log.finest("detectedLocalIP: " + localIP + " with similarity "
						+ curSimilarity);
			}
			nl = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL,
					new InetSocketAddress(InetAddress.getByName(localIP), 0),
					new InetSocketAddress(
							InetAddress.getByName(routerHostname), routerPort),
					false, new TPSettings(false));
			log.info("My individiual KNX address is: "
					+ nl.getKNXMedium().getDeviceAddress());
			pc = new ProcessCommunicatorImpl(nl);

			connected = true;

			nl.addLinkListener(new KNXListener());
			log.info("KNX tunnel established.");
		}
	}
	
	public ProcessCommunicator getProcessCommunicator(){
		return pc;
	}

	@JsonIgnore
	public boolean isConnected() {
		synchronized (this) {
			return connected;
		}
	}

	public void write(GroupAddress a, boolean value) {
		try {
			if (!isConnected()) {
				return;
			}
			log.finest("Writing " + value + " on " + a);
			pc.write(a, value);
		} catch (KNXException e) {
			e.printStackTrace();
		}
	}

	public void write(GroupAddress a, String value) {
		try {
			if (!isConnected()) {
				return;
			}
			log.finest("Writing " + value + " on " + a);
			pc.write(a, value);
		} catch (KNXException e) {
			e.printStackTrace();
		}
	}
	
	
	public void write(GroupAddress a, int value, String scaled) {
		try {
			if (!isConnected()) {
				return;
			}
			log.finest("Writing " + value + " on " + a);
			pc.write(a, value, scaled);
		} catch (KNXException e) {
			e.printStackTrace();
		}
	}

	public void write(GroupAddress a, float value) {
		try {
			if (!isConnected()) {
				return;
			}
			log.finest("Writing " + value + " on " + a);
			pc.write(a, value);
		} catch (KNXException e) {
			e.printStackTrace();
		}
	}

	public void write(GroupAddress a, double value) {
		try {
			if (!isConnected()) {
				return;
			}
			log.finest("Writing " + value + " on " + a);
			pc.write(a, (float)value);
		} catch (KNXException e) {
			e.printStackTrace();
		}
	}

	public int readInt(GroupAddress a, String scaled) {
		try {
			if (!isConnected()) {
				return 0;
			}
			int ret = pc.readUnsigned(a, scaled);
			log.finest("Read " + ret + " from " + a);
			return ret;
		} catch (KNXException e) {

			e.printStackTrace();
		}
		return 0;
	}

	public String readString(GroupAddress a) {
		try {
			if (!isConnected()) {
				return null;
			}

			String ret = pc.readString(a);
			log.finest("Read " + ret + " from " + a);
			return ret;
		} catch (KNXException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public float readFloat(GroupAddress a) {
		try {
			if (!isConnected()) {
				return 0;
			}

			float ret = pc.readFloat(a);
			log.finest("Read " + ret + " from " + a);
			return ret;
		} catch (KNXException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String read(Datapoint dp) {
		try {
			return pc.read(dp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void write(Datapoint dp, String value) {
		try {
			pc.write(dp, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public boolean readBool(GroupAddress a) {
		try {
			if (!isConnected()) {
				return false;
			}
			boolean ret = pc.readBool(a);
			log.finest("Read " + ret + " from " + a);
			return ret;
		} catch (KNXException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void disconnect() {
		log.info("Disconnecting KNX tunnel.");
		if (isConnected()) {
			nl.close();
		}
	}

	public void addWatchDog(GroupAddress observation, KNXWatchDog knxWatchDog) {
		synchronized (watchDogs) {
			if (!watchDogs.containsKey(observation.getRawAddress())) {
				watchDogs.put(observation.getRawAddress(),
						new ArrayList<KNXWatchDog>());
			}
			log.finest("Adding watchdog for address "
					+ observation.getRawAddress());
			watchDogs.get(observation.getRawAddress()).add(knxWatchDog);
		}
	}

	class KNXListener implements NetworkLinkListener {

		@Override
		public void indication(at.ac.tuwien.auto.calimero.FrameEvent e) {
			CEMILData data = (CEMILData) e.getFrame();

			GroupAddress target = (GroupAddress) data.getDestination(); // getDestinationAddress();
			log.info("Received frame for " + target + " from " + data.getSource());
			
			synchronized (watchDogs) {

				if (watchDogs.containsKey(target.getRawAddress())) {
					for (KNXWatchDog dog : watchDogs
							.get(target.getRawAddress())) {
						dog.notifyWatchDog(data.getPayload());// getData());
					}
				}
			}
		}

		@Override
		public void linkClosed(CloseEvent e) {

		}

		@Override
		public void confirmation(at.ac.tuwien.auto.calimero.FrameEvent e) {

		}
	}

	private int similarity(String s1, String s2) {
		int i = 0;
		for (i = 0; i < Math.min(s1.length(), s2.length()); i++) {
			if (s1.charAt(i) != s2.charAt(i)) {
				break;
			}
		}
		return i;
	}

	public void addNetworkListener(NetworkLinkListener listener) {
		nl.addLinkListener(listener);
	}

	public void removeNetworkListener(NetworkLinkListener listener) {
		nl.removeLinkListener(listener);
	}
	
	@Override
	public boolean isCoap() {
		return false;
	}

}
