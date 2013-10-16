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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl;

import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Uri;
//import tuwien.auto.obix.objects.iot.actuators.AirDamperActuator;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.HVACvalveActuator;


public class HVACvalveActuatorImpl extends ActuatorImpl implements HVACvalveActuator {
	protected Int value = new Int(0);

	protected Int actPosSetpHeatStageAValue = new Int(0);
	protected Int actPosSetpHeatStageBValue = new Int(0);
	protected Int actPosSetpCoolStageAValue = new Int(0);
	protected Int actPosSetpCoolStageBValue = new Int(0);
	

	public HVACvalveActuatorImpl() {
		setIs(new Contract(HVACvalveActuator.CONTRACT));
		
		//actPosSetpHeatStageAValue
		actPosSetpHeatStageAValue.setWritable(true);
		Uri actPosSetpHeatStageAValueUri = new Uri(HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_A_CONTRACT_HREF);
		actPosSetpHeatStageAValue.setHref(actPosSetpHeatStageAValueUri);
		actPosSetpHeatStageAValue.setName(HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_A_CONTRACT_NAME);
		actPosSetpHeatStageAValue.setUnit(new Uri(HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_A_CONTRACT_UNIT));
		add(actPosSetpHeatStageAValue);
		
		//actPosSetpHeatStageBValue
		actPosSetpHeatStageBValue.setWritable(true);
		Uri actPosSetpHeatStageBValueUri = new Uri(HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_B_CONTRACT_HREF);
		actPosSetpHeatStageBValue.setHref(actPosSetpHeatStageBValueUri);
		actPosSetpHeatStageBValue.setName(HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_B_CONTRACT_NAME);
		actPosSetpHeatStageBValue.setUnit(new Uri(HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_B_CONTRACT_UNIT));
		add(actPosSetpHeatStageBValue);
		
		//actPosSetpCoolStageAValue
		actPosSetpCoolStageAValue.setWritable(true);
		Uri actPosSetpCoolStageAValueUri = new Uri(HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_A_CONTRACT_HREF);
		actPosSetpCoolStageAValue.setHref(actPosSetpCoolStageAValueUri);
		actPosSetpCoolStageAValue.setName(HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_A_CONTRACT_NAME);
		actPosSetpCoolStageAValue.setUnit(new Uri(HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_A_CONTRACT_UNIT));
		add(actPosSetpCoolStageAValue);
		
		//actPosSetpCoolStageBValue
		actPosSetpCoolStageBValue.setWritable(true);
		Uri actPosSetpCoolStageBValueUri = new Uri(HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_B_CONTRACT_HREF);
		actPosSetpCoolStageBValue.setHref(actPosSetpCoolStageBValueUri);
		actPosSetpCoolStageBValue.setName(HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_B_CONTRACT_NAME);
		actPosSetpCoolStageBValue.setUnit(new Uri(HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_B_CONTRACT_UNIT));
		add(actPosSetpCoolStageBValue);
	}

	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.		
		long newActPosSetpHeatStageAValue = 0;
		long newActPosSetpHeatStageBValue = 0;
		long newActPosSetpCoolStageAValue = 0;
		long newActPosSetpCoolStageBValue = 0;
		
		
		if(input instanceof HVACvalveActuator){
			HVACvalveActuator in = (HVACvalveActuator) input;
			
				newActPosSetpHeatStageAValue = in.actPosSetpHeatStageAValue().get();
				this.actPosSetpHeatStageAValue.set(newActPosSetpHeatStageAValue);

				newActPosSetpHeatStageBValue = in.actPosSetpHeatStageBValue().get();
				this.actPosSetpHeatStageBValue.set(newActPosSetpHeatStageBValue);

				newActPosSetpCoolStageAValue = in.actPosSetpCoolStageAValue().get();
				this.actPosSetpCoolStageAValue.set(newActPosSetpCoolStageAValue);

				newActPosSetpCoolStageBValue = in.actPosSetpCoolStageBValue().get();
				this.actPosSetpCoolStageBValue.set(newActPosSetpCoolStageBValue);
		}
		
		//Decide which children are requested
		else if(input instanceof Int){
			if(input.getHref() == null){
				String resourceUriPath = input.getInvokedHref().substring(input.getInvokedHref().lastIndexOf('/') + 1);
				
				if(HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_A_CONTRACT_HREF.equals(resourceUriPath)){
						newActPosSetpHeatStageAValue = ((Int) input).get();
						this.actPosSetpHeatStageAValue.set(newActPosSetpHeatStageAValue) ;
				}
				else if (HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_B_CONTRACT_HREF.equals(resourceUriPath)){
						newActPosSetpHeatStageBValue = ((Int) input).get();
						this.actPosSetpHeatStageBValue.set(newActPosSetpHeatStageBValue);
				}
				else if (HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_A_CONTRACT_HREF.equals(resourceUriPath)){
					newActPosSetpCoolStageAValue = ((Int) input).get();
					this.actPosSetpCoolStageAValue.set(newActPosSetpCoolStageAValue);
				}
				else if (HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_B_CONTRACT_HREF.equals(resourceUriPath)){
					newActPosSetpCoolStageBValue = ((Int) input).get();
					this.actPosSetpCoolStageBValue.set(newActPosSetpCoolStageBValue);
				}	
			}
			
			//oBIX Int object contains a Href attribute
			else{ 
				if (HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_A_CONTRACT_HREF.equals(input.getHref().toString())){ 
					newActPosSetpHeatStageAValue = ((Int) input).get();
					this.actPosSetpHeatStageAValue.set(newActPosSetpHeatStageAValue) ;
				}
				else if (HVACvalveActuator.ACT_POS_SETP_HEAT_STAGE_B_CONTRACT_HREF.equals(input.getHref().toString())){			
					this.actPosSetpHeatStageBValue.set(newActPosSetpHeatStageBValue);
				}				
				else if (HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_A_CONTRACT_HREF.equals(input.getHref().toString())){					
					newActPosSetpCoolStageAValue = ((Int) input).get();
					this.actPosSetpCoolStageAValue.set(newActPosSetpCoolStageAValue);
				}
				else if (HVACvalveActuator.ACT_POS_SETP_COOL_STAGE_B_CONTRACT_HREF.equals(input.getHref().toString())){					
					newActPosSetpCoolStageBValue = ((Int) input).get();
					this.actPosSetpCoolStageBValue.set(newActPosSetpCoolStageBValue);
				}
			}	
		}	
	}
	
	@Override
	public Int actPosSetpHeatStageAValue() {
		return this.actPosSetpHeatStageAValue;
	}

	@Override
	public Int actPosSetpHeatStageBValue() {
		return this.actPosSetpHeatStageBValue;
	}

	@Override
	public Int actPosSetpCoolStageAValue() {
		return this.actPosSetpCoolStageAValue;
	}

	@Override
	public Int actPosSetpCoolStageBValue() {
		return this.actPosSetpCoolStageBValue;
	}
	

}
