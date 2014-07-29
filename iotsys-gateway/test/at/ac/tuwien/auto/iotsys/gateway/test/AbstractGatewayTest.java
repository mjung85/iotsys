package at.ac.tuwien.auto.iotsys.gateway.test;

import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import at.ac.tuwien.auto.iotsys.commons.MdnsResolver;
import at.ac.tuwien.auto.iotsys.gateway.IoTSySGateway;

public class AbstractGatewayTest {
	private static IoTSySGateway gateway;
	
	@BeforeClass
	public static void setUp() {
		gateway = new IoTSySGateway();

		try {
			Class mc = Class.forName("at.ac.tuwien.auto.iotsys.mdnssd.MdnsResolverImpl");
			MdnsResolver m = (MdnsResolver) mc.getDeclaredMethod("getInstance", null).invoke(null, null);
			gateway.setMdnsResolver(m);

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gateway.startGateway("config/devices.test.xml");
		org.junit.Assume.assumeTrue(gateway.getObjectBroker().getConfigDb() != null);
		gateway.getObjectBroker().getConfigDb().setMigrating(false);

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
