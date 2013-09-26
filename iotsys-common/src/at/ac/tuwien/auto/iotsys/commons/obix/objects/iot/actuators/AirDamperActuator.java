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

import obix.Int;

public interface AirDamperActuator extends Actuator{
	
	public static final String CONTRACT="iot:AirDamperActuator";
	
	public static final String ACT_POS_SETP_FRESH_AIR_CONTRACT_NAME="actPosSetpFreshAirValue";
	public static final String ACT_POS_SETP_FRESH_AIR_CONTRACT_HREF="actPosSetpFreshAirValue";
	public static final String ACT_POS_SETP_FRESH_AIR_CONTRACT_UNIT="obix:units/percent";
	public static final String ACT_POS_SETP_FRESH_AIR_CONTRACT = "<int name='"+ACT_POS_SETP_FRESH_AIR_CONTRACT_NAME+"' href='"+ACT_POS_SETP_FRESH_AIR_CONTRACT_HREF+"' val='0'/>";
	//Funktion muss genau so heissen wie FAN_SPEED_SETPOINT_CONTRACT_NAME !!!
	public Int actPosSetpFreshAirValue();

	public static final String ACT_POS_SETP_SUPPLY_AIR_CONTRACT_NAME="actPosSetpSupplyAirValue";
	public static final String ACT_POS_SETP_SUPPLY_AIR_CONTRACT_HREF="actPosSetpSupplyAirValue";
	public static final String ACT_POS_SETP_SUPPLY_AIR_CONTRACT_UNIT="obix:units/percent";
	public static final String ACT_POS_SETP_SUPPLY_AIR_CONTRACT = "<int name='"+ACT_POS_SETP_SUPPLY_AIR_CONTRACT_NAME+"' href='"+ACT_POS_SETP_SUPPLY_AIR_CONTRACT_HREF+"' val='0'/>";
	public Int actPosSetpSupplyAirValue();
	
	
	public static final String ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_NAME="actPosSetpDischargeAirValue";
	public static final String ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_HREF="actPosSetpDischargeAirValue";
	public static final String ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_UNIT="obix:units/percent";
	public static final String ACT_POS_SETP_DISCHARGE_AIR_CONTRACT = "<int name='"+ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_NAME+"' href='"+ACT_POS_SETP_DISCHARGE_AIR_CONTRACT_HREF+"' val='0'/>";
	public Int actPosSetpDischargeAirValue();
	
	public static final String ACT_POS_SETP_EXTRACT_AIR_CONTRACT_NAME="actPosSetpExtractAirValue";
	public static final String ACT_POS_SETP_EXTRACT_AIR_CONTRACT_HREF="actPosSetpExtractAirValue";
	public static final String ACT_POS_SETP_EXTRACT_AIR_CONTRACT_UNIT="obix:units/percent";
	public static final String ACT_POS_SETP_EXTRACT_AIR_CONTRACT = "<int name='"+ACT_POS_SETP_EXTRACT_AIR_CONTRACT_NAME+"' href='"+ACT_POS_SETP_EXTRACT_AIR_CONTRACT_HREF+"' val='0'/>";
	public Int actPosSetpExtractAirValue();
	
}
