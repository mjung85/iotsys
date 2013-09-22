/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

package at.ac.tuwien.auto.iotsys.mdnssd.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.auto.iotsys.commons.MdnsResolver;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.gateway.test.AbstractGatewayTest;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class MdnssdServiceTest extends AbstractGatewayTest {

	String[] testDeviceNames;
	String[] testDeviceAddr;
	
	MdnsResolver m;
	
	private CountDownLatch lock = new CountDownLatch(1);
	ArrayList<ServiceEvent> resolvedEvents = new ArrayList<ServiceEvent>();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see at.ac.tuwien.auto.iotsys.gateway.test.AbstractGatewayTest#setUp()
	 */
	@Before
	public void initialize() {
		
		try {
			Class mc = Class.forName("at.ac.tuwien.auto.iotsys.mdnssd.MdnsResolverImpl");
			m = (MdnsResolver) mc.getDeclaredMethod("getInstance", null).invoke(null, null);

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
			assertEquals(m.resolve(testDeviceNames[i]), testDeviceAddr[i]);
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
			JmDNS jmdns = JmDNS.create(InetAddress.getByName(PropertiesLoader.getInstance().getProperties()
					.getProperty("iotsys.gateway.authNsAddr6", "fe80::acbc:b659:71db:5cb7%20")));
			jmdns.addServiceListener("_obix._coap." + PropertiesLoader.getInstance().getProperties()
					.getProperty("iotsys.gateway.authDomain", "local."), new ServiceListener(){

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
			lock.await(20000, TimeUnit.MILLISECONDS);
			ArrayList<String> eventNames = new ArrayList<String>();
			for (ServiceEvent e : resolvedEvents){
				eventNames.add(e.getName());
			}
			// test all elements in testDeviceNames are returned in eventNames
			for (String qName : testDeviceNames){
				// if eventNames not contain qName -> fail
				String fk = qName.split("\\.")[0].toLowerCase();
				if (!eventNames.contains(fk))
					fail();
			}
			
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
