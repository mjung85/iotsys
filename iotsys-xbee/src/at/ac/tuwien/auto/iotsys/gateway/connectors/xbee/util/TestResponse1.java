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
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;

public class TestResponse1 {

	private final static Logger log = Logger.getLogger(TestResponse1.class);
	
	private XBee xbee = new XBee();
	
	public TestResponse1() throws XBeeException, IOException{
		
		xbee.open("COM9", 9600);
		
		// get the Node discovery timeout
		xbee.sendAsynchronous(new AtCommand("NT"));
		
		XBeeResponse response = xbee.getResponse();
		
		System.out.println("Response: "+response);
		
		
		
	}
	/**
	 * @param args
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XBeeException, IOException {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure("log4j.properties");
		new TestResponse1();

	}

}
