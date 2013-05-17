package at.ac.tuwien.auto.iotsys.gateway.service;

import java.net.Inet6Address;

import obix.Obj;

public interface GroupCommService {
	
	public void handleRequest(Inet6Address group, Obj payload);
	
	public void registerObject(Inet6Address group, Obj obj);
	
	public void unregisterObject(Inet6Address group, Obj obj);

	public void sendUpdate(Inet6Address group, Object state);

}
