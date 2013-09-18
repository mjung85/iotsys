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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import obix.Abstime;
import obix.Contract;
import obix.Obj;
import obix.Str;
import obix.Uri;
import obix.contracts.About;

public class AboutImpl extends Obj implements About {
	private static final Logger log = Logger.getLogger(AboutImpl.class.getName());
	private Str obixVersion = new Str("oBIX 1.1 WD 06");
	private Str serverName  = new Str("localhost");
	private Abstime serverTime = new Abstime(System.currentTimeMillis());
	private Abstime serverBootTime = new Abstime(System.currentTimeMillis());
	private Str vendorName = new Str("Automation Systems Group, Vienna University of Technology");
	private Uri vendorURL = new Uri("http://www.auto.tuwien.ac.at");
	private Str productName = new Str("IoTSyS gateway");
	private Str productVersion = new Str("0.1");
	private Uri productURL = new Uri("http://code.google.com/p/iotsys");
	
	public AboutImpl() {
		this.setHref(new Uri("/obix/about"));
		this.setIs(new Contract("obix:About"));
		try {
			serverName = new Str(java.net.InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		obixVersion.setName("obixVersion");
		serverName.setName("serverName");
		serverTime.setName("serverTime");
		serverBootTime.setName("serverBootTime");
		vendorName.setName("vendorName");
		vendorURL.setName("vendorUrl");
		productName.setName("productName");
		productVersion.setName("productVersion");
		productURL.setName("productUrl");
		
		try {
			StringBuffer addresses = new StringBuffer();
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while(networkInterfaces.hasMoreElements()){
				List<InterfaceAddress> interfaceAddresses = networkInterfaces.nextElement().getInterfaceAddresses();
				for(InterfaceAddress iface : interfaceAddresses) {
					addresses.append(iface.getAddress().getHostAddress()).append(";");
				}
			}
			serverName.set(serverName.get() + " - " + addresses.toString());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.add(obixVersion);
		this.add(serverName);
		this.add(serverTime);
		this.add(serverBootTime);
		this.add(vendorName);
		this.add(vendorURL);
		this.add(productName);
		this.add(productVersion);
		this.add(productURL);
	}

	public Str obixVersion() {		
		return obixVersion;
	}

	@Override
	public Str serverName() {
		return serverName;
	}

	@Override
	public Abstime serverTime() {
		serverTime.set(System.currentTimeMillis(), TimeZone.getDefault());
		return serverTime;
	}

	@Override
	public Abstime serverBootTime() {
		return serverBootTime;
	}

	@Override
	public Str vendorName() {
		return vendorName;
	}

	@Override
	public Uri vendorUrl() {
		return vendorURL;
	}

	@Override
	public Str productName() {
		return productName;
	}

	@Override
	public Str productVersion() {
		return productVersion;
	}

	@Override
	public Uri productUrl() {
		return productURL;
	}
	
	@Override 
	public void refreshObject(){
		serverTime.set(System.currentTimeMillis(), TimeZone.getDefault());
	}

}
