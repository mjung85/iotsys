package at.ac.tuwien.auto.iotsys.gateway.connectors.xbee;

import com.rapplogic.xbee.api.XBeeResponse;

public interface XBeeWatchdog {
	public void notifyWatchDog(XBeeResponse response);
}
