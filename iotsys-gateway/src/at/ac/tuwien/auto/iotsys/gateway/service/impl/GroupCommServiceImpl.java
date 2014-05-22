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

package at.ac.tuwien.auto.iotsys.gateway.service.impl;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.CommunicatorFactory;
import ch.ethz.inf.vs.californium.coap.Message.messageType;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.layers.MulticastUDPLayer;
import ch.ethz.inf.vs.californium.layers.MulticastUDPLayer.REQUEST_TYPE;
import obix.Bool;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.io.ObixEncoder;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.gateway.service.GroupCommService;
import at.ac.tuwien.auto.iotsys.gateway.util.EXIEncoder;

/**
 * This class takes care for the group communication.
 */
public class GroupCommServiceImpl implements GroupCommService {
	private static final Logger log = Logger
			.getLogger(GroupCommServiceImpl.class.getName());

	private final static GroupCommServiceImpl instance = new GroupCommServiceImpl();

	private static final Hashtable<Inet6Address, Hashtable<String, Obj>> groupObjectPerAddress = new Hashtable<Inet6Address, Hashtable<String, Obj>>();
	private static final Hashtable<Inet6Address, Hashtable<String, Obj>> receiverObjectPerAddress = new Hashtable<Inet6Address, Hashtable<String, Obj>>();

	private boolean MCAST_ENABLED = true;
	private boolean INTERNAL_NOTFICATION = true;

	private GroupCommServiceImpl() {
		MCAST_ENABLED = Boolean.parseBoolean(PropertiesLoader.getInstance()
				.getProperties().getProperty("iotsys.gateway.mcast", "true"));
		INTERNAL_NOTFICATION = Boolean.parseBoolean(PropertiesLoader
				.getInstance().getProperties()
				.getProperty("iotsys.gateway.internalNotification", "true"));
	}

	public static GroupCommService getInstance() {
		return instance;
	}

	@Override
	public void handleRequest(Inet6Address group, Obj payload) {

		log.finest("Handle request for " + group + ", " + payload);
		synchronized (groupObjectPerAddress) {
			Hashtable<String, Obj> groupObjects = groupObjectPerAddress
					.get(group);

			if (groupObjects != null) {
				for (Obj obj : groupObjects.values()) {
					payload.setHref(obj.getHref());
					log.finest("Writing on " + obj.getHref());
					obj.writeObject(payload);
				}
			} else {
				log.info("No group objects found!");
			}
		}
		
		// for coap proxy objects the values should be updated if a write on the group address is seen
		synchronized (receiverObjectPerAddress) {
			Hashtable<String, Obj> groupObjects = receiverObjectPerAddress
					.get(group);

			if (groupObjects != null) {
				for (Obj obj : groupObjects.values()) {
					payload.setHref(obj.getHref());
					log.finest("Writing on " + obj.getHref());
					obj.writeObject(payload);
				}
			} else {
				log.info("No group objects found as receiver!");
			}
		}
	}

