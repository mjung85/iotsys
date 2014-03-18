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

import java.util.Collections;
import java.util.List;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.TelegramField;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util.DateCalculator;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util.Measure_Unit;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util.TelegramEncoding;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.util.Converter;

public class TelegramDataField extends TelegramField {
	
	private TelegramVariableDataRecord parent;
//	private static int REAL_FRACTION = 0x7FFFFF;		// 23-Bit (fraction of real)
//	private static int REAL_EXPONENT = 0x7F80000;		// 24-Bit to 31-Bit (exponent of real)
//	private static int REAL_SIGN = 0x80000000;			// 32-Bit (signum of real)
//	private static int SIGN = 0x01;						// mask for signum
	
	public TelegramDataField() {
		super();
	}
	
	public TelegramDataField(TelegramVariableDataRecord parent) {
		this.parent = parent;
	}
	
	public void parse() {
		TelegramEncoding enc = this.parent.getDif().getDataFieldEncoding();
		
		int length = this.parent.getDif().getDataFieldLength();
		Measure_Unit unit = this.parent.getVif().getmUnit();
		int multiplier = this.parent.getVif().getMultiplier();
		
		if(length != this.fieldParts.size()) {
			System.out.println("ERROR: wrong size");
			// TODO: throw exception
			return;
		}
		
		if(this.parseDate(unit) == true) {
			// value is parsed, we are done here
			return;
		}
		
		List<String> fieldsRev = this.fieldParts;
		Collections.reverse(fieldsRev);
		
		switch (enc) {
			case ENCODING_INTEGER:				
				this.parseInteger(Long.parseLong(Converter.convertListToString(fieldsRev), 16), multiplier);
				break;
			case ENCODING_BCD:
				this.parseBCD(Long.parseLong(Converter.convertListToString(fieldsRev)), multiplier);
				break;
			case ENCODING_REAL:
				this.parseReal(Float.parseFloat(Converter.convertListToString(fieldsRev)),multiplier);
				break;
			case ENCODING_VARIABLE_LENGTH:				
				parseASCII(Converter.convertStringListToByteArray(fieldsRev));				
				break;			
			case ENCODING_NULL:
				// TODO: exception
				break;
			default:
				// TODO: exception
				break;
		}
	}
	
	/*
	 * returns true if the value of this field is a date-type. Values
	 * are parsed as dates (if it is a valid date-type) in this function as well.
	 */
	public boolean parseDate(Measure_Unit dateType) {
		switch (dateType) {
			case DATE:
				// Type G: Day.Month.Year
				this.parsedValue = DateCalculator.getDate(this.fieldParts.get(0), this.fieldParts.get(1), false);
				
				break;
			case DATE_TIME:
				// Type F: Day.Month.Year Hour:Minute
				this.parsedValue = DateCalculator.getDateTime(this.fieldParts.get(0), this.fieldParts.get(1), this.fieldParts.get(2), this.fieldParts.get(3), false);
				break;
			case TIME:
				// Typ J: Hour:Minute:Second
				this.parsedValue = DateCalculator.getTimeWithSeconds(this.fieldParts.get(0), this.fieldParts.get(1), this.fieldParts.get(2));
				break;
			case DATE_TIME_S:
				// Typ I: Day.Month.Year Hour:Minute:Second
				this.parsedValue = DateCalculator.getDateTimeWithSeconds(this.fieldParts.get(0), this.fieldParts.get(1), this.fieldParts.get(2), this.fieldParts.get(3), this.fieldParts.get(4), false);
				break;
			default:
				return false;
		}
		return true;
	}
	
	private void parseBCD(long bcd, int multiplier) {
		this.parsedValue = String.valueOf(bcd*Math.pow(10, multiplier));
	}
	
	private void parseInteger(long intValue, int multiplier) {
		this.parsedValue = String.valueOf(intValue*Math.pow(10, multiplier));
	}
	
	private void parseReal(float realValue, int multiplier) {
		this.parsedValue = String.valueOf(realValue*Math.pow(10, multiplier));
	}
	
	private void parseASCII(byte[] ascii){
		this.parsedValue = new String(ascii);
	}
	
	public TelegramVariableDataRecord getParent() {
		return parent;
	}

	public void setParent(TelegramVariableDataRecord parent) {
		this.parent = parent;
	}
	
	public void debugOutput() {
		System.out.println("Field-Value (bytes): \t" + this.getFieldPartsAsString());
		System.out.println("Field-Value: \t\t" + this.parsedValue);
	}
}
