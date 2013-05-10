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

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.BrightnessActuator;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Uri;

/**
 * This class represents an abstract brightness actuator that is technology independent and
 * allows to represent such device as OBIX contract.
 * @author Juergen Weidinger
 *
 */

public class BrightnessActuatorImpl extends ActuatorImpl implements BrightnessActuator{
	protected Int value = new Int(0);

	public BrightnessActuatorImpl(){
		setIs(new Contract(BrightnessActuator.CONTRACT));
		value.setWritable(true);
		Uri valueUri = new Uri("value");
	
		value.setHref(valueUri);
		value.setName("value");
		add(value);
	}
	
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.		
		long newVal = 0;
		if(input instanceof BrightnessActuator){
			BrightnessActuator in = (BrightnessActuator) input;
			newVal = in.value().get();
			
		}
		else if(input instanceof Int){
			newVal = ((Int) input).get();
		}
		this.value.set(newVal);
	}
	
	@Override
	public Int value() {
		return this.value;
	}	
}
