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

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;

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

		int connectorsSize = 0;
	
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
		log.info("Found " + connectorsSize + " RFID connectors.");
		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("rfid.connector(" + connector + ")");

			Object rfidConfiguredDevices = subConfig.getProperty("device.type");
			String connectorName = subConfig.getString("name");
			String serialPort = subConfig.getString("serialPort");
			Boolean enabled = subConfig.getBoolean("enabled", false);

			// PropertyConfigurator.configure("log4j.properties");
			if (enabled) {
				try {
					log.info("Connecting RFID connector to COM Port: "
							+ serialPort);
					RfidConnector rfidConnector = new RfidConnector(serialPort);
					rfidConnector.connect();

					connectors.add(rfidConnector);

					int numberOfDevices = 0;
					if (rfidConfiguredDevices instanceof Collection<?>) {
						Collection<?> rfidDevices = (Collection<?>) rfidConfiguredDevices;
						numberOfDevices = rfidDevices.size();
					
					} else if (rfidConfiguredDevices != null) {
						numberOfDevices = 1; // there is at least one device.
					}

					log.info(numberOfDevices
							+ " RFID devices found in configuration for connector "
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

										args[0] = rfidConnector;
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

											if (refreshEnabled != null
													&& refreshEnabled) {
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
