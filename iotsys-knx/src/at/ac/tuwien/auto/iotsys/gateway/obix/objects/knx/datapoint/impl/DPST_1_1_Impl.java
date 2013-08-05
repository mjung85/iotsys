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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.DPST_1_1;

public class DPST_1_1_Impl extends DataPointImpl implements DPST_1_1
{
	private static final Logger log = Logger.getLogger(DPST_1_1_Impl.class.getName());

	protected Bool value = new Bool();

	public DPST_1_1_Impl()
	{
		value.setName(DPST_1_1.VALUE_NAME);
		value.setHref(new Uri(DPST_1_1.VALUE_HREF));
		value.setWritable(true);

		this.setIs(new Contract(DPST_1_1.CONTRACT));
		this.add(value);

		this.function.set("On / Off");
		this.unit.set("on/off");
	}

	@Override
	public Bool value()
	{
		return value;
	}

	@Override
	public void writeObject(Obj input)
	{
		if (input instanceof DPST_1_1)
		{
			DPST_1_1 in = (DPST_1_1) input;
			log.info("Writing on data point.");
			this.value.set(in.value().get());
		}
		else if (input instanceof Bool)
		{
			this.value.set(((Bool) input).get());
		}
		else if (input instanceof Real)
		{
			this.value.set(((Real) input).get());
		}
		else if (input instanceof Int)
		{
			this.value.set(((Int) input).get());
		}
	}

}
