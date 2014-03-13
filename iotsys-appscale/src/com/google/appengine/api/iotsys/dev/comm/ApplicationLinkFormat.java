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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;

import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.IotReference;

public class ApplicationLinkFormat extends TransportFormat {

	public static final int CONTENT_TYPE = MediaTypeRegistry.APPLICATION_LINK_FORMAT;
	
	private Pattern pattern;
	
	public ApplicationLinkFormat() {
		super(CONTENT_TYPE);
		pattern = Pattern.compile("<([a-zA-Z0-9:/]+)>;rt=\"([a-zA-Z0-9:]+)\";if=\"obix\"");
	}

	private StringBuilder toPayloadString(IotObject object) {
		StringBuilder builder = new StringBuilder();
		if (object.getHref() == null)
			return builder;
		
		builder.append(String.format("<%s>;rt=\"%s\";if=\"obix\"", object.getHref(), object.getContractString()));
		
		for (IotObject child : object.getAllChildren()) {
			if (child instanceof IotReference) {
				continue;
			}
			builder.append(toPayloadString(child));
		}
		
		return builder;
	}
	
	@Override
	public byte[] toPayloadBytes(IotObject object) {
		String payload = toPayloadString(object).toString();
		return stringToBytes(payload);
	}

	@Override
	public IotObject fromPayloadBytes(byte[] payload) {
		String response = bytesToString(payload);
		Matcher matcher = pattern.matcher(response);
		IotObject root = new IotObject();
		root.setName("parent for link format links");
		IotObject child;
		while(matcher.find()) {
			child = new IotObject();
			child.setHref(matcher.group(1));
			child.setContract(matcher.group(2));
			root.addChild(child);
		}
		return root;
	}

}
