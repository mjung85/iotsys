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

import java.util.ArrayList;

import obix.Contract;
import obix.List;
import obix.Uri;
import obix.contracts.Range;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.language.impl.MultilingualImpl;

public abstract class RangeImpl extends List implements Range
{
	protected class EnumElement extends MultilingualImpl
	{
		private String key;

		public EnumElement(String key, String displayName)
		{
			super();
			
			this.key = key;

			this.setName(key);
			this.setHref(new Uri(key));
			this.setDisplayName(displayName);
		}

		public String getKey()
		{
			return key;
		}
	}

	private ArrayList<EnumElement> elements;

	public RangeImpl(Uri href)
	{
		this.setHref(href);
		this.setIs(new Contract(Range.CONTRACT));
		this.setHidden(true);

		this.elements = new ArrayList<EnumElement>();

		this.initValues();

		for (EnumElement e : elements)
		{
			this.add(e);
		}
	}

	protected abstract void initValues();

	protected ArrayList<EnumElement> getElements()
	{
		return elements;
	}

	public String getKey(String name)
	{
		for (EnumElement e : elements)
		{
			if (e.getName().toLowerCase().equals(name.toLowerCase()))
			{
				return e.getKey();
			}
		}
		return null;
	}

	public String getName(String key)
	{
		for (EnumElement e : elements)
		{
			if (e.getKey().toLowerCase().equals(key.toLowerCase()))
			{
				return e.getName();
			}
		}
		return null;
	}
}
