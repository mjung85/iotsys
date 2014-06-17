/*******************************************************************************
 * Copyright (c) 2014 Institute of Computer Aided Automation, Automation Systems
 * Group, TU Wien. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * Institute nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

public class MCastForwarder {
	private HashSet<String> interfaces = null;
	private Logger log = Logger.getLogger(MCastForwarder.class.getName());
	private int port = 5683;
	private final Hashtable<PcapIf, Pcap> pcaps = new Hashtable<PcapIf, Pcap>();

	// specify interfaces between which multicasts should be forwarded through
	// command line args, e.g. eth0, tun0, ...
	public static void main(String[] args) {
		HashSet<String> interfaces = new HashSet<String>();
		for (int i = 0; i < args.length; i++) {
			interfaces.add(args[i]);
		}
		MCastForwarder mcastForwarder = new MCastForwarder(interfaces);
		mcastForwarder.startForwarding();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			reader.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// test();
	}

	public static void test() {
		JPacket packet = new JMemoryPacket(JProtocol.ETHERNET_ID,
				" 001801bf 6adc0025 4bb7afec 08004500 "
						+ " 0041a983 40004006 d69ac0a8 00342f8c "
						+ " ca30c3ef 008f2e80 11f52ea8 4b578018 "
						+ " ffffa6ea 00000101 080a152e ef03002a "
						+ " 2c943538 322e3430 204e4f4f 500d0a");

		Ip4 ip = packet.getHeader(new Ip4());
		Tcp tcp = packet.getHeader(new Tcp());

		tcp.destination(80);

		ip.checksum(ip.calculateChecksum());
		tcp.checksum(tcp.calculateChecksum());
		packet.scan(Ethernet.ID);

		System.out.println(packet);
	}

	public MCastForwarder(HashSet<String> interfaces) {
		this.interfaces = interfaces;
	}

	public void startForwarding() {
		List<PcapIf> alldevs = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();

		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			log.info("No devs found");
			return;
		}

		int i = 0;
		int pick = 0;

		for (PcapIf device : alldevs) {
			String description = (device.getDescription() != null) ? device
					.getDescription() : "No description available.";
			log.info("" + (i++) + "#: " + device.getName() + " " + description);

			if (interfaces.contains(device.getName())) {
				pick = i - 1;
				startCaptureForInterface(alldevs, pick, device.getName());
			}

		}
	}

	public synchronized void forward(Inet6Address dest, Inet6Address src,
			int udpSrcPort, int udpDestPort, int udpLength, int udpChecksum,
			byte[] payload, String sourceDevice) {

		log.info("Forward " + dest.toString() + "," + src.toString() + " "
				+ payload.length + " bytes.");
		synchronized (pcaps) {
			for (PcapIf device : pcaps.keySet()) {
				if (!device.getName().equals(sourceDevice)) {
					Pcap pcap = pcaps.get(device);
					if (device.getName().startsWith("eth")) {
						int lastId = dest.getAddress().length - 1;
						byte[] packetHeader;
						packetHeader = new byte[] {
								// create ethernet frame
								0x33,
								0x33, // first two octets for mapping IPv6
										// multicast addresses
								// last 4 octets of IPv6 multicast address,
								// FF15::1 --> 0x00 0x00 0x00 0x01
								dest.getAddress()[lastId - 3],
								dest.getAddress()[lastId - 2],
								dest.getAddress()[lastId - 1],
								dest.getAddress()[lastId - 0],
								// mac src address
								//device.getHardwareAddress()[0],
								//device.getHardwareAddress()[1],
								//device.getHardwareAddress()[2],
								//device.getHardwareAddress()[3],
								//device.getHardwareAddress()[4],
								//device.getHardwareAddress()[5],
								0x00,
								0x0d,
								(byte)0xb9,
								0x30,
								0x02,
								0x50,									
								// typefield byte[] typeField = new byte[]{
								(byte) 0x86,
								(byte) 0xdd,
								// ipv6 header, flow label and traffic class
								0x60,
								0x00,
								0x00,
								0x00,
								// payload length + add udp header (4
								// octects)
								(byte) ( ((udpLength + 4) & 0xFF00) >> 8),
								(byte) ((udpLength + 4) & 0xFF),
								// nextHeaderHopLimit
								0x11,
								0x01,
								// src and destination,
								src.getAddress()[0], src.getAddress()[1],
								src.getAddress()[2], src.getAddress()[3],
								src.getAddress()[4], src.getAddress()[5],
								src.getAddress()[6], src.getAddress()[7],
								src.getAddress()[8], src.getAddress()[9],
								src.getAddress()[10], src.getAddress()[11],
								src.getAddress()[12], src.getAddress()[13],
								src.getAddress()[14],
								src.getAddress()[15],
								dest.getAddress()[0],
								dest.getAddress()[1],
								dest.getAddress()[2],
								dest.getAddress()[3],
								dest.getAddress()[4],
								dest.getAddress()[5],
								dest.getAddress()[6],
								dest.getAddress()[7],
								dest.getAddress()[8],
								dest.getAddress()[9],
								dest.getAddress()[10],
								dest.getAddress()[11],
								dest.getAddress()[12],
								dest.getAddress()[13],
								dest.getAddress()[14],
								dest.getAddress()[15],
								// udp header
								(byte) ((udpSrcPort & 0xFF00) >> 8),
								(byte) (udpSrcPort & 0xFF),
								(byte) ((udpDestPort & 0xFF00) >> 8),
								(byte) (udpDestPort & 0xFF),
								(byte) ((udpLength & 0xFF00) >> 8),
								(byte) (udpLength & 0xFF),
								(byte) ((udpChecksum & 0xFF00) >> 8),
								(byte) (udpChecksum & 0xFF), };
						byte[] packet = new byte[packetHeader.length
								+ payload.length];
						System.arraycopy(packetHeader, 0, packet, 0,
								packetHeader.length);
						System.arraycopy(payload, 0, packet,
								packetHeader.length, payload.length);

						JPacket jpacket = new JMemoryPacket(
								JProtocol.ETHERNET_ID, packet);
						jpacket.scan(Ethernet.ID);
						Ethernet ethHeader = jpacket
								.getHeader(new Ethernet());
						// calculate checksum
						ethHeader.checksum(ethHeader.calculateChecksum());

						System.out.println(jpacket);

						if (pcap.isSendPacketSupported()) {
							pcap.sendPacket(packet);
						} else {
							System.err
									.println("Cannot forward packet. PCAP interface "
											+ device.getName()
											+ " + does not support it.");
						}

					} else if (device.getName().startsWith("tun")) { // tunnel
																		// interface
																		// just
																		// send
																		// raw
																		// IP
																		// packet
						byte[] packetHeader;
						packetHeader = new byte[] {
								// ipv6 header, flow label and traffic class
								0x60,
								0x00,
								0x00,
								0x00,
								// payload length + add udp header (4 octects)
								(byte) ( ((udpLength) & 0xFF00) >> 8),
								(byte) ((udpLength) & 0xFF),
								// nextHeaderHopLimit
								0x11,
								0x01,
								// src and destination,
								src.getAddress()[0], src.getAddress()[1],
								src.getAddress()[2], src.getAddress()[3],
								src.getAddress()[4], src.getAddress()[5],
								src.getAddress()[6], src.getAddress()[7],
								src.getAddress()[8], src.getAddress()[9],
								src.getAddress()[10], src.getAddress()[11],
								src.getAddress()[12], src.getAddress()[13],
								src.getAddress()[14], src.getAddress()[15],
								dest.getAddress()[0], dest.getAddress()[1],
								dest.getAddress()[2], dest.getAddress()[3],
								dest.getAddress()[4],
								dest.getAddress()[5],
								dest.getAddress()[6],
								dest.getAddress()[7],
								dest.getAddress()[8],
								dest.getAddress()[9],
								dest.getAddress()[10],
								dest.getAddress()[11],
								dest.getAddress()[12],
								dest.getAddress()[13],
								dest.getAddress()[14],
								dest.getAddress()[15],
								// udp header
								(byte) ((udpSrcPort & 0xFF00) >> 8),
								(byte) (udpSrcPort & 0xFF),
								(byte) ((udpDestPort & 0xFF00) >> 8),
								(byte) (udpDestPort & 0xFF),
								(byte) ((udpLength & 0xFF00) >> 8),
								(byte) (udpLength & 0xFF),
								(byte) ((udpChecksum & 0xFF00) >> 8),
								(byte) (udpChecksum & 0xFF), };
						byte[] packet = new byte[packetHeader.length
								+ payload.length];
						System.arraycopy(packetHeader, 0, packet, 0,
								packetHeader.length);
						System.arraycopy(payload, 0, packet,
								packetHeader.length, payload.length);

//						JPacket jpacket = new JMemoryPacket(
//								JProtocol.ETHERNET_ID, packet);
//						jpacket.scan(Ethernet.ID);
//						Ethernet ethHeader = jpacket.getHeader(new Ethernet());
//						Udp udpHeader = jpacket.getHeader(new Udp());
//						// calculate checksum
//						ethHeader.checksum(ethHeader.calculateChecksum());
//						
//						udpHeader.checksum(udpHeader.calculateChecksum());
					
//						System.out
//								.println("#####################################");
//						System.out
//								.println("#####################################");
//						System.out.println("TUN: " + jpacket);
						if (pcap.isSendPacketSupported()) {
							pcap.sendPacket(packet);
						} else {
							System.err
									.println("Cannot forward packet. PCAP interface "
											+ device.getName()
											+ " + does not support it.");
						}
					}
				}
			}
		}
	}

	private Pcap startCaptureForInterface(List<PcapIf> alldevs, int pick,
			final String ifName) {
		log.info("opening device for pcap: " + alldevs.get(pick).getName());
		PcapIf device = alldevs.get(pick);

		int snaplen = 64 * 1024;
		int flags = Pcap.MODE_PROMISCUOUS;
		int timeout = 10 * 1000;
		StringBuilder errbuf = new StringBuilder();
		final Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags,
				timeout, errbuf);

		synchronized (pcaps) {
			pcaps.put(device, pcap);
		}

		if (pcap == null) {
			log.info("Cannot listen.");
		}
		final MCastHandler<String> mcastHandler = new MCastHandler<String>(
				port, pcap, this, device.getName());

		log.info("Registered mcastHandler handler.");

		Thread packetlistener = new Thread() {

			@Override
			public void run() {
				pcap.loop(0, mcastHandler, "McastHandler - " + ifName);
			}

		};

		packetlistener.setDaemon(true);
		packetlistener.start();
		return pcap;
	}
}

class MCastHandler<String> implements PcapPacketHandler<String> {
	private int port = 5683;
	private static final Logger log = Logger.getLogger(MCastHandler.class
			.getName());

	private Pcap pcap = null;

	private MCastForwarder mcastForwarder = null;
	private java.lang.String devName;

	public MCastHandler(int port, Pcap pcap, MCastForwarder mcastForwarder,
			java.lang.String devName) {
		if (port != 0)
			this.port = port;

		this.pcap = pcap;
		this.mcastForwarder = mcastForwarder;
		this.devName = devName;
	}

	public void nextPacket(PcapPacket packet, String user) {
		System.out.println("Next packet on " + devName);

		if (packet.getHeaderCount() == 1) { // indication for tunnel packet
			log.info("Packet received through PCAP (tunnel packet).");
			// the payload might be directly an ipv6 packet
			if (packet.getByte(0) >> 4 == 6 && packet.size() > 48) { // ipv6
																		// version,
																		// at
																		// least
																		// ipv6
																		// and
																		// udp
																		// header
				byte[] srcAddressByte = new byte[16];
				packet.getByteArray(8, srcAddressByte);
				byte[] destAddress = new byte[16];
				byte[] payload = new byte[packet.size() - 48]; // everything
																// beside IPv6
																// and UDP
																// header
				packet.getByteArray(24, destAddress);
				try {
					Inet6Address srcIpv6 = (Inet6Address) Inet6Address
							.getByAddress(srcAddressByte);
					Inet6Address destIpv6 = (Inet6Address) Inet6Address
							.getByAddress(destAddress);
					// extract udp header (4 byte)
					byte[] destPortBytes = new byte[2];
					packet.getByteArray(42, destPortBytes);

					byte[] srcPortBytes = new byte[2];
					packet.getByteArray(40, srcPortBytes);

					Integer srcPort = srcPortBytes[0] * 256 + srcPortBytes[1];
					Integer destPort = destPortBytes[0] * 256
							+ destPortBytes[1];

					byte[] lengthBytes = new byte[2];
					packet.getByteArray(44, lengthBytes);

					int udpLength = lengthBytes[0] * 256 + lengthBytes[1];

					byte[] checksumBytes = new byte[2];
					packet.getByteArray(46, checksumBytes);

					int checkSum = checksumBytes[0] * 256 + checksumBytes[1];

					packet.getByteArray(48, payload);

					if (destPort == this.port && destIpv6.isMulticastAddress()) {

						log.info("Received multicast message through PCAP (over tunnel adapter)s.");
						mcastForwarder
								.forward(destIpv6, srcIpv6, srcPort, destPort,
										udpLength, checkSum, payload, devName);
					}

				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		int idDLT = JRegistry.mapDLTToId(pcap.datalink());
		packet.scan(idDLT);

		if (packet.hasHeader(Udp.ID) && packet.hasHeader(Ip6.ID)) {
			System.out.println("UDP and IPv6");
			Ip6 ipv6 = packet.getHeader(new Ip6());
			Udp udp = packet.getHeader(new Udp());
			byte[] destination = ipv6.destination();
			Inet6Address dest = null;
			Inet6Address src = null;

			try {
				dest = (Inet6Address) InetAddress.getByAddress(destination);
				src = (Inet6Address) InetAddress.getByAddress(ipv6.source());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (udp.destination() == this.port && dest.isMulticastAddress()) {
				if (packet.hasHeader(Ethernet.ID)) {
					Ethernet ethernet = packet.getHeader(new Ethernet());
					System.out.println("Captured at ethernet: " + packet);
					
				}
				log.info("Received multicast message through PCAP.");
				mcastForwarder.forward(dest, src, udp.source(),
						udp.destination(), udp.length(), udp.checksum(),
						udp.getPayload(), devName);
			}
		}

	}
}
