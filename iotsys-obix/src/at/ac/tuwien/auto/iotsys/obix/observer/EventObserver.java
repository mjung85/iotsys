package at.ac.tuwien.auto.iotsys.obix.observer;

import java.util.List;

public interface EventObserver<ObjType> extends Observer {
	public List<ObjType> pollChanges();
}
