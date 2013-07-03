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

import java.util.logging.Logger;

import obix.Obj;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl.AnalogInputImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl.AnalogOutputImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl.AnalogValueImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl.BinaryInputImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl.BinaryOutputImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl.BinaryValueImpl;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

public class BacnetDeviceFactory {
	private static Logger log = Logger.getLogger(BacnetDeviceFactory.class.getName());
	
	public static final int ANALOG_INPUT  = 0;
	public static final int ANALOG_OUTPUT = 1;
	public static final int ANALOG_VALUE  = 2;
	public static final int BINARY_INPUT  = 3;
	public static final int BINARY_OUTPUT = 4;
	public static final int BINARY_VALUE  = 5;
	
	public static Obj createDevice(BACnetConnector bacnetConnector, RemoteDevice device, ObjectIdentifier objIdentifier) {
		BacnetDataPointInfo bacnetDataPointInfo =
				new BacnetDataPointInfo(device.getInstanceNumber(), objIdentifier, PropertyIdentifier.presentValue);
		
		switch(objIdentifier.getObjectType().intValue()) {
		case ANALOG_INPUT:
			return new AnalogInputImpl(bacnetConnector, bacnetDataPointInfo);
		case ANALOG_OUTPUT:
			return new AnalogOutputImpl(bacnetConnector, bacnetDataPointInfo);
		case ANALOG_VALUE:
			return new AnalogValueImpl(bacnetConnector, bacnetDataPointInfo);
		case BINARY_INPUT:
			return new BinaryInputImpl(bacnetConnector, bacnetDataPointInfo);
		case BINARY_OUTPUT:
			return new BinaryOutputImpl(bacnetConnector, bacnetDataPointInfo);
		case BINARY_VALUE:
			return new BinaryValueImpl(bacnetConnector, bacnetDataPointInfo);
		default:
			log.warning("No mapping available for bacnet object-type " + objIdentifier.getObjectType().intValue());
			return null;
		}
	}
}
