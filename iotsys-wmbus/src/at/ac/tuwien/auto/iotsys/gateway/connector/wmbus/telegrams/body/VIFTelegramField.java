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

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.TelegramField;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.util.Measure_Unit;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.util.Telegram_Date_Masks;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.util.VIF_Unit_Multiplier_Masks;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.util.Converter;

public class VIFTelegramField extends TelegramField {
	
	private static int EXTENSION_BIT_MASK = 0x80; 		// 1000 0000
	private static int LAST_TWO_BIT_OR_MASK = 0x03; 	// 0000 0011
	private static int LAST_THREE_BIT_OR_MASK = 0x07; 	// 0000 0111
	private static int UNIT_MULTIPLIER_MASK = 0x7F; 	// 0111 1111
	
	private boolean extensionBit = false;
	private Measure_Unit mUnit = Measure_Unit.NONE;
	private VIF_Unit_Multiplier_Masks type;
	private int multiplier = 0;

	private TelegramVariableDataRecord parent;

	public void parse() {
		String vifField = this.fieldParts.get(0);
		int iVifField = Converter.hexToInt(vifField);
		
		if((iVifField & VIFTelegramField.EXTENSION_BIT_MASK) == VIFTelegramField.EXTENSION_BIT_MASK) {
			this.extensionBit = true;
		}
		
		if(iVifField == VIF_Unit_Multiplier_Masks.FIRST_EXT_VIF_CODES.getValue()) {
			// TODO: load from next VIFE according to table 29 from DIN_EN_13757_3
		}
		else if(iVifField == VIF_Unit_Multiplier_Masks.SECOND_EXT_VIF_CODES.getValue()) {
			// TODO: load from next VIFE according to table 28 from DIN_EN_13757_3
		}
		else {
			// first get rid of the first (extension) bit
			int iVifFieldNoExt = (iVifField & (VIFTelegramField.UNIT_MULTIPLIER_MASK));
			
			// first check against complete (no wildcards) bit masks
			// can't check with switch case because we need a constant value there
			if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.DATE.getValue()) {
				this.type = VIF_Unit_Multiplier_Masks.DATE;
				this.parseDate(this.parent.getDif().getDataFieldLengthAndEncoding());
			}
			else if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.DATE_TIME_GENERAL.getValue()) {
				this.parseDate(this.parent.getDif().getDataFieldLengthAndEncoding());
			}
			else if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.UNITS_FOR_HCA.getValue()) {
				// NO UNIT
				this.type = VIF_Unit_Multiplier_Masks.UNITS_FOR_HCA;
			}
			else if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.RES_THIRD_VIFE_TABLE.getValue()) {
				// NO UNIT
				this.type = VIF_Unit_Multiplier_Masks.RES_THIRD_VIFE_TABLE;
			}
			else if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.FABRICATION_NO.getValue()) {
				// NO UNIT
				this.type = VIF_Unit_Multiplier_Masks.FABRICATION_NO;
			}
			else if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.IDENTIFICATION.getValue()) {
				// NO UNIT
				this.type = VIF_Unit_Multiplier_Masks.IDENTIFICATION;
			}
			else if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.ADDRESS.getValue()) {
				// TODO
				this.type = VIF_Unit_Multiplier_Masks.ADDRESS;
			}
			else if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.VIF_FOLLOWING.getValue()) {
				// TODO: plain String?
				this.type = VIF_Unit_Multiplier_Masks.VIF_FOLLOWING;
			}
			else if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.ANY_VIF.getValue()) {
				// TODO: check 6.4
				this.type = VIF_Unit_Multiplier_Masks.ANY_VIF;
			}
			else if(iVifFieldNoExt == VIF_Unit_Multiplier_Masks.MANUFACTURER_SPEC.getValue()) {
				// TODO: VIFE and data is manufacturer specification
				this.type = VIF_Unit_Multiplier_Masks.MANUFACTURER_SPEC;
			}
			else if(parseLastTwoBitsSet(iVifFieldNoExt) == true) {
				
			}
			else if(parseLastThreeBitsSet(iVifFieldNoExt) == true) {
				
			}
			else {
				// TODO: ERROR HANDLING
			}
		}
	}
	
	public boolean parseLastTwoBitsSet(int iVifFieldNoExt) {
		// set last two bits to 1 so that we can check against our other masks
		int iVifFieldNoExtLastTwo = (iVifFieldNoExt | VIFTelegramField.LAST_TWO_BIT_OR_MASK);
		int onlyLastTwoBits = iVifFieldNoExt & VIFTelegramField.LAST_TWO_BIT_OR_MASK;
		
		if(iVifFieldNoExtLastTwo == VIF_Unit_Multiplier_Masks.ON_TIME.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.ON_TIME;
			switch (onlyLastTwoBits) {
				case 0:
					this.mUnit = Measure_Unit.SECONDS;
					break;
				case 1:
					this.mUnit = Measure_Unit.MINUTES;
					break;
				case 2:
					this.mUnit = Measure_Unit.HOURS;
					break;
				case 3:
					this.mUnit = Measure_Unit.DAYS;
					break;
				default:
					//TODO: Exception Handling
					break;
			}
		}
		else if(iVifFieldNoExtLastTwo == VIF_Unit_Multiplier_Masks.OPERATING_TIME.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.OPERATING_TIME;
		}
		else if(iVifFieldNoExtLastTwo == VIF_Unit_Multiplier_Masks.FLOW_TEMPERATURE.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.FLOW_TEMPERATURE;
			this.multiplier = onlyLastTwoBits - 3;
			this.mUnit = Measure_Unit.C;
		}
		else if(iVifFieldNoExtLastTwo == VIF_Unit_Multiplier_Masks.RETURN_TEMPERATURE.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.RETURN_TEMPERATURE;
			this.multiplier = onlyLastTwoBits - 3;
			this.mUnit = Measure_Unit.C;
		}
		else if(iVifFieldNoExtLastTwo == VIF_Unit_Multiplier_Masks.TEMPERATURE_DIFFERENCE.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.TEMPERATURE_DIFFERENCE;
			this.multiplier = onlyLastTwoBits - 3;
			this.mUnit = Measure_Unit.K;
		}
		else if(iVifFieldNoExtLastTwo == VIF_Unit_Multiplier_Masks.EXTERNAL_TEMPERATURE.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.EXTERNAL_TEMPERATURE;
			this.multiplier = onlyLastTwoBits - 3;
			this.mUnit = Measure_Unit.C;
		}
		else if(iVifFieldNoExtLastTwo == VIF_Unit_Multiplier_Masks.PRESSURE.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.PRESSURE;
			this.multiplier = onlyLastTwoBits - 3;
			this.mUnit = Measure_Unit.BAR;
		}
		else if(iVifFieldNoExtLastTwo == VIF_Unit_Multiplier_Masks.AVG_DURATION.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.AVG_DURATION;
		}
		else if(iVifFieldNoExtLastTwo == VIF_Unit_Multiplier_Masks.ACTUALITY_DURATION.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.ACTUALITY_DURATION;
		}
		else {
			return false;
		}
		return true;
	}

	public boolean parseLastThreeBitsSet(int iVifFieldNoExt) {
		// set last three bits to 1 so that we can check against our other masks
		int iVifFieldNoExtLastThree = (iVifFieldNoExt | VIFTelegramField.LAST_THREE_BIT_OR_MASK);
		
		int onlyLastThreeBits = iVifFieldNoExt & VIFTelegramField.LAST_THREE_BIT_OR_MASK;
		
		if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.ENERGY_WH.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.ENERGY_WH;
			this.multiplier = onlyLastThreeBits - 3;
			this.mUnit = Measure_Unit.WH;
		}
		else if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.ENERGY_J.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.ENERGY_J;
			this.multiplier = onlyLastThreeBits;
			this.mUnit = Measure_Unit.J;
		}
		else if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.VOLUME.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.VOLUME;
			this.multiplier = onlyLastThreeBits - 6;
			this.mUnit = Measure_Unit.M3;
		}
		else if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.MASS.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.MASS;
			this.multiplier = onlyLastThreeBits - 3;
			this.mUnit = Measure_Unit.KG;
		}
		else if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.POWER_W.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.POWER_W;
			this.multiplier = onlyLastThreeBits - 3;
			this.mUnit = Measure_Unit.W;
		}
		else if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.POWER_J_H.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.POWER_J_H;
			this.multiplier = onlyLastThreeBits;
			this.mUnit = Measure_Unit.J_H;
		}
		else if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.VOLUME_FLOW.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.VOLUME_FLOW;
			this.multiplier = onlyLastThreeBits - 6;
			this.mUnit = Measure_Unit.M3_H;
		}
		else if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.VOLUME_FLOW_EXT.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.VOLUME_FLOW_EXT;
			this.multiplier = onlyLastThreeBits - 7;
			this.mUnit = Measure_Unit.M3_MIN;
		}
		else if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.VOLUME_FLOW_EXT_S.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.VOLUME_FLOW_EXT_S;
			this.multiplier = onlyLastThreeBits - 9;
			this.mUnit = Measure_Unit.M3_S;
		}
		else if(iVifFieldNoExtLastThree == VIF_Unit_Multiplier_Masks.MASS_FLOW.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.MASS_FLOW;
			this.multiplier = onlyLastThreeBits - 3;
			this.mUnit = Measure_Unit.KG_H;
		}
		else {
			return false;
		}
		return true;
	}
	
	private void parseDate(int dateType) {
		if(dateType == Telegram_Date_Masks.DATE.getValue()) {
			mUnit = Measure_Unit.DATE;
		}
		else if(dateType == Telegram_Date_Masks.DATE_TIME.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.DATE_TIME;
			mUnit = Measure_Unit.TIME;
		}
		else if(dateType == Telegram_Date_Masks.EXT_TIME.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.EXTENTED_TIME;
			mUnit = Measure_Unit.DATE_TIME;
		}
		else if(dateType == Telegram_Date_Masks.EXT_DATE_TIME.getValue()) {
			this.type = VIF_Unit_Multiplier_Masks.EXTENTED_DATE_TIME;
			mUnit = Measure_Unit.DATE_TIME_S;
		}
		else {
			// TODO: THROW EXCEPTION
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

	public VIF_Unit_Multiplier_Masks getType() {
		return type;
	}

	public void setType(VIF_Unit_Multiplier_Masks type) {
		this.type = type;
	}

	public void setExtensionBit(boolean extensionBit) {
		this.extensionBit = extensionBit;
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
		System.out.println("VIF-Field: ");
		System.out.println("\tExtension-Bit: \t\t" + this.extensionBit);
		
		String vifField = this.fieldParts.get(0);
		int iVifField = Converter.hexToInt(vifField);
		System.out.println("\tField (String): \t" + vifField);
		System.out.println("\tField (compl): \t\t" + Integer.toBinaryString(iVifField));
		int iVifFieldBits = (iVifField & VIFTelegramField.UNIT_MULTIPLIER_MASK);
		System.out.println("\tField-Value: \t\t" + Integer.toBinaryString(iVifFieldBits));
		System.out.println("\tField-Type: \t\t" + this.type);
		
		System.out.println("\tField-Unit: \t\t" + this.mUnit);
		System.out.println("\tField-Multiplier: \t" + this.multiplier);
	}
}
