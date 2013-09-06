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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl;

import java.util.logging.Logger;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.SunblindActuator;
import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Uri;

/**
 * This class represents an abstract light switching actuator that is technology independent and
 * allows to represent such device as OBIX contract.
 */
public class SunblindActuatorImpl extends ActuatorImpl implements SunblindActuator{
	
	protected Bool moveDownValue = new Bool(false);	
	protected Bool moveUpValue = new Bool(false);
	protected Bool stopStepUpDownValue = new Bool(false);
	protected Bool dedicatedStopValue  = new Bool(false);
	
	private static final Logger log = Logger.getLogger(SunblindActuatorImpl.class.getName());
	
	
	public SunblindActuatorImpl(){
		setIs(new Contract(SunblindActuator.CONTRACT));
		
		//moveDownValue
		moveDownValue.setWritable(true);
		Uri moveDownValueUri = new Uri(SunblindActuator.MOVE_DOWN_CONTRACT_HREF);	
		moveDownValue.setHref(moveDownValueUri);
		moveDownValue.setName(SunblindActuator.MOVE_DOWN_CONTRACT_NAME);			
		add(moveDownValue);
		
		//moveUpValue
		moveUpValue.setWritable(true);
		Uri moveUpValueUri = new Uri(SunblindActuator.MOVE_UP_CONTRACT_HREF);	
		moveUpValue.setHref(moveUpValueUri);
		moveUpValue.setName(SunblindActuator.MOVE_UP_CONTRACT_NAME);			
		add(moveUpValue);
						
	}
	
	//gehoert noch ausgemistet
	
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.		
		boolean newMoveDownValue = false;
		boolean newMoveUpValue = false;
		
		
		if(input instanceof SunblindActuator){
			SunblindActuator in = (SunblindActuator) input;
			log.finer("Writing on SunblindActuator: " + in.moveDownValue().get());
			
			newMoveDownValue = in.moveDownValue().get();
			
			newMoveUpValue = in.moveUpValue().get();
		}
		
		else if(input instanceof Bool){
			
			
			if(input.getHref() == null){
				if(input.getInvokedHref() != null && input.getInvokedHref().length() > 0){
					String resourceUriPath = input.getInvokedHref().substring(input.getInvokedHref().lastIndexOf('/') + 1);
					
					if(SunblindActuator.MOVE_DOWN_CONTRACT_HREF.equals(resourceUriPath)){
						newMoveDownValue = ((Bool) input).get();
					}
					else if (SunblindActuator.MOVE_UP_CONTRACT_HREF.equals(resourceUriPath)){
						newMoveUpValue = ((Bool) input).get();
					}

				}
			}
			else{
				if (SunblindActuator.MOVE_DOWN_CONTRACT_HREF.equals(input.getHref().toString())){
					newMoveDownValue = ((Bool) input).get();
			
				}
				else if (SunblindActuator.MOVE_UP_CONTRACT_HREF.equals(input.getHref().toString())){
					newMoveUpValue = ((Bool) input).get();
		
				}

			}
		}
		
		if(moveDownValue.get() != newMoveDownValue)
			this.moveDownValue.set(newMoveDownValue);
		if(moveUpValue.get() != newMoveUpValue)
			this.moveUpValue.set(newMoveUpValue);

	}


//	@Override
//	public Bool stopStepUpDownValue() {
//		return this.stopStepUpDownValue;
//	}
//
//	@Override
//	public Bool dedicatedStopValue() {
//		return this.dedicatedStopValue;
//	}

	@Override
	public Bool moveUpValue() {
		return this.moveUpValue;
	}

	@Override
	public Bool moveDownValue() {
		return this.moveDownValue;
	}	
}	
