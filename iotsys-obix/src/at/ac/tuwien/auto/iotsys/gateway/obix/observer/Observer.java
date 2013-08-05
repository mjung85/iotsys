package at.ac.tuwien.auto.iotsys.gateway.obix.observer;

public interface Observer {
	public void update(Object state);
	public void setSubject(Subject object);
	public Subject getSubject();
}
