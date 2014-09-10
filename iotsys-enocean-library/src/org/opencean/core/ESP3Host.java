/*******************************************************************************
 * Copyright (c) 2014
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/
package org.opencean.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.opencean.core.address.EnoceanId;
import org.opencean.core.common.EEPId;
import org.opencean.core.common.ParameterValueChangeListener;
import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.persistent.models.Connector;

public class ESP3Host extends Connector implements Runnable{
    private static Logger logger = Logger.getLogger(ESP3Host.class.getName());
    private static byte[] DEFAULT_SENDERID = {(byte)0x00, (byte)0x25, (byte)0xA2, (byte)0xDC};
    
    private List<EnoceanReceiver> receivers = new ArrayList<EnoceanReceiver>();

    final ProtocolConnector connector;
    private EnoceanId senderId;
    private String serialPortName = null;

    private ParameterChangeNotifier parameterChangeNotifier;
    
    private final Hashtable<String, ArrayList<EnoceanWatchdog>> watchDogs = new Hashtable<String, ArrayList<EnoceanWatchdog>>();

    public ESP3Host(ProtocolConnector connector) {
        this.connector = connector;
        parameterChangeNotifier = new ParameterChangeNotifier();
        parameterChangeNotifier.addParameterValueChangeListener(new LoggingListener());
        receivers.add(parameterChangeNotifier);
        senderId = new EnoceanId(DEFAULT_SENDERID);
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
    		if (receivedPacket instanceof RadioPacket) {
    			EnoceanId idNr = ((RadioPacket)receivedPacket).getSenderId();   
    			logger.info("EnOcean telegram received from device with EnOceanID " + idNr.toString());

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
        logger.finest("Starting receiveRadio.. ");
        PacketStreamReader receiver = new PacketStreamReader(connector);
        while (true) {
            try {
                BasicPacket receivedPacket = receiver.read();
                if (receivedPacket != null) {
                    logger.finest(receivedPacket.toString());
                    notifyReceivers(receivedPacket);
                } else {
                    logger.info("Sync byte received, but header not valid.");
                }
            } catch (Exception e) {
            	logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
    
    public void setSerialPortName(String name){
    	this.serialPortName = name;
    }
    
    public String getSerialPortName(){
    	return serialPortName;
    }
    
    public void setSenderId(String idString){
    	if(idString!=null){
    		this.senderId = EnoceanId.fromString(idString);
    	}    	
    }
    
    public EnoceanId getSenderId(){
    	return this.senderId;
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
    @JsonIgnore
	public boolean isCoap() {
		return false;
	}

}
