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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.knx;

import java.util.logging.Logger;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.ShuttersAndBlindsSunblindSensorImpl;

public class ShuttersAndBlindsSunblindSensorImplKnx extends ShuttersAndBlindsSunblindSensorImpl {
	private GroupAddress observation;
	
	private KNXConnector connector;
	
	public static final Logger knxBus = KNXConnector.knxBus;
	
	private static final Logger log = Logger.getLogger(ShuttersAndBlindsSunblindSensorImplKnx.class.getName());

	public ShuttersAndBlindsSunblindSensorImplKnx(KNXConnector connector , GroupAddress observation) {
		this.observation = observation;
		this.connector = connector;	
	}

	public void createWatchDog() {
		connector.addWatchDog(observation, new KNXWatchDog() {
			@Override
			public void notifyWatchDog(byte[] apdu) {			
				try {
					DPTXlatorBoolean x = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_UPDOWN);
					x.setData(apdu);
//					
//					for(int i=0; i< apdu.length; i++) {
//						System.out.print(apdu[i]);
//					}
					
					moveUpDownValue.set(x.getValueBoolean());
					
					//CsvCreator.instance.writeLine(System.currentTimeMillis() + ";" + observation.getRawAddress() + ";" + x.getValueBoolean());
									
					// notify observers of this oBIX object
					ShuttersAndBlindsSunblindSensorImplKnx.this.notifyObservers();
				} 				
				catch (KNXException e) {			
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void initialize(){
		super.initialize();
		createWatchDog();
	}	
}
