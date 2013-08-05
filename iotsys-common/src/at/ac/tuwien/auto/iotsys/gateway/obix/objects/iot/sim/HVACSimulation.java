/*******************************************************************************
 * Copyright (c) 2013, Automation Systems Group, TU Wien.
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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sim;

import obix.Bool;
import obix.IObj;
import obix.Real;
import obix.Str;

public interface HVACSimulation extends IObj {
	public static final String CONTRACT="iot:HVACSimulation";
	
	public static final String seasonContract = "<str name='season' href='season' val='winter'/>";
	public Str season();  
	
	public static final String springImpactContract = "<real name='springImpact' href='springImpact' val='0'/>";
	public Real springImpact(); 
	
	public static final String winterImpactContract = "<real name='winterImpact' href='winterImpact' val='0'/>";
	public Real winterImpact();
	
	public static final String fallImpactContract = "<real name='fallImpact' href='fallImpact' val='0'/>";
	public Real fallImpact();
	
	public static final String summerImpactContract = "<real name='summerImpact' href='summerImpact' val='0'/>";
	public Real summerImpact();
	
	public static final String heatingImpactContract = "<real name='heatingImpact' href='heatingImpact' val='0'/>";
	public Real heatingImpact();
	
	public static final String coolingImpactContract = "<real name='coolingImpact' href='coolingImpact' val='0'/>";
	public Real coolingImpact();
	
	public static final String boilerActiveContract = "<bool name='boilerActive' href='boilerActive' val='false'/>";
	public Bool boilerActive();
	
	public static final String heatPumpActiveContract = "<bool name='heatPumpActive' href='heatPumpActive' val='false'/>";
	public Bool heatPumpActive();
	
	public static final String fanInActiveContract = "<bool name='fanInActive' href='fanInActive' val='false'/>";
	public Bool fanInActive();
	
	public static final String coolerActiveContract = "<bool name='coolerActive' href='coolerActive' val='false'/>";
	public Bool coolerActive();
	
	public static final String coolPumpActiveContract = "<bool name='coolPumpActive' href='coolPumpActive' val='false'/>";
	public Bool coolPumpActive();
	
	public static final String enabledContract = "<bool name='enabled' href='enabled' val='false'/>";
	public Bool enabled();
	
	public static final String tempContract = "<real name='temp' href='temp' val='0'/>";
	public Real temp();

}
