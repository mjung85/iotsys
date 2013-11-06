package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.rfid;

import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.RfidWatchdog;
import at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.util.RfidFrame;
import at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.RfidConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.rfid.EventImpRfidTAG;

public class EventImpRfidTAG extends EventImplRFID {
	private static final Logger log = Logger.getLogger(EventImpRfidTAG.class.getName());
		
	private RfidConnector connector;
	private String hexAddress;
	
	public EventImpRfidTAG(RfidConnector connector, final String hexAddress) {
		this.connector = connector;
		this.hexAddress = hexAddress;
		
		log.info("RFID Address: " + hexAddress);
		
		connector.addWatchDog(hexAddress, new RfidWatchdog() {
			
			@Override
			public void notifyWatchDog(RfidFrame payload) {
	
				//log.info(payload.getPacketData().toString());
				log.info("WATCHDOG: " + payload.dataToString());
			}
		});
	}
}




/*
connector.addWatchDog(hexAddress, new RfidWatchdog() {

@Override
public void notifyWatchDog(RfidFrame payload) {
			if (payload.getPacket().telegram.getPayloadAsString().equals("0x50")) {
		log.info(hexAddress + "switch on");
		value().set(true);
	}
	if (payload.getPacket().telegram.getPayloadAsString().equals("0x70")) {
		log.info(hexAddress + "switch off");					
		value().set(false);
	}
			
	log.info(payload.dataToString());
}
});
*/