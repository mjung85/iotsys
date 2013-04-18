package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl;

import obix.Contract;
import obix.Real;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.AnalogValue;

public class AnalogValueImpl extends AnalogBacnetObj implements AnalogValue {
	protected Real value = new Real(0);
	
	public AnalogValueImpl(BACnetConnector bacnetConnector, BacnetDataPointInfo dataPointInfo) {
		super(bacnetConnector, dataPointInfo);
		
		setIs(new Contract(AnalogValue.CONTRACT));
		value().setWritable(true);
	}
}
