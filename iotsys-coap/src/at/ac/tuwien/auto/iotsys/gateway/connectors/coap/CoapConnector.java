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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import obix.Str;
//import obix.Obj;
//import obix.Int;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
//import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.ResponseHandler;
import ch.ethz.inf.vs.californium.coap.Message.messageType;

import at.ac.tuwien.auto.iotsys.commons.Connector;

public class CoapConnector implements Connector {
	private static final Logger log = Logger.getLogger(CoapConnector.class.getName());

	@Override
	public void connect() throws Exception {
		log.info("CoapConnector connecting.");	
	}

	@Override
	public void disconnect() throws Exception {
		log.info("CoapConnector disconnecting.");
	}
	
	private String send(String busAddress, String datapoint, String rType, String payload, ResponseHandler handler) {
		
		/*Str tempHref = new Str(busAddress);
		
		final String tempUri = tempHref.get() + "/" + datapoint;*/
		
		final String tempUri = busAddress + "/" + datapoint;
		
		//System.out.println(tempUri);
		
		Request request = null;
		
		//Specify Type of Request
		if(rType.equals("GET")){
			request = new GETRequest();
		} else if(rType.equals("PUT")){
			request = new PUTRequest();
			request.setPayload(payload);	
		/*
		} else if(rType.equals("POST")){
			
		} else if(rType.equals("DELETE")){
			request = new DELETERequest();
		} else if(rType.equals("DISCOVER")){
			request = new GETRequest();
		*/
		} else if(rType.equals("OBSERVE")){
			request = new GETRequest();
			request.setObserve();
			request.setOption(new Option(0, OptionNumberRegistry.OBSERVE));
		}
		
		request.setType(messageType.NON);
		request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.ACCEPT));
	
		// specify URI of target endpoint
		request.setURI(tempUri);
		// enable response queue for blocking I/O
		request.enableResponseQueue(true);
		
		// request.setContentType(MediaTypeRegistry.APPLICATION_EXI);
		// request.setAccept(MediaTypeRegistry.APPLICATION_XML);
		
		try {
			request.execute();
			
		} catch (IOException e) {
			System.err.println("Failed to execute request: " + e.getMessage());
		}
		
		//No Response for PUT needed
		if(rType.equals("PUT")) return null;
		
		// receive response
		try {
			Response response = request.receiveResponse();
			if(response != null && response.getPayloadString() != null){
				
				return response.getPayloadString().trim();
			}
				
		} catch (InterruptedException e) {
			System.err.println("Receiving of response interrupted: "
					+ e.getMessage());
		}
		
		if(handler != null) request.registerResponseHandler(handler);
		
		return null;
	}
	
	//sends Observe to a new Sensor/Actor with Response to be handled in Sensor/Actor
	public void createWatchDog(String busAddress, String datapoint, ResponseHandler handler) {
		send(busAddress, datapoint, "OBSERVE", "", handler);	
	}

	public Boolean readBoolean(String busAddress, String datapoint) {		
		String payload = send(busAddress, datapoint, "GET", "", null);
		if(payload != null) {
			String temp = extractAttribute("bool", "val", payload);
			return Boolean.parseBoolean(temp);
		}	
		return false;
	}
	
	public Double readDouble(String busAddress, String datapoint) {
		String payload = send(busAddress, datapoint, "GET", "", null);
		if(payload != null) {
			String temp = extractAttribute("real", "val", payload);
			return Double.parseDouble(temp);
		}
		return 0.0;
	}
	
	public Long readInt(String busAddress, String datapoint) {
		String payload = send(busAddress, datapoint, "GET", "", null);
		if(payload != null) {
			String temp = extractAttribute("int", "val", payload);
			return Long.parseLong(temp);
		}
		return 0L;
	}
	
	public String readStr(String busAddress, String datapoint) {
		String payload = send(busAddress, datapoint, "GET", "", null);
		if(payload != null) {
			String temp = extractAttribute("str", "val", payload);
			return temp;
		}
		return "";
	}
	
	//TODO: Activity Contract von bool auf str aendern?!?
	public String readActivity(String busAddress, String datapoint) {
		String payload = send(busAddress, datapoint, "GET", "", null);
		if(payload != null) {
			String temp = extractAttribute("bool", "val", payload);
			return temp;
		}
		return "";
	}

	public void writeBoolean(String busAddress, String datapoint, Boolean value) {
		String payload = "<bool val=\""+ value +"\"/>";
		send(busAddress, datapoint, "PUT", payload, null);
	}

	public void writeDouble(String busAddress, String datapoint, Double value) {
		String payload = "<real val=\""+ value +"\"/>";		
		send(busAddress, datapoint, "PUT", payload, null);
	}
	
	public void writeInt(String busAddress, String datapoint, Long value) {
		String payload = "<int val=\""+ value +"\"/>";		
		send(busAddress, datapoint, "PUT", payload, null);
	}
	
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
