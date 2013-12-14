/*******************************************************************************
 * Copyright (c) 2013 - IotSys CoAP Proxy
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


package at.ac.tuwien.auto.iotsys.gateway.connectors.coap;

import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Inet6Address;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import obix.Str;
import obix.Obj;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.ResponseHandler;
import ch.ethz.inf.vs.californium.coap.Message.messageType;

import at.ac.tuwien.auto.iotsys.commons.Connector;

public class CoapConnector implements Connector {
	private static final Logger log = Logger.getLogger(CoapConnector.class.getName());
	private static final int PORT = 5681;

	@Override
	public void connect() throws Exception {
		log.info("CoapConnector connecting.");	
	}

	@Override
	public void disconnect() throws Exception {
		log.info("CoapConnector disconnecting.");
	}
	
	private String createGETRequest(Inet6Address busAddress, String datapoint, ResponseHandler handler) {
		
		//TODO: PORT wieder entfernen ?!?
		Str tempHref = new Str("coap://[" + busAddress.getHostAddress() + "]:" + PORT);
		
		final String tempUri = tempHref.get() + "/" + datapoint;
		Request request = new GETRequest();
		
		request.setType(messageType.NON);
		// specify URI of target endpoint
		request.setURI(tempUri);
		// enable response queue for blocking I/O
		request.enableResponseQueue(true);
		
		try {
			request.execute();
			
		} catch (IOException e) {
			System.err.println("Failed to execute request: " + e.getMessage());
		}
		
		// receive response
		try {
			Response response = request.receiveResponse();
			if(response != null && response.getPayloadString() != null){
				System.out.println("Extract attribute: " + response.getPayloadString().trim());
				return response.getPayloadString().trim();
//				String temp = extractAttribute("bool", "val",
//					response.getPayloadString().trim());
//				 	// TODO: gibt Double wert zurück
//					return temp;
			}
				
		} catch (InterruptedException e) {
			System.err.println("Receiving of response interrupted: "
					+ e.getMessage());
		}
		
		request.registerResponseHandler(handler);
		
		return null;
	}
	

	public Boolean readBoolean(Inet6Address busAddress, ResponseHandler handler) {		
		String payload = createGETRequest(busAddress, "value", handler);
		if(payload != null) {
			String temp = extractAttribute("bool", "val", payload);
			return Boolean.parseBoolean(temp);
		}	
		return false;
	}

	public void writeBoolean(Inet6Address busAddress, Boolean value) {
		
		Str tempHref = new Str("coap://[" + busAddress.getHostAddress() + "]:" + PORT);
		String payload = "<bool val=\""+ value +"\"/>";
		
		final String tempUri = tempHref.get() + "/value";
		Request request = new PUTRequest();
		
		request.setType(messageType.NON);
		// specify URI of target endpoint
		request.setURI(tempUri);
		// enable response queue for blocking I/O
		request.enableResponseQueue(true);
		
		request.setPayload(payload);
		// request.setContentType(MediaTypeRegistry.APPLICATION_EXI);
		//request.setOption(new Option(0, OptionNumberRegistry.OBSERVE));
		
		// execute the request
		try {
			request.execute();
			
		} catch (IOException e) {
			System.err.println("Failed to execute request: " + e.getMessage());
		}	
		
	}
	
	public Double readDouble(Inet6Address busAddress, ResponseHandler handler) {
		String payload = createGETRequest(busAddress, "value", handler);
		if(payload != null) {
			String temp = extractAttribute("real", "val", payload);
			return Double.parseDouble(temp);
		}
		return 0.0;
	}

	public void writeDouble(Inet6Address busAddress, Double value) {
		log.info("Wert geschrieben:" + value);
	}
	
	
	//TODO: für Test
	//Werte aus xml parsen
	public static String extractAttribute(String elementName, String attributeName, String xml) {
		Document document;
		DocumentBuilder documentBuilder;
		DocumentBuilderFactory documentBuilderFactory;

		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(new ByteArrayInputStream(xml
					.getBytes()));
			NodeList elementsByTagName = document
					.getElementsByTagName(elementName);
			Node item = elementsByTagName.item(0);
			return ((Element) item).getAttribute(attributeName).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
