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

package at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.body;

import java.util.Arrays;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.body.TelegramBodyHeader;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.body.TelegramBodyPayload;


public class  TelegramBody {

	private TelegramBodyHeader bodyHeader;
	private TelegramBodyPayload bodyPayload;
	public static int bodyHeaderLength = 13;

	public TelegramBody() {
		this.bodyHeader = new TelegramBodyHeader();
		this.bodyPayload = new TelegramBodyPayload();
	}
	
	public TelegramBodyHeader getBodyHeader() {
		return bodyHeader;
	}
	
	public void setBodyHeader(TelegramBodyHeader bodyHeader) {
		this.bodyHeader = bodyHeader;
	}
	
	public TelegramBodyPayload getBodyPayload() {
		return bodyPayload;
	}

	public void setBodyPayload(TelegramBodyPayload bodyPayload) {
		this.bodyPayload = bodyPayload;
	}
	
	public void createTelegramBody(String[] body) {
		// first extract header
		this.bodyHeader.createTelegramBodyHeader(Arrays.copyOfRange(body, 0, bodyHeaderLength));
		this.bodyPayload.createTelegramBodyPayload(Arrays.copyOfRange(body, bodyHeaderLength, body.length));
	}
	
	public void parse() {
		this.bodyPayload.parse();
	}
	
	public String getIdNr() {
		return this.bodyHeader.getIdNr();
	}
	
//	public String getPowerValue() {
//		return this.getBodyPayload().getRecords().get(1).getDataField().getParsedValue(); // TODO remove this
//	}	
	
	public String getEnergyValue() {
		return this.getBodyPayload().getRecords().get(0).getDataField().getParsedValue();
	}
	
	public String getVolumeValue() {
		return this.getBodyPayload().getRecords().get(1).getDataField().getParsedValue();
	}
	
	public void debugOutput() {
		if(this.bodyHeader != null) {
			this.bodyHeader.debugOutput();
		}
		if(this.bodyPayload != null) {
			this.bodyPayload.debugOutput();
		}
	}
}
