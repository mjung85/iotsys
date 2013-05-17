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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.ResponseHandler;
import ch.ethz.inf.vs.californium.coap.Message.messageType;

public class TemperatureAlarm {
	static boolean alarm = false;
	
	static final float maxTemp = 28.5f;

	public static void main(String[] args) {
		String uri = "coap://[2001:620:2080:130::c8]/temperature";

		Request request = new GETRequest();

		request.setType(messageType.NON);
		// specify URI of target endpoint
		request.setURI(uri);
		// enable response queue for blocking I/O
		request.enableResponseQueue(true);
		// request.setContentType(MediaTypeRegistry.APPLICATION_EXI);
		request.setOption(new Option(0, OptionNumberRegistry.OBSERVE));

		request.setAccept(MediaTypeRegistry.APPLICATION_XML);

		// execute the request
		try {
			request.execute();
		} catch (IOException e) {
			System.err.println("Failed to execute request: " + e.getMessage());
			System.exit(-1);
		}

		// receive response
		try {
			Response response = request.receiveResponse();
			String extractAttribute = extractAttribute("real", "val",
					response.getPayloadString());
			System.out.println("Extract attribute: " + extractAttribute);
			if (response != null) {
				// response received, output a pretty-print
				response.prettyPrint();
			}
		} catch (InterruptedException e) {
			System.err.println("Receiving of response interrupted: "
					+ e.getMessage());
			System.exit(-1);
		}

		request.registerResponseHandler(new ResponseHandler() {
			public void handleResponse(Response response) {
				float temp = Float.parseFloat(  extractAttribute("real", "val",
						response.getPayloadString()));
				
				if(temp < maxTemp && alarm ){
					System.out.println("min alarm");
					//heat	
					PUTRequest putRequest = new PUTRequest();
					putRequest
							.setURI("coap://[2001:629:2500:570:1:4:1:1]/light4/value");
					putRequest.setPayload("<bool val=\"false\"/>");
					putRequest.enableResponseQueue(true);
					// System.out.println("sending: " +
					// putRequest.getPayloadString());
					try {
						putRequest.execute();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					alarm = false;
				}
				if(temp > maxTemp & !alarm){
					System.out.println("temp alarm");
					//heat	
					PUTRequest putRequest = new PUTRequest();
					putRequest
							.setURI("coap://[2001:629:2500:570:1:4:1:1]/light4/value");
					putRequest.setPayload("<bool val=\"true\"/>");
					putRequest.enableResponseQueue(true);
					// System.out.println("sending: " +
					// putRequest.getPayloadString());
					try {
						putRequest.execute();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					alarm = true;
				}
				
				
			}
		});
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String extractAttribute(String elementName,
			String attributeName, String xml) {
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

