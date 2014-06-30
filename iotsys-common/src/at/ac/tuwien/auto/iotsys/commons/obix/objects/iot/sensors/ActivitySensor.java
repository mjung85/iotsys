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

import obix.Bool;
import obix.Str;

/**
 * Provides the interface for a ActivitySensor (accelerometer).
 */
public interface ActivitySensor extends Sensor{
	public static final String CONTRACT="iot:ActivitySensor";
	
	public static final String ACTIVE_CONTRACT_NAME="active";
	public static final String ACTIVE_CONTRACT_HREF="active";
	public static final String ACTIVE_CONTRACT = "<bool name='"+ ACTIVE_CONTRACT_NAME +"' href='"+ ACTIVE_CONTRACT_HREF +"' val='false'/>";	
	
	public Bool activeValue();
	
	public static final String FREEFALL_CONTRACT_NAME="freefall";
	public static final String FREEFALL_CONTRACT_HREF="freefall";
	public static final String FREEFALL_CONTRACT = "<bool name='"+ FREEFALL_CONTRACT_NAME +"' href='"+ FREEFALL_CONTRACT_HREF +"' val='false'/>";	
	
	public Bool freefallValue();
}
