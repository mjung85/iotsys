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

import obix.Obj;
import obix.Uri;

import com.google.apphosting.api.IotsysServicePb;


public class IotEnum extends IotDatapoint<String> {

	private static final long serialVersionUID = 1L;

	public String rangeUri;
	
	public void setRangeUri(String uri) {
		this.rangeUri = uri;
	}

	public String getRangeUri() {
		return rangeUri;
	}
	
	@Override
	protected boolean merge(IotObject object) {
		if(object instanceof IotEnum) {
			IotEnum other = (IotEnum) object;
			this.setValue(other.getValue());
			this.setRangeUri(other.getRangeUri());
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public void initialize(Obj obj) {
		if(!obj.isEnum()) {
			throw new IllegalArgumentException("obj is not of type Enum");
		}
		super.initialize(obj);
		obix.Enum en = (obix.Enum) obj;
		this.setValue(en.get());
		if(en.getRange() != null) {
			this.setRangeUri(en.getRange().get());
		}
	}
	
	@Override
	public void initialize(IotsysServicePb.IotObjectProto protobuf) {
		super.initialize(protobuf);
		if(protobuf.hasStringValue()) {
			this.setValue(protobuf.getStringValue());
		}
		if(protobuf.hasRangeUri()) {
			this.setRangeUri(protobuf.getRangeUri());
		}
	}
	
	@Override
	public void writeToObj(Obj obj) {
		super.writeToObj(obj);
		if(!obj.isEnum()) {
			return;
		}
		obix.Enum en = (obix.Enum) obj;
		if(this.getValue() != null) {
			en.set(this.getValue());
		}
		if(this.getRangeUri() != null) {
			en.setRange(new Uri(this.getRangeUri()));
		}
	}
	
	@Override
	public void writeToProtobuf(IotsysServicePb.IotObjectProto.Builder protoBuilder) {
		super.writeToProtobuf(protoBuilder);
		if(this.getValue() != null) {
			protoBuilder.setStringValue(this.getValue());
		}
		if(this.getRangeUri() != null) {
			protoBuilder.setRangeUri(this.getRangeUri());
		}
	}
	
}
