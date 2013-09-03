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
import obix.List;
import obix.Obj;
import obix.Ref;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.entity.impl.EntityImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.Domain;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.Part;
import at.ac.tuwien.auto.iotsys.gateway.util.UriEncoder;

public class DomainImpl extends Obj implements Part
{
	private List domains = null;
	private List instances = null;
	private int instanceCount = 0;

	public DomainImpl(String name, String displayName, String display)
	{
		this.setName(name);
		this.setDisplay(display);
		this.setDisplayName(displayName);
		this.setHref(new Uri(UriEncoder.getEscapedUri(displayName)));
		this.setIs(new Contract(Domain.CONTRACT));
	}

	public void addDomain(DomainImpl domain)
	{
		if (domains == null)
		{
			this.domains = new List("domains", new Contract(Part.CONTRACT));
			this.domains.setHref(new Uri("domains"));
			this.add(this.domains);
		}
		this.domains.add(domain);
	}

	public void addInstance(EntityImpl entity)
	{
		if (instances == null)
		{
			this.instances = new List("instances", new Contract(Domain.CONTRACT_INSTANCE));
			this.instances.setHref(new Uri("instances"));
			this.add(this.instances);
		}

		Obj instance = new Obj();
		instance.setName(entity.getName());
		instance.setHref(new Uri(String.valueOf(++instanceCount)));

		Ref ref = entity.getReference(true);
		ref.setName("reference", true);

		instance.add(ref);

		this.instances.add(instance);
	}
}
