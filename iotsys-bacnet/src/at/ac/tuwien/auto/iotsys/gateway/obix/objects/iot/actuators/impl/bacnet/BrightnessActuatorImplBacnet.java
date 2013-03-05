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

import obix.Obj;

import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.BrightnessActuatorImpl;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;


public class BrightnessActuatorImplBacnet extends BrightnessActuatorImpl{
	private int deviceID;
	private ObjectIdentifier objectIdentifier;
	private PropertyIdentifier propertyIdentifier;
	private BACnetConnector bacnetConnector;

	public BrightnessActuatorImplBacnet(BACnetConnector bacnetConnector,
			int deviceID, ObjectIdentifier objectIdentifier,
			PropertyIdentifier propertyIdentifier) {
		this.deviceID = deviceID;
		this.objectIdentifier = objectIdentifier;
		this.propertyIdentifier = propertyIdentifier;
		this.bacnetConnector = bacnetConnector;
	}

	public void refreshObject() {
		try {
			Encodable property = bacnetConnector.readProperty(deviceID, objectIdentifier,propertyIdentifier);
			
			if(property instanceof Real){
				value.set((int) ((Real) property).floatValue());
			}		
		} catch (BACnetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.		
		super.writeObject(input);
		
		try {
			bacnetConnector.writeProperty(deviceID, objectIdentifier, propertyIdentifier, new Real((float) this.value().get()), new UnsignedInteger(10));
		} catch (BACnetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
