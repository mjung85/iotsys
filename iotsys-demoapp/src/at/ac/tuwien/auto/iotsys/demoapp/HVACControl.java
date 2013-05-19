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

import obix.Bool;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.Uri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.Application;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.CoapTemperatureController;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.Message.messageType;
import ch.ethz.inf.vs.californium.coap.ResponseHandler;

public class HVACControl extends Obj implements Application {
	private static final Logger log = Logger.getLogger(HVACControl.class.getName());
	
	protected Bool enabled = new Bool(false);
	protected Real setpoint = new Real(0);
	protected Str tempIPv6 = new Str("aaaa::c30c:0:0:856");
	protected Real currentTemp = new Real(0);
	
	private boolean cooling = false;
	private boolean heating = false;
	
	private double minTemp = 24;
	private double maxTemp = 26;

	public static void main(String[] args) {
		new HVACControl().doControl();
	}
	
	public HVACControl(){
		this.setDisplay("HVAC demo app.");
		
		enabled.setName("enabled");
		enabled.setHref(new Uri("enabled"));
		this.add(enabled);
	}
	
	public void doControl(){
		String tempIPv6 = PropertiesLoader.getInstance().getProperties()
				.getProperty("iotsys.demoapp.temperatureIPv6", "aaaa::c30c:0:0:856");
		String lightIPv6 = PropertiesLoader.getInstance().getProperties().
				getProperty("iotsys.demoapp.heatIPv6", "aaab::1");
		
		final String tempUri = "coap://[" + tempIPv6 + "]/temp/value";
		final String lightUri = "coap://[" + lightIPv6 + "]/value";
		
	    minTemp = Double.parseDouble(PropertiesLoader.getInstance().getProperties()
				.getProperty("iotsys.demoapp.minTemp", "30"));
		
		maxTemp = Double.parseDouble(PropertiesLoader.getInstance().getProperties()
				.getProperty("iotsys.demoapp.minTemp", "33"));

		Request request = new GETRequest();

		request.setType(messageType.NON);
		// specify URI of target endpoint
		request.setURI(tempUri);
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
				
				log.info("Curent temp: " + temp);
				log.info("heating: " + heating);
				log.info("cooling: " + cooling);
				
				if(temp < minTemp && !heating){
					log.info("now heating");
					//heat	
					PUTRequest putRequest = new PUTRequest();
					putRequest
							.setURI(lightUri);
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
					
					heating = true;
					cooling = false;
				}
				if(temp < maxTemp && cooling){
					System.out.println("Stop cooling");
					PUTRequest putRequest = new PUTRequest();
					putRequest
							.setURI(lightUri);
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
				
					cooling = false;		
				}
				if(temp > minTemp && heating){
					log.info("Stop heating");
					PUTRequest putRequest = new PUTRequest();
					putRequest
							.setURI(lightUri);
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
					
					heating = false;
				}
				if(temp > maxTemp && !cooling){
					log.info("now cooling");
					// cool
					//heat	
					PUTRequest putRequest = new PUTRequest();
					putRequest
							.setURI(lightUri);
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
					
					heating = false;
					cooling = true;
				}
			}
		});
		try {
			System.in.read();
		} catch (IOException e) {
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

	@Override
	public Bool enabled() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void writeObject(Obj input){
		if(input instanceof Application){
			Application app = (Application) input;
			this.enabled.set(app.enabled().get());
		}
		else if(input instanceof Bool){
			
		}
		
	}


}
