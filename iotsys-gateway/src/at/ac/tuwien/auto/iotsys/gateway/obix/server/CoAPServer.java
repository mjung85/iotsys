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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import obix.Obj;
import obix.io.BinObixDecoder;
import obix.io.ObixDecoder;
import obix.io.ObixEncoder;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.gateway.service.impl.GroupCommServiceImpl;
import at.ac.tuwien.auto.iotsys.gateway.util.EXIDecoder;
import at.ac.tuwien.auto.iotsys.gateway.util.ExiUtil;
import at.ac.tuwien.auto.iotsys.gateway.util.JsonUtil;
import ch.ethz.inf.vs.californium.coap.CommunicatorFactory;
import ch.ethz.inf.vs.californium.coap.CommunicatorFactory.Communicator;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.endpoint.Endpoint;
import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;
import ch.ethz.inf.vs.californium.layers.MulticastUDPLayer;
import ch.ethz.inf.vs.californium.layers.MulticastUDPLayer.REQUEST_TYPE;

public class CoAPServer extends Endpoint {
	private static final Logger log = Logger.getLogger(CoAPServer.class
			.getName());

	public static final String COAP_URL_PROTOCOL = "coap";

	private ObixServer obixServer;
	private SOAPHandler soapHandler;
	private CoAPHelper coapHelper;

	//private InterceptorBroker interceptorBroker = InterceptorBrokerImpl.getInstance();

	// exit codes for runtime errors
	public static final int ERR_INIT_FAILED = 1;

	/**
	 * Constructor for a new ExampleServer. Call {@code super(...)} to configure
	 * the port, etc. according to the {@link LocalEndpoint} constructors.
	 * <p>
	 * Add all initial {@link LocalResource}s here.
	 */
	public void InitCoAPServer() {
		CommunicatorFactory factory = CommunicatorFactory.getInstance();

		// set the parameters of the communicator
		factory.setUdpPort(Integer.parseInt(PropertiesLoader.getInstance().getProperties()
				.getProperty("iotsys.gateway.coap.port", "5685")));
		factory.setTransferBlockSize(0);
		factory.setRunAsDaemon(false);
		factory.setRequestPerSecond(0);

		// initialize communicator
		Communicator communicator = factory.getCommunicator();

		// register the endpoint as a receiver
		communicator.registerReceiver(this);
	}

	public CoAPServer(ObixServer obixServer) {
		this.obixServer = obixServer;
		this.soapHandler = new SOAPHandler(obixServer);
		this.coapHelper = new CoAPHelper(obixServer);

		InitCoAPServer();
	}

