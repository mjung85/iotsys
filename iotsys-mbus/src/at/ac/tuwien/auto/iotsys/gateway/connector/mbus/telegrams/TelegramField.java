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

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.util.Converter;

public class TelegramField {

	protected List<String> fieldParts;
	protected String parsedValue;
	
	public TelegramField() {
		
	}
	
	public void addFieldPart(String value) {
		if(this.fieldParts == null) {
			this.fieldParts = new ArrayList<String>();
		}
		this.fieldParts.add(value);
	}
	
	public void addFieldParts(String[] values) {
		for(int i = 0; i < values.length; i++) {
			this.addFieldPart(values[i]);
		}
	}
	
	public void clearTelegramPart() {
		this.fieldParts.clear();
	}
	
	public List<String> getFieldParts() {
		return fieldParts;
	}
	
	public String[] getFieldPartsAsArray() {
		return this.fieldParts.toArray(new String[this.fieldParts.size()]);
	}
	
	public String getFieldPartsAsString() {
		String retString = new String();
		for(int i = 0; i < fieldParts.size(); i++) {
			if(i+1 == fieldParts.size()) {
				retString += fieldParts.get(i);
			}
			else {
				retString += fieldParts.get(i) + " ";
			}
		}
		return retString;
	}
	
	public byte[] getFieldAsByteArray() {
		return Converter.convertStringListToByteArray(this.fieldParts);
	}
	
	public String getParsedValue() {
		return this.parsedValue;
	}
	
}
