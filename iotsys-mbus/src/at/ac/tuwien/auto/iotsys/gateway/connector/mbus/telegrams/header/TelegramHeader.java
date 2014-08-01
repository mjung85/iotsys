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

package at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.header;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.TelegramField;

public class  TelegramHeader {
	
	private TelegramField startField;
	private TelegramField lField;
	private TelegramField cField;
	private TelegramField aField;
	private TelegramField crcField;
	private TelegramField stopField;
	public static int headerLength = 6;
	public static int headerLengthCRCStop = 8;
	
	public TelegramHeader() {
		
	}
	
	public void createTelegramHeader(String header) {
		this.createTelegramHeader(header.split(" "));
	}
	
	public void createTelegramHeader(String[] header) {
		
		this.setStartField(header[0]);
		this.setLField(header[1]);
		this.setLField(header[2]);		
		this.setStartField(header[3]);	
		this.setCField(header[4]);
		this.setAField(header[5]);	
		this.setCRCField(header[header.length-2]);
		this.setStopField(header[header.length-1]);
	}
	
	public TelegramField getstartField() {
		return startField;
	}

	public void setstartField(TelegramField startField) {
		this.startField = startField;
	}
	
	public TelegramField getlField() {
		return lField;
	}

	public void setlField(TelegramField lField) {
		this.lField = lField;
	}

	public TelegramField getcField() {
		return cField;
	}

	public void setcField(TelegramField cField) {
		this.cField = cField;
	}

	public TelegramField getaField() {
		return aField;
	}

	public void setaField(TelegramField aField) {
		this.aField = aField;
	}

	public TelegramField getCrcField() {
		return crcField;
	}

	public void setCrcField(TelegramField crcField) {
		this.crcField = crcField;
	}
	
	public TelegramField getstopField() {
		return stopField;
	}

	public void setstopField(TelegramField stopField) {
		this.stopField = stopField;
	}	

	public void setStartField(String startField) {
		this.startField = new TelegramField();
		this.startField.addFieldPart(startField);
	}
	
	public void setLField(String lField) {
		this.lField = new TelegramField();
		this.lField.addFieldPart(lField);
	}
	
	public void setCField(String cField) {
		this.cField = new TelegramField();
		this.cField.addFieldPart(cField);
	}
		
	public void setAField(String aField) {
		this.aField = new TelegramField();
		this.aField.addFieldPart(aField);
	}
	
	public void setCRCField(String crcField) {
		this.crcField = new TelegramField();
		this.crcField.addFieldPart(crcField);
	}
	
	public void setStopField(String stopField) {
		this.stopField = new TelegramField();
		this.stopField.addFieldPart(stopField);		
	}
	
	public void debugOutput() {
		if(this.startField != null) {
			System.out.println("Start Field: " + this.startField.getFieldParts().get(0));
		}
		if(this.lField != null) {
			System.out.println("Length of Telegram: " + this.lField.getFieldParts().get(0));
		}
		if(this.cField != null) {
			System.out.println("C-Field (mode): " + this.cField.getFieldParts().get(0));
		}
		if(this.aField != null) {
			System.out.println("A-Field (mode): " + this.aField.getFieldParts().get(0));			
		}			
		if(this.crcField != null) {
			System.out.println("CRC: " + this.crcField.getFieldParts().get(0));
		}
		if(this.stopField != null) {
			System.out.println("Stop Field: " + this.stopField.getFieldParts().get(0));
		}
	}

}
