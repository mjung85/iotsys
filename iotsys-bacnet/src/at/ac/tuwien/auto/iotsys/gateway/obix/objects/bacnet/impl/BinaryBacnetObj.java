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
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public abstract class BinaryBacnetObj extends BacnetObj {
	private static final Logger log = Logger.getLogger(BinaryBacnetObj.class.getName());
	protected Bool value = new Bool(false);
	
	public BinaryBacnetObj(BACnetConnector bacnetConnector, BacnetDataPointInfo dataPointInfo) {
		super(bacnetConnector, dataPointInfo);
		
		Uri valueUri = new Uri("value");
		
		value.setHref(valueUri);
		value.setName("value");
		add(value);
	}
	
	public void writeObject(Obj input) {
		if (!value.isWritable()) return;
		
		if (input instanceof Bool) {
			value.setBool(input.getBool());
		
			try {
				int active = (this.value.get()) ? 1 : 0;
				bacnetConnector.writeProperty(deviceID, objectIdentifier, propertyIdentifier,
						new BinaryPV(active), new UnsignedInteger(10));
			} catch (BACnetException e) {
				e.printStackTrace();
			} catch (PropertyValueException e) {
				e.printStackTrace();
			}
		}

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
