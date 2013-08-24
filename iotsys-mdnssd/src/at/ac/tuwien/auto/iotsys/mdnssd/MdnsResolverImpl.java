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

package at.ac.tuwien.auto.iotsys.mdnssd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import at.ac.tuwien.auto.iotsys.commons.MdnsResolver;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.Actuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.Sensor;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class MdnsResolverImpl implements MdnsResolver {

	private static final MdnsResolver INSTANCE = new MdnsResolverImpl();
	private ConcurrentMap<String, String> recordDict = new ConcurrentHashMap<String, String>();
	private ExecutorService executor;
	private JmDNS jmdns;

	private MdnsResolverImpl() {
		try {
			jmdns = JmDNS.create(InetAddress
					.getByName(PropertiesLoader.getInstance().getProperties().getProperty("authNsAddr6", "fe80::acbc:b659:71db:5cb7%20")));
			executor = Executors.newFixedThreadPool(10);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static MdnsResolver getInstance() {
		return INSTANCE;
	}

	@Override
	public void addToRecordDict(String name, String addr) {
		if (name.startsWith("/"))
			name = name.substring(1);
		try {
			recordDict.putIfAbsent(name + "." + Named.AUTHORITATIVE_DOMAIN,
					addr);
		} catch (NullPointerException e) {
		}
	}

	@Override
	public String resolve(String name) {
		return recordDict.get(name);
	}

	@Override
	public int getNumberOfRecord() {
		return recordDict.size();
	}

	@Override
	public void registerDevice(String name, Class<?> deviceClass, String ipv6) {
		if (name.startsWith("/"))
			name = name.substring(1);

		String serviceType = null;
		Class<?>[] deviceInterfaces = deviceClass.getInterfaces();
		if (deviceInterfaces.length == 0) {
			serviceType = "generic-device";
		} else
			for (Class<?> c : deviceInterfaces) {
				if (Sensor.class.isAssignableFrom(c)
						|| Actuator.class.isAssignableFrom(c)) {
					serviceType = c.toString().substring(c.toString().lastIndexOf(".") + 1); 
				}
			}

		serviceType = serviceType.toLowerCase();
		final HashMap<String, String> values = new HashMap<String, String>();
		values.put("IPv6", ipv6);
		ServiceInfo pairservice = ServiceInfo.create("_" + serviceType
				+ "._tcp.local.", name, 8080, 0, 0, values);
		executor.execute(new ServiceRegistra(pairservice));

	}

	private class ServiceRegistra implements Runnable {

		ServiceInfo service;

		public ServiceRegistra(ServiceInfo si) {
			this.service = si;
		}

		@Override
		public void run() {
			try {
				jmdns.registerService(service);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
