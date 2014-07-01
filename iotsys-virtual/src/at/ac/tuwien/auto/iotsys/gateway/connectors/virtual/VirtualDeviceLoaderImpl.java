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

package at.ac.tuwien.auto.iotsys.gateway.connectors.virtual;

import java.lang.reflect.Constructor;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import obix.Obj;
import obix.Uri;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherObject;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastLocationImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.ConfigsDbImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Device;

import com.fasterxml.jackson.databind.JsonNode;

public class VirtualDeviceLoaderImpl implements DeviceLoader {
	private final ArrayList<Obj> myObjects = new ArrayList<Obj>();

	private XMLConfiguration devicesConfig;

	private static final Logger log = Logger
			.getLogger(VirtualDeviceLoaderImpl.class.getName());

	@Override
	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		setConfiguration(devicesConfig);
		
		// Hard-coded connections and object creation

		// store all created connectors, will be used by the gateway for closing
		ArrayList<Connector> connectors = new ArrayList<Connector>();
		// Open connection ???
		//VirtualConnector virtualConnector = new VirtualConnector();
		try {
			//virtualConnector.connect();

			//connectors.add(virtualConnector);
			
		

//			TemperatureSensorImpl virtualTemp1 = new TemperatureSensorImplVirtual(
//					virtualConnector, new Object());
//			virtualTemp1.setHref(new Uri("virtualTemp1"));
//			virtualTemp1.setName("virtualTemp1");

			// add virtual devices to object broker and remember all assigned
			// URIs, due to child objects there could be one or many
//			synchronized (myObjects) {
//				myObjects.addAll(objectBroker.addObj(virtualTemp1));
//			}

			// add obj with IPv6 address
			// String ipv6 = "fe80::1"
			// myObjects.addAll(objectBroker.addObj(virtualTemp1, ipv6));

			// enable history yes/no?
//			objectBroker.addHistoryToDatapoints(virtualTemp1, 100);
//
//			LightSwitchActuatorImpl virtualLight1 = new LightSwitchActuatorImplVirtual(
//					virtualConnector, new Object());
//			virtualLight1.setHref(new Uri("virtualLight1"));
//			virtualLight1.setName("virtualLight1");

			// add virtual devices to object broker
//			synchronized (myObjects) {
//				myObjects.addAll(objectBroker.addObj(virtualLight1));
//			}

			// add obj with IPv6 address
			// String ipv6 = "fe80::1"
			// objectBroker.addObj(virtualTemp1, ipv6);

			// enable history yes/no?
//			objectBroker.addHistoryToDatapoints(virtualLight1, 100);
			

		} catch (Exception e) {

			e.printStackTrace();
		}

		// parse XML configuration for connections and objects
		// NOTE: this loader allow to directly instantiate the base oBIX objects
		// for testing purposes
		
		List<JsonNode> connectorsFromDb = ConfigsDbImpl.getInstance().getConnectors("virtual");
		int connectorsSize = 0;
		// virtual
		if (connectorsFromDb.size() <= 0) {
			Object virtualConnectors = devicesConfig
					.getProperty("virtual.connector.name");
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
		} else
			connectorsSize = connectorsFromDb.size();
		
		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("virtual.connector(" + connector + ")");

			Object virtualConfiguredDevices = subConfig
					.getProperty("device.type");
			String connectorId = "";
			String connectorName = subConfig.getString("name");
			Boolean enabled = subConfig.getBoolean("enabled", false);
			
			try {
				connectorId = connectorsFromDb.get(connector).get("_id").asText();
				connectorName = connectorsFromDb.get(connector).get("name").asText();
				enabled =  connectorsFromDb.get(connector).get("enabled").asBoolean();
			} catch (Exception e){
				log.info("Cannot fetch configuration from Database, using devices.xml");//e.printStackTrace();
			}
			
