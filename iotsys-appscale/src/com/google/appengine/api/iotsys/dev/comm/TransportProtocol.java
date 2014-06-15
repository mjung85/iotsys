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

package com.google.appengine.api.iotsys.dev.comm;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.util.IotLoggingUtil;

public abstract class TransportProtocol {

	private static final Logger logger = Logger
			.getLogger(TransportProtocol.class.getName());

	private TransportFormat format;
	private int responseMediaType = -1;

	protected TransportProtocol() {
	}

	private void addConnectionData(IotObject object, String host, int port) {
		object.setHost(host);
		object.setPort(port);

		for (IotObject child : object.getAllChildren()) {
			addConnectionData(child, host, port);
		}
	}

	public TransportFormat getTransportFormat() {
		return format;
	}

	public void setTransportFormat(TransportFormat format) {
		this.format = format;
	}

	public IotObject sendGetRequest(String host, int port, String href)
			throws IOException, CommunicationException {
		byte[] response = sendGetRequestImpl(host, port, href,
				this.format.getMediaType());
		IotObject responseObject = createObject(response, host, port, href);
		responseObject.setHref(href);
		return responseObject;
	}

	public IotObject sendPostRequest(String host, int port, String href,
			IotObject object) throws IOException, CommunicationException {
		byte[] payload = null;
		if (object != null) {
			payload = this.format.toPayloadBytes(object);
		}
		byte[] response = sendPostRequestImpl(host, port, href, payload,
				this.format.getMediaType());
		return createObject(response, host, port, href);
	}

	public IotObject sendPutRequest(String host, int port, String href,
			IotObject object) throws IOException, CommunicationException {
		byte[] payload = this.format.toPayloadBytes(object);
		byte[] response = sendPutRequestImpl(host, port, href, payload,
				this.format.getMediaType());
		IotObject responseObject = createObject(response, host, port, href);
		responseObject.setHref(href);
		return responseObject;
	}

	private IotObject createObject(byte[] response, String host, int port,
			String href) throws CommunicationException {
		logger.info("received response, payload: "
				+ IotLoggingUtil.getPayloadString(response));
		TransportFormat responseFormat = this.format;
		if (this.responseMediaType >= 0
				&& this.responseMediaType != responseFormat.getMediaType()) {
			try {
				responseFormat = FormatRegistry.getFormat(responseMediaType);
			} catch (Exception e) {
				throw new CommunicationException(
						"could not find response media type in regsitry: "
								+ e.getMessage());
			}
		}
		IotObject responseObject = responseFormat.fromPayloadBytes(response);
		addConnectionData(responseObject, host, port);
		return responseObject;
	}

	protected void setResponseMediaType(int type) {
		this.responseMediaType = type;
	}

	protected abstract byte[] sendGetRequestImpl(String host, int port,
			String href, int contentType) throws IOException;

	protected abstract byte[] sendPostRequestImpl(String host, int port,
			String href, byte[] payload, int contentType) throws IOException;

	protected abstract byte[] sendPutRequestImpl(String host, int port,
			String href, byte[] payload, int contentType) throws IOException;
	
	public abstract void cleanup();

}
