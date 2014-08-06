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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators;

import obix.Bool;
import obix.Int;

public interface DefRelativeSetvalueControl extends Actuator{
	
	public static final String CONTRACT="iot:DefRelativeSetvalueControl";
	public static final String RELATIV_SETVALUE_CONTROL_CONTRACT_NAME="relativeSetvalueControl";
	public static final String RELATIV_SETVALUE_CONTROL_CONTRACT_HREF="relativeSetvalueControl";
	public static final String RELATIV_SETVALUE_CONTROL_DIMMING_DIRECTION_CONTRACT_NAME="dimmingDirectionlValue";
	public static final String RELATIV_SETVALUE_CONTROL_DIMMING_DIRECTION_CONTRACT_HREF="dimmingDirectionlValue";
	public static final String RELATIV_SETVALUE_CONTROL_DIMMING_STEP_CONTRACT_NAME="dimmingStepValue";
	public static final String RELATIV_SETVALUE_CONTROL_DIMMING_STEP_CONTRACT_HREF="dimmingStepValue";
	public static final String RELATIV_SETVALUE_CONTROL_DIMMING_STEP_CONTRACT_UNIT="obix:units/percent";
	
	public static final String RELATIV_SETVALUE_CONTROL_CONTRACT = "<obj name='"+RELATIV_SETVALUE_CONTROL_CONTRACT_NAME+"' href='"+RELATIV_SETVALUE_CONTROL_CONTRACT_HREF+"'>" +
			"<bool name='"+RELATIV_SETVALUE_CONTROL_DIMMING_DIRECTION_CONTRACT_NAME+"' href='"+RELATIV_SETVALUE_CONTROL_DIMMING_DIRECTION_CONTRACT_HREF+"' val='false'/>" +
			"<int name='"+RELATIV_SETVALUE_CONTROL_DIMMING_STEP_CONTRACT_NAME+"' href='"+RELATIV_SETVALUE_CONTROL_DIMMING_STEP_CONTRACT_HREF+"' val'0'/>" +
		"</obj>";
	
	public Bool relativSetvalueControlDimmingDirectionValue();
	public Int relativSetvalueControlDimmingStepValue();
	
}
