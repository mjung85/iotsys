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

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.TelegramField;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util.TelegramEncoding;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util.TelegramFunctionType;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.util.Converter;

public class DIFTelegramField extends TelegramField {
	
	private static int EXTENSION_BIT = 0x80; // 1000 0000
	private static int LSB_SAVE_NUMBER_BIT = 0x40; // 0100 0000
	private static int FUNCTION_MASK = 0x30; // 0011 0000
	private static int DATA_FIELD_MASK = 0x0F; // 0000 1111
	
	private static final int FILL_BYTES_MASK = 0x2F;
	
	private boolean extensionBit = false;
	private boolean saveNumberBit = false;
	private boolean endOfUserDataBit = false;
	/*
	 * functionType of the Telegram
	 * 00b (0) -> Instantaneous value
	 * 01b (1) -> Maximum value
	 * 10b (2) -> Minimum value
	 * 11b (3) -> value during error state
	 */
	private TelegramFunctionType functionType = TelegramFunctionType.INSTANTANEOUS_VALUE;

	/*
	 * encoding and length of Telegram
	 * 0000 (0-Bit) -> No data
	 * 0001 (8-Bit) -> Integer/Binary
	 * 0010 (16-Bit)-> Integer/Binary 
	 * 0011 (24-Bit)-> Integer/Binary
	 * 0100 (32-Bit)-> Integer/Binary
	 * 0101 (32-Bit)-> Real
	 * 0110 (48-Bit)-> Integer/Binary 
	 * 0111 (64-Bit)-> Integer/Binary 
	 * 1000 (0-Bit) -> Selection for Readout
	 * 1001 (8-Bit) -> 2 digit BCD
	 * 1010 (16-Bit)-> 4 digit BCD
	 * 1011 (24-Bit)-> 6 digit BCD
	 * 1100 (32-Bit)-> 8 digit BCD
	 * 1101 (32-Bit)-> variable length
	 * 1110 (48-Bit)-> 12 digit BCD
	 * 1111 (64-Bit)-> Special Functions
	 */
	private int dataFieldLengthAndEncoding = 0;

	/*
	 * length of the data field in bytes
	 * => value of 4 means 4 byte (32-Bit) length of data field
	 */
	private int dataFieldLength = 0;
	private TelegramEncoding dataFieldEncoding = TelegramEncoding.ENCODING_NULL;
	
	public DIFTelegramField() {
		
	}
	
	public void parse() {
		String difField = this.fieldParts.get(0);
		int iDifField = Converter.hexToInt(difField);
		
		// there are some special functions where the other fields
		// don't need to be interpreted (for exampl 2F as a fill byte)
		switch (iDifField) {
			case 0x0F:
				// MANUFACTURER Start of manufacturer specific data structures to end of user data
				endOfUserDataBit = true;				
				return;
			case 0x1F:
				// Same meaning as DIF = 0Fh + More records follow in next telegram
				endOfUserDataBit = true;					
				return;
			case DIFTelegramField.FILL_BYTES_MASK:
				this.functionType = TelegramFunctionType.SPECIAL_FUNCTION_FILL_BYTE;
				this.dataFieldLength = 0;
				return;
			case 0x3F:
				return;
			case 0x4F:
				return;
			case 0x5F:
				return;
			case 0x6F:
				return;
			case 0x7F:
				return;
		}
		
		if((iDifField & DIFTelegramField.EXTENSION_BIT) == DIFTelegramField.EXTENSION_BIT) {
			this.extensionBit = true;
		}
		if((iDifField & DIFTelegramField.LSB_SAVE_NUMBER_BIT) == DIFTelegramField.LSB_SAVE_NUMBER_BIT) {
			this.saveNumberBit = true;
		}
		
		// first extract only bit 5 and 6 of the telegram field
		// and afterwards move it to the right four bits so that we get 
		// an integer value (this integer value is then translated to our enum value)
		this.functionType = TelegramFunctionType.values()[(iDifField & DIFTelegramField.FUNCTION_MASK) >> 4];

		
		this.parseEncodingAndLength(iDifField);
	}
	
