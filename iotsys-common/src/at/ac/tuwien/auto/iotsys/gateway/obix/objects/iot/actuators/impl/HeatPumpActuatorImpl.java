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

import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.HeatPumpActuator;

public class HeatPumpActuatorImpl extends ActuatorImpl implements HeatPumpActuator {
	protected Bool disabled = new Bool(false);
	protected Bool targetValueInfluence = new Bool(false);
	
	public HeatPumpActuatorImpl(){
		setIs(new Contract(HeatPumpActuator.CONTRACT));
		
		disabled.setHref(new Uri(HeatPumpActuator.DISABLED_HREF));
		disabled.setName(HeatPumpActuator.DISABLED_NAME);

		
		targetValueInfluence.setHref(new Uri(HeatPumpActuator.TARGET_VALUE_INFLUENCE_HREF));
		targetValueInfluence.setName(HeatPumpActuator.TARGET_VALUE_INFLUENCE_NAME);
		
		add(disabled);
		add(targetValueInfluence);
	}
	
	@Override
	public Bool disabled() {	
		return disabled;
	}

	@Override
	public Bool targetValueInfluence() {
		return targetValueInfluence;
	}
	
	@Override
	public void writeObject(Obj input){
		if(input instanceof HeatPumpActuator){
			HeatPumpActuator in = (HeatPumpActuator) input;
			this.targetValueInfluence.set(in.targetValueInfluence().get());
			this.disabled.set(in.disabled().get());
		}
		else if(input instanceof Bool){
			Bool in = (Bool) input;
			String resourceUriPath = input.getInvokedHref().substring(input.getInvokedHref().lastIndexOf('/') + 1);
			
			if(HeatPumpActuator.DISABLED_HREF.equals(resourceUriPath)){
				this.disabled.set(in.get());
			}
			
			if(HeatPumpActuator.TARGET_VALUE_INFLUENCE_HREF.equals(resourceUriPath)){
				this.targetValueInfluence.set(in.get());
			}
		}
	}
}
