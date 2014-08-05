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

import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.auto.iotsys.commons.Named;
import at.ac.tuwien.auto.iotsys.gateway.test.AbstractGatewayTest;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class DnsServiceTest extends AbstractGatewayTest {

	Named n;

	String[] testDeviceNames;
	String[] testDeviceAddr;

	Hashtable<String, String> env;

	@Before
	public void initialize() {
		
		try {
			n = (Named) Class.forName("at.ac.tuwien.auto.iotsys.mdnssd.NamedImpl").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
//	@Test
//	public void testStartNamedService() {
//		n.startNamedService();
//		for (int i = 0; i < testDeviceNames.length; i++) {
//
//			Attributes returnAttributes = null;
//			NamingEnumeration<?> attributeEnum = null;
//
//			DirContext ictx;
//			try {
//				ictx = new InitialDirContext(env);
//				returnAttributes = ictx.getAttributes(testDeviceNames[i], new String[] { "AAAA" });
//				if (returnAttributes.size() > 0) {
//					attributeEnum = returnAttributes.get("AAAA").getAll();
//					while (attributeEnum.hasMore())
//						assertEquals(testDeviceAddr[i], (String) attributeEnum.next());
//				}
//			} catch (NamingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.iotsys.mdnssd.Named#stopNamedService()}.
	 * @throws NamingException, CommunicationException 
	 */
//	@Test
//	public void testStopNamedService() {
//		if (n.isStart())
//			n.stopNamedService();
//		
//		DirContext ictx;
//		try {
//			ictx = new InitialDirContext(env);
//			Attributes a = ictx.getAttributes("sunblind1.testdevices.iotsys.auto.tuwien.ac.at", new String[] { "AAAA" });
//			fail();
//		} catch (NamingException e) {
//		} 
//	}

}
