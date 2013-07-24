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
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;

public class XBeeDeviceLoaderImpl implements DeviceLoader {

	private final ArrayList<String> myObjects = new ArrayList<String>();

	private XMLConfiguration devicesConfig;

	private final static Logger log = Logger
			.getLogger(XBeeDeviceLoaderImpl.class.getName());

	@Override
	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		setConfiguration(devicesConfig);
		
		// Hard-coded connections and object creation

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		int connectorsSize = 0;
		// WMBus
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
					xBeeConnector.connect();

					connectors.add(xBeeConnector);

					int numberOfDevices = 0;
					if (xbeeConfiguredDevices != null) {
						numberOfDevices = 1; // there is at least one device.
					} else if (xbeeConfiguredDevices instanceof Collection<?>) {
						Collection<?> xbeeDevices = (Collection<?>) xbeeConfiguredDevices;
						numberOfDevices = xbeeDevices.size();
					}

					log.info(numberOfDevices
							+ " XBee devices found in configuration for connector "
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

										args[0] = xBeeConnector;
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
											Obj xBeeDevice = (Obj) declaredConstructors[k]
													.newInstance(args);

											xBeeDevice.setHref(new Uri(href));

											if (name != null
													&& name.length() > 0) {
												xBeeDevice.setName(name);
											}

											ArrayList<String> assignedHrefs = null;

											if (ipv6 != null) {
												assignedHrefs = objectBroker
														.addObj(xBeeDevice, ipv6);
											} else {
												assignedHrefs = objectBroker
														.addObj(xBeeDevice);
											}

											myObjects.addAll(assignedHrefs);

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
			for (String href : myObjects) {
				objectBroker.removeObj(href);
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
