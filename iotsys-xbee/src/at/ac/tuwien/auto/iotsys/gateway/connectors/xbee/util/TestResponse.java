package at.ac.tuwien.auto.iotsys.gateway.connectors.xbee.util;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.AtCommand;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;

public class TestResponse {

	private final static Logger log = Logger.getLogger(TestResponse.class);
	
	private XBee xbee = new XBee();
	
	public TestResponse() throws XBeeException, IOException{
		
		xbee.open("COM9", 9600);
		
		// get the Node discovery timeout
		xbee.sendAsynchronous(new AtCommand("NT"));
		
		XBeeResponse response = xbee.getResponse();
		
		System.err.println("Response: "+response);
		
		ApiId apiId = response.getApiId();
		if(apiId == ApiId.AT_COMMAND_QUEUE){
			System.err.println("1");
		}else if(apiId == ApiId.AT_COMMAND){
			System.err.println("2");
		}else if(apiId == ApiId.AT_RESPONSE){
			System.err.println("3");
		}
		
		Integer termperatureValue = null;
		
		boolean flag = true;
		
		while(flag){
			if(response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE){
				
				ZNetRxIoSampleResponse ioSample = (ZNetRxIoSampleResponse) response;
				int[] addressArray = ioSample.getRemoteAddress64().getAddress();
				String[] hexAddress = new String[addressArray.length];
				for(int i=0; i<addressArray.length; i++){
					hexAddress[i] = String.format("%02x", addressArray[i]);
				}
				System.err.println("Temperature: ");
				termperatureValue = ioSample.getAnalog2();
				flag = false;

			}
			
			System.err.println("Temperature: "+termperatureValue);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//xbee.sendAsynchronous(new ZNetTxRequest(address64, payload));
		//xbee.sendAsynchronous(new ZNetTxRequest(address64, payload));		
//		int[] address = {0x00,0x13,0xa2,0x00,0x40,0x7c,0x17,0x15};
		int[] address = {0x7e,0x00,0x04,0x08,0x01,0x4e,0x54,0x54};
		
		XBeeAddress64 address64 = new XBeeAddress64(address);
		int[] payload = new int[8];
		
		
		
		xbee.sendRequest(new ZNetTxRequest(address64, payload));
		
		XBeeResponse response2 = xbee.getResponse();
		
		if(response2.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE){
			ApiId apiId2 = response2.getApiId();
			System.out.println(response2);
			//ZNetTxStatusResponse responseZNet = (ZNetTxStatusResponse) response2;
			ZNetRxIoSampleResponse responseZNet = (ZNetRxIoSampleResponse)(XBeeResponse) response2;
			//ZNetRxResponse responseZNet = (ZNetRxResponse) response2;
			System.err.println(responseZNet.getRemoteAddress64());
		}
		
		
//		
//		if(apiId2 == ApiId.TX_STATUS_RESPONSE){
//			System.err.println("1");
//		}else if(apiId2 == ApiId.RX_64_RESPONSE){
//			System.err.println("2");
//		}else if(apiId2 == ApiId.RX_16_RESPONSE){
//			System.err.println("3");
//		}else if(apiId2 == ApiId.ZNET_RX_RESPONSE){
//			System.err.println("4");
//		}else if(apiId2 == ApiId.RX_64_IO_RESPONSE){
//			System.err.println("5");
//		}else if(apiId2 == ApiId.RX_16_IO_RESPONSE){
//			System.err.println("6");
//		}else if(apiId2 == ApiId.ZNET_EXPLICIT_RX_RESPONSE){
//			System.err.println("7");
//		}else if(apiId2 == ApiId.ZNET_TX_STATUS_RESPONSE){
//			System.err.println("8");
//		}else if(apiId2 == ApiId.REMOTE_AT_RESPONSE){
//			System.err.println("9");
//		}else if(apiId2 == ApiId.ZNET_IO_SAMPLE_RESPONSE){
//			System.err.println("10");
//		}else if(apiId2 == ApiId.ZNET_IO_NODE_IDENTIFIER_RESPONSE){
//			System.err.println("11");
//		}
		
	}
	/**
	 * @param args
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XBeeException, IOException {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure("log4j.properties");
		new TestResponse();

	}

}
