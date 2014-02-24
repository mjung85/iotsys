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

package at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.body.TelegramBody;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.header.TelegramHeader;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.util.Converter;

public class Telegram {

	private TelegramHeader header;
	private TelegramBody body;
	
	public Telegram() {
		this.header = new TelegramHeader();
		this.body = new TelegramBody();
	}
	
	public Telegram(TelegramHeader header, TelegramBody body) {
		super();
		this.header = header;
		this.body = body;
	}
	
	public void createTelegram(String telegram) {
		this.createTelegram(telegram.split(" "));
	}
	
	public void createTelegram(String[] telegram) {
		int headerLength = TelegramHeader.headerLength;	
		String[] firstHeader =  Arrays.copyOfRange(telegram, 0, headerLength);
		String[] resultHeader = Arrays.copyOf(firstHeader, headerLength+2);
		System.arraycopy(Arrays.copyOfRange(telegram, telegram.length - 2, telegram.length), 0, resultHeader, headerLength, 2);
		this.header.createTelegramHeader(Arrays.copyOfRange(resultHeader, 0, resultHeader.length));
		this.body.createTelegramBody(Arrays.copyOfRange(telegram, headerLength, telegram.length-2));
	}

	public TelegramHeader getHeader() {
		return header;
	}
	
	public void setHeader(TelegramHeader header) {
		this.header = header;
	}
	
	public TelegramBody getBody() {
		return body;
	}
	
	public void setBody(TelegramBody body) {
		this.body = body;
	}
	
	public void parse() {
		this.body.parse();
	}
	
	public void debugOutput() {
		if(this.header != null) {
			this.header.debugOutput();
		}
		if(this.body != null) {
			this.body.debugOutput();
		}
	}	
	
	public String getIdNr() {	
		return this.getBody().getIdNr();
	}
	
//	public String getPowerValue() {
//		return this.getBody().getPowerValue();
//	}
	
	public String getEnergyValue() {
		return this.getBody().getEnergyValue();
	}
	
	public String getVolumeValue() {
		return this.getBody().getVolumeValue();
	}
}
