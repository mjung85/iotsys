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
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.primitive.Null;

public abstract class BinaryBacnetObj extends BacnetObj {
	private static final Logger log = Logger.getLogger(BinaryBacnetObj.class.getName());
	protected Bool value = new Bool(false);
	
	public BinaryBacnetObj(BACnetConnector bacnetConnector, BacnetDataPointInfo dataPointInfo) {
		super(bacnetConnector, dataPointInfo);
		
		Uri valueUri = new Uri("/value");
		
		value.setHref(valueUri);
		value.setName("value");
		add(value);
	}
	
	public void writeObject(Obj input) {
		if (!value.isWritable()) return;
		
		Encodable val;
		if (input.isNull()) {
			val = new Null();
		} else if (input instanceof Bool) {
			int active = input.getBool() ? 1 : 0;
			val = new BinaryPV(active);
			value.setBool(input.getBool());
		} else {
			return;
		}
		
		try {
			bacnetConnector.writeProperty(deviceID, objectIdentifier, propertyIdentifier,
					val, BACnetConnector.BACNET_PRIORITY);
		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
		
		if (input.isNull()) refreshObject();
	}
	
	public Bool value() {
		return this.value;
	}
	
	@Override
	public void refreshObject(){
		log.finest("refreshing binary value.");
		super.refreshObject();
		
		try {
			// value
			Encodable property = bacnetConnector.readProperty(deviceID, objectIdentifier, propertyIdentifier);
			if(property instanceof BinaryPV) {
				BinaryPV newValue = ((BinaryPV) property);
				boolean active = (newValue.intValue() == 1);
				
				if(value.get() != active)
					value.set(active);
			}
		
		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
	}
}
