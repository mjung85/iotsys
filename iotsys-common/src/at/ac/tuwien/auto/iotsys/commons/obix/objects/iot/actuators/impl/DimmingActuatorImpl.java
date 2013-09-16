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

import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.DefRelativeSetvalueControl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.DimmingActuator;
import obix.Bool;
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

public class DimmingActuatorImpl extends ActuatorImpl implements DimmingActuator{
	
	//private IObjectBroker _objectBroker;
	
	protected Bool switchOnOffValue = new Bool(false);
	protected Int absoluteSetvalueControlValue = new Int(0);
	
	// relative setvalue control
	protected Obj relativSetvalueControl = new Obj();
	protected Bool relativSetvalueControlDimmingDirectionValue = new Bool(false);
	protected Int relativSetvalueControlDimmingStepValue = new Int();
	
	
	public DimmingActuatorImpl() {
		//_objectBroker = new ObjectBroker();
		
		setIs(new Contract(DimmingActuator.CONTRACT));
		
		//switchOnOffValue
		switchOnOffValue.setWritable(true);
		Uri switchOnOffValueUri = new Uri(DimmingActuator.SWITCH_ON_OFF_CONTRACT_HREF);
		switchOnOffValue.setHref(switchOnOffValueUri);
		switchOnOffValue.setName(DimmingActuator.SWITCH_ON_OFF_CONTRACT_NAME);
		add(switchOnOffValue);
		
		//absoluteSetvalueControlValue
		absoluteSetvalueControlValue.setWritable(true);
		Uri absoluteSetvalueControlValueUri = new Uri(DimmingActuator.ABSOLUTE_SETVALUE_CONTRACT_HREF);
		absoluteSetvalueControlValue.setHref(absoluteSetvalueControlValueUri);
		absoluteSetvalueControlValue.setName(DimmingActuator.ABSOLUTE_SETVALUE_CONTRACT_NAME);
		absoluteSetvalueControlValue.setUnit(new Uri(DimmingActuator.ABSOLUTE_SETVALUE_CONTRACT_UNIT));
		add(absoluteSetvalueControlValue);			

		DefRelativeSetvalueControlImpl relativeValueContract = new DefRelativeSetvalueControlImpl();
		Uri relativeValueContractUri = new Uri(DefRelativeSetvalueControl.RELATIV_SETVALUE_CONTROL_CONTRACT_HREF);
		relativeValueContract.setHref(relativeValueContractUri);
		add(relativeValueContract);
		//_objectBroker.addObj(relativeValueContract);
	}
	
	
	@Override
	public Bool switchOnOffValue() {
		return this.switchOnOffValue;
	}

	@Override
	public Int absoluteSetvalueControlValue() {
		return this.absoluteSetvalueControlValue;
	}

	@Override
	public Bool relativSetvalueControlDimmingDirectionValue() {
		return this.relativSetvalueControlDimmingDirectionValue;
	}

	@Override
	public Int relativSetvalueControlDimmingStepValue() {
		return this.relativSetvalueControlDimmingStepValue;
	}

}
