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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.general.language.impl;

import obix.Contract;
import obix.Enum;
import obix.Obj;
import obix.Str;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumLanguage;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumTranslation;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.language.Translation;

public class TranslationImpl extends Obj implements Translation
{
	private Enum language;
	private Enum attribute;
	private Str value;

	public TranslationImpl(String language, String attribute, String value)
	{
		this.setIs(new Contract(Translation.CONTRACT));

		// Language
		this.language = new Enum();
		this.language.setName("language");
		this.language.setHref(new Uri("language"));
		this.language.setRange(new Uri(EnumLanguage.HREF));
		this.language.set(language);
		this.add(this.language);

		// Attribute
		this.attribute = new Enum();
		this.attribute.setName("attribute");
		this.attribute.setHref(new Uri("attribute"));
		this.attribute.setRange(new Uri(EnumTranslation.HREF));
		this.attribute.set(attribute);
		this.add(this.attribute);

		// Attribute
		this.value = new Str();
		this.value.setName("value");
		this.value.setHref(new Uri("value"));
		this.value.set(value);
		this.add(this.value);
	}

	public String getLanguage()
	{
		return language.get();
	}

	public String getAttribute()
	{
		return attribute.get();
	}

	public String getValue()
	{
		return value.get();
	}
}
