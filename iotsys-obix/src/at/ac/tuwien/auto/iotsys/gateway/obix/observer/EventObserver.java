package at.ac.tuwien.auto.iotsys.gateway.obix.observer;

import java.util.List;

public interface EventObserver<ObjType> extends Observer {
	public List<ObjType> getEvents();
}
