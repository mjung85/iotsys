package at.ac.tuwien.auto.iotsys.xacml;

import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.commons.interceptor.ClassAlreadyRegisteredException;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Interceptor;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorBroker;
import at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptor;

/**
 * 
 * @author Thomas Hofer
 * 
 */
public class PDPBundleActivator implements BundleActivator, ServiceListener {

	private Interceptor interceptor;

	private boolean registered = false;

	private BundleContext context = null;

	private Logger log = Logger.getLogger(PDPBundleActivator.class.getName());

	private boolean enableXacml = false;

	@Override
	public void start(BundleContext context) throws Exception {

		// initialize interceptor broker

		enableXacml = Boolean.parseBoolean(PropertiesLoader.getInstance()
				.getProperties().getProperty("iotsys.gateway.xacml", "false"));
		
		log.info("XACML module enabled: " + enableXacml);

		if (enableXacml) {
			this.context = context;

			interceptor = new PDPInterceptor("res/");

			ServiceReference<InterceptorBroker> interceptorRef = context
					.getServiceReference(InterceptorBroker.class);

			if (interceptorRef == null) {
				log.severe("Could not find InterceptorBroker");
			} else {
				InterceptorBroker iBroker = (InterceptorBroker) context
						.getService(interceptorRef);
				try {
					iBroker.register(interceptor);
				} catch (ClassAlreadyRegisteredException e) {
					// silent exception handling ...
					log.severe(interceptor.getClass().getSimpleName()
							+ " is already registered!");
				}
			}
			context.addServiceListener(this);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (enableXacml) {
			ServiceReference<InterceptorBroker> interceptorRef = context
					.getServiceReference(InterceptorBroker.class);

			if (interceptorRef == null) {
				log.severe("Could not find a running InterceptorBroker to unregister devices!");
			} else {
				InterceptorBroker iBroker = (InterceptorBroker) context
						.getService(interceptorRef);
				iBroker.unregister(interceptor);
			}
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		String[] objectClass = (String[]) event.getServiceReference()
				.getProperty("objectClass");

		if (event.getType() == ServiceEvent.REGISTERED) {
			if (objectClass[0].equals(InterceptorBroker.class.getName())) {

				synchronized (this) {
					log.info("InterceptorBroker detected.");

					if (!registered) {
						InterceptorBroker iBroker = (InterceptorBroker) context
								.getService(event.getServiceReference());
						try {
							if (interceptor == null) {
								interceptor = new PDPInterceptor();
							}
							iBroker.register(interceptor);
						} catch (ClassAlreadyRegisteredException e) {
							// silent exception handling ...
							log.severe(interceptor.getClass().getSimpleName()
									+ " is already registered!");
						}

					}
				}
			}
		}
	}
}
