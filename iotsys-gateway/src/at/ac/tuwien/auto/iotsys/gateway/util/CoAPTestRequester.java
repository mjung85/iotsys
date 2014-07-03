package at.ac.tuwien.auto.iotsys.gateway.util;

import java.io.IOException;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.Message.messageType;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;
import at.ac.tuwien.auto.iotsys.gateway.connectors.coap.CoapConnector;

public class CoAPTestRequester {
	private static String host = "[2001:629:2500:60:1:2:0:b2]";
	private static String datapointURL = "coap://" + host + "/VirtualDevices/virtualLight/value";
	private static String objectURL = "coap://" + host + "/VirtualDevices/virtualLight";
	
	private static final String objectPayload = "<obj href=\"virtualLight/\" is=\"iot:LightSwitchActuator\"><bool name=\"value\" href=\"virtualLight/value\" val=\"false\" displayName=\"On/Off\" writable=\"true\"/><ref name=\"value groupComm\" href=\"virtualLight/value/groupComm\" is=\"iot:GroupComm\"/></obj>";
	
	public static void main(String[] args){
		if(args.length > 0){
			host = args[0];
			datapointURL = "coap://" + host + "/VirtualDevices/virtualLight/value";
			objectURL = "coap://" + host + "/VirtualDevices/virtualLight";
		}
		
		CoapTestMode coapTestMode = CoapTestMode.DATAPOINT_WRITE;
		
		if(args.length > 1){
			coapTestMode = CoapTestMode.valueOf(args[1]);
		}
		CoapConnector coapConnector = new CoapConnector();
		for(int i = 0 ; i< 1002; i++){
			System.out.println("Current request: " + i);
			if(coapTestMode == CoapTestMode.DATAPOINT_WRITE){
				Request request = null;

				String payload = new String("<bool val=\"true\"/>");				
				request = new PUTRequest();
				request.setPayload(payload);				
				request.setType(messageType.CON);
				request.setSize(64);
				
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.CONTENT_TYPE));
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.ACCEPT));
				// specify URI of target endpoint
				request.setURI(datapointURL);
				// enable response queue for blocking I/O
				request.enableResponseQueue(false);
				
				// request.setContentType(MediaTypeRegistry.APPLICATION_EXI);
				// request.setAccept(MediaTypeRegistry.APPLICATION_XML);
				
				try {
					System.out.println("Putting tempUri: " + datapointURL);
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
			}else if(coapTestMode == CoapTestMode.DATAPOINT_READ){
				Request request = null;

				request = new GETRequest();							
				request.setType(messageType.CON);
				
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.CONTENT_TYPE));
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.ACCEPT));
				// specify URI of target endpoint
				request.setURI(datapointURL);
				// enable response queue for blocking I/O
				request.enableResponseQueue(false);
				request.setSize(64);
				// request.setContentType(MediaTypeRegistry.APPLICATION_EXI);
				// request.setAccept(MediaTypeRegistry.APPLICATION_XML);
				
				try {
					System.out.println("Requesting tempUri: " + datapointURL);
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
			}else if(coapTestMode == CoapTestMode.OBJECT_WRITE){
				Request request = null;

				request = new PUTRequest();							
				request.setType(messageType.CON);
				request.setPayload(objectPayload);			
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.CONTENT_TYPE));
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.ACCEPT));			
				request.setSize(64);
				// specify URI of target endpoint
				request.setURI(objectURL);
				// enable response queue for blocking I/O
				request.enableResponseQueue(false);
				
				// request.setContentType(MediaTypeRegistry.APPLICATION_EXI);
				// request.setAccept(MediaTypeRegistry.APPLICATION_XML);
				
				try {
					System.out.println("Writing object: " + objectURL);
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
			}else if(coapTestMode == CoapTestMode.OBJECT_READ){
				Request request = null;

				request = new GETRequest();							
				request.setType(messageType.CON);
				
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.CONTENT_TYPE));
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.ACCEPT));
				// specify URI of target endpoint
				request.setURI(objectURL);
				// enable response queue for blocking I/O
				request.enableResponseQueue(false);
				request.setSize(64);
				// request.setContentType(MediaTypeRegistry.APPLICATION_EXI);
				// request.setAccept(MediaTypeRegistry.APPLICATION_XML);
				
				try {
					System.out.println("Requesting tempUri: " + objectURL);
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
		}
	}	
}

enum CoapTestMode {
	DATAPOINT_WRITE, DATAPOINT_READ, OBJECT_WRITE, OBJECT_READ
}
