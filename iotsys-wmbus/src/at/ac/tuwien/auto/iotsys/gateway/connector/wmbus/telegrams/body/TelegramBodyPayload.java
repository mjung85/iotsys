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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.Telegram;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.TelegramField;

public class TelegramBodyPayload {
	
	private TelegramField bodyFieldEncrypted;
	private TelegramField bodyFieldDecrypted;
	private List<TelegramVariableDataRecord> records;
	private boolean hasBeenEncrypted = true;

	public TelegramBodyPayload() {
		this.bodyFieldEncrypted = new TelegramField();
		this.bodyFieldDecrypted = new TelegramField();
	}
	
	public void createTelegramBodyPayload(String[] bodyPayload) {
		this.bodyFieldEncrypted = new TelegramField();
		this.bodyFieldEncrypted.addFieldParts(bodyPayload);
	}
	
	public void setDecryptedTelegramBodyPayload(String[] bodyPayload) {
		this.bodyFieldDecrypted = new TelegramField();
		this.bodyFieldDecrypted.addFieldParts(bodyPayload);
	}
	
	public void parse() {
		if(this.records == null) {
			this.records = new ArrayList<TelegramVariableDataRecord>();
		}
		this.records.clear();
		
		if (this.hasBeenEncrypted == true) {
			int startPosition = 0;
			// if encryption has been used we have to adjust the start position
			// of the first variable data record by some check fields
			if(this.hasBeenEncrypted == true) {
				this.parseVariableDataRecord(startPosition);
				this.parseVariableDataRecord(startPosition + 1);
				
				if(this.records.size() != 2) {
					// TODO: throw exception
				}
				if(this.records.get(0).getDif().isFillByte() == false) {
					// TODO: throw exception because first two records need to be 2F in encyrption mode
				}
				if(this.records.get(0).getDif().isFillByte() == false) {
					// TODO: throw exception because first two records need to be 2F in encyrption mode
				}
				startPosition = 2;
			}
			
			while(startPosition < this.bodyFieldDecrypted.getFieldParts().size()) {
				startPosition = this.parseVariableDataRecord(startPosition);
			}
		}
	}
	
	private int parseVariableDataRecord(int startPosition) {		
		TelegramVariableDataRecord rec = new TelegramVariableDataRecord();
		DIFTelegramField dif = new DIFTelegramField();
		dif.addFieldPart(this.bodyFieldDecrypted.getFieldParts().get(startPosition));
		dif.parse();
		
		rec.setDif(dif);

		if(dif.isFillByte()) {
			// fill byte for AES, do not interpret
			this.records.add(rec);
			return startPosition + 1;
		}
		
		List<DIFETelegramField> difeList = new ArrayList<DIFETelegramField>();
		if(dif.isExtensionBit() == true) {
			// increase start position by one (because we have already read DIF)
			difeList = this.parseDIFEFields(startPosition + 1);
		}
		
		rec.addDifes(difeList);
		
		// increase startPosition by 1 (DIF) and the number of DIFEs
		VIFTelegramField vif = new VIFTelegramField();
		vif.addFieldPart(this.bodyFieldDecrypted.getFieldParts().get(startPosition + 1 + difeList.size()));
		vif.setParent(rec);
		vif.parse();
		
		rec.setVif(vif);
		
		List<VIFETelegramField> vifeList = new ArrayList<VIFETelegramField>();
		if(vif.isExtensionBit() == true) {
			// increase startPosition by 2 (DIF and VIF) and the number of DIFEs
			vifeList = this.parseVIFEFields(startPosition + 2 + difeList.size());
		}
		
		rec.addVifes(vifeList);
		
		// increase startPosition by 2 (DIF and VIF) and the number of DIFEs and
		// the number of VIFEs
		int lowerBoundary = startPosition + 2 + difeList.size() + vifeList.size();
		int upperBoundary = lowerBoundary + dif.getDataFieldLength();
		
		if(dif.getDataFieldLength() == 0) {
			// no data values, nothing todo, continue with the next one
			return upperBoundary;
		}
		
		if(this.bodyFieldDecrypted.getFieldParts().size() >= upperBoundary) {
			TelegramDataField dataField = new TelegramDataField(rec);
			dataField.addFieldParts(Arrays.copyOfRange(this.bodyFieldDecrypted.getFieldPartsAsArray(), lowerBoundary, upperBoundary));
			dataField.parse();
			rec.setDataField(dataField);
		}
		
		this.records.add(rec);
		
		return upperBoundary;
	}
	
	private List<DIFETelegramField> parseDIFEFields(int position) {
		List<DIFETelegramField> difeList = new ArrayList<DIFETelegramField>();
		boolean extensionBitSet = true;
		DIFETelegramField dife = null;
		
		while(extensionBitSet == true) {
			if(this.bodyFieldDecrypted.getFieldParts().size() < position) {
				// TODO: throw exception
			}
			dife = this.processSingleDIFEField(this.bodyFieldDecrypted.getFieldParts().get(position));
			difeList.add(dife);
			extensionBitSet = dife.isExtensionBit();
			position++;
		}
		
		return difeList;
	}
	
	private DIFETelegramField processSingleDIFEField(String fieldValue) {
		DIFETelegramField dife = new DIFETelegramField();
		dife.addFieldPart(fieldValue);
		
		return dife;
	}
	
	private List<VIFETelegramField> parseVIFEFields(int position) {
		List<VIFETelegramField> vifeList = new ArrayList<VIFETelegramField>();
		boolean extensionBitSet = true;
		VIFETelegramField vife = null;
		
		while(extensionBitSet == true) {
			if(this.bodyFieldDecrypted.getFieldParts().size() < position) {
				// TODO: throw exception
			}
			vife = this.processSingleVIFEField(this.bodyFieldDecrypted.getFieldParts().get(position));
			vifeList.add(vife);
			extensionBitSet = vife.isExtensionBit();
			position++;
		}
		
		return vifeList;
	}
	
	private VIFETelegramField processSingleVIFEField(String fieldValue) {
		VIFETelegramField vife = new VIFETelegramField();
		vife.addFieldPart(fieldValue);
		
		return vife;
	}
	
	public String getPayloadAsString() {
		return this.bodyFieldEncrypted.getFieldPartsAsString();
	}
	
	public String getDecryptedPayloadAsString() {
		return this.bodyFieldDecrypted.getFieldPartsAsString();
	}
	
	public List<String> getPayloadAsList() {
		return this.bodyFieldEncrypted.getFieldParts();
	}
	
	public List<String> getDecryptedPayloadAsList() {
		return this.bodyFieldDecrypted.getFieldParts();
	}
	
	public boolean isHasBeenEncrypted() {
		return hasBeenEncrypted;
	}

	public void setHasBeenEncrypted(boolean hasBeenEncrypted) {
		this.hasBeenEncrypted = hasBeenEncrypted;
	}
	
	public void debugOutput() {
		System.out.println("-------------------------------------------------------------");
		System.out.println("-------------------- BEGIN BODY PAYLOAD ---------------------");
		System.out.println("-------------------------------------------------------------");
		System.out.println("Encrypted: " + this.hasBeenEncrypted);
		if(this.records != null) {
			for(int i = 0; i < this.records.size(); i++) {
				System.out.println("RECORD: " + i);
				this.records.get(i).debugOutput();
			}
		}
		System.out.println("-------------------------------------------------------------");
		System.out.println("--------------------- END BODY PAYLOAD ----------------------");
		System.out.println("-------------------------------------------------------------");
	}

	public List<TelegramVariableDataRecord> getRecords() {
		// TODO Auto-generated method stub
		return this.records;
	}

}
