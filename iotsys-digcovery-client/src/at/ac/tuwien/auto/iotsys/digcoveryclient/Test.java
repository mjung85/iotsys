package at.ac.tuwien.auto.iotsys.digcoveryclient;

import ch.ethz.inf.vs.californium.coap.GETRequest;

public class Test {
	public static void main(){
		GETRequest getRequest = new GETRequest();
		getRequest.send();
	}

}
