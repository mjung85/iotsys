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

package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import obix.Obj;
import obix.Uri;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;

public class EnoceanDeviceLoaderImpl implements DeviceLoader {

	private final ArrayList<Obj> myObjects = new ArrayList<Obj>();

	private XMLConfiguration devicesConfig;

	private final static Logger log = Logger
			.getLogger(EnoceanDeviceLoaderImpl.class.getName());

	@Override
	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		setConfiguration(devicesConfig);
		
		// Hard-coded connections and object creation

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		int connectorsSize = 0;
		// WMBus
		Object enoceanConnectors = devicesConfig
				.getProperty("enocean.connector.name");
		if (enoceanConnectors != null) {
			connectorsSize = 1;
		} else {
			connectorsSize = 0;
		}

		if (enoceanConnectors instanceof Collection<?>) {
			connectorsSize = ((Collection<?>) enoceanConnectors).size();
		}
		log.info("Found " + connectorsSize + " EnOcean connectors.");
		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("enocean.connector(" + connector + ")");

			Object enoceanConfiguredDevices = subConfig.getProperty("device.type");
			String connectorName = subConfig.getString("name");
			String serialPort = subConfig.getString("serialPort");
			Boolean enabled = subConfig.getBoolean("enabled", false);

			// PropertyConfigurator.configure("log4j.properties");
			if (enabled) {
				try {
					log.info("Connecting EnOcean connector to COM Port: "
							+ serialPort);
					EnoceanConnector enoceanConnector = new EnoceanConnector(serialPort);
					enoceanConnector.connect();

					connectors.add(enoceanConnector);

					int numberOfDevices = 0;
					if (enoceanConfiguredDevices != null) {
						numberOfDevices = 1; // there is at least one device.
					} else if (enoceanConfiguredDevices instanceof Collection<?>) {
						Collection<?> enoceanDevices = (Collection<?>) enoceanConfiguredDevices;
						numberOfDevices = enoceanDevices.size();
					}

					log.info(numberOfDevices
							+ " EnOcean devices found in configuration for connector "
							+ connectorName);

					// add devices
					for (int i = 0; i < numberOfDevices; i++) {
						String type = subConfig.getString("device(" + i
								+ ").type");
						List<Object> address = subConfig.getList("device(" + i
								+ ").address");
						String ipv6 = subConfig.getString("device(" + i
								+ ").ipv6");
						String href = subConfig.getString("device(" + i
								+ ").href");

						String name = subConfig.getString("device(" + i
								+ ").name");

						Boolean historyEnabled = subConfig.getBoolean("device("
								+ i + ").historyEnabled", false);

						Boolean groupCommEnabled = subConfig.getBoolean(
								"device(" + i + ").groupCommEnabled", false);

						Integer historyCount = subConfig.getInt("device(" + i
								+ ").historyCount", 0);

						Boolean refreshEnabled = subConfig.getBoolean("device("
								+ i + ").refreshEnabled", false);

						if (type != null && address != null) {
							int addressCount = address.size();
							try {
								Constructor<?>[] declaredConstructors = Class
										.forName(type)
										.getDeclaredConstructors();
								for (int k = 0; k < declaredConstructors.length; k++) {
									if (declaredConstructors[k]
											.getParameterTypes().length == addressCount + 1) { // constructor
																								// that
																								// takes
																								// the
																								// KNX
																								// connector
																								// and
																								// group
																								// address
																								// as
																								// argument
										Object[] args = new Object[address
												.size() + 1];
										// first arg is KNX connector

										args[0] = enoceanConnector;
										for (int l = 1; l <= address.size(); l++) {

											String adr = (String) address
													.get(l - 1);
											if (adr == null
													|| adr.equals("null")) {
												args[l] = null;
											} else {
												args[l] = new String(adr);
											}

										}
										try {
											// create a instance of the
											// specified KNX device
											Obj enoceanDevice = (Obj) declaredConstructors[k]
													.newInstance(args);

											enoceanDevice.setHref(new Uri(href));

											if (name != null
													&& name.length() > 0) {
												enoceanDevice.setName(name);
											}

											if (ipv6 != null) {
												objectBroker.addObj(enoceanDevice, ipv6);
											} else {
												objectBroker.addObj(enoceanDevice);
											}

											myObjects.add(enoceanDevice);

											enoceanDevice.initialize();

											if (historyEnabled != null
													&& historyEnabled) {
												if (historyCount != null
														&& historyCount != 0) {
													objectBroker
															.addHistoryToDatapoints(
																	enoceanDevice,
																	historyCount);
												} else {
													objectBroker
															.addHistoryToDatapoints(enoceanDevice);
												}
											}

											if (groupCommEnabled) {
												objectBroker
														.enableGroupComm(enoceanDevice);
											}

											if (refreshEnabled != null
													&& refreshEnabled) {
												objectBroker
														.enableObjectRefresh(enoceanDevice);
											}

										} catch (IllegalArgumentException e) {
											e.printStackTrace();
										} catch (InstantiationException e) {
											e.printStackTrace();
										} catch (IllegalAccessException e) {
											e.printStackTrace();
										} catch (InvocationTargetException e) {
											e.printStackTrace();
										}
									}
								}
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}

//					TemperatureSensorImplXBee xBeeTemperatureSensor = new TemperatureSensorImplXBee(
//							xBeeConnector, "0013a200407c1715");
//					xBeeTemperatureSensor.setHref(new Uri("temperature"));
//					xBeeTemperatureSensor.setName("temperature");
//
//					// add virtual devices to object broker and remember all
//					// assigned
//					// URIs, due to child objects there could be one or many
//					synchronized (myObjects) {
//						// myObjects.addAll(objectBroker.addObj(xBeeBrightness));
//						myObjects.addAll(objectBroker
//								.addObj(xBeeTemperatureSensor));
//					}
//
//					// enable history yes/no?
//					// objectBroker.addHistoryToDatapoints(xBeeBrightness, 100);
//					objectBroker.addHistoryToDatapoints(xBeeTemperatureSensor,
//							100);
//
//					objectBroker.enableGroupComm(xBeeTemperatureSensor);
//					// objectBroker.enableObjectRefresh(xBeeTemperatureSensor);

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
