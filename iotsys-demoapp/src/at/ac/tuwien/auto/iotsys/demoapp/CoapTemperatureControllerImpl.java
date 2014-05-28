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

package at.ac.tuwien.auto.iotsys.demoapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import ch.ethz.inf.vs.californium.coap.GETRequest;
//import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
//import ch.ethz.inf.vs.californium.coap.Option;
//import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
//import ch.ethz.inf.vs.californium.coap.Request;
//import ch.ethz.inf.vs.californium.coap.Response;
//import ch.ethz.inf.vs.californium.coap.ResponseHandler;
//import ch.ethz.inf.vs.californium.coap.Message.messageType;


import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.CoapTemperatureController;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.impl.TemperatureControllerImpl;
import obix.Obj;
import obix.Str;
import obix.Uri;

public class CoapTemperatureControllerImpl extends TemperatureControllerImpl implements CoapTemperatureController {

	@Override
	public Str tempHref() {
		// TODO Auto-generated method stub
		return null;
	}
//	private static final Logger log = Logger.getLogger(CoapTemperatureControllerImpl.class.getName());
//	
//	protected Str tempHref = new Str("coap://[aaaa::c30c:0:0:856]/temp");
//	
//	public CoapTemperatureControllerImpl(){
//		this.tempHref.setName("tempHref");
//		this.tempHref.setHref(new Uri("tempHref"));
//		this.tempHref.setWritable(true);
//		
//		this.add(tempHref);
//	}
//	
//	public static void main(String[] args){
//		String payload = "<real name=\"value\" units=\"obix:units/celsius\" val=\"27.75\"/>";
//		String temp = extractAttribute("real", "val", payload);
//		System.out.println("temp: " + temp);
//	}
//	
//	private Request lastRequest = null;
//
//	@Override
//	public Str tempHref() {
//		return tempHref();
//	}
//	
//	public void doControl(){
//		log.finest("Starting control logic.");
////		String tempIPv6 = PropertiesLoader.getInstance().getProperties()
////				.getProperty("iotsys.demoapp.temperatureIPv6", "aaaa::c30c:0:0:856");
////		String lightIPv6 = PropertiesLoader.getInstance().getProperties().
////				getProperty("iotsys.demoapp.heatIPv6", "aaab::1");
//		
//		final String tempUri = tempHref.get() + "/value";
//		
//		Request request = new GETRequest();
//
//		request.setType(messageType.NON);
//		// specify URI of target endpoint
//		request.setURI(tempUri);
//		// enable response queue for blocking I/O
//		request.enableResponseQueue(true);
//		// request.setContentType(MediaTypeRegistry.APPLICATION_EXI);
//		request.setOption(new Option(0, OptionNumberRegistry.OBSERVE));
//
//		request.setAccept(MediaTypeRegistry.APPLICATION_XML);
//
//		// execute the request
//		try {
//			request.execute();
//			lastRequest = request;
//			
//		} catch (IOException e) {
//			System.err.println("Failed to execute request: " + e.getMessage());
//		}
//
//		// receive response
//		try {
//			Response response = request.receiveResponse();
//			if(response != null && response.getPayloadString() != null){
//				System.out.println("Extract attribute: " + response.getPayloadString().trim());
//				String temp = extractAttribute("real", "val",
//					response.getPayloadString().trim());
//				CoapTemperatureControllerImpl.super.temperature.set(Float.parseFloat(temp));
//			
//				CoapTemperatureControllerImpl.super.doControl();
//			}
//				
//		} catch (InterruptedException e) {
//			System.err.println("Receiving of response interrupted: "
//					+ e.getMessage());
//		}
//
//		request.registerResponseHandler(new ResponseHandler() {
//			public void handleResponse(Response response) {
//				log.finest("Payload: " + response.getPayloadString().trim());	
//				float temp = Float.parseFloat(  extractAttribute("real", "val",
//						response.getPayloadString().trim()));
//				log.finest("Received temp: " + temp);
//				CoapTemperatureControllerImpl.super.temperature.set(temp);
//				
//				CoapTemperatureControllerImpl.super.doControl();
//			}
//		});
//		
//	}
//	
//	public void stopControl(){
//		if(lastRequest != null){
//			log.finest("### Resetting coap observe.");
//			lastRequest.setType(messageType.RST);
//			try {
//				lastRequest.reject();
////				lastRequest.execute();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		// enable response queue for blocking I/O
//	}
//	
//	@Override
//	public void writeObject(Obj input){
//		String resourceUriPath = "";
//		if (input.getHref() == null) {
//			resourceUriPath = input.getInvokedHref().substring(
//					input.getInvokedHref().lastIndexOf('/') + 1);
//		} else {
//			resourceUriPath = input.getHref().get();
//		}
//		if(input instanceof CoapTemperatureController){
//			CoapTemperatureController in = (CoapTemperatureController) input;
//			this.tempHref.set(in.tempHref().get());
//		}
//		else if(input instanceof Str){
//			if ("tempHref".equals(resourceUriPath)) {
//				this.tempHref.set(((Str) input).get());
//			}
//		}
//		
//		super.writeObject(input);
//		
//		if(this.enabled.get() == false){
//			stopControl();
//		}
//	}
//	
//	public static String extractAttribute(String elementName,
//			String attributeName, String xml) {
//		Document document;
//		DocumentBuilder documentBuilder;
//		DocumentBuilderFactory documentBuilderFactory;
//
//		try {
//			documentBuilderFactory = DocumentBuilderFactory.newInstance();
//			documentBuilder = documentBuilderFactory.newDocumentBuilder();
//			document = documentBuilder.parse(new ByteArrayInputStream(xml
//					.getBytes()));
//			NodeList elementsByTagName = document
//					.getElementsByTagName(elementName);
//			Node item = elementsByTagName.item(0);
//			return ((Element) item).getAttribute(attributeName).toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//	
}
