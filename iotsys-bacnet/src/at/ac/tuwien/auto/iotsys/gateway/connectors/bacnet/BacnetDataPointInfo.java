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

package at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet;

import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

public class BacnetDataPointInfo {
	private int deviceIdentifier = 0;
	private ObjectIdentifier objectIdentifier = null;	
	private PropertyIdentifier propertyIdentifier = null;

	public ObjectIdentifier getObjectIdentifier() {
		return objectIdentifier;
	}

	public void setObjectIdentifier(ObjectIdentifier objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}


	public PropertyIdentifier getPropertyIdentifier() {
		return propertyIdentifier;
	}

	public void setPropertyIdentifier(PropertyIdentifier propertyIdentifier) {
		this.propertyIdentifier = propertyIdentifier;
	}

	public int getDeviceIdentifier() {
		return deviceIdentifier;
	}

	public void setDeviceIdentifier(int deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	public BacnetDataPointInfo(int deviceIdentifier,
			ObjectIdentifier objectIdentifier, 
			PropertyIdentifier propertyIdentifier) {
		super();
		this.deviceIdentifier = deviceIdentifier;
		this.objectIdentifier = objectIdentifier;
		this.propertyIdentifier = propertyIdentifier;
	}

	public String toString() {
		return "BacnetDataPointInfo [deviceIdentifier=" + deviceIdentifier
				+ ", objectIdentifier=" + objectIdentifier
				+ ", propertyIdentifier=" + propertyIdentifier + "]";
	}	
}

	
