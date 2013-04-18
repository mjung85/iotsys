package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl;

import obix.Contract;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.AnalogOutput;

public class AnalogOutputImpl extends AnalogBacnetObj implements AnalogOutput {
	
	public AnalogOutputImpl(BACnetConnector bacnetConnector, BacnetDataPointInfo dataPointInfo) {
		super(bacnetConnector, dataPointInfo);
		
		value().setWritable(true);
		setIs(new Contract(AnalogOutput.CONTRACT));
	}
}
