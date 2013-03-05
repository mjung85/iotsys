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

package at.ac.tuwien.auto.iotsys.gateway.obix.server;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;

import at.ac.tuwien.auto.iotsys.gateway.util.ExiUtil;
import at.ac.tuwien.auto.iotsys.gateway.util.JsonUtil;

import obix.io.BinObixDecoder;
import obix.io.BinObixEncoder;
import obix.io.ObixDecoder;
import obix.io.ObixEncoder;


import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.Communicator;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.Endpoint;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;
import ch.ethz.inf.vs.californium.util.Properties;

public class CoAPServer extends Endpoint {

	public static final String COAP_URL_PROTOCOL = "coap";

	private ObixServer obixServer;
	private SOAPHandler soapHandler;

	// exit codes for runtime errors
	public static final int ERR_INIT_FAILED = 1;

	/**
	 * Constructor for a new ExampleServer. Call {@code super(...)} to configure
	 * the port, etc. according to the {@link LocalEndpoint} constructors.
	 * <p>
	 * Add all initial {@link LocalResource}s here.
	 */
	public void InitCoAPServer() throws SocketException {
		Communicator.setupPort(Properties.std.getInt("DEFAULT_PORT"));
		Communicator.setupTransfer(0);
		Communicator.setupDeamon(false);
		Communicator.getInstance().registerReceiver(this);
	}

