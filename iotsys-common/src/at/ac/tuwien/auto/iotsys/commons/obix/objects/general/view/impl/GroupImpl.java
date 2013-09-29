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
import obix.Enum;
import obix.Int;
import obix.List;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl.DatapointImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumConnector;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.view.Group;

public class GroupImpl extends ElementImpl implements Group
{
	private DatapointImpl function = null;

	public GroupImpl(String name, String displayName, String display, long address)
	{
		super(name, displayName, display, new Contract(Group.CONTRACT));

		Int adr = new Int();
		adr.setName("address");
		adr.setHref(new Uri("address"));
		adr.setMin(0);
		adr.set(address);
		this.add(adr);
	}

	@Override
	public void initElements(List elements)
	{
		elements.setName("groups");
		elements.setHref(new Uri("groups"));
		elements.setOf(new Contract(Group.CONTRACT));
	}

	@Override
	public void initInstances(List instances)
	{
		instances.setName("instances");
		instances.setHref(new Uri("instances"));
		instances.setOf(new Contract(Group.CONTRACT_INSTANCE));
	}

	public void addGroup(GroupImpl group)
	{
		this.addElement(group);
	}

	public void addFunction(DatapointImpl function)
	{
		if (this.function == null && function != null)
		{
			this.function = function;

			if (!this.function.getName().equals("function"))
				this.function.setName("function", true);
			
			this.function.setHref(new Uri("function"));
			this.function.setDisplay(null);
			this.function.setDisplayName(null);

			this.add(function);
		}
	}

	public Obj addInstance(DatapointImpl datapoint, String connector)
	{
		Obj instance = addInstance(datapoint, new Contract(Group.CONTRACT_INSTANCE));

		Enum con = new Enum();
		con.setName("connector");
		con.setHref(new Uri("connector"));
		con.setRange(new Uri(EnumConnector.HREF));
		con.set(connector);
		instance.add(con);

		return instance;
	}
}
