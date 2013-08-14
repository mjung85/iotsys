package at.ac.tuwien.auto.iotsys.gateway.service;

import at.ac.tuwien.auto.iotsys.gateway.obix.groupcomm.GroupComm;
import at.ac.tuwien.auto.iotsys.gateway.obix.groupcomm.GroupCommImpl;
import at.ac.tuwien.auto.iotsys.gateway.service.impl.GroupCommServiceImpl;
import obix.Obj;
import obix.contracts.History;

public class GroupCommHelper {
	public static void enableGroupCommForObject(Obj obj){
		if(obj.isInt() || obj.isStr() || obj.isBool() || obj.isReal()){
			GroupCommImpl impl = new GroupCommImpl(obj, GroupCommServiceImpl.getInstance());
			impl.initialize();
		}
		
		for(Obj child : obj.list()){
			if (child.isHidden()) continue;
			
			if (!(child instanceof History || child instanceof GroupComm))
				enableGroupCommForObject(child);
		}
	}
}
