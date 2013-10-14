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

import obix.Obj;
import obix.Str;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

public abstract class BacnetObj extends Obj {
	private Str name;
	private Str description;
	
	protected int deviceID;
	protected ObjectIdentifier objectIdentifier;
	protected PropertyIdentifier propertyIdentifier;
	protected BACnetConnector bacnetConnector;
	
	public BacnetObj(BACnetConnector bacnetConnector, BacnetDataPointInfo dataPointInfo) {
		this.deviceID = dataPointInfo.getDeviceIdentifier();
		this.objectIdentifier = dataPointInfo.getObjectIdentifier();
		this.propertyIdentifier = dataPointInfo.getPropertyIdentifier();
		this.bacnetConnector = bacnetConnector;
		
		name = new Str();
		name.setHref(new Uri("name"));
		name.setName("name");
		name.setWritable(false);
		add(name);
		
		description = new Str();
		description.setHref(new Uri("description"));
		description.setName("description");
		description.setWritable(false);
		add(description);
		
//		refreshObject();
	}
	
	/**
	 * Refreshes the value's writable-status
	 */
	protected void refreshWritable() {
		return;
	}
	
	public void refreshObject() {
		refreshWritable();
		Encodable property;
		
		try {
			// name
			if (name.getStr().equals("")) {
				property = bacnetConnector.readProperty(deviceID, objectIdentifier, PropertyIdentifier.objectName);
				if(property instanceof CharacterString){
					String newName = ((CharacterString) property).getValue();
					name.set(newName);
				}
			}
			
			// description
			if (description.getStr().equals("")) {
				property = bacnetConnector.readProperty(deviceID, objectIdentifier, PropertyIdentifier.description);
				if(property instanceof CharacterString){
					String newDesc = ((CharacterString) property).getValue();
					description.set(newDesc);
					this.setDisplayName(description.toString());
				}
			}
		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public boolean isOutOfService() {
		try {
			Encodable outOfService = bacnetConnector.readProperty(
					deviceID, objectIdentifier, PropertyIdentifier.outOfService);
			
			if (outOfService instanceof Boolean) {
				Boolean oos = (Boolean) outOfService;
				return oos.booleanValue();
			}
			
		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean isValueCommandable() {
		try {
			bacnetConnector.readProperty(
					deviceID, objectIdentifier, PropertyIdentifier.priorityArray);
			bacnetConnector.readProperty(
					deviceID, objectIdentifier, PropertyIdentifier.relinquishDefault);
		} catch (PropertyValueException e) {
			return false;
		} catch (BACnetException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void initialize(){
		refreshObject();
	}
}
