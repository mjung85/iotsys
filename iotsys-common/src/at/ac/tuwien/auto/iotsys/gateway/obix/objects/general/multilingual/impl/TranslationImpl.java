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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.multilingual.impl;

import obix.Contract;
import obix.Enum;
import obix.Obj;
import obix.Str;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumTranslation;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumLanguage;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.multilingual.Translation;

public class TranslationImpl extends Obj implements Translation
{
	private EnumLanguage.Enum language;
	private EnumTranslation.Enum attribute;
	private String value;
	
	public TranslationImpl(EnumLanguage.Enum language, EnumTranslation.Enum attribute, String value)
	{
		this.setIs(new Contract("knx:translation"));

		// Language
		Enum l = new Enum();
		l.setName("language");
		l.setHref(new Uri("language"));
		l.setRange(new Uri(EnumLanguage.HREF));
		l.set(language.getKey());
		this.add(l);

		// Attribute
		Enum a = new Enum();
		a.setName("attribute");
		a.setHref(new Uri("attribute"));
		a.setRange(new Uri(EnumTranslation.HREF));
		a.set(attribute.getKey());
		this.add(a);

		// Attribute
		Str v = new Str();
		v.setName("value");
		v.setHref(new Uri("value"));
		v.set(value);
		this.add(v);
	}

	@Override
	public EnumLanguage.Enum getLanguage()
	{
		return language;
	}

	@Override
	public EnumTranslation.Enum getAttribute()
	{
		return attribute;
	}

	@Override
	public String getValue()
	{
		return value;
	}
}
