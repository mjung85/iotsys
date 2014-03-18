package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean;

import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3Frame;

public interface EnoceanWatchdog {
	public void notifyWatchDog(ESP3Frame payload);
}
