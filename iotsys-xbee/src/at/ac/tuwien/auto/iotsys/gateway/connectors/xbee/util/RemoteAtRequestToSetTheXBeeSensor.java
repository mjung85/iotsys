package at.ac.tuwien.auto.iotsys.gateway.connectors.xbee.util;


import org.apache.log4j.PropertyConfigurator;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.RemoteAtRequest;
import com.rapplogic.xbee.api.RemoteAtResponse;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.wpan.IoSample;
import com.rapplogic.xbee.api.wpan.RxResponseIoSample;

public class RemoteAtRequestToSetTheXBeeSensor {
	
	private XBee xbee = new XBee();
	private XBeeResponse response;
	
	public RemoteAtRequestToSetTheXBeeSensor() throws XBeeException{
		
		xbee.open("COM3", 9600);
		
		int[] address = {0x00,0x13,0xa2,0x00,0x40,0x7c,0x17,0x17};
		XBeeAddress64 address64 = new XBeeAddress64(address);
		int[] valueIR = {0x0b,0xb8};
		int[] valueD0 = {0x02};
		int[] valueD4 = {0x03};
		
		RemoteAtRequest requestIR = new RemoteAtRequest(address64, "IR", valueIR);
		RemoteAtRequest requestD0 = new RemoteAtRequest(address64, "D0", valueD0);
		RemoteAtRequest requestD4 = new RemoteAtRequest(address64, "D4", valueD4);
		//System.err.println("Response: "+request.toString());
		//System.err.println("Response: "+request.getFrameData());
		//System.err.println("Response Byte: "+responseByte.toString());
		//System.err.println("Response Raw Data: "+response.getRawPacketBytes());
		
		RemoteAtResponse responseIR = (RemoteAtResponse) xbee.sendSynchronous(requestIR, 1000);
		RemoteAtResponse responseD0 = (RemoteAtResponse) xbee.sendSynchronous(requestD0, 1000);
		RemoteAtResponse responseD4 = (RemoteAtResponse) xbee.sendSynchronous(requestD4, 1000);
		
		if (responseIR.isOk() && responseD0.isOk() && responseD4.isOk()) {

			System.err.println("successfully turned on pin 20 (D0)");	
		} else {
			throw new RuntimeException("failed to turn IR.  status is " + responseIR.getStatus()+
										"failed to turn on pin D0.  status is " + responseD0.getStatus()+
										"failed to turn on pin D4.  status is " + responseD4.getStatus());
		}
		
		response = xbee.getResponse();

		if (response.getApiId() == ApiId.RX_16_IO_RESPONSE || response.getApiId() == ApiId.RX_64_RESPONSE) {
		        RxResponseIoSample ioSample = (RxResponseIoSample)response;
		        
		        System.err.println("Received a sample from " + ioSample.getSourceAddress());
		        System.err.println("RSSI is " + ioSample.getRssi());
		        
		        // loops IT times
		        for (IoSample sample: ioSample.getSamples()) {          
		                System.err.println("Analog D0 (pin 20) 10-bit reading is " + sample.getAnalog0());
		                System.err.println("Digital D4 (pin 11) is " + (sample.isD4On() ? "on" : "off"));
		        }
		}
		
	}

	/**
	 * @param args
	 * @throws XBeeException 
	 */
	public static void main(String[] args) throws XBeeException {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure("res/log4j.properties");
		new RemoteAtRequestToSetTheXBeeSensor();

	}

}
