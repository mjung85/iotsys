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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import at.ac.tuwien.auto.iotsys.commons.MdnsResolver;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.Actuator;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sensors.Sensor;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class MdnsResolverImpl implements MdnsResolver {

	private static final MdnsResolver INSTANCE = new MdnsResolverImpl();
	private ConcurrentMap<String, String> recordDict = new ConcurrentHashMap<String, String>();
	private ExecutorService executor;
	private JmDNS jmdns;
	private String gwIp = PropertiesLoader.getInstance().getProperties().getProperty("iotsys.gateway.authNsAddr6", "fe80::acbc:b659:71db:5cb7%20");
	private String domain = PropertiesLoader.getInstance().getProperties().getProperty("iotsys.gateway.authDomain", "local.");

	private MdnsResolverImpl() {
		try {
			jmdns = JmDNS.create(InetAddress.getByName(gwIp));
			executor = Executors.newFixedThreadPool(10);

			// / Making bonjour happy
			final HashMap<String, String> values = new HashMap<String, String>();
			values.put("text", "text value");
			ServiceInfo serviceSOAP = ServiceInfo.create("_obix._soap." + domain, "iotsysgateway", 8080, null);
			serviceSOAP.setText(values);
			try {
				serviceSOAP.setIpv6Addr(gwIp);
			} catch (Exception e) {
				e.printStackTrace();
			}

			executor.execute(new ServiceRegistra(serviceSOAP));

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

	public String hrefNorm(String href) {
		if (href.startsWith("/"))
			href = href.substring(1);

		String[] ar = href.split("/");
		String convertedName = "";
		int i;
		for (i = ar.length; i > 0; i--) {
			convertedName += ar[i - 1] + ".";
		}
		return convertedName;
	}

	@Override
	public void addToRecordDict(String name, String addr) {
		name = hrefNorm(name);
		try {
			recordDict.putIfAbsent(name.toLowerCase() + NamedImpl.AUTHORITATIVE_DOMAIN, addr);
		} catch (NullPointerException e) {
		}
	}

	@Override
	public String resolve(String name) {
		return recordDict.get(name.toLowerCase());
	}

	@Override
	public ArrayList<String> reverseResolve(String ipv6) {
		ArrayList<String> result = new ArrayList<String>();
		if (recordDict.containsValue(ipv6))
			for (Map.Entry<String, String> he : recordDict.entrySet())
				if (he.getValue().equals(ipv6))
					result.add(he.getKey());
		return result.isEmpty() ? null : result;
	}

	@Override
	public int getNumberOfRecord() {
		return recordDict.size();
	}

	@Override
	public void registerDevice(String deviceName, Class<?> deviceClass, String ipv6) {
		String qualifiedDeviceName = hrefNorm(deviceName).toLowerCase();
		deviceName = qualifiedDeviceName.split("\\.")[0];

		String subServiceType = null;
		Class<?>[] deviceInterfaces = deviceClass.getInterfaces();
		if (deviceInterfaces.length == 0) {
			subServiceType = "generic-device";
		} else
			for (Class<?> c : deviceInterfaces) {
				if (Sensor.class.isAssignableFrom(c) || Actuator.class.isAssignableFrom(c)) {
					subServiceType = c.toString().substring(c.toString().lastIndexOf(".") + 1);
				}
			}

		subServiceType = subServiceType.toLowerCase();
		ServiceInfo subTypedServiceCoAP = ServiceInfo.create("_obix._coap." + domain, deviceName, "_" + subServiceType, 5683, null);
		ServiceInfo subTypedServiceHTTP = ServiceInfo.create("_obix._http." + domain, deviceName, "_" + subServiceType, 8080, null);

		// / Making bonjour happy
		final HashMap<String, String> values = new HashMap<String, String>();
		values.put("text", "text value");
		ServiceInfo serviceCoAP = ServiceInfo.create("_obix._udp." + domain, deviceName, 5683, null);
		ServiceInfo serviceHTTP = ServiceInfo.create("_obix._tcp." + domain, deviceName, 8080, null);

		try {
			subTypedServiceCoAP.setIpv6Addr(ipv6);
			subTypedServiceCoAP.setServer(qualifiedDeviceName + domain);
			subTypedServiceHTTP.setIpv6Addr(ipv6);
			subTypedServiceHTTP.setServer(qualifiedDeviceName + domain);

			serviceCoAP.setIpv6Addr(ipv6);
			serviceCoAP.setText(values);
			serviceCoAP.setServer(qualifiedDeviceName + domain);
			serviceHTTP.setIpv6Addr(ipv6);
			serviceHTTP.setText(values);
			serviceHTTP.setServer(qualifiedDeviceName + domain);
		} catch (Exception e) {
			e.printStackTrace();
		}

		executor.execute(new ServiceRegistra(subTypedServiceCoAP));
		executor.execute(new ServiceRegistra(subTypedServiceHTTP));
		executor.execute(new ServiceRegistra(serviceCoAP));
		executor.execute(new ServiceRegistra(serviceHTTP));
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

	@Override
	public void unregisterAllDevice() {
		jmdns.unregisterAllServices();
	}
}
