package at.ac.tuwien.auto.iotsys.gateway.connectors.xbee;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

import at.ac.tuwien.auto.iotsys.commons.Connector;

public class XBeeConnector implements Connector{
	private static final Logger log = Logger.getLogger(XBeeConnector.class.getName());

	String port;
	int baudRate;
	private XBee xbee;
	
	private final Hashtable<String, ArrayList<XBeeWatchdog>> watchDogs = new Hashtable<String, ArrayList<XBeeWatchdog>>();
	
	public XBeeConnector(String port, int baudRate) {
		this.port = port;
		this.baudRate = baudRate;
	}
	
	@Override
	public void connect() throws Exception {
		log.info(port +", "+baudRate);
		xbee = new XBee();
		xbee.open(port, baudRate);
		xbee.addPacketListener(new PacketListener(){

			@Override
			public void processResponse(XBeeResponse response) {
				if(response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE){
					ZNetRxIoSampleResponse ioSample = (ZNetRxIoSampleResponse) response;
					int[] addressArray = ioSample.getRemoteAddress64().getAddress();
					StringBuffer hexAddress = new StringBuffer();
					
					for(int i=0; i<addressArray.length; i++){
						hexAddress.append(String.format("%02x", addressArray[i]));
					}
					String source = hexAddress.toString();
					log.finest("Received packet from: " + hexAddress);	
					
					synchronized (watchDogs) {

						if (watchDogs.containsKey(source)) {
							for (XBeeWatchdog dog : watchDogs
									.get(source)) {
								dog.notifyWatchDog(response);
							}
						}
						else{
							log.finest("There is no watchdog registered for " + hexAddress);
						}
					}
				}
			}	
		});
		
	}

	@Override
	public void disconnect() throws Exception {
		xbee.close();	
	}
	
	public void addWatchDog(String observation, XBeeWatchdog xbeeWatchdog) {
		synchronized (watchDogs) {
			if (!watchDogs.containsKey(observation)) {
				watchDogs.put(observation,
						new ArrayList<XBeeWatchdog>());
			}
			log.finest("Adding watchdog for address "
					+ observation);
			watchDogs.get(observation).add(xbeeWatchdog);
		}
	}
	
	@Override
	public boolean isCoap() {
		return false;
	}
}