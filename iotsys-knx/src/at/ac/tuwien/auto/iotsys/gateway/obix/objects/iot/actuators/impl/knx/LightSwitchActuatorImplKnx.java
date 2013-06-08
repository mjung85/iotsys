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
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.LightSwitchActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.util.CsvCreator;
import obix.Obj;

/**
 * Provides the KNX specific implementation for a light switching actuator.
 */
public class LightSwitchActuatorImplKnx extends LightSwitchActuatorImpl  {
	private GroupAddress status;
	private GroupAddress switching;
	private KNXConnector knxConnector;
	
	public static final Logger knxBus = KNXConnector.knxBus;
	
	public LightSwitchActuatorImplKnx(KNXConnector knxConnector, GroupAddress status, final GroupAddress switching){
		super();
		this.status = status;
		this.switching = switching;
		this.knxConnector = knxConnector;
		if(status == null){
			// add watchdog on switching group address
			knxConnector.addWatchDog(switching, new KNXWatchDog() {
				@Override
				public void notifyWatchDog(byte[] apdu) {			
					try {
						DPTXlatorBoolean x = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_SWITCH);
											
						x.setData(apdu);
						
						

						if(x.getValueBoolean() != LightSwitchActuatorImplKnx.this.value.get()){
							LightSwitchActuatorImplKnx.this.value.set(x.getValueBoolean());
						}
						
					} catch (KNXException e) {					
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.	
		super.writeObject(input);
		knxConnector.write(switching, this.value().get());	
//		CsvCreator.instance.writeLine("" + System.currentTimeMillis() + ";" + switching.toString() + ";" + this.value().get());
	}
	
	public void refreshObject(){
		if(status != null){
			boolean value = knxConnector.readBool(status);		
			this.value().set(value);
		}	
	}
}
