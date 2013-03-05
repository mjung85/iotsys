/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2013 
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.bacnet;

import static at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector.BACNET_PRIORITY;
import obix.Obj;

import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.CoolerActuatorImpl;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.type.Encodable;


public class CoolerActuatorImplBacnet extends CoolerActuatorImpl {
	private BacnetDataPointInfo switchDP;
	private BACnetConnector bacnetConnector;
	
	public CoolerActuatorImplBacnet(BACnetConnector bacnetConnector,
			BacnetDataPointInfo switchDP) {
		this.switchDP = switchDP;
		this.bacnetConnector = bacnetConnector;
	}

	public void refreshObject() {
		try {
			Encodable property = bacnetConnector.readProperty(
					switchDP.getDeviceIdentifier(),
					switchDP.getObjectIdentifier(),
					switchDP.getPropertyIdentifier());

			if (property instanceof  com.serotonin.bacnet4j.type.primitive.Boolean) {
				switchState.set(((com.serotonin.bacnet4j.type.primitive.Boolean) property).booleanValue());
			}			

		} catch (BACnetException e) {			
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
	}

	public void writeObject(Obj input) {
		// A write on this object was received, update the according data point.
		super.writeObject(input);
	
		try {
			bacnetConnector.writeProperty(switchDP.getDeviceIdentifier(), switchDP.getObjectIdentifier(),
					switchDP.getPropertyIdentifier(), new com.serotonin.bacnet4j.type.primitive.Enumerated(switchState.get()?1:0), BACNET_PRIORITY);			
		} catch (BACnetException e) {			
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
	}
}

