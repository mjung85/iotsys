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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.impl;

import obix.Contract;
import obix.Int;
import obix.List;
import obix.Obj;
import obix.Str;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.entity.impl.EntityImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.Area;

public class AreaImpl extends ElementImpl implements Area
{
	public AreaImpl(String name, String displayName, String display, long address, String mediaType)
	{
		super(name, displayName, display, new Contract(Area.CONTRACT));

		Int adr = new Int();
		adr.setName("address");
		adr.setHref(new Uri("address"));
		adr.setMin(0);
		adr.set(address);
		this.add(adr);

		if (mediaType != null)
		{
			Str mt = new Str();
			mt.setName("mediaType");
			mt.setHref(new Uri("mediaType"));
			mt.set(mediaType);
			this.add(mt);
		}
	}

	@Override
	public void initElements(List elements)
	{
		elements.setName("areas");
		elements.setHref(new Uri("areas"));
		elements.setOf(new Contract(Area.CONTRACT));
	}

	@Override
	public void initInstances(List instances)
	{
		instances.setName("instances");
		instances.setHref(new Uri("instances"));
		instances.setOf(new Contract(Area.CONTRACT_INSTANCE));
	}

	public void addArea(AreaImpl area)
	{
		this.addElement(area);
	}

	public Obj addInstance(EntityImpl entity, long address)
	{
		Obj instance = addInstance(entity, new Contract(Area.CONTRACT_INSTANCE));

		Int adr = new Int();
		adr.setName("address");
		adr.setHref(new Uri("address"));
		adr.setMin(0);
		adr.set(address);
		instance.add(adr);

		return instance;
	}
}
