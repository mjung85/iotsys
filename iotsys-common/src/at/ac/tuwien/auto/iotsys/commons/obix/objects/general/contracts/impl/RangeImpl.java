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

import obix.Bool;
import obix.Contract;
import obix.IObj;
import obix.Int;
import obix.List;
import obix.Obj;
import obix.Uri;
import obix.contracts.Range;
import at.ac.tuwien.auto.iotsys.commons.util.UriEncoder;

public abstract class RangeImpl extends List implements Range
{
	public interface RangeElement extends IObj
	{
	}

	protected class ObjElement extends Obj implements RangeElement
	{
		public ObjElement(String key)
		{
			this(key, null);
		}

		public ObjElement(String key, String displayName)
		{
			this.setName(key);
			this.setHref(new Uri(UriEncoder.getEscapedUri(key)));

			if (displayName != null)
				this.setDisplayName(displayName);
		}
	}

	public class BoolElement extends Bool implements RangeElement
	{
		public BoolElement(String key, String displayName, boolean value)
		{
			this.setName(key);
			this.setHref(new Uri(UriEncoder.getEscapedUri(key)));

			if (displayName != null)
				this.setDisplayName(displayName);

			this.set(value);
		}
	}

	public class IntElement extends Int implements RangeElement
	{
		public IntElement(String key, int value)
		{
			this(key, null, value);
		}

		public IntElement(String key, String displayName, int value)
		{
			this.setName(key);
			this.setHref(new Uri(UriEncoder.getEscapedUri(key)));

			if (displayName != null)
				this.setDisplayName(displayName);

			this.set(value);
		}
	}

	private ArrayList<RangeElement> elements;

	public RangeImpl(Uri href)
	{
		this.setHref(href);
		this.setIs(new Contract(Range.CONTRACT));
		this.setHidden(true);

		this.elements = new ArrayList<RangeElement>();

		this.initValues();

		for (RangeElement e : elements)
		{
			if (e instanceof Obj)
			{
				this.add((Obj) e);
			}
		}
	}

	protected abstract void initValues();

	protected void addElement(RangeElement element)
	{
		elements.add(element);
	}

	public String getKey(String name)
	{
		// search key by name
		for (RangeElement e : elements)
		{
			if ((e.getDisplayName() != null && e.getDisplayName().toLowerCase().equals(name.toLowerCase())) || (e.getDisplayName() == null && e.getName().toLowerCase().equals(name.toLowerCase())))
			{
				return e.getName();
			}
		}

		// search key in list of keys
		for (RangeElement e : elements)
		{
			if (e.getName().toLowerCase().equals(name.toLowerCase()))
			{
				return e.getName();
			}
		}

		return null;
	}

	public String getName(String key)
	{
		for (RangeElement e : elements)
		{
			if (e.getName().toLowerCase().equals(key.toLowerCase()))
			{
				if (e.getDisplayName() != null)
					return e.getDisplayName();
				return e.getName();
			}
		}
		return null;
	}

	public boolean getBool(String key)
	{
		for (RangeElement e : elements)
		{
			if (e instanceof BoolElement)
			{
				BoolElement b = (BoolElement) e;

				if (e.getName().toLowerCase().equals(key.toLowerCase()))
				{
					return b.get();
				}
			}
		}
		return false;
	}

	public long getInt(String key)
	{
		for (RangeElement e : elements)
		{
			if (e instanceof IntElement)
			{
				IntElement i = (IntElement) e;

				if (e.getName().toLowerCase().equals(key.toLowerCase()))
				{
					return i.get();
				}
			}
		}
		return 0;
	}
}
