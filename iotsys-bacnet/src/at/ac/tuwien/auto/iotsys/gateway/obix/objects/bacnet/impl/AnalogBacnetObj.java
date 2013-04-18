package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl;

import java.util.logging.Logger;

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
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

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
		if (!value.isWritable()) return;
		
		if (input instanceof Real) {
			value.setReal(input.getReal());
			
			try {
				bacnetConnector.writeProperty(deviceID, objectIdentifier, propertyIdentifier, 
						new com.serotonin.bacnet4j.type.primitive.Real((float) this.value().get()),
						new UnsignedInteger(10));
			} catch (BACnetException e) {
				e.printStackTrace();
			} catch (PropertyValueException e) {
				e.printStackTrace();
			}
		}

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
				property = bacnetConnector.readProperty(deviceID, objectIdentifier, new PropertyIdentifier(117));
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
