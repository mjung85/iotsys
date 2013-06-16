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

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.logging.Logger;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.mdnssd.Named;
import at.ac.tuwien.auto.iotsys.mdnssd.Resolver;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class TestResolver {

	private static final Logger log = Logger.getLogger(TestResolver.class.getName());

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("Init device: parsing device.xml to get all ipv6 addressable devices, building up record dictionary");
		XMLConfiguration devicesConfig = new XMLConfiguration(DeviceLoader.DEVICE_CONFIGURATION_LOCATION);
		int connectorsSize = 0;
		Object virtualConnectors = devicesConfig.getProperty("virtual.connector.name");

		if (virtualConnectors != null) {
			if (virtualConnectors instanceof String) {
				connectorsSize = 1;
			} else {
				connectorsSize = ((Collection<?>) virtualConnectors).size();
			}
		} else {
			connectorsSize = 0;
		}

		if (virtualConnectors instanceof Collection<?>) {
			virtualConnectors = ((Collection<?>) virtualConnectors).size();
		}

		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig.configurationAt("virtual.connector("
					+ connector + ")");

			Object virtualConfiguredDevices = subConfig.getProperty("device.type");
			String connectorName = subConfig.getString("name");

			try {
				if (virtualConfiguredDevices instanceof Collection<?>) {
					Collection<?> wmbusDevice = (Collection<?>) virtualConfiguredDevices;
					log.info(wmbusDevice.size()
							+ " virtual devices found in configuration for connector "
							+ connectorName);

					for (int i = 0; i < wmbusDevice.size(); i++) {
						String ipv6 = subConfig.getString("device(" + i
								+ ").ipv6");
						String href = subConfig.getString("device(" + i
								+ ").href");
						Resolver.getInstance().addToRecordDict(href, ipv6);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log.info("No of records built: " + Resolver.getInstance().getNumberOfRecord());
	}


	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.iotsys.mdnssd.Resolver#resolve(java.lang.String)}
	 * .
	 */
	@Test
	public void testResolve() {
		assertTrue(Resolver.getInstance().resolve("virtualIndoorBrightnessSensor" + "." + Named.AUTHORITATIVE_DOMAIN).equals("2001:629:2500:570::10d"));
		assertTrue(Resolver.getInstance().resolve("virtualPresence" + "." + Named.AUTHORITATIVE_DOMAIN).equals("2001:629:2500:570::10f"));
		assertTrue(Resolver.getInstance().resolve("sunblindMiddleA" + "." + Named.AUTHORITATIVE_DOMAIN).equals("2001:629:2500:570::11b"));
		assertTrue(Resolver.getInstance().resolve("virtualFanSpeed" + "." + Named.AUTHORITATIVE_DOMAIN).equals("2001:629:2500:570::11d"));
	}

}
