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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.general.internals.impl;

import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.encoding.impl.EncodingsImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.impl.EnumsImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.internals.Internals;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.parameter.impl.ParametersImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.unit.impl.UnitsImpl;

public class InternalsImpl implements Internals
{
	private EnumsImpl enums = null;
	private UnitsImpl units = null;
	private ParametersImpl parameters = null;
	private EncodingsImpl encodings = null;

	public InternalsImpl(ObjectBroker objectBroker)
	{
		// Enumerations
		enums = EnumsImpl.getInstance();

		objectBroker.addObj(enums, true);

		// Units
		units = UnitsImpl.getInstance();
		objectBroker.addObj(units, true);

		// Parameters
		parameters = ParametersImpl.getInstance();
		objectBroker.addObj(parameters, true);

		// Encodings
		encodings = EncodingsImpl.getInstance();
		objectBroker.addObj(encodings, true);
	}

	@Override
	public EnumsImpl enums()
	{
		return enums;
	}

	@Override
	public UnitsImpl units()
	{
		return units;
	}

	@Override
	public EncodingsImpl encodings()
	{
		return encodings;
	}

	@Override
	public ParametersImpl parameters()
	{
		return parameters;
	}
}
