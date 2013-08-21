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
	public static void main(String[] args){
		
		Inet6Address group = null;
		try {
			group = (Inet6Address) Inet6Address.getByName("FF02::1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Object state = new Bool(true);
		PUTRequest putRequest = new PUTRequest();
		putRequest.setType(messageType.NON);
		putRequest
				.setURI("coap://[" + group.getHostAddress() + "]:5684/");
		
		if(state instanceof Bool){
			Bool bool = (Bool) state;
			Bool b = new Bool();
			b.set(bool.get());
			try {
				byte[] payload =  EXIEncoder.getInstance().toBytes(b, true);
				// work around application octet stream
				putRequest.setContentType(ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry.APPLICATION_OCTET_STREAM);
				putRequest.setPayload(payload);
			} catch (Exception e){
				// fall back to XML encoding
				e.printStackTrace();
				String payload = ObixEncoder.toString(b);
				putRequest.setPayload(payload);
			}
						
		}
		else if(state instanceof Real){
			Real real = (Real) state;
			Real r = new Real();
			r.set(real.get());
			try {
				byte[] payload =  EXIEncoder.getInstance().toBytes(r, true);
				// work around application octet stream
				putRequest.setContentType(ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry.APPLICATION_OCTET_STREAM);
				putRequest.setPayload(payload);
			} catch (Exception e){
				// fall back to XML encoding
				e.printStackTrace();
				String payload = ObixEncoder.toString(r);
				putRequest.setPayload(payload);
			}
		} else if(state instanceof Int){
			Int intObj = (Int) state;
			Int i = new Int();
			i.set(intObj.get());
			try {
				byte[] payload =  EXIEncoder.getInstance().toBytes(i, true);
				// work around application octet stream
				putRequest.setContentType(ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry.APPLICATION_OCTET_STREAM);
				putRequest.setPayload(payload);
			} catch (Exception e){
				// fall back to XML encoding
				e.printStackTrace();
				String payload = ObixEncoder.toString(i);
				putRequest.setPayload(payload);
			}
		} 
	
		putRequest.enableResponseQueue(false);
		try {
			putRequest.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
