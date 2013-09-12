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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.impl;

import java.util.HashMap;

import obix.Contract;
import obix.IObj;
import obix.List;
import obix.Uri;
import obix.contracts.Range;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.contracts.impl.RangeImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumEnabled;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumLanguage;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumPart;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumPriority;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumStandard;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumTranslation;

public class EnumsImpl extends List implements IObj
{
	private HashMap<String, RangeImpl> enums;
	
	public EnumsImpl()
	{
		this.setName("enums");
		this.setOf(new Contract(Range.CONTRACT));
		this.setHref(new Uri("/enums"));

		// Create enumerations
		enums = new HashMap<String, RangeImpl>();

		enums.put(EnumConnector.HREF, new EnumConnectorImpl());
		enums.put(EnumEnabled.HREF, new EnumEnabledImpl());
		enums.put(EnumLanguage.HREF, new EnumLanguageImpl());
		enums.put(EnumPart.HREF, new EnumPartImpl());
		enums.put(EnumPriority.HREF, new EnumPriorityImpl());
		enums.put(EnumStandard.HREF, new EnumStandardImpl());
		enums.put(EnumTranslation.HREF, new EnumTranslationImpl());
		
		// Add enumerations
		for(RangeImpl e: enums.values())
		{
			e.setHref(e.getRelativePath());
			
			this.add(e);
			this.add(e.getReference(false));
		}
	}
	
	public RangeImpl getEnum(String href)
	{
		return enums.get(href);
	}
}
