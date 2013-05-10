package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.xbee;

import java.util.logging.Logger;

import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeTimeoutException;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.TemperatureSensorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.xbee.XBeeConnector;

public class TemperatureSensorImplXBee extends TemperatureSensorImpl {
	
	private static final Logger log = Logger.getLogger(TemperatureSensorImplXBee.class.getName());
	
	XBeeConnector connector;
	
	public TemperatureSensorImplXBee(XBeeConnector connector){
		
		this.connector = connector;
	}
	
	public void refreshObject(){
		
		Integer tempValue = null;
		Integer temperatureMax = 1023;
		
		try {
			tempValue = connector.getTemperatureValue();
		} catch (XBeeTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double temperatureFahrenheitValue = (131*(double)tempValue)/(double)temperatureMax;
		double temperatureCelsiusValue = (temperatureFahrenheitValue-32)*5/9;
		System.out.println("Temperature 1: "+temperatureFahrenheitValue);
		System.out.println("Temperature 2: "+temperatureCelsiusValue);
		
		if(this.value().get() != temperatureCelsiusValue)
			this.value.set(temperatureCelsiusValue);
	}
	
	@Override
	public void initialize(){
		super.initialize();
	}

}
