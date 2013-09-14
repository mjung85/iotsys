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

import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.contracts.impl.RangeImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumLanguage;

public class EnumLanguageImpl extends RangeImpl implements EnumLanguage
{
	public EnumLanguageImpl()
	{
		super(new Uri(EnumLanguage.HREF));
	}

	protected void initValues()
	{
		getElements().add(new EnumElement(EnumLanguage.KEY_DE_DE, "German"));
		getElements().add(new EnumElement(EnumLanguage.KEY_EN_EN, "English"));
		getElements().add(new EnumElement(EnumLanguage.KEY_EN_US, "English (United States)"));
		getElements().add(new EnumElement(EnumLanguage.KEY_ES_ES, "Spanish"));
		getElements().add(new EnumElement(EnumLanguage.KEY_IT_IT, "Italian"));
		getElements().add(new EnumElement(EnumLanguage.KEY_FR_FR, "French"));
		getElements().add(new EnumElement(EnumLanguage.KEY_ID_ID, "Indonesian"));
		getElements().add(new EnumElement(EnumLanguage.KEY_NB_NO, "Norwegian"));
		getElements().add(new EnumElement(EnumLanguage.KEY_SV_SE, "Swedish"));
		getElements().add(new EnumElement(EnumLanguage.KEY_DA_DK, "Danish"));
		getElements().add(new EnumElement(EnumLanguage.KEY_NL_NL, "Dutch"));
		getElements().add(new EnumElement(EnumLanguage.KEY_EL_GR, "Greek"));
		getElements().add(new EnumElement(EnumLanguage.KEY_RU_RU, "Russian"));
	}
}
