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
import java.util.List;

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.TelegramField;

public class TelegramVariableDataRecord {
	
	private DIFTelegramField dif;
	private List<DIFETelegramField> difes;
	private VIFTelegramField vif;
	private List<VIFETelegramField> vifes;
	
	private TelegramDataField dataField;
	
	public void parse() {
		this.dif = new DIFTelegramField();
		this.dif.parse();
		this.vif = new VIFTelegramField();
		this.vif.setParent(this);
		this.vif.parse();
		
		this.dataField = new TelegramDataField();
		this.dataField.setParent(this);
		this.dataField.parse();
	}
	
	public DIFTelegramField getDif() {
		return dif;
	}
	
	public void setDif(DIFTelegramField dif) {
		this.dif = dif;
	}
	
	public void addDifes(List<DIFETelegramField> difes) {
		if(this.difes == null) {
			this.difes = new ArrayList<DIFETelegramField>();
			this.difes.addAll(difes);
		}
	}
	
	public VIFTelegramField getVif() {
		return vif;
	}
	
	public void setVif(VIFTelegramField vif) {
		this.vif = vif;
	}
	
	public void addVifes(List<VIFETelegramField> vifes) {
		if(this.vifes == null) {
			this.vifes = new ArrayList<VIFETelegramField>();
			this.vifes.addAll(vifes);
		}
	}
	
	public TelegramDataField getDataField() {
		return this.dataField;
	}
	
	public void setDataField(TelegramDataField dataField) {
		this.dataField = dataField;
	}
	
	public void getDataField(TelegramDataField dataField) {
		this.dataField = dataField;
	}
	
	public void debugOutput() {
		System.out.println("VARIABLE DATA RECORD: ");
		if(this.dif != null) {
			this.dif.debugOutput();
		}
		
		if(this.difes != null) {
			for(int i = 0; i < this.difes.size(); i++) {
				this.difes.get(i).debugOutput();
			}
		}
		
		if(this.vif != null) {
			this.vif.debugOutput();
		}
		
		if(this.vifes != null) {
			for(int i = 0; i < this.vifes.size(); i++) {
				this.vifes.get(i).debugOutput();
			}
		}
		
		if(this.dataField != null) {
			this.dataField.debugOutput();
		}
		System.out.println("==================================================");
	}

}
