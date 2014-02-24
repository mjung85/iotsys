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
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util.Measure_Unit;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util.TelegramEncoding;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util.VIF_Extension_FD_Mask;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.util.VIF_Unit_Multiplier_Masks;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.util.Converter;

public class VIFETelegramField extends TelegramField {
	
	private static int EXTENSION_BIT_MASK = 0x80; 		// 1000 0000
	private static int LAST_TWO_BIT_OR_MASK = 0x03; 	// 0000 0011
	private static int LAST_FOUR_BIT_OR_MASK = 0x0F; 	// 0000 1111
	private static int UNIT_MULTIPLIER_MASK = 0x7F; 	// 0111 1111
	
	private boolean extensionBit = false;
	private boolean lvarBit = false;
	private Measure_Unit mUnit = Measure_Unit.NONE;
	private VIF_Extension_FD_Mask type;
	private int multiplier = 0;

	private TelegramVariableDataRecord parent;
	
	public void parse() {
		String vifeField = this.fieldParts.get(0);
		int iViefField = Converter.hexToInt(vifeField);
		
		if((iViefField & VIFETelegramField.EXTENSION_BIT_MASK) == VIFETelegramField.EXTENSION_BIT_MASK) {
			this.extensionBit = true;
		}		
		
		// first get rid of the first (extension) bit
		int iVifFieldNoExt = (iViefField & (VIFETelegramField.UNIT_MULTIPLIER_MASK));

		// first check against complete (no wildcards) bit masks
		// can't check with switch case because we need a constant value there
		if(iVifFieldNoExt == VIF_Extension_FD_Mask.ACCESS_NUMBER.getValue()) {
			this.type = VIF_Extension_FD_Mask.ACCESS_NUMBER;			
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.MEDIUM.getValue()) {
			this.type = VIF_Extension_FD_Mask.MEDIUM;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.MANUFACTURER.getValue()) {
			this.type = VIF_Extension_FD_Mask.MANUFACTURER;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.PARAMETER_SET_ID.getValue()) {
			this.type = VIF_Extension_FD_Mask.PARAMETER_SET_ID;
			this.parent.getDif().setDataFieldEncoding(TelegramEncoding.ENCODING_VARIABLE_LENGTH);
			this.lvarBit = true;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.MODEL_VERSION.getValue()) {
			this.type = VIF_Extension_FD_Mask.MODEL_VERSION;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.HARDWARE_VERSION.getValue()) {
			this.type = VIF_Extension_FD_Mask.HARDWARE_VERSION;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.FIRMWARE_VERSION.getValue()) {
			this.type = VIF_Extension_FD_Mask.FIRMWARE_VERSION;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.SOFTWARE_VERSION.getValue()) {
			this.type = VIF_Extension_FD_Mask.SOFTWARE_VERSION;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.CUSTOMER_LOCATION.getValue()) {
			this.type = VIF_Extension_FD_Mask.CUSTOMER_LOCATION;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.CUSTOMER.getValue()) {
			this.type = VIF_Extension_FD_Mask.CUSTOMER;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.ACCESS_CODE_USER.getValue()) {
			this.type = VIF_Extension_FD_Mask.ACCESS_CODE_USER;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.ACCESS_CODE_OPERATOR.getValue()) {
			this.type = VIF_Extension_FD_Mask.ACCESS_CODE_OPERATOR;
		} 
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.ACCESS_CODE_SYSTEM_OPERATOR.getValue()) {
			this.type = VIF_Extension_FD_Mask.ACCESS_CODE_SYSTEM_OPERATOR;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.ACCESS_CODE_DEVELOPER.getValue()) {
			this.type = VIF_Extension_FD_Mask.ACCESS_CODE_DEVELOPER;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.PASSWORD.getValue()) {
			this.type = VIF_Extension_FD_Mask.PASSWORD;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.ERROR_FLAGS.getValue()) {
			this.type = VIF_Extension_FD_Mask.ERROR_FLAGS;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.ERROR_MASKS.getValue()) {
			this.type = VIF_Extension_FD_Mask.ERROR_MASKS;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.RESERVED.getValue()) {
			this.type = VIF_Extension_FD_Mask.RESERVED;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.DIGITAL_OUTPUT.getValue()) {
			this.type = VIF_Extension_FD_Mask.DIGITAL_OUTPUT;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.DIGITAL_INPUT.getValue()) {
			this.type = VIF_Extension_FD_Mask.DIGITAL_INPUT;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.BAUDRATE.getValue()) {
			this.type = VIF_Extension_FD_Mask.BAUDRATE;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.RESPONSE_DELAY.getValue()) {
			this.type = VIF_Extension_FD_Mask.RESPONSE_DELAY;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.RETRY.getValue()) {
			this.type = VIF_Extension_FD_Mask.RETRY;
		}
		else if(iVifFieldNoExt == VIF_Extension_FD_Mask.RESERVED_2.getValue()) {
			this.type = VIF_Extension_FD_Mask.RESERVED_2;
		}		
		else {
			// TODO: ERROR HANDLING AND IMPLEMENTING THE OTHER CODES + MULTIPLIER
		}
		
	}
	
	public TelegramVariableDataRecord getParent() {
		return parent;
	}

	public void setParent(TelegramVariableDataRecord parent) {
		this.parent = parent;
	}

	public boolean isExtensionBit() {
		return extensionBit;
	}

	public void setExtensionBit(boolean extensionBit) {
		this.extensionBit = extensionBit;
	}
	
	public boolean isLvarBit() {
		return lvarBit;
	}

	public void setlvarBit(boolean lvarBit) {
		this.lvarBit = lvarBit;
	}
	
	public VIF_Extension_FD_Mask getType() {
		return type;
	}

	public void setType(VIF_Extension_FD_Mask type) {
		this.type = type;
	}
	
	public Measure_Unit getmUnit() {
		return mUnit;
	}

	public void setmUnit(Measure_Unit mUnit) {
		this.mUnit = mUnit;
	}
	
	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}
	
	public void debugOutput() {
		System.out.println("VIFE-Field: ");
		System.out.println("\tExtension-Bit: \t" + this.extensionBit);
		
		String vifField = this.fieldParts.get(0);
		int iVifeField = Converter.hexToInt(vifField);
		System.out.println("\tField (String): \t" + vifField);
		System.out.println("\tField (compl): \t\t" + Integer.toBinaryString(iVifeField));
		int iVifeFieldBits = (iVifeField & VIFETelegramField.UNIT_MULTIPLIER_MASK);
		System.out.println("\tField-Value: \t\t" + Integer.toBinaryString(iVifeFieldBits));
		System.out.println("\tField-Type: \t\t" + this.type);
		
		System.out.println("\tField-Unit: \t\t" + this.mUnit);
		System.out.println("\tField-Multiplier: \t" + this.multiplier);
	}
}
