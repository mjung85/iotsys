package at.ac.tuwien.auto.iotsys.obix.observer;

public interface Observer {
	public void update(Object state);
	public void setSubject(Subject object);
	public Subject getSubject();
}
