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
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.AirDamperActuator;

public class AirDamperActuatorImpl extends ActuatorImpl implements AirDamperActuator{
	protected Int actPosSetpFreshAirValue = new Int(0);
	protected Int actPosSetpSupplyAirValue = new Int(0);
	protected Int actPosSetpDischargeAirValue = new Int(0);
	protected Int actPosSetpExtractAirValue = new Int(0);
	
	public AirDamperActuatorImpl() {
	
		setIs(new Contract(AirDamperActuator.CONTRACT));
		
		//actPosSetpFreshAirValue
		actPosSetpFreshAirValue.setWritable(true);
		Uri actPosSetpFreshAirValueUri = new Uri(AirDamperActuator.ACT_POS_SETP_FRESH_AIR_CONTRACT_HREF);
		actPosSetpFreshAirValue.setHref(actPosSetpFreshAirValueUri);
		actPosSetpFreshAirValue.setName(AirDamperActuator.ACT_POS_SETP_FRESH_AIR_CONTRACT_NAME);
		actPosSetpFreshAirValue.setUnit(new Uri(AirDamperActuator.ACT_POS_SETP_FRESH_AIR_CONTRACT_UNIT));
		add(actPosSetpFreshAirValue);
		
		//actPosSetpSupplyAirValue
		actPosSetpSupplyAirValue.setWritable(true);
		Uri actPosSetpSupplyAirValueUri = new Uri(AirDamperActuator.ACT_POS_SETP_SUPPLY_AIR_CONTRACT_HREF);
		actPosSetpSupplyAirValue.setHref(actPosSetpSupplyAirValueUri);
		actPosSetpSupplyAirValue.setName(AirDamperActuator.ACT_POS_SETP_SUPPLY_AIR_CONTRACT_NAME);
		actPosSetpSupplyAirValue.setUnit(new Uri(AirDamperActuator.ACT_POS_SETP_SUPPLY_AIR_CONTRACT_UNIT));
		add(actPosSetpSupplyAirValue);
		
		//actPosSetpDischargeAirValue
		actPosSetpDischargeAirValue.setWritable(true);
		Uri actPosSetpDischargeAirValueUri = new Uri(AirDamperActuator.ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_HREF);
		actPosSetpDischargeAirValue.setHref(actPosSetpDischargeAirValueUri);
		actPosSetpDischargeAirValue.setName(AirDamperActuator.ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_NAME);
		actPosSetpDischargeAirValue.setUnit(new Uri(AirDamperActuator.ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_UNIT));
		add(actPosSetpDischargeAirValue);
		
		//actPosSetpExtractAirValue
		actPosSetpExtractAirValue.setWritable(true);
		Uri actPosSetpExtractAirValueUri = new Uri(AirDamperActuator.ACT_POS_SETP_EXTRACT_AIR_CONTRACT_HREF);
		actPosSetpExtractAirValue.setHref(actPosSetpExtractAirValueUri);
		actPosSetpExtractAirValue.setName(AirDamperActuator.ACT_POS_SETP_EXTRACT_AIR_CONTRACT_NAME);
		actPosSetpExtractAirValue.setUnit(new Uri(AirDamperActuator.ACT_POS_SETP_EXTRACT_AIR_CONTRACT_UNIT));
		add(actPosSetpExtractAirValue);
		
	}
	
	
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.		
		long newActPosSetpFreshAirValue = 0;
		long newActPosSetpSupplyAirValue = 0;
		long newActPosSetpDischargeAirValue = 0;
		long newActPosSetpExtractAirValue = 0;
				
		if(input instanceof AirDamperActuator){
			AirDamperActuator in = (AirDamperActuator) input;
									
				newActPosSetpFreshAirValue = in.actPosSetpFreshAirValue().get();
				this.actPosSetpFreshAirValue.set(newActPosSetpFreshAirValue);
			
				newActPosSetpSupplyAirValue = in.actPosSetpSupplyAirValue().get();
				this.actPosSetpSupplyAirValue.set(newActPosSetpSupplyAirValue);

				newActPosSetpDischargeAirValue = in.actPosSetpDischargeAirValue().get();
				this.actPosSetpDischargeAirValue.set(newActPosSetpDischargeAirValue);
	
				newActPosSetpExtractAirValue = in.actPosSetpExtractAirValue().get();
				this.actPosSetpExtractAirValue.set(newActPosSetpExtractAirValue);	
		}
		
		//Decide shich child are requested
		else if(input instanceof Int){
			if(input.getHref() == null){
				String resourceUriPath = input.getInvokedHref().substring(input.getInvokedHref().lastIndexOf('/') + 1);
				
				if(AirDamperActuator.ACT_POS_SETP_FRESH_AIR_CONTRACT_HREF.equals(resourceUriPath)){
						newActPosSetpFreshAirValue = ((Int) input).get();
						this.actPosSetpFreshAirValue.set(newActPosSetpFreshAirValue);
				}
				else if (AirDamperActuator.ACT_POS_SETP_SUPPLY_AIR_CONTRACT_HREF.equals(resourceUriPath)){
						newActPosSetpSupplyAirValue = ((Int) input).get();
						this.actPosSetpSupplyAirValue.set(newActPosSetpSupplyAirValue);
				}
				else if (AirDamperActuator.ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_HREF.equals(resourceUriPath)){
						newActPosSetpDischargeAirValue = ((Int) input).get();
						this.actPosSetpDischargeAirValue.set(newActPosSetpDischargeAirValue);
				}
				else if (AirDamperActuator.ACT_POS_SETP_EXTRACT_AIR_CONTRACT_HREF.equals(resourceUriPath)){
						newActPosSetpExtractAirValue = ((Int) input).get();
						this.actPosSetpExtractAirValue.set(newActPosSetpExtractAirValue);
				}
				
			}
			else{
				if (AirDamperActuator.ACT_POS_SETP_FRESH_AIR_CONTRACT_HREF.equals(input.getHref().toString())){
					newActPosSetpFreshAirValue = ((Int) input).get();
					this.actPosSetpFreshAirValue.set(newActPosSetpFreshAirValue);
				}
				else if (AirDamperActuator.ACT_POS_SETP_SUPPLY_AIR_CONTRACT_HREF.equals(input.getHref().toString())){
					newActPosSetpSupplyAirValue = ((Int) input).get();
					this.actPosSetpSupplyAirValue.set(newActPosSetpSupplyAirValue);
				}
				else if (AirDamperActuator.ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_HREF.equals(input.getHref().toString())){
					newActPosSetpDischargeAirValue = ((Int) input).get();
					this.actPosSetpDischargeAirValue.set(newActPosSetpDischargeAirValue);
				}
				else if (AirDamperActuator.ACT_POS_SETP_EXTRACT_AIR_CONTRACT_HREF.equals(input.getHref().toString())){
					newActPosSetpExtractAirValue = ((Int) input).get();
					this.actPosSetpExtractAirValue.set(newActPosSetpExtractAirValue);
				}
			  
			}

		}
		
	}
	
	
	@Override
	public Int actPosSetpFreshAirValue() {
		return this.actPosSetpFreshAirValue;
	}

	@Override
	public Int actPosSetpSupplyAirValue() {
		return this.actPosSetpSupplyAirValue;
	}

	@Override
	public Int actPosSetpDischargeAirValue() {
		return this.actPosSetpDischargeAirValue;
	}

	@Override
	public Int actPosSetpExtractAirValue() {
		return this.actPosSetpExtractAirValue;
	}

}
