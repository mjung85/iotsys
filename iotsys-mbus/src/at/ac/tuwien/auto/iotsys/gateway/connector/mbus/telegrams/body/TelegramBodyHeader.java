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

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.TelegramField;

public class TelegramBodyHeader {

	private TelegramField ciField;		// control information field
	private TelegramField idNrField;	// identification number field	
	private TelegramField mField; 		// manufacturer
	private TelegramField vField;		// version
	private TelegramField medField;		// Measured Medium
	private TelegramField accNrField;	// access number
	private TelegramField statusField;	// status
	private TelegramField sigField;		// signature field
	
	public TelegramBodyHeader() {
		
	}
	
	public void createTelegramBodyHeader(String[] bodyHeader) {		
		this.setCiField(bodyHeader[0]);
		this.setIdNrField(Arrays.copyOfRange(bodyHeader, 1, 5));
		this.setMField(Arrays.copyOfRange(bodyHeader, 5, 7));
		this.setVField(bodyHeader[7]);
		this.setMedField(bodyHeader[8]);
		this.setAccNrField(bodyHeader[9]);	
		this.setStatusField(bodyHeader[10]);
		this.setSigField(Arrays.copyOfRange(bodyHeader, 10, 12));
	}
	
	public void setCiField(String ciField) {
		this.ciField = new TelegramField();
		this.ciField.addFieldPart(ciField);
	}	
	
	public void setIdNrField(String[] idNrField) {
		this.idNrField = new TelegramField();
		this.idNrField.addFieldParts(idNrField);
	}
	
	public void setMField(String[] mField) {
		this.mField = new TelegramField();
		this.mField.addFieldParts(mField);
	}
	
	public void setVField(String vField) {
		this.vField = new TelegramField();
		this.vField.addFieldPart(vField);
	}
	
	public void setMedField(String medField) {
		this.medField = new TelegramField();
		this.medField.addFieldPart(medField);
	}
	
	public void setAccNrField(String accField) {
		this.accNrField = new TelegramField();
		this.accNrField.addFieldPart(accField);
	}
	
	public void setStatusField(String statusField) {
		this.statusField = new TelegramField();
		this.statusField.addFieldPart(statusField);
	}
	
	public void setSigField(String[] sigField) {
		this.sigField = new TelegramField();
		this.sigField.addFieldParts(sigField);
	}	
	
	public String getIdNr(){
		return this.idNrField.getFieldParts().get(3) 
				+ this.idNrField.getFieldParts().get(2)
				+ this.idNrField.getFieldParts().get(1)
				+ this.idNrField.getFieldParts().get(0);
	}
	
	public void debugOutput() {
		if(this.ciField != null) {
			System.out.println("Type of TelegramBodyHeader: " + this.ciField.getFieldParts().get(0));
		}
		if(this.idNrField != null) {
			System.out.println("Identification#: " + this.idNrField.getFieldParts().get(3) 
								+ this.idNrField.getFieldParts().get(2)
								+ this.idNrField.getFieldParts().get(1)
								+ this.idNrField.getFieldParts().get(0));			
		}
		if(this.mField != null) {
			ManufacturerTelegramField field = new ManufacturerTelegramField();
			field.addFieldParts(this.mField.getFieldPartsAsArray());
			field.parse();
			System.out.println("Manufacturer: "	+ field.getParsedValue());			
		}
		if(this.vField != null) {
			System.out.println("Version: " + this.vField.getFieldParts().get(0));
		}
		if(this.medField != null) {
			// TODO parse medium
			System.out.println("Medium: " + this.medField.getFieldParts().get(0));
		}		
		if(this.accNrField != null) {
			System.out.println("AccessNumber: " + this.accNrField.getFieldParts().get(0));
		}
		if(this.statusField != null) {			
			System.out.println("StatusField: " + this.statusField.getFieldParts().get(0));
		}
		if(this.sigField != null) {
			// not in use --> value 00 00h
			System.out.println("Sig-Field1: " + this.sigField.getFieldParts().get(0));
			System.out.println("Sig-Field2: " + this.sigField.getFieldParts().get(1));
		}
	}
}
