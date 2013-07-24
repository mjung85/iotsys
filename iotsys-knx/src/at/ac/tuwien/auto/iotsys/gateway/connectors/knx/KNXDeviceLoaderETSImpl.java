package at.ac.tuwien.auto.iotsys.gateway.connectors.knx;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import obix.Obj;
import obix.Uri;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;

public class KNXDeviceLoaderETSImpl  implements DeviceLoader {
	private static Logger log = Logger.getLogger(KNXDeviceLoaderImpl.class
			.getName());

	private XMLConfiguration devicesConfig;
	
	private ArrayList<String> myObjects = new ArrayList<String>();

	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		setConfiguration(devicesConfig);
		
		ArrayList<Connector> connectors = new ArrayList<Connector>();

		
		KNXConnector knxConnector = new KNXConnector("192.168.161.59",
							3671, "auto");
//		try {
//			knxConnector.connect();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KNXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Obj networks = new Obj();
		networks.setName("networks");
		networks.setHref(new Uri("networks"));
		
		
		Obj p0944 = new Obj();
		p0944.setName("P-0944");
		p0944.setHref(new Uri("P-0944"));
		
		networks.add(p0944);
		
		objectBroker.addObj(networks);
		
		
		connectors.add(knxConnector);		

		return connectors;
	}

	@Override
	public void removeDevices(ObjectBroker objectBroker) {
		synchronized(myObjects){
			for(String href : myObjects){
				objectBroker.removeObj(href);
			}
		}
	}
	

	@Override
	public void setConfiguration(XMLConfiguration devicesConfiguration) {
		this.devicesConfig = devicesConfiguration;
		if (devicesConfiguration == null) {
			try {
				devicesConfig = new XMLConfiguration(DEVICE_CONFIGURATION_LOCATION);
			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}
