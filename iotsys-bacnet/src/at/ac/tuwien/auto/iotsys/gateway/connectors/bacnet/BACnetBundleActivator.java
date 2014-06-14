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

import java.util.ArrayList;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;

public class BACnetBundleActivator implements BundleActivator, ServiceListener{

	private static final Logger log = Logger.getLogger(BACnetBundleActivator.class
			.getName());

	private DeviceLoader deviceLoader = new BacnetDeviceLoaderImpl();
	private ArrayList<Connector> connectors = null;

	private volatile boolean registered = false;

	private BundleContext context = null;

	public void start(BundleContext context) throws Exception {
		log.info("Starting BACnet connector");
		this.context = context;
		ServiceReference serviceReference = context
				.getServiceReference(ObjectBroker.class.getName());
		if (serviceReference == null) {
			log.info("Could not find a running object broker to register devices! Waiting for service announcement.");

		} else {
			synchronized (this) {
				log.info("Initiating BACnet devices.");
				ObjectBroker objectBroker = (ObjectBroker) context
						.getService(serviceReference);
				connectors = deviceLoader.initDevices(objectBroker);
				registered = true;
			}

		}

		context.addServiceListener(this);
	}

	public void stop(BundleContext context) throws Exception {
		log.info("Stopping BACnet connector");
		ServiceReference serviceReference = context
				.getServiceReference(ObjectBroker.class.getName());
		if (serviceReference == null) {
			log.severe("Could not find a running object broker to unregister devices!");
		} else {
			log.info("Removing BACnet Devices.");
			ObjectBroker objectBroker = (ObjectBroker) context
					.getService(serviceReference);
			deviceLoader.removeDevices(objectBroker);
			if (connectors != null) {
				for (Connector connector : connectors) {
					try {
						connector.disconnect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		String[] objectClass = (String[]) event.getServiceReference()
				.getProperty("objectClass");

		if (event.getType() == ServiceEvent.REGISTERED) {
			if (objectClass[0].equals(ObjectBroker.class.getName())) {

				synchronized (this) {
					log.info("Object Broker detected.");

					if (!registered) {
						ObjectBroker objectBroker = (ObjectBroker) context
								.getService(event.getServiceReference());
						try {
							connectors = deviceLoader.initDevices(objectBroker);
							registered = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

		}
	}
}
