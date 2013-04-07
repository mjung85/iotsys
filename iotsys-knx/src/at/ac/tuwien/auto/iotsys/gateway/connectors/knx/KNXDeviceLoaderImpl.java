/*
  	Copyright (c) 2013 - IotSyS KNX Connector
 	Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
  	All rights reserved.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package at.ac.tuwien.auto.iotsys.gateway.connectors.knx;

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

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;

public class KNXDeviceLoaderImpl implements DeviceLoader {
	private static Logger log = Logger.getLogger(KNXDeviceLoaderImpl.class
			.getName());

	private XMLConfiguration devicesConfig = new XMLConfiguration();
	
	private ArrayList<String> myObjects = new ArrayList<String>();

	public KNXDeviceLoaderImpl() {
		String devicesConfigFile = DEVICE_CONFIGURATION_LOCATION;

		try {
			devicesConfig = new XMLConfiguration(devicesConfigFile);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		ArrayList<Connector> connectors = new ArrayList<Connector>();

		Object knxConnectors = devicesConfig.getProperty("knx.connector.name");
		int connectorsSize = 0;

		if (knxConnectors != null) {
			connectorsSize = 1;
		}

		if (knxConnectors instanceof Collection<?>) {
			connectorsSize = ((Collection<?>) knxConnectors).size();
		}

		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("knx.connector(" + connector + ")");

			Object knxConfiguredDevices = subConfig.getProperty("device.type");
			String connectorName = subConfig.getString("name");
			String routerIP = subConfig.getString("router.ip");
			int routerPort = subConfig.getInteger("router.port", 3671);
			String localIP = subConfig.getString("localIP");
			Boolean enabled = subConfig.getBoolean("enabled", false);

			if (enabled) {
				try {
					KNXConnector knxConnector = new KNXConnector(routerIP,
							routerPort, localIP);
					knxConnector.connect();
					connectors.add(knxConnector);

					if (knxConfiguredDevices instanceof Collection<?>) {

						Collection<?> knxDevices = (Collection<?>) knxConfiguredDevices;
						log.info(knxDevices.size()
								+ " KNX devices found in configuration for connector "
								+ connectorName);

						for (int i = 0; i < knxDevices.size(); i++) {
							String type = subConfig.getString("device(" + i
									+ ").type");
							List<Object> address = subConfig.getList("device("
									+ i + ").address");
							String ipv6 = subConfig.getString("device(" + i
									+ ").ipv6");
							String href = subConfig.getString("device(" + i
									+ ").href");

							Boolean historyEnabled = subConfig.getBoolean(
									"device(" + i + ").historyEnabled", false);

							Integer historyCount = subConfig.getInt("device("
									+ i + ").historyCount", 0);
							
							Boolean refreshEnabled = subConfig.getBoolean("device(" + i + ").refreshEnabled", false);

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

											args[0] = knxConnector;
											for (int l = 1; l <= address.size(); l++) {
												try {
													String adr = (String) address
															.get(l - 1);
													if (adr == null
															|| adr.equals("null")) {
														args[l] = null;
													} else {
														args[l] = new GroupAddress(
																adr);
													}
												} catch (KNXFormatException e) {
													e.printStackTrace();
												}
											}
											try {
												// create a instance of the
												// specified KNX device
												Obj knxDevice = (Obj) declaredConstructors[k]
														.newInstance(args);

												// Obj knxDevice = new
												// LightSwitchActuatorImplKnx(knxConnector,
												// groupAddress,...);
												knxDevice
														.setHref(new Uri(href));

												ArrayList<String> assignedHrefs = null;
												if (ipv6 != null) {
													assignedHrefs = objectBroker.addObj(
															knxDevice, ipv6);
												} else {
													assignedHrefs = objectBroker
															.addObj(knxDevice);
												}
												
												myObjects.addAll(assignedHrefs);

												knxDevice.initialize();

												if (historyEnabled != null
														&& historyEnabled) {
													if (historyCount != null
															&& historyCount != 0) {
														objectBroker
																.addHistoryToDatapoints(
																		knxDevice,
																		historyCount);
													} else {
														objectBroker
																.addHistoryToDatapoints(knxDevice);
													}
												}
												
												if(refreshEnabled != null && refreshEnabled){
													objectBroker.enableObjectRefresh(knxDevice);
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
					} else {
						log.info("No KNX devices configured for connector "
								+ connectorName);
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
		synchronized(myObjects){
			for(String href : myObjects){
				objectBroker.removeObj(href);
			}
		}
	}

}