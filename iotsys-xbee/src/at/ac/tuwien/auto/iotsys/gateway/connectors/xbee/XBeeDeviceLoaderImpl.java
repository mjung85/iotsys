package at.ac.tuwien.auto.iotsys.gateway.connectors.xbee;

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
import at.ac.tuwien.auto.iotsys.commons.Device;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.persistent.DeviceConfigs;

import com.fasterxml.jackson.databind.JsonNode;

public class XBeeDeviceLoaderImpl implements DeviceLoader {

	private final ArrayList<Obj> myObjects = new ArrayList<Obj>();

	private XMLConfiguration devicesConfig = new XMLConfiguration();

	private final static Logger log = Logger
			.getLogger(XBeeDeviceLoaderImpl.class.getName());

	public XBeeDeviceLoaderImpl() {
		String devicesConfigFile = DEVICE_CONFIGURATION_LOCATION;

		try {
			devicesConfig = new XMLConfiguration(devicesConfigFile);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		// Hard-coded connections and object creation

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		int connectorsSize = 0;
	
		Object xbeeConnectors = devicesConfig
				.getProperty("xbee.connector.name");
		if (xbeeConnectors != null) {
			connectorsSize = 1;
		} else {
			connectorsSize = 0;
		}

		if (xbeeConnectors instanceof Collection<?>) {
			connectorsSize = ((Collection<?>) xbeeConnectors).size();
		}
		log.info("Found " + connectorsSize + " XBee connectors.");
		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("xbee.connector(" + connector + ")");

			Object xbeeConfiguredDevices = subConfig.getProperty("device.type");
			String connectorName = subConfig.getString("name");
			String serialPort = subConfig.getString("serialPort");
			Boolean enabled = subConfig.getBoolean("enabled", false);

			// PropertyConfigurator.configure("log4j.properties");
			if (enabled) {
				try {
					log.info("Connecting XBee connector to COM Port: "
							+ serialPort);
					XBeeConnector xBeeConnector = new XBeeConnector(serialPort,
							9600);
					xBeeConnector.setName(connectorName);
					xBeeConnector.setPort(serialPort);
					xBeeConnector.setBaudRate(9600);
					xBeeConnector.setEnabled(enabled);
					xBeeConnector.setTechnology("xbee");
					//xBeeConnector.connect();

					connectors.add(xBeeConnector);
					
					log.info(xbeeConfiguredDevices.getClass().getName());
					
					int numberOfDevices = 0;
					if (xbeeConfiguredDevices != null) {
						numberOfDevices = 1; // there is at least one device.
					} 

					if (xbeeConfiguredDevices instanceof Collection<?>) {
						Collection<?> xbeeDevices = (Collection<?>) xbeeConfiguredDevices;
						numberOfDevices = xbeeDevices.size();
						log.info("device size: " + numberOfDevices);
					}

					log.info(numberOfDevices
							+ " XBee devices found in configuration for connector "
							+ connectorName);

					List<Device> ds = new ArrayList<Device>();
					// add devices
					for (int i = 0; i < numberOfDevices; i++) {
						String type = subConfig.getString("device(" + i
								+ ").type");
						List<Object> address = subConfig.getList("device(" + i
								+ ").address");
						String addressString = subConfig.getString("device("
								+ i + ").address");
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

						// Transition step: comment when done
						JsonNode thisConnector = DeviceConfigs.getInstance()
								.getConnectors("xbee")
								.get(connector);
						Device d = new Device(type, ipv6, addressString, href, name, null, historyCount, historyEnabled, groupCommEnabled, refreshEnabled);
						d.setConnectorId(thisConnector.get("_id").asText());
						ds.add(d);
						
						if (type != null && address != null) {
							//int addressCount = address.size();
							
							
							Object[] args = new Object[2];
							args[0] = xBeeConnector;
							args[1] = (String) address.get(0);
							//args[2] = (String) address.get(1);
							
	
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
									//	Object[] args = new Object[address
									//			.size() + 1];
										// first arg is KNX connector
										
//										Object[] args = new Object[2];
//										args[0] = xBeeConnector;
//										args[1] = (String) address.get(0);
										
										//args[2] = (String) address.get(1);
										
//										for (int l = 1; l <= address.size(); l++) {
//
//											String adr = (String) address
//													.get(l - 1);
//											if (adr == null
//													|| adr.equals("null")) {
//												args[l] = null;
//											} else {
//												args[l] = new String(adr);
//											}
//
//										}
										
										
										
										try {
											// create a instance of the
											// specified KNX device
											Obj xBeeDevice = (Obj) declaredConstructors[k]
													.newInstance(args);

											xBeeDevice.setHref(new Uri(href));

											if (name != null
													&& name.length() > 0) {
												xBeeDevice.setName(name);
											}

											

											if (ipv6 != null) {
												objectBroker
														.addObj(xBeeDevice, ipv6);
											} else {
												objectBroker
														.addObj(xBeeDevice);
											}

											myObjects.add(xBeeDevice);

											xBeeDevice.initialize();

											if (historyEnabled != null
													&& historyEnabled) {
												if (historyCount != null
														&& historyCount != 0) {
													objectBroker
															.addHistoryToDatapoints(
																	xBeeDevice,
																	historyCount);
												} else {
													objectBroker
															.addHistoryToDatapoints(xBeeDevice);
												}
											}

											if (groupCommEnabled) {
												objectBroker
														.enableGroupComm(xBeeDevice);
											}

											if (refreshEnabled != null
													&& refreshEnabled) {
												objectBroker
														.enableObjectRefresh(xBeeDevice);
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
								log.info(e.getMessage());
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
					DeviceConfigs.getInstance().addDevices(ds);
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
