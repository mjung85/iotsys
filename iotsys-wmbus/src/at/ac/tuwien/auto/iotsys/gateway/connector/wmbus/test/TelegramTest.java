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

package at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.test;

import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.TelegramManagerInterface;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.WMBusConnector;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.SimpleTelegram;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.Telegram;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.util.Measure_Unit;

public class TelegramTest {
	private static final Logger log = Logger.getLogger(TelegramTest.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String telegramString = "";
		TelegramManagerInterface manager = new WMBusConnector();
		
		String aesKey =  "66 77 66 77 66 77 66 77 66 77 66 77 66 77 66 77";
	
		telegramString = "3E 44 2D 4C 74 44 00 15 1E 02 7A 04 00 30 85 16 D9 B7 3C F2 A6 6B 34 2A 49 E3 7B 80 5A A7 86 D3 E8 9F 3E BD CD E2 0D 7E DC 79 B8 CD 45 37 CB 95 50 1F 55 4B EE 52 F6 0A F5 F1 EA D3 82 54 E3";
		
		Telegram telegram = new Telegram();
		telegram.createTelegram(telegramString, false);
		if(telegram.decryptTelegram(aesKey) == false) {
			log.severe("Decryption of AES telegram not possible.");
			return;
		}
		telegram.parse();
		
		String serialNr = telegram.getSerialNr();		
		log.fine("Serial number: " + serialNr);
		
		SimpleTelegram simpleTelegram = null;
		
		simpleTelegram = new SimpleTelegram();
		
		simpleTelegram.setEnergy(new Double(telegram.getEnergyValue()));
		simpleTelegram.setEnergyUnit(Measure_Unit.WH);
		log.fine("Energy is: " + telegram.getEnergyValue());
		simpleTelegram.setPower(Double.parseDouble(telegram.getPowerValue()));
		simpleTelegram.setEnergyUnit(Measure_Unit.W);
		log.fine("Power is: " + telegram.getPowerValue());
				
		telegram.debugOutput();
	}

}
