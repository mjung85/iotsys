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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.DPT_12;

public abstract class DPT_12_Impl extends DatapointImpl implements DPT_12
{
	private static final Logger log = Logger.getLogger(DPT_9_Impl.class.getName());

	private Int value = new Int();

	public DPT_12_Impl(String name, String displayName, String display, boolean writable, boolean readable)
	{
		super(name, displayName, display, writable, readable);

		this.addIs(new Contract(DPT_12.CONTRACT));

		this.value.setName("value");
		this.value.setHref(new Uri("value"));
		this.value.setWritable(writable);
		this.value.setReadable(readable);
		this.value.setNull(true);
		this.add(value);
	}

	@Override
	public boolean isValueWritable()
	{
		return value.isWritable();
	}

	@Override
	public boolean isValueReadable()
	{
		return value.isReadable();
	}

	@Override
	public Int value()
	{
		return value;
	}

	@Override
	public void writeObject(Obj input)
	{
		if (this.value.isWritable())
		{
			if (input instanceof DPT_12)
			{
				DPT_12 in = (DPT_12) input;
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
}