	@Override
	public void execute(Request request) throws IOException {
		String resourcePath = getResourcePath(request);
		log.info("Coap serving " + resourcePath + " for "
				+ request.getPeerAddress().getAddress());
		
		if (intercept(request)) return;
		
		// handle multicast requests first
		if (handleMulticastRequest(request)) return;
		
		LOG.info(String.format("Execution: %s", resourcePath));
		
		try {
			setCoAPResponse(request);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (request instanceof GETRequest) {
			if (request.hasOption(OptionNumberRegistry.OBSERVE)) {
				ObixObservingManager.getInstance().addObserver((GETRequest) request);
			}
		}

		request.sendResponse();
		log.info("Coap serving " + resourcePath + " for "
				+ request.getPeerAddress().getAddress() + " done.");
	}

	private void setCoAPResponse(Request request) throws URISyntaxException {
		String obixResponse = getObixResponse(request);
		coapHelper.encodeResponse(obixResponse, request);
	}

	private String getObixResponse(Request request) throws URISyntaxException {
		String resourcePath = getResourcePath(request);
		String payloadString = getPayload(request);
		String ipv6address = coapHelper.getIPv6Address(request);
		
		StringBuffer obixResponse;
		
		if (resourcePath.endsWith("/soap")) {
			obixResponse = new StringBuffer(soapHandler.process(payloadString, null));
			
		} else if (resourcePath.endsWith(".well-known/core") && !obixServer.containsIPv6(ipv6address)) {
			obixResponse = new StringBuffer(obixServer.getCoRELinks());
			request.respond(CodeRegistry.RESP_CONTENT,
					obixResponse.toString(),
					MediaTypeRegistry.APPLICATION_LINK_FORMAT);
			
		} else if(resourcePath.endsWith("qrcode"))
		{	// entered if Coap post with /qrcode at the end
			// payloadString contains the qrcode
			obixResponse = new StringBuffer(obixServer.getQRCode(payloadString));
		}
		else {
			Obj responseObj = null;
			
			if (request instanceof GETRequest) {
				responseObj = obixServer.readObj(new URI(resourcePath), true);
			} else if (request instanceof PUTRequest) {
				responseObj = obixServer.writeObj(new URI(resourcePath), payloadString);
			} else if (request instanceof POSTRequest) {
				responseObj = obixServer.invokeOp(new URI(resourcePath), payloadString);
			}
			
			obixResponse = new StringBuffer(coapHelper.encodeObj(responseObj, request));
		}

		obixResponse = new StringBuffer(obixResponse.toString()
				.replaceFirst(ObixServer.DEFAULT_OBIX_URL_PROTOCOL, COAP_URL_PROTOCOL));
		
		return obixResponse.toString();
	}
	
	private String getPayload(Request request) {
		String payloadString = "";

		// check for application/exi content
		if (request.getContentType() == MediaTypeRegistry.APPLICATION_EXI) {
			try {
				payloadString = ExiUtil.getInstance().decodeEXI(
						request.getPayload());
			} catch (Exception e) {
				e.printStackTrace();
				payloadString = request.getPayloadString();
			}
		} else if (request.getContentType() == MediaTypeRegistry.APPLICATION_OCTET_STREAM) {
			try {
				payloadString = ExiUtil.getInstance().decodeEXI(
						request.getPayload(), true);
			} catch (Exception e) {
				e.printStackTrace();
				payloadString = request.getPayloadString();
			}
		} else if (request.getContentType() == MediaTypeRegistry.APPLICATION_X_OBIX_BINARY) {
			try {
				payloadString = ObixEncoder.toString(BinObixDecoder.fromBytes(request.getPayload()));
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
				ObixServer.DEFAULT_OBIX_URL_PROTOCOL);
		
		return payloadString;
	}

	private boolean handleMulticastRequest(Request request) {
		if (MulticastUDPLayer.getRequestType() == REQUEST_TYPE.MULTICAST_REQUEST) {
			log.finest("Handle multicast request!");
			Obj obj = null;
			if (request.getContentType() == MediaTypeRegistry.APPLICATION_OCTET_STREAM) {
				try {
					log.finest("Received EXI encoded multicast.");
					obj = EXIDecoder.getInstance().fromBytesSchema(
							request.getPayload());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// try to decode from string
				obj = ObixDecoder.fromString(request.getPayloadString());
			}
			GroupCommServiceImpl.getInstance().handleRequest(
					MulticastUDPLayer.getMulticastAddress(), obj);
			return true;
		}
		
		return false;
	}
	
	private boolean intercept(Request request) {
		/* INTERCEPTORS START */
		/*
		if (interceptorBroker != null && interceptorBroker.hasInterceptors())
		{
			log.fine("Interceptors found ... starting to prepare.");
			String resourcePath = getResourcePath(request);
			
			InterceptorRequest interceptorRequest = new InterceptorRequestImpl();
			HashMap<Parameter, String> interceptorParams = new HashMap<Parameter,
					String>();

			String resource = COAP_URL_PROTOCOL + "://"
					+ request.getNetworkInterface().getCanonicalHostName()
					+ ":" + Communicator.getInstance().port() + resourcePath;
			LOG.info(resource);
			String action = CodeRegistry.toString(request.getCode());

			interceptorParams.put(Parameter.SUBJECT, request.getPeerAddress()
					.toString());
			interceptorParams.put(Parameter.SUBJECT_IP_ADDRESS, request
					.getPeerAddress().toString());
			interceptorParams.put(Parameter.RESOURCE, resource);
			interceptorParams.put(Parameter.RESOURCE_PROTOCOL,
					COAP_URL_PROTOCOL);
			interceptorParams.put(Parameter.RESOURCE_IP_ADDRESS, request
					.getNetworkInterface().getHostAddress());
			interceptorParams.put(Parameter.RESOURCE_HOSTNAME, request
					.getNetworkInterface().getHostName());
			interceptorParams.put(Parameter.RESOURCE_PATH, resourcePath);
			interceptorParams.put(Parameter.ACTION, action);

			interceptorRequest.setInterceptorParams(interceptorParams);

			log.fine("Calling interceptions ...");
			InterceptorResponse resp = interceptorBroker
					.handleRequest(interceptorRequest);

			if (!resp.getStatus().equals(StatusCode.OK)) {
				if (resp.forward()) {
					request.respond(CodeRegistry.RESP_FORBIDDEN,
							resp.getMessage(), MediaTypeRegistry.TEXT_PLAIN);
					request.sendResponse();
					return true;
				}
			}
		}
		*/
		/* INTERCEPTORS END */
		
		return false;
	}
	
	private String getResourcePath(Request request) {
		return coapHelper.getResourcePath(request);
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