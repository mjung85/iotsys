package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

import org.apache.log4j.PropertyConfigurator;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

public class ResponseExample {

	private XBee xbee = new XBee();
	public ResponseExample() throws XBeeException{
		
		xbee.open("COM9", 9600);
		
		try{
			while(true){
				
				XBeeResponse response = xbee.getResponse(10000);
				System.err.println("Response: "+response.toString());
				if(response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE){
					System.err.println("Response: " +response.getLength());
					
					ZNetRxIoSampleResponse ioSample = (ZNetRxIoSampleResponse) response;
					int[] addressArray = ioSample.getRemoteAddress64().getAddress();
					String[] hexAddress = new String[addressArray.length];
					for(int i=0; i<addressArray.length; i++){
						hexAddress[i] = String.format("%02x", addressArray[i]);
					}
/*					
					for(int i=0; i<hexAddress.length; i++){
						System.err.println("String Array: " +hexAddress[i]);
					}
*/					
					System.err.println("Value first input pin: "+ioSample.getAnalog0());
					System.err.println("Value first input pin Luminosit�: "+ioSample.getAnalog1());
					
					Integer lightMax = 1023;
					Integer lightValue = ioSample.getAnalog1();
					Integer temperatureMax = 1023;
					Integer tempValue = ioSample.getAnalog2();
					
					//double intensityPercentualValue = (ioSample.getAnalog0() - (ioSample.getAnalog0()-ioSample.getAnalog1()));
					double lightPercentualValue = (lightMax - (lightMax-lightValue))*100/1023;
					double temperatureFahrenheitValue = (131*(double)tempValue)/(double)temperatureMax;
					double temperatureCelsiusValue = (temperatureFahrenheitValue-32)*5/9;
					
					System.err.println("Light Intensity Value: "+lightPercentualValue+"%");
					System.err.println("Temperature Fahrenheit Value: "+temperatureFahrenheitValue+"�F");
					System.err.println("Temperature Celsius Value: "+temperatureCelsiusValue+"�C");

/*					
					System.err.println("Value first input pin Temperature: "+ioSample.getAnalog2());
					System.err.println("Value first input pin: "+ioSample.getAnalog3());
					
					System.err.println("Response from: " +addressArray);
					System.err.println("Analog D0 (pin 20) 10-bit reading is " + ioSample.getAnalog0());
					System.err.println("Digital D4 (pin 11) is " + (ioSample.isD4On() ? "on" : "off"));
*/
				}
			}
		}catch(XBeeTimeoutException e){
			System.err.println("Exception: " +e);
		}
	}
	
	/**
	 * @param args
	 * @throws XBeeException 
	 */
	public static void main(String[] args) throws XBeeException {
		PropertyConfigurator.configure("log4j.properties");
		new ResponseExample();
	}
}
