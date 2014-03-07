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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.coap;

//import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.ResponseHandler;
import obix.Obj;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.LedsActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.coap.CoapConnector;

public class LedsActuatorImplCoap extends LedsActuatorImpl{
	//private static final Logger log = Logger.getLogger(LedsActuatorImplCoap.class.getName());
	
	private CoapConnector coapConnector;
	private String busAddress;

	public LedsActuatorImplCoap(CoapConnector coapConnector, String busAddress) {
		// technology specific initialization
		this.coapConnector = coapConnector;
		this.busAddress = busAddress;
	}

	@Override
	public void initialize(){
		super.initialize();
		// But stuff here that should be executed after object creation
		//addWatchDog();
	}
	
	public void addWatchDog(){
		coapConnector.createWatchDog(busAddress, LED_BLUE_CONTRACT_HREF, new ResponseHandler() {
			public void handleResponse(Response response) {	
				boolean temp = Boolean.parseBoolean(CoapConnector.extractAttribute("bool", "val", 
						response.getPayloadString().trim()));
				LedsActuatorImplCoap.this.blue().set(temp);
			}
		});	
		
		coapConnector.createWatchDog(busAddress, LED_RED_CONTRACT_HREF, new ResponseHandler() {
			public void handleResponse(Response response) {	
				boolean temp = Boolean.parseBoolean(CoapConnector.extractAttribute("bool", "val", 
						response.getPayloadString().trim()));
				LedsActuatorImplCoap.this.red().set(temp);
			}
		});
		
		coapConnector.createWatchDog(busAddress, LED_GREEN_CONTRACT_HREF, new ResponseHandler() {
			public void handleResponse(Response response) {	
				boolean temp = Boolean.parseBoolean(CoapConnector.extractAttribute("bool", "val", 
						response.getPayloadString().trim()));
				LedsActuatorImplCoap.this.green().set(temp);
			}
		});
	}
	
	@Override
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.	
		// The base class knows how to update the internal variable and to trigger
		// all the oBIX specific processing.
		super.writeObject(input);
		
		// write it out to the technology bus
		coapConnector.writeBoolean(busAddress, LED_BLUE_CONTRACT_HREF, this.blue().get());
		coapConnector.writeBoolean(busAddress, LED_RED_CONTRACT_HREF, this.red().get());
		coapConnector.writeBoolean(busAddress, LED_GREEN_CONTRACT_HREF, this.green().get());
	}
	
	@Override
	public void refreshObject(){
		// value is the protected instance variable of the base class (FanSpeedActuatorImpl)
		if(blue != null){
			Boolean value = coapConnector.readBoolean(busAddress, LED_BLUE_CONTRACT_HREF);	
			this.blue().set(value);
		}
		if(red != null){
			Boolean value = coapConnector.readBoolean(busAddress, LED_RED_CONTRACT_HREF);	
			this.red().set(value);
		}
		if(green != null){
			Boolean value = coapConnector.readBoolean(busAddress, LED_GREEN_CONTRACT_HREF);	
			this.green().set(value);
		}
	}
}
