package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean;

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
import at.ac.tuwien.auto.iotsys.commons.persistent.ConfigsDbImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Device;

import com.fasterxml.jackson.databind.JsonNode;

public class EnoceanDeviceLoaderImpl implements DeviceLoader {

	private XMLConfiguration devicesConfig = new XMLConfiguration();

	private final static Logger log = Logger
			.getLogger(EnoceanDeviceLoaderImpl.class.getName());
	
	private ArrayList<Obj> myObjects = new ArrayList<Obj>();

	public EnoceanDeviceLoaderImpl() {
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

		List<JsonNode> connectorsFromDb = ConfigsDbImpl.getInstance().getConnectors("enocean");
		int connectorsSize = 0;
		// WMBus
		if (connectorsFromDb.size() <= 0) {
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
		} else
			connectorsSize = connectorsFromDb.size();
		
		log.info("Found " + connectorsSize + " EnOcean connectors.");
		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig.configurationAt("enocean.connector(" + connector + ")");

			Object enoceanConfiguredDevices = subConfig.getProperty("device.type");
			String connectorId = "";
			String connectorName = subConfig.getString("name");
			String serialPort = subConfig.getString("serialPort");
			Boolean enabled = subConfig.getBoolean("enabled", false);

			try {
				connectorId = connectorsFromDb.get(connector).get("_id").asText();
				connectorName = connectorsFromDb.get(connector).get("name").asText();
				enabled =  connectorsFromDb.get(connector).get("enabled").asBoolean();
			} catch (Exception e){
				log.info("Cannot fetch configuration from Database, using devices.xml");
			}
			
			
			// PropertyConfigurator.configure("log4j.properties");
			if (enabled) {
				try {
					log.info("Connecting EnOcean connector to COM Port: "+ serialPort);
					EnoceanConnector enoceanConnector = new EnoceanConnector(serialPort);
					enoceanConnector.setName(connectorName);
					enoceanConnector.setTechnology("enocean");
					enoceanConnector.setEnabled(enabled);
					
					//enoceanConnector.connect();

					connectors.add(enoceanConnector);

					int numberOfDevices = 0;
					List<Device> devicesFromDb = ConfigsDbImpl.getInstance().getDevices(connectorId);

					if (devicesFromDb.size() <= 0) {
						if (enoceanConfiguredDevices instanceof Collection<?>) {
							Collection<?> enoceanDevices = (Collection<?>) enoceanConfiguredDevices;
							numberOfDevices = enoceanDevices.size();
						} else if (enoceanConfiguredDevices != null) {
							numberOfDevices = 1;
						}
					} else
						numberOfDevices = devicesFromDb.size();

					log.info(numberOfDevices
							+ " EnOcean devices found in configuration for connector "
							+ connectorName);
					
					// add devices
					for (int i = 0; i < numberOfDevices; i++) {
						String type = subConfig.getString("device(" + i + ").type");
						List<Object> address = subConfig.getList("device(" + i + ").address");
						String addressString = address.toString();
						String ipv6 = subConfig.getString("device(" + i + ").ipv6");
						String href = subConfig.getString("device(" + i + ").href");

						String name = subConfig.getString("device(" + i + ").name");
						Boolean historyEnabled = subConfig.getBoolean("device(" + i + ").historyEnabled", false);
						Boolean groupCommEnabled = subConfig.getBoolean( "device(" + i + ").groupCommEnabled", false);
						Integer historyCount = subConfig.getInt("device(" + i + ").historyCount", 0);

						Boolean refreshEnabled = subConfig.getBoolean("device(" + i + ").refreshEnabled", false);
						
						Device deviceFromDb;
						try {
							deviceFromDb = devicesFromDb.get(i);
							type = deviceFromDb.getType();
							addressString = deviceFromDb.getAddress();
							ipv6 = deviceFromDb.getIpv6();
							href = deviceFromDb.getHref();
							name = deviceFromDb.getName();
							historyEnabled = deviceFromDb.isHistoryEnabled();
							groupCommEnabled = deviceFromDb.isGroupcommEnabled();
							refreshEnabled = deviceFromDb.isRefreshEnabled();
							historyCount = deviceFromDb.getHistoryCount();
						} 
						catch (Exception e) {}
						
						// Transition step: comment when done
						Device d = new Device(type, ipv6, addressString, href, name, null, historyCount, historyEnabled, groupCommEnabled, refreshEnabled);
						ConfigsDbImpl.getInstance().prepareDevice(connectorName, d);		
						
						log.info("type: " + type);
						
						if (type != null && address != null) {
							int addressCount = address.size();
							try {
								Constructor<?>[] declaredConstructors = Class.forName(type).getDeclaredConstructors();
								for (int k = 0; k < declaredConstructors.length; k++) {
									if (declaredConstructors[k].getParameterTypes().length == addressCount + 1) { 
										
										Object[] args = new Object[address.size() + 1];
										// first arg is KNX connector
										
										args[0] = enoceanConnector;
										for (int l = 1; l <= address.size(); l++) {

											String adr = (String) address.get(l - 1);
											if (adr == null || adr.equals("null")) {
												args[l] = null;
											} else {
												args[l] = new String(adr);
											}

										}
										try {
											// create a instance of the
											// specified KNX device
											Obj enoceanDevice = (Obj) declaredConstructors[k].newInstance(args);

											enoceanDevice.setHref(new Uri(URLEncoder.encode(connectorName, "UTF-8") + "/" + href));

											if (name != null && name.length() > 0) {
												enoceanDevice.setName(name);
											}

											ArrayList<String> assignedHrefs = null;

											if (ipv6 != null) 
											{
												objectBroker.addObj(enoceanDevice, ipv6);
											} else {
												objectBroker.addObj(enoceanDevice);
											}

											myObjects.add(enoceanDevice);
											enoceanDevice.initialize();

											if (historyEnabled != null && historyEnabled) 
											{
												if (historyCount != null && historyCount != 0) {
													objectBroker.addHistoryToDatapoints(enoceanDevice, historyCount);
												} else {
													objectBroker.addHistoryToDatapoints(enoceanDevice);
												}
											}

											if (groupCommEnabled) 
											{
												objectBroker.enableGroupComm(enoceanDevice);
											}

											if (refreshEnabled != null && refreshEnabled) 
											{
												objectBroker.enableObjectRefresh(enoceanDevice);
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
								e.getMessage();//.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.getMessage();//.printStackTrace();
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
