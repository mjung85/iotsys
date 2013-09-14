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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.network.impl;

import obix.Contract;
import obix.Enum;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.entity.impl.EntitiesImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumStandard;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.network.Network;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.impl.ViewBuildingImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.impl.ViewDomainsImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.impl.ViewFunctionalImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.impl.ViewTopologyImpl;
import at.ac.tuwien.auto.iotsys.gateway.util.UriEncoder;

public class NetworkImpl extends Obj implements Network
{
	protected Enum standard;

	protected EntitiesImpl entities;
	protected ViewFunctionalImpl functional;
	protected ViewBuildingImpl building;
	protected ViewTopologyImpl topology;
	protected ViewDomainsImpl domains;

	public NetworkImpl(String name, String displayName, String display, String standard)
	{		
		this.setName(name);
		this.setDisplay(display);
		this.setDisplayName(displayName);
		this.setIs(new Contract(Network.CONTRACT));
		this.setHref(new Uri(UriEncoder.getEscapedUri(displayName)));
		this.setHidden(true);
				
		// Standard
		this.standard = new Enum();
		this.standard.setName("standard");
		this.standard.setHref(new Uri("standard"));
		this.standard.setRange(new Uri(EnumStandard.HREF));
		this.standard.set(standard);
		this.add(this.standard);

		// Views
		this.functional = new ViewFunctionalImpl();
		this.functional.setHidden(true);
		this.add(functional);
		this.add(functional.getReference(false));

		this.topology = new ViewTopologyImpl();
		this.add(topology);
		this.add(topology.getReference(false));

		this.building = new ViewBuildingImpl();
		this.add(building);
		this.add(building.getReference(false));
		
		this.domains = new ViewDomainsImpl();
		this.add(domains);
		this.add(domains.getReference(false));

		// Entities
		this.entities = new EntitiesImpl();
		this.add(entities);
		this.add(entities.getReference(false));
	}

	public ViewFunctionalImpl getFunctional()
	{
		return this.functional;
	}

	public ViewTopologyImpl getTopology()
	{
		return this.topology;
	}

	public ViewBuildingImpl getBuilding()
	{
		return this.building;
	}

	public ViewDomainsImpl getDomains()
	{
		return this.domains;
	}

	public EntitiesImpl getEntities()
	{
		return this.entities;
	}

}
