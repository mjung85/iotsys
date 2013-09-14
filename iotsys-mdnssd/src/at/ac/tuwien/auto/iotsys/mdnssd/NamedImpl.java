/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/
package at.ac.tuwien.auto.iotsys.mdnssd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jmdns.impl.DNSIncoming;
import javax.jmdns.impl.DNSOutgoing;
import javax.jmdns.impl.DNSQuestion;
import javax.jmdns.impl.DNSRecord;
import javax.jmdns.impl.constants.DNSConstants;
import javax.jmdns.impl.constants.DNSRecordClass;
import javax.jmdns.impl.constants.DNSRecordType;
import javax.jmdns.utils.MdnsUtils;

import at.ac.tuwien.auto.iotsys.commons.Named;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class NamedImpl implements Named {

	public static final String AUTHORITATIVE_DOMAIN = PropertiesLoader.getInstance().getProperties()
			.getProperty("authDomain", "iotsys.auto.tuwien.ac.at.");
	public static final String AUTHORITATIVE_NAME_SERVER = PropertiesLoader.getInstance().getProperties()
			.getProperty("authNs", "ns.iotsys.auto.tuwien.ac.at.");
	public static final String AUTHORITATIVE_NAME_SERVER_ADDR = PropertiesLoader.getInstance().getProperties()
			.getProperty("authNsAddr", "143.248.56.162");
	public static final String AUTHORITATIVE_NAME_SERVER_ADDR6 = PropertiesLoader.getInstance().getProperties()
			.getProperty("authNsAddr6", "2002:8ff8:38a2::8ff8:38a2");
	public static final String AUTHORITATIVE_NAME_REVERSE = PropertiesLoader.getInstance().getProperties()
			.getProperty("authNr", "143.in-addr.arpa.");
	public static final String AUTHORITATIVE_NAME_ADDR_REVERSE = PropertiesLoader.getInstance().getProperties()
			.getProperty("authNar", "162.248.56.143.in-addr.arpa.");
	static final int AUTHORITATIVE_STACK = 46;

	UDPListener ul;

	public NamedImpl() {
		ul = new UDPListener(this);
	}
	
	@Override
	public boolean isStart(){
		return ul.isAlive();
	}
	
	@Override
	public void startNamedService() {
		ul.start();
	}

	@Override
	public void stopNamedService() {
		ul.stopThread();
		try {
			ul.join();
		} catch (InterruptedException ex) {
			Logger.getLogger(NamedImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private class UDPListener extends Thread {

		DatagramSocket sock;// = new DatagramSocket(port, addr);
		public static final int IPv4 = 1;
		public static final int IPv6 = 2;
		boolean stop = true;
		Logger logger = Logger.getLogger(UDPListener.class.getName());

		public UDPListener(NamedImpl sn) {
			try {
				sock = new DatagramSocket(53, MdnsUtils.getByAddress("0.0.0.0"));
			} catch (SocketException ex) {
				Logger.getLogger(NamedImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		public void stopThread() {
			stop = true;
			sock.close();
			sock = null;
		}

		@Override
		public void run() {
			stop = false;
			byte buf[] = new byte[DNSConstants.MAX_MSG_ABSOLUTE];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			while (!stop) {
				packet.setLength(buf.length);
				try {
					this.sock.receive(packet);// blocked
					DNSIncoming msg = new DNSIncoming(packet);
					System.out.println(msg.print(true));
					if (msg.isQuery()) {
						handleQuery(msg);
					}
				} catch (IOException ex) {
					Logger.getLogger(NamedImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

		private void handleQuery(DNSIncoming msg) {

			ArrayList<DNSRecord> ansRcrdList = new ArrayList<DNSRecord>();
			ArrayList<DNSRecord> authRcrdList = new ArrayList<DNSRecord>();
			ArrayList<DNSRecord> addtnRcrdList = new ArrayList<DNSRecord>();

			DNSOutgoing out = new DNSOutgoing(DNSConstants.FLAGS_QR_RESPONSE | DNSConstants.FLAGS_AA, false,
					msg.getSenderUDPPayload());

			Collection<? extends DNSQuestion> questions = msg.getQuestions();
			Iterator i = questions.iterator();
			while (i.hasNext()) {
				DNSQuestion aQuestion = (DNSQuestion) i.next();
				String requestedName = aQuestion.getName();

				if (aQuestion.getRecordType() == DNSRecordType.TYPE_PTR) {
					ansRcrdList.add(new DNSRecord.Pointer(AUTHORITATIVE_NAME_ADDR_REVERSE, DNSRecordClass.CLASS_IN,
							true, DNSConstants.DNS_TTL, AUTHORITATIVE_DOMAIN));
					authRcrdList.add(createAuthRecord(true));
					break;
				} else {
					if (!((requestedName.endsWith("." + AUTHORITATIVE_DOMAIN)) || (requestedName
							.equals(AUTHORITATIVE_DOMAIN)))) {
						out = new DNSOutgoing(DNSConstants.FLAGS_QR_RESPONSE | DNSConstants.FLAGS_RF, false,
								msg.getSenderUDPPayload());
						break;
					}

					DNSRecord ansRcrd = createAnsRecord(requestedName,
							(aQuestion.getRecordType() == DNSRecordType.TYPE_A) ? false : true);
					if (ansRcrd != null) {
						ansRcrdList.add(ansRcrd);
					}
					authRcrdList.add(createAuthRecord(false));
				}

				// Attach questions to response
				try {
					out.addQuestion(aQuestion);
				} catch (IOException ex) {
					Logger.getLogger(NamedImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			if (!out.isRefused()) {
				// Acceptable, in-domain request
				if (ansRcrdList.isEmpty()) {
					// Not exist
					out = new DNSOutgoing(DNSConstants.FLAGS_QR_RESPONSE | DNSConstants.FLAGS_NE, false,
							msg.getSenderUDPPayload());
				} else {
					addtnRcrdList.add(createAddtnRecord(AUTHORITATIVE_NAME_SERVER, AUTHORITATIVE_NAME_SERVER_ADDR,
							false));
					addtnRcrdList.add(createAddtnRecord(AUTHORITATIVE_NAME_SERVER, AUTHORITATIVE_NAME_SERVER_ADDR6,
							true));

					try {
						for (DNSRecord r : ansRcrdList) {
							out.addAnswer(msg, r);
						}
						for (DNSRecord r : authRcrdList) {
							out.addAuthorativeAnswer(r);
						}
						for (DNSRecord r : addtnRcrdList) {
							out.addAdditionalAnswer(msg, r);
						}
					} catch (IOException ex) {
						Logger.getLogger(NamedImpl.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

			out.setId(msg.getId());
			byte[] message = out.data();
			DatagramPacket packet = new DatagramPacket(message, message.length, msg.getPacket().getAddress(), msg
					.getPacket().getPort());
			try {
				sock.send(packet);
			} catch (IOException ex) {
				Logger.getLogger(NamedImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		private DNSRecord createAnsRecord(String requestedName, boolean ipv6) {
			DNSRecord result = null;

			// look up
			String resolved = MdnsResolverImpl.getInstance().resolve(requestedName); // virtualLight.iotsys.auto.tuwien.ac.at.
			if (resolved != null) {
				if (!ipv6) {
					// result = new DNSRecord.IPv4Address(requestedName,
					// DNSRecordClass.CLASS_IN, DNSRecordClass.UNIQUE,
					// DNSConstants.DNS_TTL, getByAddress(resolved));
				} else {
					result = new DNSRecord.IPv6Address(requestedName, DNSRecordClass.CLASS_IN, DNSRecordClass.UNIQUE,
							DNSConstants.DNS_TTL, MdnsUtils.getByAddress(resolved));
				}
			}

			return result;

		}

		private DNSRecord createAuthRecord(boolean reverse) {
			if (reverse) {
				return new DNSRecord.Authoritative(AUTHORITATIVE_NAME_REVERSE, DNSRecordClass.CLASS_IN,
						DNSRecordClass.UNIQUE, DNSConstants.DNS_TTL, AUTHORITATIVE_NAME_SERVER);
			}
			return new DNSRecord.Authoritative(AUTHORITATIVE_DOMAIN, DNSRecordClass.CLASS_IN, DNSRecordClass.UNIQUE,
					DNSConstants.DNS_TTL, AUTHORITATIVE_NAME_SERVER);
		}

		private DNSRecord createAddtnRecord(String serverName, String serverAddr, boolean ipv6) {
			if (!ipv6) {
				return new DNSRecord.IPv4Address(serverName, DNSRecordClass.CLASS_IN, DNSRecordClass.UNIQUE,
						DNSConstants.DNS_TTL, MdnsUtils.getByAddress(serverAddr));
			}
			return new DNSRecord.IPv6Address(serverName, DNSRecordClass.CLASS_IN, DNSRecordClass.UNIQUE,
					DNSConstants.DNS_TTL, MdnsUtils.getByAddress(serverAddr));
		}
	}
}
