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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators;

import obix.Bool;
import obix.Int;

/**
 * Provides the interface for a Fan Speed Actuator.
 */
public interface FanSpeedActuator extends Actuator{
	public static final String CONTRACT="iot:FanSpeedActuator";
	
	public static final String FAN_SPEED_SETPOINT_CONTRACT_NAME="fanSpeedSetpointValue";
	public static final String FAN_SPEED_SETPOINT_CONTRACT_HREF="fanSpeedSetpoint";
	public static final String FAN_SPEED_SETPOINT_CONTRACT_UNIT="obix:units/percent";
	public static final String FAN_SPEED_SETPOINT_CONTRACT = "<int name='"+FAN_SPEED_SETPOINT_CONTRACT_NAME+"' href='"+FAN_SPEED_SETPOINT_CONTRACT_HREF+"' val='0'/>";
	
	public static final String ENABLED_CONTRACT_NAME="enabled";
	public static final String ENBALED_CONTRACT_HREF="enabled";
	public static final String ENABLED_CONTRACT = "<bool name='" + ENABLED_CONTRACT_NAME + "' href='" + ENBALED_CONTRACT_HREF + "' val='false'/>";
		
	public Int fanSpeedSetpointValue();
	public Bool enabled();
}
