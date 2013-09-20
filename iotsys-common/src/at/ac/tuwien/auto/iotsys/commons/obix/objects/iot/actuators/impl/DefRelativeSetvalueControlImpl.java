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
import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Uri;

public class DefRelativeSetvalueControlImpl extends ActuatorImpl implements
		DefRelativeSetvalueControl {

	// relative setvalue control
	protected Obj relativSetvalueControl = new Obj();
	protected Bool relativSetvalueControlDimmingDirectionValue = new Bool(false);
	protected Int relativSetvalueControlDimmingStepValue = new Int();

	public DefRelativeSetvalueControlImpl() {

		setIs(new Contract(DefRelativeSetvalueControl.CONTRACT));

		relativSetvalueControlDimmingDirectionValue.setWritable(true);
		Uri relativSetvalueControlDimmingDirectionValueUri = new Uri(
				DefRelativeSetvalueControl.RELATIV_SETVALUE_CONTROL_DIMMING_DIRECTION_CONTRACT_HREF);
		relativSetvalueControlDimmingDirectionValue
				.setHref(relativSetvalueControlDimmingDirectionValueUri);
		relativSetvalueControlDimmingDirectionValue
				.setName(DefRelativeSetvalueControl.RELATIV_SETVALUE_CONTROL_DIMMING_DIRECTION_CONTRACT_NAME);
		add(relativSetvalueControlDimmingDirectionValue);

		relativSetvalueControlDimmingStepValue.setWritable(true);
		Uri relativSetvalueControlDimmingStepValueUri = new Uri(
				DefRelativeSetvalueControl.RELATIV_SETVALUE_CONTROL_DIMMING_STEP_CONTRACT_HREF);
		relativSetvalueControlDimmingStepValue
				.setHref(relativSetvalueControlDimmingStepValueUri);
		relativSetvalueControlDimmingStepValue
				.setName(DefRelativeSetvalueControl.RELATIV_SETVALUE_CONTROL_DIMMING_STEP_CONTRACT_NAME);
		relativSetvalueControlDimmingStepValue
				.setUnit(new Uri(
						DefRelativeSetvalueControl.RELATIV_SETVALUE_CONTROL_DIMMING_STEP_CONTRACT_UNIT));
		add(relativSetvalueControlDimmingStepValue);

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
