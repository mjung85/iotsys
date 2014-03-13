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

package com.google.appengine.api.iotsys.dev.comm;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.iotsys.dev.comm.obix.ObixBinaryTransportFormat;
import com.google.appengine.api.iotsys.dev.comm.obix.ObixPlainTextTransportFormat;

public class FormatRegistry {

	private static final Map<Integer, Class<? extends TransportFormat>> formatClassForIdentifier;

	static {
		formatClassForIdentifier = new HashMap<Integer, Class<? extends TransportFormat>>();
		formatClassForIdentifier.put(ObixPlainTextTransportFormat.CONTENT_TYPE,
				ObixPlainTextTransportFormat.class);
		formatClassForIdentifier.put(ObixBinaryTransportFormat.CONTENT_TYPE,
				ObixBinaryTransportFormat.class);
		formatClassForIdentifier.put(ApplicationLinkFormat.CONTENT_TYPE,
				ApplicationLinkFormat.class);
	}
	
	public static synchronized TransportFormat getFormat(int format)
			throws InstantiationException, IllegalAccessException {
		if (!formatClassForIdentifier.containsKey(format)) {
			throw new InstantiationException("specified format not found: " + format);
		}
		return formatClassForIdentifier.get(format).newInstance();
	}
				
}
