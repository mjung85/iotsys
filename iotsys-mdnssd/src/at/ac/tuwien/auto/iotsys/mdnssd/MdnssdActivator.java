package at.ac.tuwien.auto.iotsys.mdnssd;

import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import at.ac.tuwien.auto.iotsys.commons.MdnsResolver;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;


public class MdnssdActivator  implements BundleActivator {

	private static final Logger log = Logger.getLogger(MdnssdActivator.class
			.getName());
	
	private NamedImpl named;
		
	@Override
	public void start(BundleContext context) throws Exception {
		boolean enableServiceDiscovery = Boolean.parseBoolean(PropertiesLoader.getInstance().getProperties().getProperty("iotsys.gateway.servicediscovery.enabled", "false"));
		
		if(enableServiceDiscovery){
			log.info("Starting Mdnssd module");
			context.registerService(MdnsResolver.class.getName(), MdnsResolverImpl.getInstance(), null);
			log.info("Register Mdnssd resolver");
			
			named = new NamedImpl();
			named.startNamedService();
			log.info("Started named service");
		}
		else{
			log.info("mdnssd module disabled.");
		}
		
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		log.info("Stopping Mdnssd module");
		named.stopNamedService();
		
	}

}
