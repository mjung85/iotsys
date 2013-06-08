package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean;

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

public class EnoceanBundleActivator implements BundleActivator, ServiceListener {
	private static final Logger log = Logger.getLogger(EnoceanBundleActivator.class
			.getName());

	private DeviceLoader deviceLoader = new EnoceanDeviceLoaderImpl();
	private ArrayList<Connector> connectors = null;

	private volatile boolean registered = false;

	private BundleContext context = null;

	public void start(BundleContext context) throws Exception {
		log.info("Starting EnOcean connector");
		this.context = context;
		ServiceReference<ObjectBroker> serviceReference = context
				.getServiceReference(ObjectBroker.class);
		if (serviceReference == null) {
			log.severe("Could not find a running object broker to register devices!");

		} else {
			synchronized (this) {
				log.info("Initiating EnOcean devices.");
				ObjectBroker objectBroker = (ObjectBroker) context
						.getService(serviceReference);
				connectors = deviceLoader.initDevices(objectBroker);
				registered = true;
			}

		}

		context.addServiceListener(this);
	}

	public void stop(BundleContext context) throws Exception {
		log.info("Stopping EnOcean connector");
		ServiceReference<ObjectBroker> serviceReference = context
				.getServiceReference(ObjectBroker.class);
		if (serviceReference == null) {
			log.severe("Could not find a running object broker to unregister devices!");
		} else {
			log.info("Removing EnOcean Devices.");
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
