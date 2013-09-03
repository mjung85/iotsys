package at.ac.tuwien.auto.iotsys.gateway.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import at.ac.tuwien.auto.iotsys.gateway.IoTSySGateway;

public class AbstractGatewayTest {
	private static IoTSySGateway gateway;
	
	@BeforeClass
	public static void setUp() {
		gateway = new IoTSySGateway();
		gateway.startGateway("config/devices.test.xml");
	}
	
	@AfterClass
	public static void tearDown() {
		gateway.stopGateway();
	}
	
	public static void main(String[] args){
		gateway = new IoTSySGateway();
		gateway.startGateway("config/devices.test.xml");
	}

}
