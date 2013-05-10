package at.ac.tuwien.auto.iotsys.gateway.connectors.xbee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import obix.Uri;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.xbee.TemperatureSensorImplXBee;

public class XBeeDeviceLoaderImpl implements DeviceLoader {

	private final ArrayList<String> myObjects = new ArrayList<String>();

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
					log.info("Connecting XBee connector to COM Port: " + serialPort);
					XBeeConnector xBeeConnector = new XBeeConnector(serialPort,
							9600);
					xBeeConnector.connect();

					connectors.add(xBeeConnector);

					// add devices
//
//					IndoorBrightnessSensorImpl xBeeBrightness = new IndoorBrightnessSensorImplXBee(
//							xBeeConnector);
//					xBeeBrightness.setHref(new Uri("brightnessSensor"));
//					xBeeBrightness.setName("brightnessSensor");

					TemperatureSensorImplXBee xBeeTemperatureSensor = new TemperatureSensorImplXBee(
							xBeeConnector, "0013a200407c1715");
					xBeeTemperatureSensor.setHref(new Uri("temperature"));
					xBeeTemperatureSensor.setName("temperature");

					// add virtual devices to object broker and remember all
					// assigned
					// URIs, due to child objects there could be one or many
					synchronized (myObjects) {
//						myObjects.addAll(objectBroker.addObj(xBeeBrightness));
						myObjects.addAll(objectBroker
								.addObj(xBeeTemperatureSensor));
					}

					// enable history yes/no?
//					objectBroker.addHistoryToDatapoints(xBeeBrightness, 100);
					objectBroker.addHistoryToDatapoints(xBeeTemperatureSensor,
							100);
					
					objectBroker.enableGroupComm(xBeeTemperatureSensor);
//					objectBroker.enableObjectRefresh(xBeeTemperatureSensor);

				} catch (Exception e) {

					e.printStackTrace();
				}
			}

		}

		/*
		 * // parse XML configuration
		 *  for connections and objects // NOTE: this
		 * loader allow to directly instantiate the base oBIX objects // for
		 * testing purposes int connectorsSize = 0; // virtual Object
		 * virtualConnectors = devicesConfig
		 * .getProperty("virtual.connector.name"); if (virtualConnectors !=
		 * null) { connectorsSize = 1; } else { connectorsSize = 0; }
		 * 
		 * if (virtualConnectors instanceof Collection<?>) { virtualConnectors =
		 * ((Collection<?>) virtualConnectors).size(); }
		 * 
		 * for (int connector = 0; connector < connectorsSize; connector++) {
		 * HierarchicalConfiguration subConfig = devicesConfig
		 * .configurationAt("virtual.connector(" + connector + ")");
		 * 
		 * Object virtualConfiguredDevices = subConfig
		 * .getProperty("device.type"); String connectorName =
		 * subConfig.getString("name"); Boolean enabled =
		 * subConfig.getBoolean("enabled", false);
		 * 
		 * if (enabled) { try { if (virtualConfiguredDevices instanceof
		 * Collection<?>) { Collection<?> wmbusDevice = (Collection<?>)
		 * virtualConfiguredDevices; log.info(wmbusDevice.size() +
		 * " virtual devices found in configuration for connector " +
		 * connectorName);
		 * 
		 * for (int i = 0; i < wmbusDevice.size(); i++) { String type =
		 * subConfig.getString("device(" + i + ").type"); List<Object> address =
		 * subConfig.getList("device(" + i + ").address"); String ipv6 =
		 * subConfig.getString("device(" + i + ").ipv6"); String href =
		 * subConfig.getString("device(" + i + ").href");
		 * 
		 * Boolean historyEnabled = subConfig.getBoolean( "device(" + i +
		 * ").historyEnabled", false);
		 * 
		 * Integer historyCount = subConfig.getInt("device(" + i +
		 * ").historyCount", 0);
		 * 
		 * if (type != null && address != null) { try {
		 * 
		 * Obj virtualObj = (Obj) Class.forName(type) .newInstance();
		 * virtualObj.setHref(new Uri(href));
		 * 
		 * if (ipv6 != null) { myObjects.addAll(objectBroker.addObj( virtualObj,
		 * ipv6)); } else { myObjects.addAll(objectBroker .addObj(virtualObj));
		 * }
		 * 
		 * virtualObj.initialize();
		 * 
		 * if (historyEnabled != null && historyEnabled) { if (historyCount !=
		 * null && historyCount != 0) { objectBroker .addHistoryToDatapoints(
		 * virtualObj, historyCount); } else { objectBroker
		 * .addHistoryToDatapoints(virtualObj); } }
		 * 
		 * } catch (SecurityException e) { e.printStackTrace(); } catch
		 * (ClassNotFoundException e) { e.printStackTrace(); } } } } else {
		 * log.info("No virtual devices configured for connector " +
		 * connectorName); } } catch (Exception e) { e.printStackTrace(); } } }
		 */
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

}
