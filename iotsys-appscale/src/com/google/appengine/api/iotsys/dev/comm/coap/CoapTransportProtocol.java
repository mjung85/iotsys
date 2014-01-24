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

package com.google.appengine.api.iotsys.dev.comm.coap;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.EndpointAddress;
import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;

import com.google.appengine.api.iotsys.dev.comm.TransportProtocol;
import com.google.appengine.api.iotsys.util.IotLoggingUtil;

public class CoapTransportProtocol extends TransportProtocol {

	private static final Logger logger = Logger
			.getLogger(CoapTransportProtocol.class.getName());

	private SynchronousCoapChannel channel;

	public CoapTransportProtocol() throws IOException {
		super();
		channel = SynchronousCoapChannelFactory.getChannel();
	}

	private Message getResponse(int method, String host, int port, String href,
			byte[] payload, int contentType) throws IOException {

		InetAddress address = InetAddress.getByName(host);

		Request request = Request.getRequestForMethod(method);
		request.setContentType(contentType);
		request.setPeerAddress(new EndpointAddress(address, port));
		request.setUriPath(href);
		if(payload != null) {
			request.setPayload(payload);
		}
		Message response = channel.sendRequest(request);
		return response;
	}

	@Override
	public byte[] sendGetRequestImpl(String host, int port, String href,
			int contentType) throws IOException {
		logger.info("sending get request to " + host + ":" + port + "/" + href);
		Message response = getResponse(CodeRegistry.METHOD_GET, host, port,
				href, null, contentType);
		if (response == null) {
			return null;
		}
		this.setResponseMediaType(response.getContentType());
		return response.getPayload();
	}

	@Override
	public byte[] sendPostRequestImpl(String host, int port, String href,
			byte[] payload, int contentType) throws IOException {
		logger.info("sending post request to " + host + ":" + port + "/" + href);
		logger.info("payload: " + IotLoggingUtil.getPayloadString(payload));
		Message response = getResponse(CodeRegistry.METHOD_POST, host, port,
				href, payload, contentType);
		if (response == null) {
			return null;
		}
		this.setResponseMediaType(response.getContentType());
		return response.getPayload();
	}

	@Override
	public byte[] sendPutRequestImpl(String host, int port, String href,
			byte[] payload, int contentType) throws IOException {
		logger.info("sending put request to " + host + ":" + port + "/" + href);
		logger.info("payload: " + IotLoggingUtil.getPayloadString(payload));
		Message response = getResponse(CodeRegistry.METHOD_PUT, host, port,
				href, payload, contentType);
		if (response == null) {
			return null;
		}
		this.setResponseMediaType(response.getContentType());
		return response.getPayload();
	}
	
	@Override
	public void cleanup() {
		channel.close();
	}

}
