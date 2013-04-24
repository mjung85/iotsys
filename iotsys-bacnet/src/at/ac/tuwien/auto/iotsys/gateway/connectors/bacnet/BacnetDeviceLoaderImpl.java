/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2013 
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet;

import java.lang.reflect.Constructor;
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

import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

public class BacnetDeviceLoaderImpl implements DeviceLoader {

	private static Logger log = Logger.getLogger(BacnetDeviceLoaderImpl.class
			.getName());

	private XMLConfiguration devicesConfig = new XMLConfiguration();
	
	private ArrayList<String> myObjects = new ArrayList<String>();
	
	public BacnetDeviceLoaderImpl() {
		String devicesConfigFile = DEVICE_CONFIGURATION_LOCATION;

		try {
			devicesConfig = new XMLConfiguration(devicesConfigFile);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		ArrayList<Connector> connectors = new ArrayList<Connector>();

		int connectorsSize = 0;
		// bacnet
		Object bacnetConnectors = devicesConfig
				.getProperty("bacnet.connector.name");

		if (bacnetConnectors != null) {
			connectorsSize = 1;
		} else {
			connectorsSize = 0;
		}

		if (bacnetConnectors instanceof Collection<?>) {
			connectorsSize = ((Collection<?>) bacnetConnectors).size();
		}

		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("bacnet.connector(" + connector + ")");

			Object bacnetConfiguredDevices = subConfig
					.getProperty("device.type");
			String connectorName = subConfig.getString("name");
			String broadcastAddress = subConfig.getString("broadcastAddress");
			int localPort = subConfig.getInteger("localPort", 3671);
			int localDeviceID = subConfig.getInteger("localDeviceID", 12345);
			Boolean enabled = subConfig.getBoolean("enabled", false);

			if (enabled) {
				try {
					BACnetConnector bacnetConnector = new BACnetConnector(
							localDeviceID, broadcastAddress, localPort);
					bacnetConnector.getRootObj().setName(connectorName.replaceAll(" ", ""));
					bacnetConnector.connect();
					
					Boolean discoveryEnabled = subConfig.getBoolean("discovery-enabled", false);
					if (discoveryEnabled) bacnetConnector.discover(new DeviceDiscoveryListener(objectBroker));
					
					connectors.add(bacnetConnector);

					if (bacnetConfiguredDevices instanceof Collection<?>) {

						Collection<?> bacnetDevices = (Collection<?>) bacnetConfiguredDevices;
						log.info(bacnetDevices.size()
								+ " BACnet devices found in configuration for connector "
								+ connectorName);

						for (int i = 0; i < bacnetDevices.size(); i++) {
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
							

							Boolean groupCommEnabled = subConfig.getBoolean(
									"device(" + i + ").groupCommEnabled", false);
							
							String name = subConfig.getString("device(" + i
									+ ").name");

							
							Boolean refreshEnabled = subConfig.getBoolean("device(" + i + ").refreshEnabled", false);

							Integer historyCount = subConfig.getInt("device("
									+ i + ").historyCount", 0);

							if (type != null && address != null) {
								
								// now follow possible multiple data points
								// identified through
								// the device Id, object type, the instance number and the
								// property identifier, which shall be packaged into an BacnetDatapointInfo object
								
								ObjectIdentifier[] objectIdentifier = new ObjectIdentifier[(address
										.size() ) / 4];
								PropertyIdentifier[] propertyIdentifier = new PropertyIdentifier[(address
										.size() ) / 4];
								
								BacnetDataPointInfo[] bacnetDataPointInfo = new BacnetDataPointInfo[(address
										.size() ) / 4];

								int q = 0;
								for (int p = 0; p <= address.size() - 4; p += 4) {
									int remoteDeviceID = Integer.parseInt((String)address.get(p));
									ObjectIdentifier objIdentifier = new ObjectIdentifier(
											new ObjectType(
													Integer.parseInt((String) address
															.get(p+1))),
											Integer.parseInt((String) address
													.get(p + 2)));
									PropertyIdentifier propIdentifier = new PropertyIdentifier(
											Integer.parseInt((String) address
													.get(p + 3)));
									objectIdentifier[q] = objIdentifier;
									propertyIdentifier[q] = propIdentifier;
									bacnetDataPointInfo[q] = new BacnetDataPointInfo(remoteDeviceID, objIdentifier, propIdentifier);
									q = q + 1;
								}
								Object[] args = new Object[q  + 1];
								args[0] = bacnetConnector;
//								args[1] = Integer.parseInt(remoteDeviceID);
								for (int p = 0; p < bacnetDataPointInfo.length; p++) {
									args[1 + p] = bacnetDataPointInfo[p];		
								}

								try {

									Constructor<?>[] declaredConstructors = Class
											.forName(type)
											.getDeclaredConstructors();
									for (int k = 0; k < declaredConstructors.length; k++) {
										if (declaredConstructors[k]
												.getParameterTypes().length == args.length) { // constructor
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
											Obj bacnetDevice = (Obj) declaredConstructors[k]
													.newInstance(args); // create
																		// a
																		// instance
																		// of
																		// the
																		// specified
																		// KNX
																		// device
											bacnetDevice.setHref(new Uri(href));
											if(name != null && name.length() > 0){
												bacnetDevice.setName(name);
											}
											
											ArrayList<String> assignedHrefs = null;

											if (ipv6 != null) {
												assignedHrefs = objectBroker.addObj(
														bacnetDevice, ipv6);
											} else {
												assignedHrefs = objectBroker
														.addObj(bacnetDevice);
											}
											
											myObjects.addAll(assignedHrefs);
											
											bacnetDevice.initialize();

											if (historyEnabled != null
													&& historyEnabled) {
												if (historyCount != null
														&& historyCount != 0) {
													objectBroker
															.addHistoryToDatapoints(
																	bacnetDevice,
																	historyCount);
												} else {
													objectBroker
															.addHistoryToDatapoints(bacnetDevice);
												}
											}
											
											if(refreshEnabled != null && refreshEnabled){
												objectBroker.enableObjectRefresh(bacnetDevice);
											}
											
											if(groupCommEnabled != null && groupCommEnabled){
												objectBroker.enableGroupComm(bacnetDevice);
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
						log.info("No BACnet devices configured for connector "
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
	
	private class DeviceDiscoveryListener implements BACnetConnector.DeviceDiscoveryListener {
		private ObjectBroker objectBroker;
	
		public DeviceDiscoveryListener(ObjectBroker objectBroker) {
			this.objectBroker = objectBroker;
		}
		
		@Override
		public void deviceDiscovered(Obj device) {
			ArrayList<String> assignedHrefs = objectBroker.addObj(device);
			myObjects.addAll(assignedHrefs);
			
			device.initialize();
		}
		
	}
}
