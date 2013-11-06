package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.enocean;

import java.util.logging.Logger;

import obix.Bool;

import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.EnoceanConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.EnoceanWatchdog;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3Frame;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sensors.impl.PushButtonImpl;
// eigenes objekt erstellen usw ...

public class WindowSwitchImplEnocean extends PushButtonImpl {
	private static final Logger log = Logger.getLogger(PushButtonImplEnocean.class.getName());
	
	private EnoceanConnector connector;
	private String hexAddress;
	
	public WindowSwitchImplEnocean(EnoceanConnector connector, final String hexAddress) {
		this.connector = connector;
		this.hexAddress = hexAddress;
		
		log.info("EnOcean Address: " + hexAddress);
		
		connector.addWatchDog(hexAddress, new EnoceanWatchdog() {
			
			@Override
			public void notifyWatchDog(ESP3Frame payload) {
				if (payload.getPacket().telegram.getPayloadAsString().equals("0x08")) {
					log.info(hexAddress + "switch on");
					value().set(true);
				}
				if (payload.getPacket().telegram.getPayloadAsString().equals("0x09")) {
					log.info(hexAddress + "switch off");					
					value().set(false);
				}
				
				log.info(payload.getPacket().telegram.getPayloadAsString());
			}
		});
	}
}
