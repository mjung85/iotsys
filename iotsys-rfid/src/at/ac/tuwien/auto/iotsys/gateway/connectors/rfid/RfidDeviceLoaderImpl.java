package at.ac.tuwien.auto.iotsys.gateway.connectors.rfid;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import at.ac.tuwien.auto.iotsys.commons.persistent.DeviceConfigs;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Device;

import com.fasterxml.jackson.databind.JsonNode;

public class RfidDeviceLoaderImpl implements DeviceLoader {
	
	
	private ArrayList<Obj> myObjects = new ArrayList<Obj>();

	private XMLConfiguration devicesConfig = new XMLConfiguration();

	private final static Logger log = Logger
			.getLogger(RfidDeviceLoaderImpl.class.getName());

	public RfidDeviceLoaderImpl() {
		String devicesConfigFile = DEVICE_CONFIGURATION_LOCATION;
		
		try {
			
			devicesConfig = new XMLConfiguration(devicesConfigFile);
		} catch (Exception e) {
			log.info("devices loader ERROR!");
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		// Hard-coded connections and object creation

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		List<JsonNode> connectorsFromDb = DeviceConfigs.getInstance().getConnectors("rfid");
		int connectorsSize = 0;

		if (connectorsFromDb.size() <= 0) {
			Object rfidConnectors = devicesConfig
					.getProperty("rfid.connector.name");
			if (rfidConnectors != null) {
				connectorsSize = 1;
			} else {
				connectorsSize = 0;
			}

			if (rfidConnectors instanceof Collection<?>) {
				connectorsSize = ((Collection<?>) rfidConnectors).size();
			}
		} else
			connectorsSize = connectorsFromDb.size();
		log.info("Found " + connectorsSize + " RFID connectors.");
		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("rfid.connector(" + connector + ")");

			Object rfidConfiguredDevices = subConfig.getProperty("device.type");
			String connectorId = "";
			String connectorName = subConfig.getString("name");
			String serialPort = subConfig.getString("serialPort");
			Boolean enabled = subConfig.getBoolean("enabled", false);
			
			try {
				connectorId = connectorsFromDb.get(connector).get("_id").asText();
				connectorName = connectorsFromDb.get(connector).get("name").asText();
				enabled =  connectorsFromDb.get(connector).get("enabled").asBoolean();
			} catch (Exception e){}
			
			// PropertyConfigurator.configure("log4j.properties");
			if (enabled) {
				try {
					log.info("Connecting RFID connector to COM Port: "
							+ serialPort);
					RfidConnector rfidConnector = new RfidConnector(serialPort);
					rfidConnector.setName(connectorName);
					rfidConnector.setEnabled(enabled);
					rfidConnector.setTechnology("rfid");
					
					//rfidConnector.connect();

					connectors.add(rfidConnector);

					int numberOfDevices = 0;
					List<Device> devicesFromDb = DeviceConfigs.getInstance().getDevices(connectorId);

					if (devicesFromDb.size() <= 0) {
						if (rfidConfiguredDevices instanceof Collection<?>) {
							Collection<?> rfidDevices = (Collection<?>) rfidConfiguredDevices;
							numberOfDevices = rfidDevices.size();

						} else if (rfidConfiguredDevices != null) {
							numberOfDevices = 1; // there is at least one
													// device.
						}
					} else
						numberOfDevices = devicesFromDb.size();

					log.info(numberOfDevices
							+ " RFID devices found in configuration for connector "
							+ connectorName);

					// add devices
					for (int i = 0; i < numberOfDevices; i++) {
						String type = subConfig.getString("device(" + i
								+ ").type");
						
						String ipv6 = subConfig.getString("device(" + i
								+ ").ipv6");
						String href = subConfig.getString("device(" + i
								+ ").href");

						String name = subConfig.getString("device(" + i
								+ ").name");

						Boolean historyEnabled = subConfig.getBoolean("device("
								+ i + ").historyEnabled", false);

						boolean groupCommEnabled = subConfig.getBoolean(
								"device(" + i + ").groupCommEnabled", false);

						boolean refreshEnabled = subConfig.getBoolean("device("
								+ i + ").refreshEnabled", false);

						Integer historyCount = subConfig.getInt("device(" + i
								+ ").historyCount", 0);

						Device deviceFromDb;
						try {
							deviceFromDb = devicesFromDb.get(i);
							type = deviceFromDb.getType();
							ipv6 = deviceFromDb.getIpv6();
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
						Device d = new Device(type, ipv6, null, href, name, null, historyCount, historyEnabled, groupCommEnabled, refreshEnabled);
						DeviceConfigs.getInstance().prepareDevice(connectorName, d);
						
						if (type != null) {
							
							try {
								Constructor<?>[] declaredConstructors = Class
										.forName(type)
										.getDeclaredConstructors();
								for (int k = 0; k < declaredConstructors.length; k++) {
									if (declaredConstructors[k]
											.getParameterTypes().length == 1) { // constructor
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
										Object[] args = new Object[1];
										// first arg is KNX connector

										args[0] = rfidConnector;
									
										try {
											// create a instance of the
											// specified KNX device
											Obj rfidDevice = (Obj) declaredConstructors[k].newInstance(args);

											rfidDevice.setHref(new Uri(URLEncoder.encode(connectorName, "UTF-8") + "/" + href));

											if (name != null
													&& name.length() > 0) {
												rfidDevice.setName(name);
											}										

											if (ipv6 != null) {
												objectBroker
														.addObj(rfidDevice, ipv6);
											} else {
												objectBroker
														.addObj(rfidDevice);
											}																				

											myObjects.add(rfidDevice);

											rfidDevice.initialize();

											if (historyEnabled != null
													&& historyEnabled) {
												if (historyCount != null
														&& historyCount != 0) {
													objectBroker
															.addHistoryToDatapoints(
																	rfidDevice,
																	historyCount);
												} else {
													objectBroker
															.addHistoryToDatapoints(rfidDevice);
												}
											}

											if (groupCommEnabled) {
												objectBroker
														.enableGroupComm(rfidDevice);
											}

											if (refreshEnabled) {
												objectBroker
														.enableObjectRefresh(rfidDevice);
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
		synchronized(myObjects){
			for(Obj obj : myObjects) {
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
