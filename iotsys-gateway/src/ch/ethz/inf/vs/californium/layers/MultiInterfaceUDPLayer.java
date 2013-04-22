/*******************************************************************************
 * Copyright (c) 2012, Institute for Pervasive Computing, ETH Zurich.
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
 * This file is part of the Californium (Cf) CoAP framework.
 ******************************************************************************/

package ch.ethz.inf.vs.californium.layers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import ch.ethz.inf.vs.californium.coap.Message;
import static java.lang.System.out;

/**
 * UDP layer that is aware of multiple network interfaces
 * 
 * @author Markus Jung
 * 
 */
public class MultiInterfaceUDPLayer extends Layer {

	private int port = 0;

	private Hashtable<InetAddress, UDPLayer> udplayers = new Hashtable<InetAddress, UDPLayer>();

	private UDPLayer defaultUDPLayer = null;

	public MultiInterfaceUDPLayer() throws SocketException {
		this(0, true);
	}

	public MultiInterfaceUDPLayer(int port, boolean runAsDaemon)
			throws SocketException {
		this.port = port;

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
				.getNetworkInterfaces();
		defaultUDPLayer = new UDPLayer();
			
		defaultUDPLayer.registerReceiver(this);
		
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface iface = networkInterfaces.nextElement();
			
			
			Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
			for (InetAddress inetAddress : Collections.list(inetAddresses)) {

				
				try{
					UDPLayer udpLayer = new UDPLayer(inetAddress, port, runAsDaemon);
					udpLayer.registerReceiver(this);
					udplayers.put(inetAddress, udpLayer);
				}
				catch(Exception e){
					// do nothing. may conflict with default UDP layer
				}							
			}
		}
	}

	@Override
	protected void doSendMessage(Message msg) throws IOException {
		if (msg.getNetworkInterface() == null || udplayers.get(msg.getNetworkInterface()) == null) {
			msg.setNetworkInterface(defaultUDPLayer.getInetAddress());
			defaultUDPLayer.sendMessage(msg);
		} else {		
			
			udplayers.get(msg.getNetworkInterface()).sendMessage(msg);
		}
	}

	@Override
	protected void doReceiveMessage(Message msg) {
		deliverMessage(msg);
	}

	public int getPort() {
		return port;
	}
}
