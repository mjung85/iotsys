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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
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
import at.ac.tuwien.auto.iotsys.commons.persistent.DeviceConfigs;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Device;

import com.fasterxml.jackson.databind.JsonNode;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

public class BacnetDeviceLoaderImpl implements DeviceLoader {

	private static Logger log = Logger.getLogger(BacnetDeviceLoaderImpl.class
			.getName());

	private XMLConfiguration devicesConfig;
	
	private ArrayList<Obj> myObjects = new ArrayList<Obj>();

	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		setConfiguration(devicesConfig);
		
		ArrayList<Connector> connectors = new ArrayList<Connector>();

		List<JsonNode> connectorsFromDb = DeviceConfigs.getInstance().getConnectors("bacnet");
		int connectorsSize = 0;
		// bacnet
		if (connectorsFromDb.size() <= 0) {
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
		} else
			connectorsSize = connectorsFromDb.size();

		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("bacnet.connector(" + connector + ")");

			Object bacnetConfiguredDevices = subConfig
					.getProperty("device.type");
			String connectorId = "";
			String connectorName = subConfig.getString("name");
			String broadcastAddress = subConfig.getString("broadcastAddress");
			int localPort = subConfig.getInteger("localPort", 3671);
			int localDeviceID = subConfig.getInteger("localDeviceID", 12345);
			boolean enabled = subConfig.getBoolean("enabled", false);
			Boolean groupCommEnabled = subConfig.getBoolean("groupCommEnabled", null);
			Boolean historyEnabled = subConfig.getBoolean("historyEnabled", null);

			try {
				connectorId = connectorsFromDb.get(connector).get("_id").asText();
				connectorName = connectorsFromDb.get(connector).get("name").asText();
				enabled =  connectorsFromDb.get(connector).get("enabled").asBoolean();
				groupCommEnabled =  connectorsFromDb.get(connector).get("groupCommEnabled").asBoolean();
				historyEnabled =  connectorsFromDb.get(connector).get("historyEnabled").asBoolean();
				broadcastAddress = connectorsFromDb.get(connector).get("broadcastAddress").asText();
				localPort = connectorsFromDb.get(connector).get("localPort").asInt();
				localDeviceID = connectorsFromDb.get(connector).get("localDeviceID").asInt();
			} catch (Exception e){}
			
			if (enabled) {
				try {
					BACnetConnector bacnetConnector = new BACnetConnector(
							localDeviceID, broadcastAddress, localPort);
					bacnetConnector.setName(connectorName);
					bacnetConnector.setTechnology("bacnet");
					bacnetConnector.setEnabled(enabled);
					
					Obj bacRoot = bacnetConnector.getRootObj();
					bacRoot.setName(connectorName);
					bacRoot.setHref(new Uri(connectorName.replaceAll("[^a-zA-Z0-9-~\\(\\)]", "")));
					objectBroker.addObj(bacRoot, true);
					
					// Transition step: Moving configs to DB: all connector enabled, uncomment when done
					//bacnetConnector.connect();
					
					Boolean discoveryEnabled = subConfig.getBoolean("discovery-enabled", false);
					if (discoveryEnabled) bacnetConnector.discover(new DeviceDiscoveryListener(objectBroker, groupCommEnabled, historyEnabled));
					
					connectors.add(bacnetConnector);
					
					int numberOfDevices = 0;
					List<Device> devicesFromDb = null;
					try {
						devicesFromDb = DeviceConfigs.getInstance().getDevices(connectorId);
						numberOfDevices = devicesFromDb.size();
					} catch (Exception e){
						// TODO: bacnetConfiguredDevices is from devices.xml --> mismatch when a connector does not have any device associated,
						// e.g., bacnet a-lab (auto) connector
						// do like this for other device loaders!
						if (bacnetConfiguredDevices != null) {
							numberOfDevices = 1; // there is at least one device.
						}
						if (bacnetConfiguredDevices instanceof Collection<?>) {
							Collection<?> bacnetDevices = (Collection<?>) bacnetConfiguredDevices;
							numberOfDevices = bacnetDevices.size();
						}
					}
					
					log.info(numberOfDevices
							+ " BACnet devices found in configuration for connector "
							+ connectorName);

					// Transition step: comment when done
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

						// device specific setting
						Boolean historyEnabledDevice = subConfig.getBoolean(
								"device(" + i + ").historyEnabled", null);
						
						if(historyEnabledDevice != null){
							historyEnabled = historyEnabledDevice;
						}
						
						// device specific setting
						Boolean groupCommEnabledDevice = subConfig.getBoolean(
								"device(" + i + ").groupCommEnabled", null);
						
						if(groupCommEnabledDevice != null){
							// overwrite general settings
							groupCommEnabled = groupCommEnabledDevice;
						}
						
						String name = subConfig.getString("device(" + i
								+ ").name");
						
						String displayName = subConfig.getString("device(" + i
								+ ").displayName");

						
						Boolean refreshEnabled = subConfig.getBoolean("device(" + i + ").refreshEnabled", false);

						Integer historyCount = subConfig.getInt("device("
								+ i + ").historyCount", 0);

						Device deviceFromDb;
						try {
							deviceFromDb = devicesFromDb.get(i);
							type = deviceFromDb.getType();
							addressString = deviceFromDb.getAddress();
							String subAddr[] = addressString.substring(1, addressString.length() - 1).split(", ");
							address = Arrays.asList((Object[])subAddr);
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
						DeviceConfigs.getInstance().prepareDevice(connectorName, d);
						
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
										bacnetDevice.setHref(new Uri(URLEncoder.encode(connectorName, "UTF-8") + "/" + href));
										
										if(name != null && name.length() > 0){
											bacnetDevice.setName(name);
										}
										
										if(displayName != null && displayName.length() > 0){
											bacnetDevice.setDisplayName(displayName);
										}
										

										if (ipv6 != null) {
											objectBroker.addObj(bacnetDevice, ipv6);
										} else {
											objectBroker.addObj(bacnetDevice);
										}
										
										myObjects.add(bacnetDevice);
										
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
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		return connectors;
	}
	
	@Override
	public void removeDevices(ObjectBroker objectBroker) {
		synchronized(myObjects) {
			for(Obj obj : myObjects) {
				objectBroker.removeObj(obj.getFullContextPath());
			}
		}
	}
	
	private class DeviceDiscoveryListener implements BACnetConnector.DeviceDiscoveryListener {
		private ObjectBroker objectBroker;
		private Boolean groupCommEnabled; 
		private Boolean historyEnabled;
	
		public DeviceDiscoveryListener(ObjectBroker objectBroker, Boolean groupCommEnabled, Boolean historyEnabled) {
			this.objectBroker = objectBroker;
			this.groupCommEnabled = groupCommEnabled;
			this.historyEnabled = historyEnabled;
		}
		
		@Override
		public void deviceDiscovered(Obj device) {
			
			
			
			// add all children objects also in the object broker
			
			Obj[] list = device.list();
			
//			for(Obj obj : list){
//				objectBroker.addObj(obj, true);
//			}
			
			if(groupCommEnabled != null && groupCommEnabled){
				objectBroker.enableGroupComm(device);
			}
			
			if(historyEnabled != null && historyEnabled){
				objectBroker.addHistoryToDatapoints(device);
			}
			
			myObjects.add(device);
			
			device.initialize();
			objectBroker.addObj(device, true);
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
