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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sensors;

import obix.IObj;
import obix.Real;

public interface SmartMeter extends IObj {
	public static final String CONTRACT="iot:SmartMeter";
	
	public static final String POWER_CONTRACT_NAME="power";
	public static final String POWER_CONTRACT_HREF="power";
	public static final String POWER_CONTRACT_UNIT="obix:units/watt";
	public static final String POWER_CONTRACT = "<int name='"+POWER_CONTRACT_NAME+"' href='"+POWER_CONTRACT_HREF+"' val='0'/>";
	public Real powerValue();
	
	public static final String VOLUME_CONTRACT_NAME="volume";
	public static final String VOLUME_CONTRACT_HREF="volume";
	public static final String VOLUME_CONTRACT_UNIT="obix:units/liter";
	public static final String VOLUME_CONTRACT = "<int name='"+VOLUME_CONTRACT_NAME+"' href='"+VOLUME_CONTRACT_HREF+"' val='0'/>";
	public Real volumeValue();
	
	public static final String ENERGY_CONTRACT_NAME="energy";
	public static final String ENERGY_CONTRACT_HREF="energy";
	public static final String ENERGY_CONTRACT_UNIT="obix:units/kilowatthours";
	public static final String ENERGY_CONTRACT = "<int name='"+ENERGY_CONTRACT_NAME+"' href='"+ ENERGY_CONTRACT_HREF +"' val='0'/>";
	public Real energyValue();
}
