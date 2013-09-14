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

import java.util.logging.Logger;

import obix.Obj;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.FanSpeedActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.primitive.Real;

import static at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector.BACNET_PRIORITY;

public class FanSpeedActuatorImplBacnet extends FanSpeedActuatorImpl {
	private static final Logger log = Logger.getLogger(FanSpeedActuatorImplBacnet.class.getName());

	private BacnetDataPointInfo fanSpeed;
	private BacnetDataPointInfo enabled;
	private BACnetConnector bacnetConnector;

	public FanSpeedActuatorImplBacnet(BACnetConnector bacnetConnector,
			BacnetDataPointInfo fanSpeed, BacnetDataPointInfo enabled) {
		this.bacnetConnector = bacnetConnector;
		this.fanSpeed = fanSpeed;
		this.enabled = enabled;
	}

	public void refreshObject() {
		try {
			Encodable property = bacnetConnector.readProperty(
					fanSpeed.getDeviceIdentifier(),
					fanSpeed.getObjectIdentifier(),
					fanSpeed.getPropertyIdentifier());
			if (property instanceof Real) {
				fanSpeedSetpointValue.set((int) ((Real) property).floatValue());
			}
			if(enabled != null){
				property = bacnetConnector.readProperty(
						enabled.getDeviceIdentifier(),
						enabled.getObjectIdentifier(),
						enabled.getPropertyIdentifier());	
				if (property instanceof com.serotonin.bacnet4j.type.primitive.Boolean) {
					enabledValue.set(((com.serotonin.bacnet4j.type.primitive.Boolean) property).booleanValue());
				}
			}

		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
	}

	public void writeObject(Obj input) {
		super.writeObject(input);

		try {
			log.fine("Writing fan speed actuator to: " + fanSpeed + " value. " + this.fanSpeedSetpointValue().get() + " " + enabled + " value: " + enabledValue.get());
			bacnetConnector.writeProperty(fanSpeed.getDeviceIdentifier(), fanSpeed.getObjectIdentifier(),
					fanSpeed.getPropertyIdentifier(), new Real((float)this.fanSpeedSetpointValue()
							.get()), BACNET_PRIORITY);
			bacnetConnector.writeProperty(enabled.getDeviceIdentifier(), enabled.getObjectIdentifier(), enabled.getPropertyIdentifier(), 
					new com.serotonin.bacnet4j.type.primitive.Enumerated(enabledValue.get()?1:0), BACNET_PRIORITY);
			
		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
	}
}
