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

package at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast;

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
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastLocationImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.ConfigsDbImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Device;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.objects.WeatherControlImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.objects.WeatherObjectImplYR_NO;

import com.fasterxml.jackson.databind.JsonNode;

public class WeatherForecastDeviceLoaderImpl implements DeviceLoader {

	private final ArrayList<Obj> myObjects = new ArrayList<Obj>();

	private XMLConfiguration devicesConfig;

	private final static Logger log = Logger.getLogger(WeatherForecastDeviceLoaderImpl.class.getName());

	@Override
	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		setConfiguration(devicesConfig);
		objectBroker.getConfigDb().prepareDeviceLoader(getClass().getName());
		
		// Hard-coded connections and object creation
		ArrayList<Connector> connectors = new ArrayList<Connector>();

		List<JsonNode> connectorsFromDb = objectBroker.getConfigDb().getConnectors("weather-forecast");
		int connectorsSize = 0;
		
		if (connectorsFromDb.size() <= 0) {
			Object configuredConnectors = devicesConfig
					.getProperty("weather-forecast.connector.name");
			if (configuredConnectors != null) {
				connectorsSize = 1;
			} else {
				connectorsSize = 0;
			}

			if (configuredConnectors instanceof Collection<?>) {
				connectorsSize = ((Collection<?>) configuredConnectors).size();
			}
		} else
			connectorsSize = connectorsFromDb.size();
		log.info("Found " + connectorsSize + " weather forecast connectors.");
		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig.configurationAt("weather-forecast.connector(" + connector + ")");

			Object configuredDevices = subConfig.getProperty("device.type");
			String connectorId = "";
			String connectorName = subConfig.getString("name");
			Boolean enabled = subConfig.getBoolean("enabled", false);

			try {
				connectorId = connectorsFromDb.get(connector).get("_id").asText();
				connectorName = connectorsFromDb.get(connector).get("name").asText();
				enabled =  connectorsFromDb.get(connector).get("enabled").asBoolean();
			} catch (Exception e){
				log.info("Cannot fetch configuration from Database, using devices.xml");
			}
			
			
			if (enabled) {
				try {
					log.info("Creating weather forecast connector.");
					WeatherForecastConnector forecastConnector = new WeatherForecastConnector();
					forecastConnector.setName(connectorName);
					forecastConnector.setTechnology("weather-forecast");
					forecastConnector.setEnabled(enabled);
					
					connectors.add(forecastConnector);

					int numberOfDevices = 0;
					List<Device> devicesFromDb = objectBroker.getConfigDb().getDevices(connectorId);

					if (connectorsFromDb.size() <= 0) {
						if (configuredDevices != null) {
							numberOfDevices = 1; // there is at least one
													// device.
							if (configuredDevices instanceof Collection<?>) {
								Collection<?> devices = (Collection<?>) configuredDevices;
								numberOfDevices = devices.size();
							}
						}
					} else
						numberOfDevices = devicesFromDb.size();

					log.info(numberOfDevices
							+ " weather forecast devices found in configuration for connector "
							+ connectorName);

					// add devices
					for (int i = 0; i < numberOfDevices; i++) {
						String type = subConfig.getString("device(" + i + ").type");
						String desc = subConfig.getString("device(" + i + ").location.description", "");
						double latitude = subConfig.getDouble("device(" + i + ").location.latitude", 0);
						double longitude = subConfig.getDouble("device(" + i + ").location.longitude", 0);
						long height = subConfig.getLong("device(" + i + ").location.height", 0);
						String href = subConfig.getString("device(" + i	+ ").href");
						String name = subConfig.getString("device(" + i + ").name");
						Boolean refreshEnabled = subConfig.getBoolean("device("	+ i + ").refreshEnabled", true);
						Boolean historyEnabled = subConfig.getBoolean(
								"device(" + i + ").historyEnabled", false);
						Boolean groupCommEnabled = subConfig.getBoolean(
								"device(" + i + ").groupCommEnabled", false);
						Integer historyCount = subConfig.getInt("device("
								+ i + ").historyCount", 0);
						
						Device deviceFromDb;
						try {
							deviceFromDb = devicesFromDb.get(i);
							type = deviceFromDb.getType();
							href = deviceFromDb.getHref();
							name = deviceFromDb.getName();
							historyEnabled = deviceFromDb.isHistoryEnabled();
							groupCommEnabled = deviceFromDb.isGroupcommEnabled();
							refreshEnabled = deviceFromDb.isRefreshEnabled();
							historyCount = deviceFromDb.getHistoryCount();
						} 
						catch (Exception e) {
						}
						
						// Transition step: comment when done
						Device d = new Device(type, null, null, href, name, null, historyCount, historyEnabled, groupCommEnabled, refreshEnabled);
						objectBroker.getConfigDb().prepareDevice(connectorName, d);
						
						if (type != null && name != null) {
							try {
								Constructor<?>[] declaredConstructors = Class.forName(type).getDeclaredConstructors();
								for (int k = 0; k < declaredConstructors.length; k++) {
									if (declaredConstructors[k].getParameterTypes().length == 3) {
										// constructor that takes name, location, and connector as argument
										Object[] args = new Object[3];

										args[0] = name;
										args[1] = new WeatherForecastLocationImpl(desc, latitude, longitude, height);
										args[2] = forecastConnector;
										try {
											// create an instance of the specified weather forecast crawler
											Obj crawler = (Obj) declaredConstructors[k].newInstance(args);
			
											crawler.setHref(new Uri(URLEncoder.encode(connectorName, "UTF-8") + "/" + href));
	
											
											objectBroker.addObj(crawler, true);
											
											if(crawler instanceof WeatherObjectImplYR_NO){
												WeatherObjectImplYR_NO weatherObject = (WeatherObjectImplYR_NO) crawler;
												objectBroker.addObj(weatherObject.getChildByHref(new Uri("upcoming")));
												objectBroker.addObj(weatherObject.getChildByHref(new Uri("location")));
											}
											myObjects.add(crawler);
											//crawler.initialize();
											
											if (refreshEnabled != null && refreshEnabled) {
												// refresh weather forecast automatically (once per hour)						
												objectBroker.enableObjectRefresh(crawler, 3600000);
											}
											else {
												// refresh weather forecast manually											
												crawler.refreshObject();
											}
											
											if (historyEnabled != null
													&& historyEnabled) {
												if (historyCount != null
														&& historyCount != 0) {
													objectBroker
															.addHistoryToDatapoints(
																	crawler,
																	historyCount);
												} else {
													objectBroker
															.addHistoryToDatapoints(crawler);
												}
											}
											
											if(groupCommEnabled != null && groupCommEnabled){
												objectBroker.enableGroupComm(crawler);
											}
										 
											crawler.initialize();

										} catch (Exception e) {
											log.log(Level.SEVERE, e.getMessage(), e);
										}
									}
								}
							} catch (Exception e) {
								log.log(Level.SEVERE, e.getMessage(), e);
							}
						}
					}
					// add weather control for manual overwriting of weather
					WeatherControlImpl weatherControl = new WeatherControlImpl("weatherControl",forecastConnector);
					weatherControl.setHref(new Uri(URLEncoder.encode(connectorName, "UTF-8") + "/weatherControl"));
					myObjects.add(weatherControl);
					objectBroker.addObj(weatherControl, true);
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
			try
			{
				devicesConfig = new XMLConfiguration(DEVICE_CONFIGURATION_LOCATION);
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}
