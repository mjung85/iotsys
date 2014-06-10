package at.ac.tuwien.auto.iotsys.gateway.util;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;

import obix.Bool;
import obix.Int;
import obix.Real;
import obix.io.ObixEncoder;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Message.messageType;

public class MulticastTestRequest {
	public static void main(String[] args) {

		Inet6Address group = null;
		try {
			group = (Inet6Address) Inet6Address.getByName("FF15::1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		for (int l = 0; l < 1002; l++) {
			System.out.println("Current request: " + l);
			PUTRequest putRequest = new PUTRequest();
			putRequest.setType(messageType.NON);
			putRequest.setURI("coap://[" + group.getHostAddress() + "]:5683/");
			putRequest.setSize(128);			
		
			String payload = "<bool val=\"true\"/>";
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
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
