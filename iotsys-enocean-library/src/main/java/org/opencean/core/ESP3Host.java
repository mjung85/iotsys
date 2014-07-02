package org.opencean.core;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.opencean.core.address.EnoceanId;
import org.opencean.core.common.EEPId;
import org.opencean.core.common.ParameterValueChangeListener;
import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket;
import org.opencean.core.packets.RadioPacketRPS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.auto.iotsys.commons.Connector;

public class ESP3Host extends Thread implements Connector{
    private static Logger logger = LoggerFactory.getLogger(ESP3Host.class);

    private List<EnoceanReceiver> receivers = new ArrayList<EnoceanReceiver>();

    final ProtocolConnector connector;
    private String serialPortName = null;

    private ParameterChangeNotifier parameterChangeNotifier;
    
    private final Hashtable<String, ArrayList<EnoceanWatchdog>> watchDogs = new Hashtable<String, ArrayList<EnoceanWatchdog>>();

    public ESP3Host(ProtocolConnector connector) {
        this.connector = connector;
        parameterChangeNotifier = new ParameterChangeNotifier();
        parameterChangeNotifier.addParameterValueChangeListener(new LoggingListener());
        receivers.add(parameterChangeNotifier);
    }    
    
    public void addWatchDog(EnoceanId id, EnoceanWatchdog enoceanWatchdog) {
		logger.info("Adding watchdog for EnOceanID: " + id.toString());
		synchronized(watchDogs){
			if (!watchDogs.containsKey(id.toString())) {
				watchDogs.put(id.toString(), new ArrayList<EnoceanWatchdog>());
			}
			watchDogs.get(id.toString()).add(enoceanWatchdog);
		}
	}

    public void addDeviceProfile(EnoceanId id, EEPId epp) {
        parameterChangeNotifier.addDeviceProfile(id, epp);
    }

    public void addParameterChangeListener(ParameterValueChangeListener listener) {
        parameterChangeNotifier.addParameterValueChangeListener(listener);
    }

    public void addListener(EnoceanReceiver receiver) {
        this.receivers.add(receiver);
    }

    public void removeListener(EnoceanReceiver receiver) {
        receivers.remove(receiver);
    }

    public void sendRadio(BasicPacket packet) {
        connector.write(packet.toBytes());
    }

    private void notifyReceivers(BasicPacket receivedPacket) {
    	for (EnoceanReceiver receiver : this.receivers) {
    		receiver.receivePacket(receivedPacket);
    		
    		// check if received packet is a RadioPacket
    		if (receivedPacket instanceof RadioPacketRPS) {
    			EnoceanId idNr = ((RadioPacket)receivedPacket).getSenderId();    			

    			synchronized(watchDogs){
    				if(watchDogs.containsKey(idNr.toString())){
    					// notify listeners
    					ArrayList<EnoceanWatchdog> arrayList = watchDogs.get(idNr.toString());
    					logger.info("Notifying watchdog for telegram from device with EnOceanID " + idNr.toString());
    					for(EnoceanWatchdog watchDog : arrayList){    					
    						watchDog.notifyWatchDog(receivedPacket);
    					}
    				}
    			}
    		}
    	}
    }

    public void sendRadioSubTel() {

    }

    public void receiveRadioSubTel() {

    }

    @Override
    public void run() {
        logger.info("starting receiveRadio.. ");
        PacketStreamReader receiver = new PacketStreamReader(connector);
        while (true) {
            try {
                BasicPacket receivedPacket = receiver.read();
                if (receivedPacket != null) {
                    logger.info(receivedPacket.toString());
                    notifyReceivers(receivedPacket);
                } else {
                    logger.debug("Sync byte received, but header not valid.");
                }
            } catch (Exception e) {
                logger.error("Error", e);
            }
        }
    }
    
    public void setSerialPortName(String name){
    	this.serialPortName = name;
    }
    
    public String getSerialPortName(){
    	return serialPortName;
    }
    
    @Override
	public void connect() throws Exception {
    	if(serialPortName!=null){
    		connector.connect(serialPortName);
    	} else {
    		throw new RuntimeException("Comm port not specified");
    	}
	}
    
    
    @Override
	public void disconnect() throws Exception {
		connector.disconnect();
	}
    
    @Override
	public boolean isCoap() {
		return false;
	}

}