			if (enabled) {
				try {
					VirtualConnector vConn = new VirtualConnector();
					vConn.setName(connectorName);
					vConn.setEnabled(enabled);
					vConn.setTechnology("virtual");
					
					connectors.add(vConn);
					
					int numberOfDevices = 0;
					List<Device> devicesFromDb = ConfigsDbImpl.getInstance().getDevices(connectorId);

					if (devicesFromDb.size() <= 0) {
						if (virtualConfiguredDevices != null) {
							numberOfDevices = 1; // there is at least one
													// device.
						}
						if (virtualConfiguredDevices instanceof Collection<?>) {
							Collection<?> virtualDevices = (Collection<?>) virtualConfiguredDevices;
							numberOfDevices = virtualDevices.size();
						}
					} else
						numberOfDevices = devicesFromDb.size();
					
					log.info(numberOfDevices
							+ " virtual devices found in configuration for connector "
							+ connectorName);
					for (int i = 0; i < numberOfDevices; i++) {
						String type = subConfig.getString("device(" + i
								+ ").type");
						List<Object> address = subConfig.getList("device("
								+ i + ").address");
						String addressString = address.toString();
						String ipv6 = subConfig.getString("device(" + i
								+ ").ipv6");
						String href = subConfig.getString("device(" + i
								+ ").href");
						
						String name = subConfig.getString("device(" + i
								+ ").name");
						
						String displayName = subConfig.getString("device(" + i + ").displayName");

						Boolean historyEnabled = subConfig.getBoolean(
								"device(" + i + ").historyEnabled", false);
						
						Boolean groupCommEnabled = subConfig.getBoolean(
								"device(" + i + ").groupCommEnabled", false);
						
						Boolean refreshEnabled = subConfig.getBoolean("device(" + i + ").refreshEnabled", false);

						Integer historyCount = subConfig.getInt("device("
								+ i + ").historyCount", 0);
						
						Device deviceFromDb;
						try {
							deviceFromDb = devicesFromDb.get(i);
							type = deviceFromDb.getType();
							addressString = deviceFromDb.getAddress();
							ipv6 = deviceFromDb.getIpv6();
							href = deviceFromDb.getHref();
							name = deviceFromDb.getName();
							displayName = deviceFromDb.getDisplayName();
							historyEnabled = deviceFromDb.isHistoryEnabled();
							groupCommEnabled = deviceFromDb.isGroupcommEnabled();
							refreshEnabled = deviceFromDb.isRefreshEnabled();
							historyCount = deviceFromDb.getHistoryCount();
						} 
						catch (Exception e) {
						}
						
						// Transition step: comment when done
						Device d = new Device(type, ipv6, addressString, href, name, displayName, historyCount, historyEnabled, groupCommEnabled, refreshEnabled);
						ConfigsDbImpl.getInstance().prepareDevice(connectorName, d);						
						// for weather forcast services only
						String description = subConfig.getString("device(" + i + ").location.description", "");
						Double latitude = subConfig.getDouble("device(" + i + ").location.latitude", 0);
						Double longitude = subConfig.getDouble("device(" + i + ").location.longitude", 0);
						Long height = subConfig.getLong("device(" + i + ").location.height", 0);

						if (type != null && address != null) {
							try {

								Constructor<?>[] declaredConstructors = Class
										.forName(type)
										.getDeclaredConstructors();
								
								Object[] args = new Object[1];
								args[0] = vConn;
								Obj virtualObj = null;
								for (int k = 0; k < declaredConstructors.length; k++) {
									if (declaredConstructors[k]
											.getParameterTypes().length == 0) { 
										virtualObj = (Obj) Class.forName(type)
												.newInstance();
									}
									else if(declaredConstructors[k].getParameterTypes().length == 1){
										virtualObj = (Obj) declaredConstructors[k].newInstance(args);
									}
									// for weather forcast services only
									else if (WeatherObject.class.isAssignableFrom(Class.forName(type)) &&
												declaredConstructors[k].getParameterTypes().length == 3) {
										// constructor that takes name, location, and connector as argument
										args = new Object[3];

										args[0] = name;
										args[1] = new WeatherForecastLocationImpl(description, latitude, longitude, height);
										args[2] = null;
										
										virtualObj = (Obj) declaredConstructors[k].newInstance(args);
									}
								}
								
								virtualObj.setHref(new Uri(URLEncoder.encode(connectorName, "UTF-8") + "/" + href));
								
								if(name != null && name.length() > 0 && virtualObj.getName() == null){
									virtualObj.setName(name);
								}
								
								if(displayName != null && displayName.length() > 0){
									virtualObj.setDisplayName(displayName);
								}
								
								if (ipv6 != null) {
									objectBroker.addObj(virtualObj, ipv6);
								} else {
									objectBroker.addObj(virtualObj);
								}
								
								myObjects.add(virtualObj);
								
								virtualObj.initialize();

								if (historyEnabled != null
										&& historyEnabled) {
									if (historyCount != null
											&& historyCount != 0) {
										objectBroker
												.addHistoryToDatapoints(
														virtualObj,
														historyCount);
									} else {
										objectBroker
												.addHistoryToDatapoints(virtualObj);
									}
								}
								
								if(groupCommEnabled){
									objectBroker.enableGroupComm(virtualObj);
								}
								
								if(refreshEnabled != null && refreshEnabled){
									virtualObj.setRefreshInterval(5000);
									objectBroker.enableObjectRefresh(virtualObj);
								}

							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	
		return connectors;
	}

	@Override
	public void removeDevices(ObjectBroker objectBroker) {
		synchronized (myObjects) {
			for (Obj obj : myObjects) {
				objectBroker.removeObj(obj.getFullContextPath());
			}
		}

	}

	@Override
	public void setConfiguration(XMLConfiguration devicesConfiguration) {
		this.devicesConfig = devicesConfiguration;
		if (devicesConfiguration == null) {
			try {
				devicesConfig = new XMLConfiguration(DEVICE_CONFIGURATION_LOCATION);
			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}
