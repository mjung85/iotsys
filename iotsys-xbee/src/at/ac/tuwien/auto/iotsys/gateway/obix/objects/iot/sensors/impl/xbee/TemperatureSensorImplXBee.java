package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.xbee;

import java.util.logging.Logger;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.TemperatureSensorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.xbee.XBeeConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.xbee.XBeeWatchdog;

public class TemperatureSensorImplXBee extends TemperatureSensorImpl {
	
	private static final Logger log = Logger.getLogger(TemperatureSensorImplXBee.class.getName());
	
	private XBeeConnector connector;
	private String hexAddress;
	
	public TemperatureSensorImplXBee(XBeeConnector connector, String hexAddress){	
		this.connector = connector;
		this.hexAddress = hexAddress;
		
		connector.addWatchDog(hexAddress, new XBeeWatchdog(){

			@Override
			public void notifyWatchDog(XBeeResponse response) {
				if(response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE){
					
					ZNetRxIoSampleResponse ioSample = (ZNetRxIoSampleResponse) response;
					int[] addressArray = ioSample.getRemoteAddress64().getAddress();
					String[] hexAddress = new String[addressArray.length];
					for(int i=0; i<addressArray.length; i++){
						hexAddress[i] = String.format("%02x", addressArray[i]);
					}
					
					Integer temperatureMax = 1023;
					double tempValue = 0;
					tempValue = ioSample.getAnalog2();
					double temperatureFahrenheitValue = (131*(double)tempValue)/(double)temperatureMax;
					double temperatureCelsiusValue = (temperatureFahrenheitValue-32)*5/9;
					
					if(TemperatureSensorImplXBee.this.value().get() != temperatureCelsiusValue)
						TemperatureSensorImplXBee.this.value.set(temperatureCelsiusValue);
					
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
