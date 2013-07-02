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

package at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams;

import java.util.Arrays;

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.AESEncrypt;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.body.TelegramBody;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.header.TelegramHeader;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.util.Converter;

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
	
	public void createTelegram(String telegram, boolean crc) {
		this.createTelegram(telegram.split(" "), crc);
	}
	
	public void createTelegram(String[] telegram, boolean crc) {
		int headerLength = TelegramHeader.headerLengthCRC;
		if(crc == false) {
			headerLength = TelegramHeader.headerLengthNoCRC;
		}
		this.header.createTelegramHeader(Arrays.copyOfRange(telegram, 0, headerLength + 1));
		this.body.createTelegramBody(Arrays.copyOfRange(telegram, headerLength + 1, telegram.length));
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
	
	public boolean decryptTelegram(String aesKey) {
		if(aesKey == null){
			return false;
		}
		byte[] keyArr = Converter.convertStringArrToByteArray(aesKey.split(" "));
		byte[] initCTRVArr = this.getAESCBCInitVector();
		byte[] payloadArr = Converter.convertStringListToByteArray(this.getBody().getBodyPayload().getPayloadAsList());
		
		try {
			byte[] result = AESEncrypt.decrypt(payloadArr, keyArr, initCTRVArr);
			this.getBody().getBodyPayload().setDecryptedTelegramBodyPayload(Converter.convertByteArrayToString(result).split(" "));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void parse() {
		this.body.parse();
	}
	
	public byte[] getAESCBCInitVector() {
		return this.generateAESCBCInitVector();
	}
	
	private byte[] generateAESCBCInitVector() {
		byte[] aesCbcInitVector = new byte[16];
		byte[] aesCbcInitVectorHeaderPart = this.header.getAESCBCInitVectorPart();
		byte[] aesCbcInitVectorBodyPart = this.body.getBodyHeader().getAESCBCInitVectorPart();
		System.arraycopy(aesCbcInitVectorHeaderPart, 0, aesCbcInitVector, 0, aesCbcInitVectorHeaderPart.length);
		System.arraycopy(aesCbcInitVectorBodyPart, 0, aesCbcInitVector, aesCbcInitVectorHeaderPart.length, aesCbcInitVectorBodyPart.length);
		
		return aesCbcInitVector;
	}
	
	public void debugOutput() {
		if(this.header != null) {
			this.header.debugOutput();
		}
		if(this.body != null) {
			this.body.debugOutput();
		}
	}
	
	public String getSerialNr() {
		return this.getHeader().getSerialNr();
	}
	
	public String getPowerValue() {
		return this.getBody().getPowerValue();
	}
	
	public String getEnergyValue() {
		return this.getBody().getEnergyValue();
	}
	
}
