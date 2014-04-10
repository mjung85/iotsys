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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.coap;

//import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.ResponseHandler;

import obix.Obj;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.Addressable;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sensors.impl.ActivitySensorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.coap.CoapConnector;

public class ActivitySensorImplCoap extends ActivitySensorImpl implements Addressable {
	//private static final Logger log = Logger.getLogger(TemperatureSensorImplCoap.class.getName());
	
	private CoapConnector coapConnector;
	private String busAddress; 
	private boolean isObserved;
	
	public ActivitySensorImplCoap(CoapConnector coapConnector, String busAddress){
		// technology specific initialization
		this.coapConnector = coapConnector;
		this.busAddress = busAddress;
		this.isObserved = false;
	}
	
	@Override
	public void initialize(){
		super.initialize();
		// But stuff here that should be executed after object creation
		//addWatchDog();
	}
	
	public void addWatchDog(){
		coapConnector.createWatchDog(busAddress, ACTIVE_CONTRACT_HREF, new ResponseHandler() {
			public void handleResponse(Response response) {	
					String payload = response.getPayloadString().trim();
					
					if(payload.equals("") || payload.equals("TooManyObservers")) return;
					
					if(payload.startsWith("Added")) {
						isObserved = true;
						return;
					}
					
					String bool = CoapConnector.extractAttribute("bool", "val", payload);
					ActivitySensorImplCoap.this.activeValue().set(Boolean.valueOf(bool));
			}
		});	
		coapConnector.createWatchDog(busAddress, FREEFALL_CONTRACT_HREF, new ResponseHandler() {
			public void handleResponse(Response response) {	
					String payload = response.getPayloadString().trim();
					
					if(payload.equals("") || payload.equals("TooManyObservers")) return;
					
					if(payload.startsWith("Added")) {
						isObserved = true;
						return;
					}
					
					String bool = CoapConnector.extractAttribute("bool", "val", payload);
					ActivitySensorImplCoap.this.freefallValue().set(Boolean.valueOf(bool));
			}
		});	
	}
	
	@Override
	public void writeObject(Obj input){
		//Sensor not writable
	}

	@Override
	public void refreshObject(){
		//value is the protected instance variable of the base class (ActivitySensorImpl)
		if(active != null && !isObserved){
			Boolean value = coapConnector.readBoolean(busAddress, ACTIVE_CONTRACT_HREF);
			// this calls the implementation of the base class, which triggers also
			// oBIX services (e.g. watches, history) and CoAP observe!			
			this.activeValue().set(value); 
		}	
		
		//value is the protected instance variable of the base class (ActivitySensorImpl)
		if(freefall != null && !isObserved){
			Boolean value = coapConnector.readBoolean(busAddress, FREEFALL_CONTRACT_HREF);
			// this calls the implementation of the base class, which triggers also
			// oBIX services (e.g. watches, history) and CoAP observe!			
			this.freefallValue().set(value); 
		}
	}

	@Override
	public String getBusAddress() {
		return busAddress;
	}
}
