package at.ac.tuwien.auto.iotsys.control;

import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;

/**
 * Basic client interaction with the oBIX object broker.
 * 
 * @author Markus Jung
 */
public class TestClientActivator implements BundleActivator, ServiceListener {
	private static final Logger log = Logger
			.getLogger(TestClientActivator.class.getName());

	private TestClient testClient = null;

	private volatile boolean started = false;

	private BundleContext context = null;

	private boolean knxActive = false;
	private boolean virtualActive = false;
	private boolean gatewayActive = false;

	private boolean objectBrokerDetected = false;

	@Override
	public void start(BundleContext context) throws Exception {
		log.info("Starting Test Client.");
		this.context = context;

		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if(bundle.getSymbolicName() != null){
				if(bundle.getSymbolicName().equals("at.ac.tuwien.auto.iotsys.virtual") && bundle.getState() == Bundle.ACTIVE){
					virtualActive = true;
				} else if(bundle.getSymbolicName().equals("at.ac.tuwien.auto.iotsys.knx") && bundle.getState() == Bundle.ACTIVE){
					knxActive = true;
				} else if(bundle.getSymbolicName().equals("at.ac.tuwien.auto.iotsys.gateway") && bundle.getState() == Bundle.ACTIVE){
					gatewayActive = true;
				}
			}
		}

		ServiceReference serviceReference = context
				.getServiceReference(ObjectBroker.class.getName());
		if (serviceReference == null) {
			log.severe("Could not find a running object broker to register devices!");

		} else {

			synchronized (this) {
				log.info("Starting test client.");
				ObjectBroker objectBroker = (ObjectBroker) context
						.getService(serviceReference);
				testClient = new TestClient(objectBroker);

			}
			if (virtualActive && knxActive && gatewayActive) {
				testClient.runTests();
				started = true;
			}
		}
		context.addServiceListener(this);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		log.info("Stopping Test Client.");

		// nothing to do

	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		log.info("Service changed.");
		// require VirtualConnector and KNXConnector and ObjectBroker to be started

		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if(bundle.getSymbolicName() != null){
				if(bundle.getSymbolicName().equals("at.ac.tuwien.auto.iotsys.virtual") && bundle.getState() == Bundle.ACTIVE){
					virtualActive = true;
				} else if(bundle.getSymbolicName().equals("at.ac.tuwien.auto.iotsys.knx") && bundle.getState() == Bundle.ACTIVE){
					knxActive = true;
				} else if(bundle.getSymbolicName().equals("at.ac.tuwien.auto.iotsys.gateway") && bundle.getState() == Bundle.ACTIVE){
					gatewayActive = true;
				}
			}
		}

		
		

		String[] objectClass = (String[]) event.getServiceReference()
				.getProperty("objectClass");
		if (event.getType() == ServiceEvent.REGISTERED) {
			if (objectClass[0].equals(ObjectBroker.class.getName())) {

				synchronized (this) {
					log.info("ObjectBroker detected.");

					if (!started) {
						ObjectBroker objectBroker = (ObjectBroker) context
								.getService(event.getServiceReference());
						objectBrokerDetected = true;
						try {
							testClient = new TestClient(objectBroker);
							started = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}
		}

		if (virtualActive && knxActive && gatewayActive && objectBrokerDetected) {
			log.info("Starting control tests.");
			testClient.runTests();
		}

	}
}
