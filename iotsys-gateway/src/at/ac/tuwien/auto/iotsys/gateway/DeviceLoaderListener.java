package at.ac.tuwien.auto.iotsys.gateway;

import java.util.logging.Logger;

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBrokerImpl;

public class DeviceLoaderListener implements ServiceListener {
	private static final Logger log = Logger.getLogger(DeviceLoaderListener.class.getName());

	public void serviceChanged(ServiceEvent event)
    {
        String[] objectClass = (String[])
            event.getServiceReference().getProperty("objectClass");

        if (event.getType() == ServiceEvent.REGISTERED)
        {
           if(objectClass[0].equals(DeviceLoader.class.getName())){
        	   log.info("DeviceLoader detected.");
        	   DeviceLoader deviceLoader =  (DeviceLoader) event.getServiceReference();
        	   deviceLoader.initDevices(ObjectBrokerImpl.getInstance());
           }
                
        }
        else if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            System.out.println(
                "Ex1: Service of type " + objectClass[0] + " unregistered.");
        }
        else if (event.getType() == ServiceEvent.MODIFIED)
        {
            System.out.println(
                "Ex1: Service of type " + objectClass[0] + " modified.");
        }
    }

}
