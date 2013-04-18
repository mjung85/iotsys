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
	}
	
	public void refreshObject() {
		Encodable property;

		try {
			// name
			if (name.getStr().equals("")) {
				property = bacnetConnector.readProperty(deviceID, objectIdentifier, new PropertyIdentifier(77));
				if(property instanceof CharacterString){
					String newName = ((CharacterString) property).getValue();
					name.set(newName);
				}
			}
			
			// description
			if (description.getStr().equals("")) {
				property = bacnetConnector.readProperty(deviceID, objectIdentifier, new PropertyIdentifier(28));
				if(property instanceof CharacterString){
					String newDesc = ((CharacterString) property).getValue();
					description.set(newDesc);
				}
			}
		} catch (BACnetException e) {
			e.printStackTrace();
		} catch (PropertyValueException e) {
			e.printStackTrace();
		}
	}
}
