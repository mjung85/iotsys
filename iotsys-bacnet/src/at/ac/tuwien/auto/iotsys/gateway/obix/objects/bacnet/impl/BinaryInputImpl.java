package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl;

import obix.Contract;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.BinaryInput;

public class BinaryInputImpl extends BinaryBacnetObj implements BinaryInput {
	public BinaryInputImpl(BACnetConnector bacnetConnector, BacnetDataPointInfo dataPointInfo) {
		super(bacnetConnector, dataPointInfo);
		
		setIs(new Contract(BinaryInput.CONTRACT));
		value().setWritable(false);
	}
}
