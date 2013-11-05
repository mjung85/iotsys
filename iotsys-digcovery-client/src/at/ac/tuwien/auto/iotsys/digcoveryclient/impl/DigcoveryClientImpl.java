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

package at.ac.tuwien.auto.iotsys.digcoveryclient.impl;

import java.io.IOException;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.DELETERequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Response;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.digcoveryclient.DigcoveryClient;

public class DigcoveryClientImpl implements DigcoveryClient {
	private static final String DIGCOVERY_ENDPOINT = PropertiesLoader
			.getInstance()
			.getProperties()
			.getProperty("iotsys.gateway.digcovery.endpoint",
					"coap://[2001:720:1710:10::1000]:5683/dig");

	private static final Logger log = Logger
			.getLogger(DigcoveryClientImpl.class.getName());

	@Override
	public void registerDevice(String ep, String domain, String addr,
			String protocol, String port, String latitute, String longitute,
			String cityName) {
		StringBuilder queryString = new StringBuilder(DIGCOVERY_ENDPOINT);
		log.info("Digcovery client registering devices: " + ep + ", domain: "
				+ domain + ", addr: " + addr + ", protocol: " + protocol
				+ ", port: " + port + latitute + ", longitute: " + longitute
				+ ", cityName: " + cityName);

		if (ep == null || ep.length() == 0) {
			log.severe("Endpoint parameter is mandatory - stopping device registration.");
			return;
		}

		queryString.append("?ep=" + ep);

		if (domain != null && domain.length() > 0) {
			queryString.append("&d=" + domain);
		}

		if (latitute != null && latitute.length() > 0) {
			queryString.append("&lat=" + latitute);
		}

		if (longitute != null && longitute.length() > 0) {
			queryString.append("&long=" + longitute);
		}

		if (cityName != null && cityName.length() > 0) {
			queryString.append("&z=" + cityName);
		}

		if (protocol != null && protocol.length() > 0) {
			queryString.append("&proto=" + protocol);
		}

		if (port != null && port.length() > 0) {
			queryString.append("&port=" + port);
		}

		if (addr != null && addr.length() > 0) {
			queryString.append("&addr=" + addr);
		}

		PUTRequest putRequest = new PUTRequest();

		log.info("Registering device with request: " + queryString);

		putRequest.setURI(queryString.toString());

		putRequest.enableResponseQueue(true);

		// putRequest.send();

		try {
			putRequest.execute();

			// receive response
			log.info("Receiving response...");
			Response response = null;
			try {
				response = putRequest.receiveResponse();
			} catch (InterruptedException e) {
				System.err.println("Failed to receive response: "
						+ e.getMessage());
			}

			// output response
			if (response != null) {
				response.prettyPrint();
				log.info("Time elapsed (ms): " + response.getRTT());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void unregisterDevice(String ep, String domain) {
		if (domain == null || domain.length() == 0 || ep == null
				|| ep.length() == 0) {
			log.severe("Domain parameter is mandatory - cannot delete device registration.");
			return;
		}
		
		log.info("Unregistering at digcovery ep: " + ep + " domain: " + domain);

		StringBuilder queryString = new StringBuilder(DIGCOVERY_ENDPOINT);

		queryString.append("?target=2&ep=" + ep);

		if (domain != null && domain.length() > 0) {
			queryString.append("&d=" + domain);
		}

		DELETERequest delRequest = new DELETERequest();
		delRequest.enableResponseQueue(true);
		delRequest.setURI(queryString.toString());

		try {
			log.info("Delete request: " + queryString.toString());
			delRequest.execute();

			// receive response
			log.info("Receiving response...");
			Response response = null;
			try {
				response = delRequest.receiveResponse();
			} catch (InterruptedException e) {
				System.err.println("Failed to receive response: "
						+ e.getMessage());
			}

			// output response
			if (response != null) {
				response.prettyPrint();
				log.info("Time elapsed (ms): " + response.getRTT());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unregisterDomain(String domain) {
		if (domain == null || domain.length() == 0) {
			log.severe("Domain parameter is mandatory - cannot delete device registration.");
			return;
		}

		StringBuilder queryString = new StringBuilder(DIGCOVERY_ENDPOINT);

		queryString.append("?target=1&d=" + domain);

		if (domain != null && domain.length() > 0) {
			queryString.append("&d=" + domain);
		}

		DELETERequest delRequest = new DELETERequest();
		delRequest.enableResponseQueue(true);
		delRequest.setURI(queryString.toString());

		try {
			delRequest.execute();

			// receive response
			log.info("Receiving response...");
			Response response = null;
			try {
				response = delRequest.receiveResponse();
			} catch (InterruptedException e) {
				System.err.println("Failed to receive response: "
						+ e.getMessage());
			}

			// output response
			if (response != null) {
				response.prettyPrint();
				log.info("Time elapsed (ms): " + response.getRTT());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
