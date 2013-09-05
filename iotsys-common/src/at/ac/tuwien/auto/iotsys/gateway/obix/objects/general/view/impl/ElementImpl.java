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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.impl;

import obix.Contract;
import obix.IObj;
import obix.List;
import obix.Obj;
import obix.Ref;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.util.UriEncoder;

public abstract class ElementImpl extends Obj implements IObj
{
	private List elements = null;
	private List instances = null;
	private int instanceCount = 0;

	public ElementImpl(String name, String displayName, String display, Contract is)
	{
		this.setName(name);
		this.setDisplay(display);
		this.setDisplayName(displayName);
		this.setIs(is);
		this.setHref(new Uri(UriEncoder.getEscapedUri(displayName)));
	}

	protected void addElement(ElementImpl element)
	{
		if (this.elements == null)
		{
			this.elements = new List();
			this.initElements(elements);
			this.add(this.elements);
		}
		this.elements.add(element);
	}

	protected Obj addInstance(Obj obj, Contract is)
	{
		if (instances == null)
		{
			this.instances = new List();
			this.initInstances(instances);
			this.add(this.instances);
		}

		Obj instance = new Obj();
		instance.setName(obj.getName());
		instance.setIs(is);
		instance.setHref(new Uri(String.valueOf(++instanceCount)));

		Ref ref = obj.getReference(true);
		ref.setName("reference", true);

		instance.add(ref);

		this.instances.add(instance);
		
		return instance;
	}

	public abstract void initElements(List elements);

	public abstract void initInstances(List instances);
}
