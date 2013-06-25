/*
  	Copyright (c) 2013 - IotSyS KNX Connector
 	Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
  	All rights reserved.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package at.ac.tuwien.auto.iotsys.gateway.connectors.knx;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;

public class KNXBundleActivator implements BundleActivator, ServiceListener {
	private static final Logger log = Logger.getLogger(KNXBundleActivator.class
			.getName());

	private DeviceLoader deviceLoader = new KNXDeviceLoaderImpl();
	private ArrayList<Connector> connectors = null;

	private volatile boolean registered = false;

	private BundleContext context = null;

	public void start(BundleContext context) throws Exception {
		log.info("Starting KNX connector");
		this.context = context;
		ServiceReference serviceReference = context
				.getServiceReference(ObjectBroker.class.getName());
		if (serviceReference == null) {
			log.severe("Could not find a running object broker to register devices!");

		} else {
			synchronized (this) {
				log.info("Initiating KNX devices.");
				ObjectBroker objectBroker = (ObjectBroker) context
						.getService(serviceReference);
				connectors = deviceLoader.initDevices(objectBroker);
				registered = true;
			}

		}

		context.addServiceListener(this);
	}

	public void stop(BundleContext context) throws Exception {
		log.info("Stopping KNX connector");
		ServiceReference serviceReference = context
				.getServiceReference(ObjectBroker.class.getName());
		if (serviceReference == null) {
			log.severe("Could not find a running object broker to unregister devices!");
		} else {
			log.info("Removing KNX Devices.");
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
					log.info("ObjectBroker detected.");

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
