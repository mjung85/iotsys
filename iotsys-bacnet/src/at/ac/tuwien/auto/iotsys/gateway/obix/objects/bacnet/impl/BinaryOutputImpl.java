package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.impl;

import obix.Contract;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BACnetConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDataPointInfo;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.BinaryOutput;

public class BinaryOutputImpl extends BinaryBacnetObj implements BinaryOutput {
	public BinaryOutputImpl(BACnetConnector bacnetConnector, BacnetDataPointInfo dataPointInfo) {
		super(bacnetConnector, dataPointInfo);
		
		setIs(new Contract(BinaryOutput.CONTRACT));
		value().setWritable(true);
	}
}
