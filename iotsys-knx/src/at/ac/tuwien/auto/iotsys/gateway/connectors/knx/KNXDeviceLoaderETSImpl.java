package at.ac.tuwien.auto.iotsys.gateway.connectors.knx;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import obix.Contract;
import obix.List;
import obix.Obj;
import obix.Ref;
import obix.Str;
import obix.Uri;

import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl.DPST_9_1_ImplKnx;

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
		p0944.setDisplayName("Uebungswand_pt1000_Dimmer");
		p0944.setDisplay("any description");
		p0944.setIs(new Contract("knx:network"));
		
		networks.add(p0944);
		
		objectBroker.addObj(networks, true);
		
		// skip standard element since for KNX mapping only knx is relevant.	
		Obj entities = new Obj();
		entities.setHref(new Uri(p0944.getHref().getPath() + "/" + "entities"));
		entities.setName("entities");
		p0944.add(entities);
		
		// Workaround, addObj registers currently only the obj and direct children 
		objectBroker.addObj(entities, false);	
		
		Obj overview = new Obj();
		overview.setHref(new Uri(entities.getHref().getPath() + "/" + "overview"));
		overview.setName("overview");
		entities.add(overview);
		
		// Workaround, addObj registers currently only the obj and direct children 
		objectBroker.addObj(overview, false);	
		
		Obj list = new Obj();
		list.setHref(new Uri(overview.getHref().getPath() + "/" + "list"));
		list.setName("list");
		// muss im oBIX Toolkit noch nachgezogen werden
//		list.setOf(new Contract("obix:Ref"));
			
		Ref ref = new Ref();
		ref.setName("temperature_sensor_N_258_02_1");
		ref.setHref(new Uri("/networks/P-0944/entities/temperature_sensor_N_258_02_01"));
		ref.setIs(new Contract("knx:entity"));
		
		list.add(ref);
		
		overview.add(list);
		
		// Workaround, addObj registers currently only the obj and direct children 
		objectBroker.addObj(list, false);	
		
		// now create entities
		
		Obj tempSensor258_02_1 = new Obj();
		tempSensor258_02_1.setName("temperature_sensor_N_258_02_1");
		tempSensor258_02_1.setHref(new Uri(entities.getHref().getPath() + "/" + "temperature_sensor_N_258_02_1"));
		tempSensor258_02_1.setDisplayName("Temperature Sensor N 258/02");
		tempSensor258_02_1.setIs(new Contract("knx:entity"));
		
		Str manfact = new Str();
		manfact.setName("manufacturer");
		manfact.set("Siemens");
		
		Str orderNumber = new Str();
		orderNumber.setName("orderNumber");
		orderNumber.set("5WG1 258-1AB02");
		
		tempSensor258_02_1.add(manfact);
		tempSensor258_02_1.add(orderNumber);
		
		List datapoints = new List();
		datapoints.setHref(new Uri(tempSensor258_02_1.getHref().getPath() + "/" + "datapoints"));
		datapoints.setName("datapoints");
		
		// here come the datapoint specific obix objects
		try {
			DPST_9_1_ImplKnx tempKanalA = new DPST_9_1_ImplKnx(knxConnector, new GroupAddress("1/1/0"));
			tempKanalA.setName("temperature_Kanal_A");
			tempKanalA.setHref(new Uri(datapoints.getHref().getPath() + "/" + "temperature_Kanal_A"));
			tempKanalA.setDisplayName("Temperatur, Kanal A");
			tempKanalA.setIs(new Contract("knx:DPST-9-1 knx:datapoint"));
			
			datapoints.add(tempKanalA);
			
			DPST_9_1_ImplKnx tempKanalB = new DPST_9_1_ImplKnx(knxConnector, new GroupAddress("1/1/1"));
			tempKanalB.setName("temperature_Kanal_B");
			tempKanalB.setHref(new Uri(datapoints.getHref().getPath() + "/" + "temperature_Kanal_B"));
			tempKanalB.setDisplayName("Temperatur, Kanal B");
			tempKanalB.setIs(new Contract("knx:DPST-9-1 knx:datapoint"));
			
			datapoints.add(tempKanalB);
			
			DPST_9_1_ImplKnx tempKanalC = new DPST_9_1_ImplKnx(knxConnector, new GroupAddress("1/1/2"));
			tempKanalC.setName("temperature_Kanal_C");
			tempKanalC.setHref(new Uri(datapoints.getHref().getPath() + "/" + "temperature_Kanal_C"));
			tempKanalC.setDisplayName("Temperatur, Kanal C");
			tempKanalC.setIs(new Contract("knx:DPST-9-1 knx:datapoint"));
			
			datapoints.add(tempKanalC);
			
			DPST_9_1_ImplKnx tempKanalD = new DPST_9_1_ImplKnx(knxConnector, new GroupAddress("1/1/3"));
			tempKanalD.setName("temperature_Kanal_D");
			tempKanalD.setHref(new Uri(datapoints.getHref().getPath() + "/" + "temperature_Kanal_D"));
			tempKanalD.setDisplayName("Temperatur, Kanal D");
			tempKanalD.setIs(new Contract("knx:DPST-9-1 knx:datapoint"));
			
			datapoints.add(tempKanalD);
			
		} catch (KNXFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		entities.add(tempSensor258_02_1);
		objectBroker.addObj(tempSensor258_02_1, false);
		
		tempSensor258_02_1.add(datapoints);
		
		objectBroker.addObj(datapoints, false);
		
				
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
