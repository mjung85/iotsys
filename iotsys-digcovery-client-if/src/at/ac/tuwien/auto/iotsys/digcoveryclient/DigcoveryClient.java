package at.ac.tuwien.auto.iotsys.digcoveryclient;

public interface DigcoveryClient {
	public void registerDevice(String ep, String domain, String addr, String protocol,
			String port, String latitute, String longitute, String cityName);

	public void unregisterDevice(String ep, String domain);

	public void unregisterDomain(String domain);
}
