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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.persistent.ConfigsDbImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;

public class DeviceLoaderImpl implements DeviceLoader {
	private static Logger log = Logger.getLogger(DeviceLoaderImpl.class.getName());

	private XMLConfiguration devicesConfig;

	public DeviceLoaderImpl() {
		this(DEVICE_CONFIGURATION_LOCATION);
	}
	
	public DeviceLoaderImpl(String devicesConfigFile) {
		try {
			devicesConfig = new XMLConfiguration(devicesConfigFile);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		ArrayList<Connector> connectors = new ArrayList<Connector>();
		
		// use technology specific device loaders
		int deviceLoadersSize = 0;
		
		Object deviceLoaders = devicesConfig.getProperty("deviceloaders.device-loader");
		
		if(deviceLoaders != null){
			deviceLoadersSize = 1;
		}
		
		if(deviceLoaders instanceof Collection<?>){
			deviceLoadersSize = ((Collection<?>) deviceLoaders).size();
		}
		
		// Transition step: replace deviceLoadersSize with DeviceConfigs.getInstance().getAllDeviceLoader().length when done
		for(int i = 0; i< deviceLoadersSize; i++){
		
			// Transition step: change to DeviceConfigs.getInstance().getAllDeviceLoader()[i] when done
			String deviceLoaderName = devicesConfig.getString("deviceloaders.device-loader(" + i + ")");

			log.info("Found device loader: " + deviceLoaderName);
			
			// Transition step: comment when done
			ConfigsDbImpl.getInstance().prepareDeviceLoader(deviceLoaderName);
			
			try {
				DeviceLoader devLoader = (DeviceLoader) Class.forName(deviceLoaderName).newInstance();
				devLoader.setConfiguration(devicesConfig);
				ArrayList<Connector> connectorsList = devLoader.initDevices(objectBroker);
				if(connectorsList != null)
					connectors.addAll(connectorsList);			
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				log.severe("Could not instantiate device loader " + deviceLoaderName + " - not found on classpath!");
				log.severe(" Debug Info:" + e.getMessage());
			}
		}
	
		return connectors;
	}

	@Override
	public void removeDevices(ObjectBroker objectBroker) {
		// This device loader acts as a parent loader for all technology specific device loaders.
	}

	@Override
	public void setConfiguration(XMLConfiguration devicesConfiguration) {
		this.devicesConfig = devicesConfiguration;
	}

}
