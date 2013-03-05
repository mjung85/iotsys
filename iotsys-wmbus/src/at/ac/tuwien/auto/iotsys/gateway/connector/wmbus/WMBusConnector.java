/*******************************************************************************
 * Copyright (c) 2013
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

package at.ac.tuwien.auto.iotsys.gateway.connector.wmbus;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.WMBusWatchDog;

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.TelegramManagerInterface;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.SimpleTelegram;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.util.Measure_Unit;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.test.SmartMeterTestTelegrams;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.reader.ComPortReader;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.Telegram;

public class WMBusConnector implements TelegramManagerInterface, Connector{
	private static final Logger log = Logger.getLogger(WMBusConnector.class.getName());
	
	private final Hashtable<String, ArrayList<WMBusWatchDog>> watchDogs = new Hashtable<String, ArrayList<WMBusWatchDog>>();	

	private ArrayList<SimpleTelegram> simpleTestTelegrams;
	
	private ComPortReader comPortReader; 
	
	private String aesKey;
	
	private String serialPort;
	
	public WMBusConnector(String serialPort ){
		this.serialPort = serialPort;		
	}		
	
	public void addWatchDog(String serialNr, WMBusWatchDog wmbusWatchDog) {
		log.finest("Adding watchdog for serial number: " + serialNr);
		synchronized(watchDogs){
			if (!watchDogs.containsKey(serialNr)) {
				watchDogs.put(serialNr, new ArrayList<WMBusWatchDog>());
			}
			watchDogs.get(serialNr).add(wmbusWatchDog);
		}
	}
			
	
	/* (non-Javadoc)
	 * @see at.ac.auto.smartmeter.TelegramManagerInterface#addTelegram(java.lang.String)
	 */
	@Override
	public void addTelegram(String telegramString) {
		this.createTelegram(telegramString);
	}
	
	private Telegram createTelegram(String telegramString) {
		return this.createTelegram(telegramString, 0);
	}
	
	private Telegram createTelegram(String telegramString, long timeStamp) {
		log.fine("Received WMBus telegram.");
		Telegram telegram = new Telegram();
		telegram.createTelegram(telegramString, false);
		if(telegram.decryptTelegram(aesKey) == false) {
			log.severe("Decryption of AES telegram not possible.");
			return null;
		}
		telegram.parse();
		
		String serialNr = telegram.getSerialNr();		
		log.fine("Serial number: " + serialNr);
		
		SimpleTelegram simpleTelegram = null;
		if(timeStamp == 0) {
			simpleTelegram = new SimpleTelegram();
		}
		else {
			simpleTelegram = new SimpleTelegram(timeStamp);
		}
		simpleTelegram.setEnergy(new Double(telegram.getEnergyValue()));
		simpleTelegram.setEnergyUnit(Measure_Unit.WH);
		log.fine("Energy is: " + telegram.getEnergyValue());
		simpleTelegram.setPower(Double.parseDouble(telegram.getPowerValue()));
		simpleTelegram.setEnergyUnit(Measure_Unit.W);
		log.fine("Power is: " + telegram.getPowerValue());
		
		synchronized(watchDogs){
			if(watchDogs.containsKey(serialNr)){
				// notfiy listeners
				ArrayList<WMBusWatchDog> arrayList = watchDogs.get(serialNr);
				log.finest("Notifying watchdog for telegram from smart meter with serial number " + serialNr);
				for(WMBusWatchDog watchDog : arrayList){
					watchDog.notifyWatchDog(simpleTelegram.getPower(), simpleTelegram.getEnergy() / 1000);
					watchDog.notifyWatchDog(simpleTelegram);
				}
			}
		}
				
		//telegram.debugOutput();
		
		return telegram;
	}
	
	/* (non-Javadoc)
	 * @see at.ac.auto.smartmeter.TelegramManagerInterface#addTelegram(at.ac.auto.smartmeter.telegrams.Telegram)
	 */
	@Override
	public void addTelegram(Telegram telegram) {
	
	}
	
	public void loadTelegrams() {
		simpleTestTelegrams = new ArrayList<SimpleTelegram>();
		for(int i = 0; i < SmartMeterTestTelegrams.telegrams.length; i++) {
			long curTime = new java.util.Date().getTime();
			// just for some test values set the timestamp in 10 minute intervalls
			curTime = curTime + 600000*i;
			
			Telegram tel = this.createTelegram(SmartMeterTestTelegrams.telegrams[i], curTime);
			
			SimpleTelegram simpleTelegram = new SimpleTelegram(curTime);
			simpleTelegram.setEnergy(new Double(tel.getBody().getBodyPayload().getRecords().get(3).getDataField().getParsedValue()));
			simpleTelegram.setEnergyUnit(Measure_Unit.WH);
			simpleTelegram.setPower(Double.parseDouble(tel.getBody().getBodyPayload().getRecords().get(5).getDataField().getParsedValue()));
			simpleTelegram.setEnergyUnit(Measure_Unit.W);
			simpleTestTelegrams.add(simpleTelegram);
		}
	}

	public void registerAESKey(String serialNr, String aesKey) {
		this.aesKey = aesKey;		
	}	
	
	public void connect(){
		CommPortIdentifier portId = ComPortReader.lookupPorts(serialPort);
		comPortReader =  new ComPortReader(portId, this);
	}
	
	public void disconnect(){
		comPortReader.closePort();
	}
}
