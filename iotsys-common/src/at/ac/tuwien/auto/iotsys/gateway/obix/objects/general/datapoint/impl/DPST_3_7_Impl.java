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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.impl;

import obix.Contract;
import obix.Obj;
import obix.Op;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_3_7;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPT_3;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DataPoint;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.language.Multilingual;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.parameter.ParameterDimming;

public class DPST_3_7_Impl extends DPT_3_Impl implements DPST_3_7
{
	private Op increase;
	private Op decrease;

	public DPST_3_7_Impl(String name, String displayName, String display)
	{
		super(name, displayName, display, new Contract(new String[] { DPST_3_7.CONTRACT, DPT_3.CONTRACT, DataPoint.CONTRACT, Multilingual.CONTRACT }));

		// Operation increase
		this.increase = new Op();
		this.increase.setName("increase");
		this.increase.setIn(new Contract(ParameterDimming.HREF));
		this.add(increase);

		// Operation decrease
		this.decrease = new Op();
		this.decrease.setName("decrease");
		this.decrease.setIn(new Contract(ParameterDimming.HREF));
		this.add(decrease);
	}

	@Override
	public void writeObject(Obj input)
	{
		// TODO
	}

}
