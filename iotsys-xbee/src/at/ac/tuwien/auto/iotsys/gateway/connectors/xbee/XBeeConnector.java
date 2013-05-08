package at.ac.tuwien.auto.iotsys.gateway.connectors.xbee;


import java.io.IOException;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.AtCommand;
import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeRequest;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;

import at.ac.tuwien.auto.iotsys.commons.Connector;

public class XBeeConnector implements Connector{

	String port;
	int baudRate;
	private XBee xbee;
	
	public XBeeConnector(String port, int baudRate) {
		
		//PropertyConfigurator.configure("log4j.properties");
		this.port = port;
		this.baudRate = baudRate;
	}
	
	@Override
	public void connect() throws Exception {
		System.out.println(port +", "+baudRate);
		xbee = new XBee();
		xbee.open(port, baudRate);
		xbee.addPacketListener(new PacketListener(){

			@Override
			public void processResponse(XBeeResponse arg0) {
				System.out.println("XBeeResponse: " + arg0);
				
			}
			
		});
		
	}

	@Override
	public void disconnect() throws Exception {
		xbee.close();	
	}
	
	public Integer getLightValue() throws XBeeTimeoutException, XBeeException{
		XBeeResponse response = xbee.getResponse();
		Integer lightValue = null;
		
		if(response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE){
				
			ZNetRxIoSampleResponse ioSample = (ZNetRxIoSampleResponse) response;
			int[] addressArray = ioSample.getRemoteAddress64().getAddress();
			String[] hexAddress = new String[addressArray.length];
			for(int i=0; i<addressArray.length; i++){
				hexAddress[i] = String.format("%02x", addressArray[i]);
			}
			
			lightValue = ioSample.getAnalog1();
		}
		return lightValue;
	}
	
	public Integer getTemperatureValue() throws XBeeTimeoutException, XBeeException{
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
		
		//xbee.sendAsynchronous(new ZNetTxRequest(address64, payload));
		//xbee.sendAsynchronous(new ZNetTxRequest(address64, payload));		
//		int[] address = {0x00,0x13,0xa2,0x00,0x40,0x7c,0x17,0x15};
		int[] address = {0x7e,0x00,0x04,0x08,0x01,0x4e,0x54,0x54};
		
		XBeeAddress64 address64 = new XBeeAddress64(address);
		int[] payload = new int[8];
		
		try {
			xbee.sendRequest(new ZNetTxRequest(address64, payload));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response = xbee.getResponse();
		Integer temperatureValue = new Integer(0);
		
		if(response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE){
				
			ZNetRxIoSampleResponse ioSample = (ZNetRxIoSampleResponse) response;
			int[] addressArray = ioSample.getRemoteAddress64().getAddress();
			String[] hexAddress = new String[addressArray.length];
			for(int i=0; i<addressArray.length; i++){
				hexAddress[i] = String.format("%02x", addressArray[i]);
			}
			
			temperatureValue = ioSample.getAnalog2();
		}
		return temperatureValue;
	}
}