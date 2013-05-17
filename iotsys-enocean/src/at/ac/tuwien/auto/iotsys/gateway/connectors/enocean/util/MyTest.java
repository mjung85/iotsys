package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.AtCommand;
import com.rapplogic.xbee.api.RemoteAtRequest;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.wpan.IoSample;
import com.rapplogic.xbee.api.wpan.RxResponseIoSample;

public class MyTest {
	private final static Logger log = Logger.getLogger(TestResponse.class);

	public MyTest() throws XBeeException, IOException {

	}

	public static void main(String[] args) {
		XBee xbee = new XBee();
		try {
			xbee.open("COM9", 9600);
			
			XBeeResponse response = xbee.getResponse(10000);

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
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
	
//			XBeeAddress64 address64 = new XBeeAddress64(address);
//			// get the Node discovery timeout
//			xbee.sendAsynchronous(new AtCommand("ND"));

//			//XBeeResponse response = xbee.getResponse();
//			
//			XBeeAddress address = new XBeeAddress64(0, 0x13, 0xa2, 0, 0x40, 0x0a, 0x3e, 0x02);
//
//			// pin 20 corresponds to D0, and 5 activates the output (Digital output high) 
//			RemoteAtRequest request = new RemoteAtRequest(address, "D0", new int[] {5});
//			try {
//			    // send a request and wait up to 10 seconds for the response
//			     response = xbee.sendSynchronous(new AtCommand("NT"), 10*1000);
//			} catch (XBeeTimeoutException e) {
//			    e.printStackTrace();
//			}
//			System.err.println("Response: " + response);
//
//			ApiId apiId = response.getApiId();
//			if (apiId == ApiId.AT_COMMAND_QUEUE) {
//				System.err.println("1");
//			} else if (apiId == ApiId.AT_COMMAND) {
//				System.err.println("2");
//			} else if (apiId == ApiId.AT_RESPONSE) {
//				
//				System.err.println("3");
//			}
//		} catch (XBeeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

