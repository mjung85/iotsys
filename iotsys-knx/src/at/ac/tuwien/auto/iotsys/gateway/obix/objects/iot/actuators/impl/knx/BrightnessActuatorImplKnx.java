/*
  	Copyright (c) 2013 - IotSyS KNX Connector
 	Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
  	All rights reserved.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.knx;

import java.util.logging.Logger;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.BrightnessActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import obix.Obj;

/**
 * Provides the KNX specific implementation for a brightness actuator.
 */

public class BrightnessActuatorImplKnx extends BrightnessActuatorImpl{
	private static final Logger log = Logger.getLogger(BrightnessActuatorImplKnx.class.getName());
	
	//CalimeroNG
	private GroupAddress status;
	private GroupAddress brightness;
	
	private KNXConnector knxConnector;
	
	public BrightnessActuatorImplKnx(KNXConnector knxConnector, GroupAddress status, final GroupAddress brightness){
		super();
		this.status = status;
		this.brightness = brightness;
		this.knxConnector = knxConnector;
//		if(status == null){
//			// add watch dog on switching group address
//			knxConnector.addWatchDog(brightness, new KNXWatchDog() {
//				@Override
//				public void notifyWatchDog(byte[] apdu) {			
//					try {						
//						DPTXlator8BitUnsigned x = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_VALUE_1_UCOUNT);
//					
//						x.setData(apdu);
//																				
//						if(x.getValueUnscaled() != (short)BrightnessActuatorImplKnx.this.value.get()){
//							BrightnessActuatorImplKnx.this.value.set(x.getValueUnscaled());
//						}
//						
//					} catch (KNXException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		}
	}
	
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.	
		super.writeObject(input);				
		log.info("Writing on brightness actuator!");
		knxConnector.write(brightness, (int)this.value().get(), ProcessCommunicator.UNSCALED);
	}
	
	public void refreshObject(){
		if(status != null){			
			int value = knxConnector.readInt(status,ProcessCommunicator.UNSCALED);		
			this.value().set(value);
		}		
	}
}