	@Override
	public void registerObject(Inet6Address group, Obj obj) {
		synchronized (groupObjectPerAddress) {
			Hashtable<String, Obj> groupObjects = groupObjectPerAddress
					.get(group);

			if (groupObjects != null) {
				groupObjects.put(obj.getFullContextPath(), obj);
			} else {
				// we need to create a multicast socket for the specified port
				try {
					CommunicatorFactory.getInstance().getCommunicator().getMultiInterfaceUDPLayer()
							.openMulticastSocket(group);
					Hashtable<String, Obj> newGroupObjects = new Hashtable<String, Obj>();
					groupObjectPerAddress.put(group, newGroupObjects);
					newGroupObjects.put(obj.getFullContextPath(), obj);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void unregisterObject(Inet6Address group, Obj obj) {
		synchronized (groupObjectPerAddress) {
			Hashtable<String, Obj> groupObjects = groupObjectPerAddress
					.get(group);

			if (groupObjects != null) {
				groupObjects.remove(obj.getFullContextPath());

				if (groupObjects.size() == 0) {
					// close group comm socket
					try {
						CommunicatorFactory.getInstance().getCommunicator().getMultiInterfaceUDPLayer()
								.closeMulticastSocket(group);
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					groupObjectPerAddress.remove(group);
				}
			}
		}
	}

	@Override
	public void sendUpdate(final Inet6Address group, final Obj state) {

		log.finest("Sending new state of object " + state + " to group "
				+ group.getHostAddress());

		// notify internal group objects
		if (INTERNAL_NOTFICATION) {
			// if(MulticastUDPLayer.getRequestType() !=
			// REQUEST_TYPE.LOCAL_REQUEST){
			// Thread run = new Thread(){
			// public void run(){
//			MulticastUDPLayer.setRequestType(REQUEST_TYPE.LOCAL_REQUEST);
			GroupCommServiceImpl.this.handleRequest(group, state);
			// }
			// };
			// run.start();
		}

		if (MCAST_ENABLED) {
			PUTRequest putRequest = new PUTRequest();
			putRequest.setType(messageType.NON);
			putRequest.setURI("coap://[" + group.getHostAddress() + "]:5684/");

			if (state instanceof Bool) {
				Bool bool = (Bool) state;
				Bool b = new Bool();
				b.set(bool.get());
				try {
					byte[] payload = EXIEncoder.getInstance().toBytes(b, true);
					// work around application octet stream
					putRequest
							.setContentType(MediaTypeRegistry.APPLICATION_OCTET_STREAM);
					putRequest.setPayload(payload);
				} catch (Exception e) {
					// fall back to XML encoding
					e.printStackTrace();
					String payload = ObixEncoder.toString(b);
					putRequest.setPayload(payload);
				}

			} else if (state instanceof Real) {
				Real real = (Real) state;
				Real r = new Real();
				r.set(real.get());
				try {
					byte[] payload = EXIEncoder.getInstance().toBytes(r, true);
					// work around application octet stream
					putRequest
							.setContentType(MediaTypeRegistry.APPLICATION_OCTET_STREAM);
					putRequest.setPayload(payload);
				} catch (Exception e) {
					// fall back to XML encoding
					e.printStackTrace();
					String payload = ObixEncoder.toString(r);
					putRequest.setPayload(payload);
				}
			} else if (state instanceof Int) {
				Int intObj = (Int) state;
				Int i = new Int();
				i.set(intObj.get());
				try {
					byte[] payload = EXIEncoder.getInstance().toBytes(i, true);
					// work around application octet stream
					putRequest
							.setContentType(MediaTypeRegistry.APPLICATION_OCTET_STREAM);
					putRequest.setPayload(payload);
				} catch (Exception e) {
					// fall back to XML encoding
					e.printStackTrace();
					String payload = ObixEncoder.toString(i);
					putRequest.setPayload(payload);
				}
			}

			putRequest.enableResponseQueue(false);
			try {
				putRequest.execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void registerAsReceiver(Inet6Address group, Obj obj) {
		synchronized (receiverObjectPerAddress) {
			Hashtable<String, Obj> groupObjects = receiverObjectPerAddress
					.get(group);

			if (groupObjects != null) {
				groupObjects.put(obj.getFullContextPath(), obj);
			} else {
				// we need to create a multicast socket for the specified port
				try {
					CommunicatorFactory.getInstance().getCommunicator().getMultiInterfaceUDPLayer()
							.openMulticastSocket(group);
					Hashtable<String, Obj> newGroupObjects = new Hashtable<String, Obj>();
					receiverObjectPerAddress.put(group, newGroupObjects);
					newGroupObjects.put(obj.getFullContextPath(), obj);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	@Override
	public void unregisterReceiverObject(Inet6Address group, Obj obj) {
		synchronized (receiverObjectPerAddress) {
			Hashtable<String, Obj> groupObjects = receiverObjectPerAddress
					.get(group);

			if (groupObjects != null) {
				groupObjects.remove(obj.getFullContextPath());

				if (groupObjects.size() == 0) {
					// close group comm socket
					try {
						CommunicatorFactory.getInstance().getCommunicator().getMultiInterfaceUDPLayer()
								.closeMulticastSocket(group);
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					receiverObjectPerAddress.remove(group);
				}
			}
		}
		
	}
}
