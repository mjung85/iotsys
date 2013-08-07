package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.xbee;

import java.util.logging.Logger;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.IndoorBrightnessSensorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.xbee.XBeeConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.xbee.XBeeWatchdog;

public class IndoorBrightnessSensorImplXBee extends IndoorBrightnessSensorImpl {
	
	private static final Logger log = Logger.getLogger(IndoorBrightnessSensorImplXBee.class.getName());
	
	private XBeeConnector connector;
	private String hexAddress;
	
	public IndoorBrightnessSensorImplXBee(XBeeConnector connector, String hexAddress){	
		this.connector = connector;
		this.hexAddress = hexAddress;
		
		connector.addWatchDog(hexAddress, new XBeeWatchdog(){

			@Override
			public void notifyWatchDog(XBeeResponse response) {
				if(response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE){
					log.finest("Received brightness update.");
					ZNetRxIoSampleResponse ioSample = (ZNetRxIoSampleResponse) response;
					int[] addressArray = ioSample.getRemoteAddress64().getAddress();
					String[] hexAddress = new String[addressArray.length];
					for(int i=0; i<addressArray.length; i++){
						hexAddress[i] = String.format("%02x", addressArray[i]);
					}
					
				
					Integer brightnessMax = 1023;
					double brightnessValue = 0;
					brightnessValue = ioSample.getAnalog1();
					
					
					double brightnessPercentalValue = (brightnessMax - (brightnessMax-brightnessValue))*100/1023;
					
					
					if(IndoorBrightnessSensorImplXBee.this.roomIlluminationValue().get() != brightnessPercentalValue){
						IndoorBrightnessSensorImplXBee.this.roomIlluminationValue().set(brightnessPercentalValue);
					}

					
				}
			}
			
		});
	}
	
	public void refreshObject(){
		
	}
	
	@Override
	public void initialize(){
		super.initialize();
	}

}
