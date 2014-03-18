package at.ac.tuwien.auto.iotsys.gateway.connector.mbus.test;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.MBusConnector;

public class MBusTest {
	//public static String COMPORT = "/dev/ttyAMA0";
    private static String COMPORT = "COM4";
	
	public static void main(String[] args) {
		MBusConnector mbusConnector = new MBusConnector(COMPORT);
		mbusConnector.connect();
		mbusConnector.setInterval(5);;
		System.out.println("Start connect");
		
		//mbusConnector.loadTelegrams();
		
		try {				
			Thread.sleep(17000); 												
		} catch (Exception e) {
			System.out.println("Error while sleeping: " +e);
		}
		
		mbusConnector.disconnect();
		System.out.println("Disconnected");	
	}	
}

