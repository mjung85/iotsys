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

package at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.body;

import java.util.Arrays;

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.TelegramField;

public class TelegramBodyHeader {

	private TelegramField ciField;
	private TelegramField accNrField;
	private TelegramField statusField;
	private TelegramField sigField;
	
	public TelegramBodyHeader() {
		
	}
	
	public void createTelegramBodyHeader(String[] bodyHeader) {
		// TODO: check CI-Field for correct Header length
		this.setCiField(bodyHeader[0]);
		this.setAccNrField(bodyHeader[1]);
		this.setStatusField(bodyHeader[2]);
		this.setSigField(Arrays.copyOfRange(bodyHeader, 3, 5));
	}
	
	public void setCiField(String ciField) {
		this.ciField = new TelegramField();
		this.ciField.addFieldPart(ciField);
	}
	
	public void setAccNrField(String accNrField) {
		this.accNrField = new TelegramField();
		this.accNrField.addFieldPart(accNrField);
	}
	
	public void setStatusField(String statusField) {
		this.statusField = new TelegramField();
		this.statusField.addFieldPart(statusField);
	}
	
	public void setSigField(String[] sigField) {
		this.sigField = new TelegramField();
		this.sigField.addFieldParts(sigField);
	}
	
	public byte[] getAESCBCInitVectorPart() {
		byte[] aesCbcInitVectorPart = new byte[8];
		byte[] accNrFieldByteArr = this.accNrField.getFieldAsByteArray();
		System.arraycopy(accNrFieldByteArr, 0, aesCbcInitVectorPart, 0, accNrFieldByteArr.length);
		System.arraycopy(accNrFieldByteArr, 0, aesCbcInitVectorPart, accNrFieldByteArr.length, accNrFieldByteArr.length);
		System.arraycopy(accNrFieldByteArr, 0, aesCbcInitVectorPart, accNrFieldByteArr.length + 1, accNrFieldByteArr.length);
		System.arraycopy(accNrFieldByteArr, 0, aesCbcInitVectorPart, accNrFieldByteArr.length + 2, accNrFieldByteArr.length);
		System.arraycopy(accNrFieldByteArr, 0, aesCbcInitVectorPart, accNrFieldByteArr.length + 3, accNrFieldByteArr.length);
		System.arraycopy(accNrFieldByteArr, 0, aesCbcInitVectorPart, accNrFieldByteArr.length + 4, accNrFieldByteArr.length);
		System.arraycopy(accNrFieldByteArr, 0, aesCbcInitVectorPart, accNrFieldByteArr.length + 5, accNrFieldByteArr.length);
		System.arraycopy(accNrFieldByteArr, 0, aesCbcInitVectorPart, accNrFieldByteArr.length + 6, accNrFieldByteArr.length);
		return aesCbcInitVectorPart;
	}
	
	public void debugOutput() {
		if(this.ciField != null) {
			System.out.println("Type of TelegramBodyHeader: " + this.ciField.getFieldParts().get(0));
		}
		if(this.accNrField != null) {
			System.out.println("AccessNumber: " + this.accNrField.getFieldParts().get(0));
		}
		if(this.statusField != null) {
			// TODO: parse Status-Field
			System.out.println("StatusField: " + this.statusField.getFieldParts().get(0));
		}
		if(this.sigField != null) {
			// TODO: parse Sig-Field
			System.out.println("Sig-Field1: " + this.sigField.getFieldParts().get(0));
			System.out.println("Sig-Field2: " + this.sigField.getFieldParts().get(1));
		}
	}
}