	public CoAPServer(ObixServer obixServer) throws IOException {
		this.obixServer = obixServer;
		this.soapHandler = new SOAPHandler(obixServer);
		
		InitCoAPServer();
	}

	
	@Override
	public void execute(Request request) throws IOException {

		String resourcePath = request.getUriPath();

		String localSocket = request.getNetworkInterface().getHostAddress()
				.toString();
		int lastIndex = localSocket.lastIndexOf("%");
		String localSocketSplitted = request.getNetworkInterface()
				.getHostAddress().toString();

		if (lastIndex > 0) {
			localSocketSplitted = localSocket.substring(0, lastIndex);
		}

		String[] splitedpeerAddress = request.getNetworkInterface()
				.getHostAddress().toString().split("%");

		if (!localSocketSplitted.startsWith("/")) {
			localSocketSplitted = "/" + localSocketSplitted;
		}

		if (request.getPeerAddress().getAddress() instanceof Inet6Address
				&& resourcePath.equalsIgnoreCase("/")
				&& obixServer.containsIPv6(localSocketSplitted)) {
			resourcePath = obixServer.getIPv6LinkedHref(localSocketSplitted);

		} else if (request.getPeerAddress().getAddress() instanceof Inet6Address
				&& obixServer.containsIPv6(localSocketSplitted + resourcePath)) {

			resourcePath = obixServer.getIPv6LinkedHref(localSocketSplitted
					+ resourcePath)
					+ resourcePath;
		}

		if (resourcePath.endsWith("/")) {
			resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
		}

		LOG.info(String.format("Execution: %s", resourcePath));

		String payloadString = "";

		// check for application/exi content
		if (request.getContentType() == MediaTypeRegistry.APPLICATION_EXI) {
			try {
				payloadString = ExiUtil.decodeEXI(request.getPayload());
			} catch (Exception e) {
				e.printStackTrace();
				payloadString = request.getPayloadString();
			}
		} else if (request.getContentType() == MediaTypeRegistry.APPLICATION_OCTET_STREAM) {
			try {
				payloadString = ExiUtil.decodeEXI(request.getPayload(), true);
			} catch (Exception e) {
				e.printStackTrace();
				payloadString = request.getPayloadString();
			}
		} else if (request.getContentType() == MediaTypeRegistry.APPLICATION_X_OBIX_BINARY) {
			try {
				payloadString = ObixEncoder.toString(BinObixDecoder
						.fromBytes(request.getPayload()));
			} catch (Exception e) {
				e.printStackTrace();
				payloadString = request.getPayloadString();
			}
		} else if (request.getContentType() == MediaTypeRegistry.APPLICATION_JSON) {
			try {
				payloadString = JsonUtil.fromJSONtoXML(request
						.getPayloadString());

			} catch (Exception e) {
				e.printStackTrace();
				payloadString = request.getPayloadString();
			}
		} else {
			payloadString = request.getPayloadString();
		}

		payloadString = payloadString.replaceFirst(COAP_URL_PROTOCOL,
				obixServer.DEFAULT_OBIX_URL_PROTOCOL);

		String obixMessage = "";

		try {
			StringBuffer obixResponse = new StringBuffer("");
			if (resourcePath.endsWith("/soap")) {
				obixResponse = new StringBuffer(soapHandler.process(
						payloadString, null));
			} 
			else if (resourcePath.endsWith(".well-known/core")) {
				obixResponse = new StringBuffer(obixServer.getCoRELinks());				
				request.respond(CodeRegistry.RESP_CONTENT,
								obixResponse.toString(),
								MediaTypeRegistry.APPLICATION_LINK_FORMAT);
			} 
			else {

				if (request instanceof GETRequest) {

					obixResponse = new StringBuffer(
							ObixEncoder.toString(obixServer.readObj(new URI(
									resourcePath), "guest")));

				}

				if (request instanceof PUTRequest) {

					obixResponse = new StringBuffer(
							ObixEncoder.toString(obixServer.writeObj(new URI(
									resourcePath), payloadString)));

				}
				if (request instanceof POSTRequest) {
					obixResponse = new StringBuffer(
							ObixEncoder.toString(obixServer.invokeOp(new URI(
									resourcePath), payloadString)));

				}
			}

			obixResponse = new StringBuffer(obixResponse.toString()
					.replaceFirst(obixServer.DEFAULT_OBIX_URL_PROTOCOL,
							COAP_URL_PROTOCOL));
			obixResponse = new StringBuffer(obixResponse.toString());

			fixHref(request.getUriPath(), obixResponse);

			if (request.getFirstAccept() == MediaTypeRegistry.APPLICATION_EXI) {
				try {
					byte[] exiData = ExiUtil.encodeEXI(obixResponse.toString());
					request.respond(CodeRegistry.RESP_CONTENT, exiData,
							MediaTypeRegistry.APPLICATION_EXI);
					
				} catch (Exception e) {
					e.printStackTrace();
					request.respond(CodeRegistry.RESP_CONTENT,
							obixResponse.toString(), MediaTypeRegistry.TEXT_XML);
				}
			} else if(request.getFirstAccept() == MediaTypeRegistry.APPLICATION_OCTET_STREAM){
				try {
					byte[] exiData = ExiUtil.encodeEXI(obixResponse.toString(), true);
					request.respond(CodeRegistry.RESP_CONTENT, exiData,
							MediaTypeRegistry.APPLICATION_EXI);
				
				} catch (Exception e) {
					e.printStackTrace();
					request.respond(CodeRegistry.RESP_CONTENT,
							obixResponse.toString(), MediaTypeRegistry.TEXT_XML);
				}
			}			
			else if (request.getFirstAccept() == MediaTypeRegistry.APPLICATION_X_OBIX_BINARY) {
				try {
					byte[] exiData = BinObixEncoder.toBytes(ObixDecoder
							.fromString(obixResponse.toString()));
					request.respond(CodeRegistry.RESP_CONTENT, exiData,
							MediaTypeRegistry.APPLICATION_X_OBIX_BINARY);
				
				} catch (Exception e) {
					e.printStackTrace();
					request.respond(CodeRegistry.RESP_CONTENT,
							obixResponse.toString(), MediaTypeRegistry.TEXT_XML);
				}
			} 
			
			
			else if (request.getFirstAccept() == MediaTypeRegistry.APPLICATION_JSON) {
				try {
					request.respond(CodeRegistry.RESP_CONTENT,
							JsonUtil.fromXMLtoJSON(obixResponse.toString()),
							MediaTypeRegistry.APPLICATION_JSON);
				} catch (JSONException e) {
					e.printStackTrace();
					request.respond(CodeRegistry.RESP_CONTENT,
							obixResponse.toString(), MediaTypeRegistry.TEXT_XML);
				}
			} else {
				if (request.getUriPath().endsWith(".well-known/core")) {
					obixResponse = new StringBuffer(obixServer.getCoRELinks());
					request.respond(CodeRegistry.RESP_CONTENT,
							obixResponse.toString(),
							MediaTypeRegistry.APPLICATION_LINK_FORMAT);
					
				} else {
					request.respond(CodeRegistry.RESP_CONTENT,
							obixResponse.toString(), MediaTypeRegistry.TEXT_XML);
				}

			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (request instanceof GETRequest) {
			if (request.hasOption(OptionNumberRegistry.OBSERVE)) {
				ObixObservingManager.getInstance().addObserver(
						(GETRequest) request, resourcePath);
			}
		}

		request.sendResponse();
	}
	
	private void fixHref(String href, StringBuffer obixResponse) {		
		int hrefIndex = obixResponse.indexOf("href");
		// get index of first quota - " - this is the start of the href
		int firstQuota = obixResponse.indexOf("\"", hrefIndex);
		// get index of the second quota - " - this is the end of the href
		int secondQuota = obixResponse.indexOf("\"", firstQuota + 1);

		if (hrefIndex >= 0 && firstQuota > hrefIndex
				&& secondQuota > firstQuota) {
			// now delete everything from localhost to the position of the
			// closing quota
			obixResponse.delete(firstQuota + 1, secondQuota);

			// put the IPv6 address there instead
			obixResponse.insert(firstQuota +1 , href);
		}	
	}

	@Override
	public void handleResponse(Response response) {
	
	}

	@Override
	public void handleRequest(Request request) {
		request.prettyPrint();

		try {
			execute(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}