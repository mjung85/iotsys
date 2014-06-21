/*******************************************************************************
 * Copyright (c) 2013 - IotSys CoAP Proxy
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

package at.ac.tuwien.auto.iotsys.gateway.connectors.coap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;





//import obix.Bool;
//import obix.Int;
import obix.Obj;
//import obix.Real;
import obix.Uri;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.persistent.ConfigsDbImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Device;

import com.fasterxml.jackson.databind.JsonNode;

public class CoapDeviceLoaderImpl implements DeviceLoader {
	private final ArrayList<Obj> myObjects = new ArrayList<Obj>();

	private XMLConfiguration devicesConfig;

	private static final Logger log = Logger
			.getLogger(CoapDeviceLoaderImpl.class.getName());

	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		setConfiguration(devicesConfig);

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		List<JsonNode> connectorsFromDb = ConfigsDbImpl.getInstance().getConnectors("coap");
		int connectorsSize = 0;
		
		if (connectorsFromDb.size() <= 0) {
			Object coapConnectors = devicesConfig
					.getProperty("coap.connector.name");

			if (coapConnectors != null) {
				if (coapConnectors instanceof String) {
					connectorsSize = 1;
				} else {
					connectorsSize = ((Collection<?>) coapConnectors).size();
				}
			} else {
				connectorsSize = 0;
			}
			if (coapConnectors instanceof Collection<?>) {
				coapConnectors = ((Collection<?>) coapConnectors).size();
			}
		} else
			connectorsSize = connectorsFromDb.size();
		
		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("coap.connector(" + connector + ")");

			Object coapConfiguredDevices = subConfig.getProperty("device.type");
			String connectorId = "";
			String connectorName = subConfig.getString("name");
			Boolean enabled = subConfig.getBoolean("enabled", false);

			try {
				connectorId = connectorsFromDb.get(connector).get("_id").asText();
				connectorName = connectorsFromDb.get(connector).get("name").asText();
				enabled =  connectorsFromDb.get(connector).get("enabled").asBoolean();
			} catch (Exception e){}
			
			
			if (enabled) {
				try {
					CoapConnector coapConnector = new CoapConnector();
					coapConnector.setEnabled(enabled);
					coapConnector.setTechnology("coap");
					coapConnector.setName(connectorName);
					
					//coapConnector.connect();
					connectors.add(coapConnector);

					int numberOfDevices = 0;
					List<Device> devicesFromDb = ConfigsDbImpl.getInstance().getDevices(connectorId);

					if (devicesFromDb.size() <= 0) {
						if (coapConfiguredDevices != null) {
							numberOfDevices = 1; // there is at least one
													// device.
						}
						if (coapConfiguredDevices instanceof Collection<?>) {
							Collection<?> coapDevices = (Collection<?>) coapConfiguredDevices;
							numberOfDevices = coapDevices.size();
						}
					} else
						numberOfDevices = devicesFromDb.size();

					log.info(numberOfDevices
							+ " CoAP devices found in configuration for connector "
							+ connectorName);

					for (int i = 0; i < numberOfDevices; i++) {
						String type = subConfig.getString("device(" + i
								+ ").type");
						List<Object> address = subConfig.getList("device("
								+ i + ").address");
						String addressString = address.toString();
						String href = subConfig.getString("device(" + i
								+ ").href");

						String name = subConfig.getString("device(" + i
								+ ").name");

						String displayName = subConfig.getString("device("
								+ i + ").displayName");

						Boolean historyEnabled = subConfig.getBoolean(
								"device(" + i + ").historyEnabled", false);

						Boolean groupCommEnabled = subConfig
								.getBoolean("device(" + i
										+ ").groupCommEnabled", false);
						Boolean shouldObserve = subConfig
								.getBoolean("device(" + i
										+ ").observe", false);
						
						Boolean forwardGroupAddress = subConfig
								.getBoolean("device(" + i
										+ ").forwardGroupAddress", true);

						Integer historyCount = subConfig.getInt("device("
								+ i + ").historyCount", 0);

						Boolean refreshEnabled = subConfig.getBoolean(
								"device(" + i + ").refreshEnabled", false);

						Device deviceFromDb;
						try {
							deviceFromDb = devicesFromDb.get(i);
							type = deviceFromDb.getType();
							addressString = deviceFromDb.getAddress();
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
						Device d = new Device(type, null, addressString, href, name, displayName, historyCount, historyEnabled, groupCommEnabled, refreshEnabled);
						ConfigsDbImpl.getInstance().prepareDevice(connectorName, d);						
						if (type != null && address != null) {
							try {
								Constructor<?>[] declaredConstructors = Class
										.forName(type)
										.getDeclaredConstructors();

								//constructor that takes connector and IPv6 coap URI as argument
								Object[] args = new Object[4];

								// first arg is Coap connector
								args[0] = coapConnector;
								args[2] = shouldObserve;
								args[3] = forwardGroupAddress;

								Obj coapDevice = null;
								String adr = "";

								for (int k = 0; k < declaredConstructors.length; k++) {
									if (declaredConstructors[k]
											.getParameterTypes().length == 4) {

										if(!address.isEmpty()) {
											adr = (String) address.get(0);
										}
							
										args[1] = adr;

										log.info("Added Device with URI " + adr);

										coapDevice = (Obj) declaredConstructors[k].newInstance(args);
										
									} else if (declaredConstructors[k].getParameterTypes().length == 0) {
										//TODO: no constructor with 4 arguments - throw exception?
										coapDevice = (Obj) declaredConstructors[k].newInstance();
									}
								}

								// create a instance of the specified CoAP device
								coapDevice.setHref(new Uri(URLEncoder
										.encode(connectorName, "UTF-8")
										+ "/" + href));

								if (name != null && name.length() > 0) {
									coapDevice.setName(name);
								}

								if (displayName != null
										&& displayName.length() > 0) {
									coapDevice.setDisplayName(displayName);
								}
								
								objectBroker.addObj(coapDevice);
								myObjects.add(coapDevice);
								coapDevice.initialize();

								if (historyEnabled != null && historyEnabled) {
									if (historyCount != null
											&& historyCount != 0) {
										objectBroker.addHistoryToDatapoints(coapDevice,historyCount);
									} else {
										objectBroker.addHistoryToDatapoints(coapDevice);
									}
								}

								if (groupCommEnabled) {
									objectBroker.enableGroupComm(coapDevice, coapConnector, adr);
								}

								if (refreshEnabled != null && refreshEnabled) {
									objectBroker.enableObjectRefresh(coapDevice);
								}
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
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
				devicesConfig = new XMLConfiguration(
						DEVICE_CONFIGURATION_LOCATION);
			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}