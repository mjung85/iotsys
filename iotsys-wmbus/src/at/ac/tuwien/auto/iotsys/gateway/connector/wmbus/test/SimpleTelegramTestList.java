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

import java.util.ArrayList;

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.SimpleTelegram;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.Telegram;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.util.Measure_Unit;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.test.SmartMeterTestTelegrams;

public class SimpleTelegramTestList {
	
	private ArrayList<SimpleTelegram> telegrams;
	
	public SimpleTelegramTestList() {
		
	}
//	
//	private SimpleTelegram createTelegram(String telegramString) {
//		Telegram telegram = new Telegram();
//		telegram.createTelegram(telegramString, false);
//		telegram.decryptTelegram();
//		telegram.parse();
//		
//		//System.out.println(telegram.getHeader().getaField().getParsedValue());
//		String serialNr = telegram.getHeader().getaField().getFieldParts().get(3) + telegram.getHeader().getaField().getFieldParts().get(2) +
//				telegram.getHeader().getaField().getFieldParts().get(1) + telegram.getHeader().getaField().getFieldParts().get(0);
//		
//		System.out.println("WMBus Telegram received #################################################");
//		System.out.println("serialNr: " + serialNr);
//						
//		System.out.println("Load W: " + telegram.getBody().getBodyPayload().getRecords().get(5).getDataField().getParsedValue());
//		System.out.println("Energy Wh: " + telegram.getBody().getBodyPayload().getRecords().get(3).getDataField().getParsedValue());
//		
//		SimpleTelegram simpleTelegram = new SimpleTelegram();
//		simpleTelegram.setEnergy(new Double(telegram.getBody().getBodyPayload().getRecords().get(3).getDataField().getParsedValue()));
//		simpleTelegram.setEnergyUnit(Measure_Unit.WH);
//		simpleTelegram.setPower(Double.parseDouble(telegram.getBody().getBodyPayload().getRecords().get(5).getDataField().getParsedValue()));
//		simpleTelegram.setEnergyUnit(Measure_Unit.W);
//		
//		return simpleTelegram;
//	}
//	
//	public void loadTelegrams() {
//		this.telegrams = new ArrayList<SimpleTelegram>();
//		for(int i = 0; i < SmartMeterTestTelegrams.telegrams.length; i++) {
//			telegrams.add(this.createTelegram(SmartMeterTestTelegrams.telegrams[i]));
//		}
//	}
//	
//	public ArrayList<SimpleTelegram> getTestSimpleTelegrams() {
//		return this.telegrams;
//	}
}

