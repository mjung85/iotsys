package at.ac.tuwien.auto.iotsys.gateway.connectors.xbee;


import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeRequest;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

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
		XBeeResponse response = xbee.getResponse(6000);
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
		
		XBeeResponse response = xbee.getResponse(6000);
		Integer temperatureValue = null;
		
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