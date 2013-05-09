package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.xbee;

import java.util.logging.Logger;

import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeTimeoutException;

import at.ac.tuwien.auto.iotsys.gateway.connectors.xbee.XBeeConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.IndoorBrightnessSensorImpl;

public class IndoorBrightnessSensorImplXBee extends IndoorBrightnessSensorImpl{
	
	private static final Logger log = Logger.getLogger(TemperatureSensorImplXBee.class.getName());
	
	XBeeConnector connector;
	
	public IndoorBrightnessSensorImplXBee(XBeeConnector connector){
		
		this.connector = connector;
	}
	
	public void refreshObject(){
		
		Integer lightValue = null;
		Integer lightMax = 1023;
		
		try {
			lightValue = connector.getLightValue();
		} catch (XBeeTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double lightPercentualValue = (lightMax - (lightMax-lightValue))*100/1023;
		//System.err.println("Light Intensity Value: "+lightPercentualValue+"%");
		this.roomIlluminationValue.set(lightPercentualValue);
	}
	
	@Override
	public void initialize(){
		super.initialize();
	}

}
