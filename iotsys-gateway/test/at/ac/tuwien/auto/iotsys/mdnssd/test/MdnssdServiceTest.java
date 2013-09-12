package at.ac.tuwien.auto.iotsys.mdnssd.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.auto.iotsys.gateway.test.AbstractGatewayTest;
import at.ac.tuwien.auto.iotsys.mdnssd.MdnsResolverImpl;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class MdnssdServiceTest extends AbstractGatewayTest {

	String[] testDeviceNames;
	String[] testDeviceAddr;

	private CountDownLatch lock = new CountDownLatch(1);
	ArrayList<ServiceEvent> resolvedEvents = new ArrayList<ServiceEvent>();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see at.ac.tuwien.auto.iotsys.gateway.test.AbstractGatewayTest#setUp()
	 */
	@Before
	public void initialize() {
		testDeviceNames = new String[2];
		testDeviceAddr = new String[2];

		testDeviceNames[0] = "brightnessSensor1.testdevices.iotsys.auto.tuwien.ac.at.";
		testDeviceNames[1] = "sunblind1.testdevices.iotsys.auto.tuwien.ac.at.";

		testDeviceAddr[0] = "2001:629:2500:570::10d";
		testDeviceAddr[1] = "2001:629:2500:570::11b";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.ac.tuwien.auto.iotsys.gateway.test.AbstractGatewayTest#tearDown()
	 */
	@After
	public void finalize() {
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.iotsys.mdnssd.MdnsResolverImpl#resolve(java.lang.String)}
	 * .
	 */
	@Test
	public void testResolve() {
		for (int i = 0; i < testDeviceNames.length; i++) {
			assertEquals(MdnsResolverImpl.getInstance().resolve(testDeviceNames[i]), testDeviceAddr[i]);
		}
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.iotsys.mdnssd.MdnsResolverImpl#registerDevice(java.lang.String, java.lang.Class, java.lang.String)}
	 * .
	 */
	@Test
	public void testRegisterDevice() {
		try {
			MdnsResolverImpl.getInstance().registerDevice("aTestDeviceName1", Class
					.forName("at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.IndoorBrightnessSensorImpl"),
					"2001:629:2500:570::10d");
			MdnsResolverImpl.getInstance().registerDevice("aTestDeviceName2", Class
					.forName("at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.SunblindActuatorImpl"), 
					"2001:629:2500:570::11b");
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			JmDNS jmdns = JmDNS.create(InetAddress.getByName("fe80::acbc:b659:71db:5cb7%20"));
			jmdns.addServiceListener("_obix._coap.local.", new ServiceListener(){

				@Override
				public void serviceAdded(ServiceEvent event) {
				}

				@Override
				public void serviceRemoved(ServiceEvent event) {
				}

				@Override
				public void serviceResolved(ServiceEvent event) {
					resolvedEvents.add(event);
				}
			});
			lock.await(10000, TimeUnit.MILLISECONDS);
			ArrayList<String> eventName = new ArrayList<String>();
			for (ServiceEvent e : resolvedEvents){
				eventName.add(e.getName());
			}
			
			assertTrue(eventName.contains("aTestDeviceName1".toLowerCase()));
			assertTrue(eventName.contains("aTestDeviceName2".toLowerCase()));
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
