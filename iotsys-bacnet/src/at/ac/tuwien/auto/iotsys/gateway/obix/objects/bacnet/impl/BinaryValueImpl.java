package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl;

import obix.Contract;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.BinaryValue;

public class BinaryValueImpl extends BinaryBacnetObj implements BinaryValue {
	public BinaryValueImpl(BACnetConnector bacnetConnector, BacnetDataPointInfo dataPointInfo) {
		super(bacnetConnector, dataPointInfo);
		
		setIs(new Contract(BinaryValue.CONTRACT));
		value().setWritable(true);
	}
}
