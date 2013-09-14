package at.ac.tuwien.auto.iotsys.mdnssd.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.auto.iotsys.gateway.test.AbstractGatewayTest;
import at.ac.tuwien.auto.iotsys.mdnssd.Named;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class DnsServiceTest extends AbstractGatewayTest {

	Named n = new Named();

	String[] testDeviceNames;
	String[] testDeviceAddr;

	Hashtable<String, String> env;

	@Before
	public void initialize() {
		
		testDeviceNames = new String[2];
		testDeviceAddr = new String[2];
		env = new Hashtable<String, String>();

		testDeviceNames[0] = "brightnessSensor1.testdevices.iotsys.auto.tuwien.ac.at";
		testDeviceNames[1] = "sunblind1.testdevices.iotsys.auto.tuwien.ac.at";

		testDeviceAddr[0] = "2001:629:2500:570::10d";
		testDeviceAddr[1] = "2001:629:2500:570::11b";

		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		env.put("java.naming.provider.url", "dns://localhost");

	}
	
	@After
	public void finalize(){
		n.stopNamedService();
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.iotsys.mdnssd.Named#startNamedService()}.
	 */
	@Test
	public void testStartNamedService() {
		n.startNamedService();
		for (int i = 0; i < testDeviceNames.length; i++) {

			Attributes returnAttributes = null;
			NamingEnumeration<?> attributeEnum = null;

			DirContext ictx;
			try {
				ictx = new InitialDirContext(env);
				returnAttributes = ictx.getAttributes(testDeviceNames[i], new String[] { "AAAA" });
				if (returnAttributes.size() > 0) {
					attributeEnum = returnAttributes.get("AAAA").getAll();
					while (attributeEnum.hasMore())
						assertEquals(testDeviceAddr[i], (String) attributeEnum.next());
				}
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.iotsys.mdnssd.Named#stopNamedService()}.
	 * @throws NamingException, CommunicationException 
	 */
	@Test
	public void testStopNamedService() {
		if (n.isStart())
			n.stopNamedService();
		
		DirContext ictx;
		try {
			ictx = new InitialDirContext(env);
			Attributes a = ictx.getAttributes("sunblind1.testdevices.iotsys.auto.tuwien.ac.at", new String[] { "AAAA" });
			fail();
		} catch (NamingException e) {
		} 
	}

}
