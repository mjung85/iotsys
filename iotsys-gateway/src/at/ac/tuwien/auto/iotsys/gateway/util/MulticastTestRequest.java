package at.ac.tuwien.auto.iotsys.gateway.util;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;

import at.ac.tuwien.auto.iotsys.gateway.connectors.coap.CoapConnector;
import obix.Bool;
import obix.Int;
import obix.Real;
import obix.io.ObixEncoder;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.Message.messageType;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;

public class MulticastTestRequest {
	private static String host = "128.130.56.29";
	
	private static String groupCommJoinUrl = "coap://" + host + "/VirtualDevices/virtualLight/value/groupComm/joinGroup";
	private static String groupCommLeaveUrl = "coap://" + host + "/VirtualDevices/virtualLight/value/groupComm/leaveGroup";

	private static String payload = "{\"is\":\"iot:LightSwitchActuator\",\"nodes\":[{\"val\":true,\"tag\":\"bool\",\"writable\":true,\"name\":\"value\",\"displayName\":\"On/Off\",\"href\":\"value\"},{\"is\":\"iot:GroupComm\",\"tag\":\"ref\",\"name\":\"value groupComm\",\"href\":\"value/groupComm\"}],\"tag\":\"obj\",\"href\":\"/VirtualDevices/virtualLight/\"}"; 
	private static String groupCommPayload = "{\"val\":\"FF15::1\",\"tag\":\"str\"}";
	
	public static void main(String[] args) {
				
		// Init
		boolean init = false;
		if(init){
			CoapConnector coapConnector = new CoapConnector();	
			
			Request request = null;
	
			String payload = new String("<str val=\"FF15::1\"/>");				
			request = new POSTRequest();
			request.setPayload(payload);				
			request.setType(messageType.CON);
			request.setSize(64);
			
			request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.CONTENT_TYPE));
			request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.ACCEPT));
			// specify URI of target endpoint
			request.setURI(groupCommJoinUrl);
			// enable response queue for blocking I/O
			request.enableResponseQueue(false);
			
			// request.setContentType(MediaTypeRegistry.APPLICATION_EXI);
			// request.setAccept(MediaTypeRegistry.APPLICATION_XML);
			
			try {
				System.out.println("Putting tempUri: " + groupCommJoinUrl);
				request.execute();
				
			} catch (IOException e) {
				System.err.println("Failed to execute request: " + e.getMessage());
			}
							
			// receive response
			try {
				Response response = request.receiveResponse();
				if(response != null && response.getPayloadString() != null){
					System.out.println("response: " + response.getPayloadString());
				}										
			} catch (InterruptedException e) {
				System.err.println("Receiving of response interrupted: "
						+ e.getMessage());
			}	
		}
				
		// send multicast requests		
		Inet6Address group = null;
		try {
			group = (Inet6Address) Inet6Address.getByName("FF15::1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		for (int l = 0; l < 10002; l++) {
			System.out.println("Current request: " + l);
			PUTRequest putRequest = new PUTRequest();
			putRequest.setType(messageType.NON);
			putRequest.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.CONTENT_TYPE));
			putRequest.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.ACCEPT));
			putRequest.setURI("coap://[" + group.getHostAddress() + "]:5683/");
			putRequest.setSize(128);			
		
			payload = "<bool val=\"true\"/>";
			// work around application octet stream
			putRequest
			.setContentType(ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry.APPLICATION_XML);
			putRequest.setPayload(payload);
				
			putRequest.enableResponseQueue(false);
			try {
				putRequest.execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
