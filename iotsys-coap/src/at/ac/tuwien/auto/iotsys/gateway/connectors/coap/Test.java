package at.ac.tuwien.auto.iotsys.gateway.connectors.coap;

import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.ResponseHandler;

public class Test {
	
	public static void main(String[] args){
		CoapConnector connector = new CoapConnector();
		Double readDouble = connector.readDouble("coap://[aaaa::c30c:0:0:2]:5683/temp", "value");
		System.out.println("Temp is: " + readDouble);
	}

}
