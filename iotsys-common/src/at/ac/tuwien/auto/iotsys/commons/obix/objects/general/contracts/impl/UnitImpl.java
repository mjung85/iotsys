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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.general.contracts.impl;

import obix.Contract;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.Uri;
import obix.contracts.Dimension;
import obix.contracts.Unit;

public class UnitImpl extends Obj implements Unit
{
	private Str symbol;
	private DimensionImpl dimension;
	private Real scale;
	private Real offset;

	public UnitImpl(String name, String displayName, Uri href, String symbol, double scale, double offset, DimensionImpl dimension)
	{
		this.setName(name);
		this.setDisplayName(displayName);
		this.setIs(new Contract(Unit.CONTRACT));
		this.setHref(href);
		this.setHidden(true);

		this.symbol = new Str("symbol", symbol);
		this.add(this.symbol);

		if (scale != 1)
		{
			this.scale = new Real("scale", scale);
			this.add(this.scale);
		}

		if (offset != 0)
		{
			this.offset = new Real("offset", offset);
			this.add(this.offset);
		}

		this.dimension = dimension;
		if (dimension.kg() != null || dimension.K() != null || dimension.mol() != null || dimension.cd() != null || dimension.m() != null || dimension.sec() != null || dimension.A() != null)
		{
			this.add(this.dimension);
		}
	}

	@Override
	public Str symbol()
	{
		return symbol;
	}

	@Override
	public Dimension dimension()
	{
		return dimension;
	}

	@Override
	public Real scale()
	{
		return scale;
	}

	@Override
	public Real offset()
	{
		return offset;
	}
}
