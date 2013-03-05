/*******************************************************************************
 * Copyright (c) 2013
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

package at.ac.tuwien.auto.iotsys.gateway;

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
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.HistoryHelper;

public class DeviceLoaderImpl implements DeviceLoader {
	private static Logger log = Logger.getLogger(DeviceLoaderImpl.class.getName());

	private XMLConfiguration devicesConfig = new XMLConfiguration();

	public DeviceLoaderImpl() {
//		String devicesConfigFile = PropertiesLoader.getInstance()
//				.getProperties()
//				.getProperty("iotsys.gateway.deviceConfigFile", "devices.xml");
		
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
		
		if(knxConnectors != null){
			connectorsSize = 1;
		}

		if (knxConnectors instanceof Collection<?>) {
			connectorsSize = ((Collection<?>) knxConnectors).size();
		}		
		
		// use technology specific device loaders
		int deviceLoadersSize = 0;
		
		Object deviceLoaders = devicesConfig.getProperty("deviceloaders.device-loader");
		
		if(deviceLoaders != null){
			deviceLoadersSize = 1;
		}
		
		if(deviceLoaders instanceof Collection<?>){
			deviceLoadersSize = ((Collection<?>) deviceLoaders).size();
		}
		
		for(int i = 0; i< deviceLoadersSize; i++){
		
			String deviceLoaderName = devicesConfig.getString("deviceloaders.device-loader(" + i + ")");
			log.info("Found device loader: " + deviceLoaderName);
			
			try {
				DeviceLoader devLoader = (DeviceLoader) Class.forName(deviceLoaderName).newInstance();				
				devLoader.initDevices(objectBroker);			
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				log.severe("Could not instantiate device loader " + deviceLoaderName + " - not found on classpath!");
			}
		}

		// virtual
		Object virtualConnectors = devicesConfig
				.getProperty("virtual.connector.name");
		if(virtualConnectors != null){
			connectorsSize = 1;
		}
		else{
			connectorsSize = 0;
		}

		if (virtualConnectors instanceof Collection<?>) {
			virtualConnectors = ((Collection<?>) virtualConnectors).size();
		}

		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("virtual.connector(" + connector + ")");

			Object virtualConfiguredDevices = subConfig
					.getProperty("device.type");
			String connectorName = subConfig.getString("name");
			Boolean enabled = subConfig.getBoolean("enabled", false);

			if (enabled) {
				try {
					if (virtualConfiguredDevices instanceof Collection<?>) {
						Collection<?> wmbusDevice = (Collection<?>) virtualConfiguredDevices;
						log.info(wmbusDevice.size()
								+ " virtual devices found in configuration for connector "
								+ connectorName);

						for (int i = 0; i < wmbusDevice.size(); i++) {
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

							if (type != null && address != null) {
								try {

									Obj virtualObj = (Obj) Class.forName(type)
											.newInstance();
									virtualObj.setHref(new Uri(href));

									if (ipv6 != null) {
										objectBroker.addObj(virtualObj, ipv6);
									} else {
										objectBroker.addObj(virtualObj);
									}

									virtualObj.initialize();
									
									if (historyEnabled != null
											&& historyEnabled) {
										if (historyCount != null
												&& historyCount != 0) {
											HistoryHelper
													.addHistoryToDatapoints(
															virtualObj,
															historyCount);
										} else {
											HistoryHelper
													.addHistoryToDatapoints(virtualObj);
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
						log.info("No WMBus devices configured for connector "
								+ connectorName);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		// if(bacnetAlab){
		//
		// TemperatureSensorImpl temperatureSensorBacnet =
		// new TemperatureSensorImplBacnet(bacnetConnector, 2098177, new
		// ObjectIdentifier(ObjectType.analogInput, 1),
		// PropertyIdentifier.presentValue);
		// temperatureSensorBacnet.setName("roomTemperature");
		// temperatureSensorBacnet.setHref(new
		// Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/roomTemperature"));
		// _objectBroker.addObj(temperatureSensorBacnet);
		//
		// FanSpeedActuatorImpl fanSpeedActuatorImplZul = new
		// FanSpeedActuatorImplBacnet(bacnetConnector, new
		// BacnetDataPointInfo(2098177, new
		// ObjectIdentifier(ObjectType.analogOutput,4),
		// PropertyIdentifier.presentValue),
		// new BacnetDataPointInfo(2098177, new
		// ObjectIdentifier(ObjectType.binaryOutput,3),
		// PropertyIdentifier.presentValue));
		// fanSpeedActuatorImplZul.setName("Zuluft");
		// fanSpeedActuatorImplZul.setHref(new
		// Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/fanIn"));
		// _objectBroker.addObj(fanSpeedActuatorImplZul);
		//
		// FanSpeedActuatorImpl fanSpeedActuatorImplAbl = new
		// FanSpeedActuatorImplBacnet(bacnetConnector, new
		// BacnetDataPointInfo(2098177, new
		// ObjectIdentifier(ObjectType.analogOutput,4),
		// PropertyIdentifier.presentValue),
		// new BacnetDataPointInfo(2098177, new
		// ObjectIdentifier(ObjectType.binaryOutput,4),
		// PropertyIdentifier.presentValue));
		// fanSpeedActuatorImplAbl.setName("Abluft");
		// fanSpeedActuatorImplAbl.setHref(new
		// Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/fanOut"));
		// _objectBroker.addObj(fanSpeedActuatorImplAbl);
		//
		// PumpActuatorImpl pumpActuatorHeat = new
		// PumpActuatorImplBacnet(bacnetConnector, new
		// BacnetDataPointInfo(2098177, new
		// ObjectIdentifier(ObjectType.analogOutput,2),
		// PropertyIdentifier.presentValue));
		// pumpActuatorHeat.setName("WaermePumpeZul");
		// pumpActuatorHeat.setHref(new
		// Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/pumpHeating"));
		// _objectBroker.addObj(pumpActuatorHeat);
		//
		// PumpActuatorImpl pumpActuatorCool = new
		// PumpActuatorImplBacnet(bacnetConnector, new
		// BacnetDataPointInfo(2098177, new
		// ObjectIdentifier(ObjectType.analogOutput,1),
		// PropertyIdentifier.presentValue));
		// pumpActuatorCool.setName("KaeltePumpZul");
		// pumpActuatorCool.setHref(new
		// Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/pumpCooling"));
		// _objectBroker.addObj(pumpActuatorCool);
		//
		// CoolerActuatorImpl cooler = new
		// CoolerActuatorImplBacnet(bacnetConnector, new
		// BacnetDataPointInfo(2098177, new
		// ObjectIdentifier(ObjectType.binaryOutput,1),
		// PropertyIdentifier.presentValue));
		// cooler.setName("cooler");
		// cooler.setHref(new
		// Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/cooler"));
		// _objectBroker.addObj(cooler);
		//
		// BoilerActuatorImpl boiler = new
		// BoilerActuatorImplBacnet(bacnetConnector, new
		// BacnetDataPointInfo(2098177, new
		// ObjectIdentifier(ObjectType.binaryOutput,2),
		// PropertyIdentifier.presentValue));
		// boiler.setName("boiler");
		// boiler.setHref(new
		// Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/boiler"));
		// _objectBroker.addObj(boiler);
		//
		// }

		// LightSwitchActuatorImpl tempTest = new LightSwitchActuatorImpl();
		// tempTest.setHref(new
		// Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/lightTest"));
		// _objectBroker.addObj(tempTest, IPv6_7);
		//

		// if (virtualActive) {
		//
		// // Sensor
		// ShuttersAndBlindsSunblindSensorImpl sabImpl = new
		// ShuttersAndBlindsSunblindSensorImpl();
		// sabImpl.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/sab"));
		// objectBroker.addObj(sabImpl, IPv6_10);
		//
		// PresenceDetectorSensorImpl pdImpl = new PresenceDetectorSensorImpl();
		// pdImpl.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/pd"));
		// objectBroker.addObj(pdImpl, IPv6_9);
		//
		// RoomRelativeHumiditySensorImpl rrhImpl = new
		// RoomRelativeHumiditySensorImpl();
		// rrhImpl.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/rrh"));
		// objectBroker.addObj(rrhImpl, IPv6_8);
		//
		// SunIntensitySensorImpl siImpl = new SunIntensitySensorImpl();
		// siImpl.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/si"));
		// objectBroker.addObj(siImpl, IPv6_7);
		//
		// TemperatureSensorImpl tempVImpl = new TemperatureSensorImpl();
		// tempVImpl.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/tempV"));
		// objectBroker.addObj(tempVImpl, IPv6_6);
		//
		// // Actuator
		// FanSpeedActuatorImpl fsActImpl = new FanSpeedActuatorImpl();
		// fsActImpl.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/fanSpeed"));
		// objectBroker.addObj(fsActImpl, IPv6_5);
		//
		// BrightnessActuatorImpl brightnessActuatorImpl = new
		// BrightnessActuatorImpl();
		// brightnessActuatorImpl.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/brightness"));
		// objectBroker.addObj(brightnessActuatorImpl, IPv6_4);
		//
		// HVACvalveActuatorImpl hvacvalveactuatorimpl = new
		// HVACvalveActuatorImpl();
		// hvacvalveactuatorimpl.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/hvacvalve"));
		// objectBroker.addObj(hvacvalveactuatorimpl, IPv6_3);
		//
		// AirDamperActuatorImpl airdamper = new AirDamperActuatorImpl();
		// airdamper.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/airdamper"));
		// objectBroker.addObj(airdamper, IPV6_TEST);
		//
		// LightSwitchActuatorImpl lightA = new LightSwitchActuatorImpl();
		// lightA.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/lightA"));
		// objectBroker.addObj(lightA, IPv6_1);
		//
		// lightA.initialize();
		//
		// TemperatureSensorImpl temperatureSensorBacnet = new
		// TemperatureSensorImpl();
		// temperatureSensorBacnet.setName("roomTemperature");
		// temperatureSensorBacnet.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/roomTemperature"));
		// objectBroker.addObj(temperatureSensorBacnet);
		//
		// temperatureSensorBacnet.initialize();
		//
		// PumpActuatorImpl pumpActuatorHeat = new PumpActuatorImpl();
		// pumpActuatorHeat.setName("WaermePumpeZul");
		// pumpActuatorHeat.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/pumpHeating"));
		// objectBroker.addObj(pumpActuatorHeat);
		//
		// // LightSwitchActuatorImpl lightA = new LightSwitchActuatorImpl();
		// // lightA.setHref(new
		// // Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/lightA"));
		// // _objectBroker.addObj(lightA, IPv6_LOCAL1);
		//
		// // LightSwitchActuatorImpl lightA = new LightSwitchActuatorImpl();
		// // lightA.setHref(new
		// // Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/obix/lightA"));
		// // _objectBroker.addObj(lightA, IPv6_1);
		//
		// // DimmingActuatorImpl dimmingA = new DimmingActuatorImpl();
		// // dimmingA.setHref(new
		// // Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/dimmingA"));
		// // _objectBroker.addObj(dimmingA);
		//
		// // SmartMeterImpl smartMeter = new SmartMeterImpl();
		// // smartMeter.setHref(new
		// // Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/smartmeter"));
		// // _objectBroker.addObj(smartMeter);
		// }
		//
		// // if(wmbusActive){
		// // SmartMeterImplWMBus smartMeter = new
		// // SmartMeterImplWMBus(wmbusConnector, "15004474");
		// // smartMeter.createWatchDog();
		// // smartMeter.setHref(new
		// // Uri(DEFAULT_OBIX_URL_PROTOCOL+"://localhost/smartmeter"));
		// // _objectBroker.addObj(smartMeter);
		// // }
		//
		// if (wmbusActive) {
		// wmbusConnector = new WMBusConnector();
		//
		// SmartMeterImplWMBus smartMeter = new SmartMeterImplWMBus(
		// wmbusConnector, "15004474");
		// smartMeter.createWatchDog();
		// smartMeter.setHref(new Uri(DEFAULT_OBIX_URL_PROTOCOL
		// + "://localhost/smartmeter"));
		// objectBroker.addObj(smartMeter);
		//
		// }
		//
		return connectors;
	}

	@Override
	public void removeDevices(ObjectBroker objectBroker) {
		// TODO Auto-generated method stub
		
	}

}
