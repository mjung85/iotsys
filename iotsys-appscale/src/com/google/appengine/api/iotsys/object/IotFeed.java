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

package com.google.appengine.api.iotsys.object;

import obix.Contract;
import obix.Feed;
import obix.Obj;

import com.google.apphosting.api.IotsysServicePb;


public class IotFeed extends IotObject {

	private static final long serialVersionUID = 1L;

	private String inType;
	private String elementType;

	public void setIn(String type) {
		this.inType = type;
	}

	public String getIn() {
		return inType;
	}

	public void setElementType(String type) {
		this.elementType = type;
	}

	public String getElementType() {
		return elementType;
	}
	
	@Override
	protected boolean merge(IotObject object) {
		if(object instanceof IotFeed) {
			IotFeed other = (IotFeed) object;
			this.setIn(other.getIn());
			this.setElementType(other.getElementType());
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public void initialize(Obj obj) {
		if(!obj.isFeed()) {
			throw new IllegalArgumentException("obj is not of type Feed");
		}
		super.initialize(obj);
		Feed feed = (Feed) obj;
		if(feed.getIn() != null) {
			this.setIn(feed.getIn().toString());
		}
		if(feed.getOf() != null) {
			this.setElementType(feed.getOf().toString());
		}
	}
	
	@Override
	public void initialize(IotsysServicePb.IotObjectProto protobuf) {
		super.initialize(protobuf);
		if(protobuf.hasStringValue()) {
			this.setIn(protobuf.getStringValue());
		}
		if(protobuf.hasElementType()) {
			this.setElementType(protobuf.getElementType());
		}
	}
	
	@Override
	public void writeToObj(Obj obj) {
		super.writeToObj(obj);
		if(!obj.isFeed()) {
			return;
		}
		Feed feed = (Feed) obj;
		if(this.getIn() != null) {
			feed.setIn(new Contract(this.getIn()));
		}
		if(this.getElementType() != null) {
			feed.setOf(new Contract(this.getElementType()));
		}
	}
	
	@Override
	public void writeToProtobuf(IotsysServicePb.IotObjectProto.Builder protoBuilder) {
		super.writeToProtobuf(protoBuilder);
		if(this.getIn() != null) {
			protoBuilder.setStringValue(this.getIn());
		}
		if(this.getElementType() != null) {
			protoBuilder.setElementType(this.getElementType());
		}
	}
	
}