	private void parseEncodingAndLength(int iDifField) {
		this.dataFieldLengthAndEncoding =  (iDifField & DIFTelegramField.DATA_FIELD_MASK);
		switch (this.dataFieldLengthAndEncoding) {
			case 0:
				this.dataFieldLength = 0;
				this.dataFieldEncoding = TelegramEncoding.ENCODING_NULL;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				this.dataFieldLength = this.dataFieldLengthAndEncoding;
				this.dataFieldEncoding = TelegramEncoding.ENCODING_INTEGER;
				break;
			case 5:
				this.dataFieldLength = 4;
				this.dataFieldEncoding = TelegramEncoding.ENCODING_REAL;
				break;
			case 6:
				this.dataFieldLength = 6;
				this.dataFieldEncoding = TelegramEncoding.ENCODING_INTEGER;
				break;
			case 7:
				this.dataFieldLength = 8;
				this.dataFieldEncoding = TelegramEncoding.ENCODING_INTEGER;
				break;
			case 8:
				this.dataFieldLength = 0;
				this.dataFieldEncoding = TelegramEncoding.ENCODING_NULL;
				break;
			case 9:
			case 10:
			case 11:
			case 12:
				this.dataFieldLength = this.dataFieldLengthAndEncoding - 8;
				this.dataFieldEncoding = TelegramEncoding.ENCODING_BCD;
				break;
			case 13:
				this.dataFieldLength = 6;
				this.dataFieldEncoding = TelegramEncoding.ENCODING_VARIABLE_LENGTH;
				break;
			case 14:
				this.dataFieldLength = 6;
				this.dataFieldEncoding = TelegramEncoding.ENCODING_BCD;
				break;
			case 15:
				//this.dataFieldLength = 8;
				//this.dataFieldEncoding = TelegramEncoding.ENCODING_NULL;
				// we have already processed these values earlier
				break;
		}
	}
	
	public boolean isFillByte() {
		return this.functionType == TelegramFunctionType.SPECIAL_FUNCTION_FILL_BYTE;
	}
	
	public boolean isExtensionBit() {
		return extensionBit;
	}

	public void setExtensionBit(boolean extensionBit) {
		this.extensionBit = extensionBit;
	}
	
	public boolean isEndOfUserData() {
		return endOfUserDataBit;
	}

	public void setEndOfUserData(boolean endOfUserDataBit) {
		this.endOfUserDataBit = endOfUserDataBit;
	}
	
	public TelegramFunctionType getFunctionType() {
		return functionType;
	}

	public void setFunctionType(TelegramFunctionType functionType) {
		this.functionType = functionType;
	}
	
	public int getDataFieldLengthAndEncoding() {
		return dataFieldLengthAndEncoding;
	}

	public void setDataFieldLengthAndEncoding(int dataFieldLengthAndEncoding) {
		this.dataFieldLengthAndEncoding = dataFieldLengthAndEncoding;
	}
	
	public int getDataFieldLength() {
		return dataFieldLength;
	}

	public void setDataFieldLength(int dataFieldLength) {
		this.dataFieldLength = dataFieldLength;
	}
	
	public TelegramEncoding getDataFieldEncoding() {
		return dataFieldEncoding;
	}

	public void setDataFieldEncoding(TelegramEncoding dataFieldEncoding) {
		this.dataFieldEncoding = dataFieldEncoding;
	}

	public void debugOutput() {
		System.out.println("DIF-Field: ");
		System.out.println("\tExtension-Bit: \t\t" + this.extensionBit);
		System.out.println("\tSaveNumber-Bit: \t" + this.saveNumberBit);
		System.out.println("\tEndOfUserData-Bit: \t" + this.endOfUserDataBit);
		System.out.println("\tFunction-Type: \t\t" + this.functionType);
		System.out.println("\tDataField: \t\t" + Integer.toBinaryString(this.dataFieldLengthAndEncoding));
		System.out.println("\tDataFieldEncoding: \t" + this.dataFieldEncoding);
		System.out.println("\tDataFieldLength: \t" + this.dataFieldLength);
	}

}
