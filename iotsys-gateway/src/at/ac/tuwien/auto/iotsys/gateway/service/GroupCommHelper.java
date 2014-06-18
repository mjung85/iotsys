package at.ac.tuwien.auto.iotsys.gateway.service;

import obix.Obj;
import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.IoTSySDevice;
import at.ac.tuwien.auto.iotsys.gateway.connectors.coap.CoapConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.groupcomm.CoapGroupCommImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.groupcomm.GroupCommImpl;
import at.ac.tuwien.auto.iotsys.gateway.service.impl.GroupCommServiceImpl;

public class GroupCommHelper {
	public static void enableGroupCommForObject(Obj obj){
		enableGroupCommForObject(obj, null, null);
	}
	
	public static void enableGroupCommForObject(Obj obj, Connector connector, String adr){
		if(obj.isInt() || obj.isStr() || obj.isBool() || obj.isReal()){
			if (connector instanceof CoapConnector){
				if(obj.getParent() instanceof IoTSySDevice){
					if(((IoTSySDevice) obj.getParent()).forwardGroupAddress()){
						new CoapGroupCommImpl(obj, GroupCommServiceImpl.getInstance());
					}
					else{
						new GroupCommImpl(obj, GroupCommServiceImpl.getInstance());
					}
				}
			} else {
				new GroupCommImpl(obj, GroupCommServiceImpl.getInstance());
			}
		}
		
		for(Obj child : obj.list()){
			if (child.isHidden()) continue;
			enableGroupCommForObject(child, connector, adr);
		}
	}
}
