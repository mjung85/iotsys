package at.ac.tuwien.auto.iotsys.gateway.connectors.coap;

import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.ResponseHandler;

public class Test {
	
	public static void main(String[] args){
		CoapConnector connector = new CoapConnector();
		Double readDouble = connector.readDouble("coap://[aaaa::c30c:0:0:2]:5683/temp", "value", new ResponseHandler() {
				public void handleResponse(Response response) {	
					double temp = Double.parseDouble( CoapConnector.extractAttribute("real", "val",
							response.getPayloadString().trim()));
					System.out.println("Observed temp: " + temp);
									
				}
			});
		System.out.println("Temp is: " + readDouble);
	}

}
