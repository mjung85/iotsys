package at.ac.tuwien.auto.iotsys.gateway.service;

import obix.Obj;
import at.ac.tuwien.auto.iotsys.gateway.obix.groupcomm.GroupCommImpl;
import at.ac.tuwien.auto.iotsys.gateway.service.impl.GroupCommServiceImpl;

public class GroupCommHelper {
	public static void enableGroupCommForObject(Obj obj){
		if(obj.isInt() || obj.isStr() || obj.isBool() || obj.isReal()){
			new GroupCommImpl(obj, GroupCommServiceImpl.getInstance());
		}
		
		for(Obj child : obj.list()){
			if (child.isHidden()) continue;
			enableGroupCommForObject(child);
		}
	}
}
