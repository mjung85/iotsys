package at.ac.tuwien.auto.iotsys.commons;

public interface Named {

	public void startNamedService();
	public void stopNamedService();
	public boolean isStart();
}
