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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl;

import java.util.logging.Logger;

import obix.Bool;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetUnits;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Null;

public abstract class AnalogBacnetObj extends BacnetObj {
	private static final Logger log = Logger.getLogger(AnalogBacnetObj.class.getName());
	protected Real value = new Real(0);
	
	public AnalogBacnetObj(BACnetConnector bacnetConnector, BacnetDataPointInfo dataPointInfo) {
		super(bacnetConnector, dataPointInfo);
		
		Uri valueUri = new Uri("value");
		
		value.setHref(valueUri);
		value.setName("value");
		add(value);
	}
	
	public void writeObject(Obj input) {
		refreshWritable();
		if (!value.isWritable()) return;
		
		Encodable val;
		if (input.isNull()) {
			val = new Null();
		} else if (input instanceof Real) {
			val = new com.serotonin.bacnet4j.type.primitive.Real((float) input.getReal());
			value.setReal(input.getReal());
		} else if (input instanceof Int) {
			val = new com.serotonin.bacnet4j.type.primitive.Real((float) input.getInt());
			value.setReal(input.getInt());
		} else if (input instanceof Bool){
			val = new com.serotonin.bacnet4j.type.primitive.Real( (input.getBool()?(100):(0)));
			value.setReal((input.getBool()?(100):(0)));
		} else {
			return;
		}
		
		try {
			log.info("Writing " + val + " on " + deviceID + ", " + objectIdentifier + ", " + propertyIdentifier);
			bacnetConnector.writeProperty(deviceID, objectIdentifier, propertyIdentifier, 
					val, BACnetConnector.BACNET_PRIORITY);
		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
		
		if (input.isNull()) refreshObject();
	}
	
	public Real value() {
		return this.value;
	}
	
	@Override
	public void refreshObject(){
		log.finest("refreshing analog value.");
		super.refreshObject();
		
		try {
			// value
			Encodable property = bacnetConnector.readProperty(deviceID, objectIdentifier, propertyIdentifier);
			float newValue = ((com.serotonin.bacnet4j.type.primitive.Real) property).floatValue();
			if(property instanceof com.serotonin.bacnet4j.type.primitive.Real){
				if(value.get() != newValue)
					value.set(newValue);
			}
			
			// units
			if (value.getUnit() == null) {
				property = bacnetConnector.readProperty(deviceID, objectIdentifier, PropertyIdentifier.units);
				if(property instanceof EngineeringUnits) {
					int unit = ((EngineeringUnits) property).intValue();
					String bUnit = BacnetUnits.getUnit(unit);
					if (bUnit != null) {
						String unitUri = "obix:units/" + bUnit;
						value.setUnit(new Uri(unitUri));
					}
				}
			}
		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
	}
}
