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
import obix.Str;

import com.google.apphosting.api.IotsysServicePb;

public class IotString extends IotDatapoint<String> {

	private static final long serialVersionUID = 1L;

	private Integer min;
	private Integer max;


	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMin() {
		return min;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Integer getMax() {
		return max;
	}
	
	@Override
	protected boolean merge(IotObject object) {
		if(object instanceof IotString) {
			IotString other = (IotString) object;
			this.setValue(other.getValue());
			this.setMin(other.getMin());
			this.setMax(other.getMax());
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public void initialize(Obj obj) {
		if(!obj.isStr()) {
			throw new IllegalArgumentException("obj is not of type Str");
		}
		super.initialize(obj);
		Str string = (Str) obj;
		
		this.setValue(string.get());
		
		this.setMin(string.getMin());
		this.setMax(string.getMax());
	}
	
	@Override
	public void initialize(IotsysServicePb.IotObjectProto protobuf) {
		super.initialize(protobuf);
		this.setValue(protobuf.getStringValue());
		this.setMin((int)protobuf.getIntMin());
		this.setMax((int)protobuf.getIntMax());
	}
	
	@Override
	public void writeToObj(Obj obj) {
		super.writeToObj(obj);
		if(!obj.isStr()) {
			return;
		}
		Str string = (Str) obj;
		if(this.getValue() != null) {
			string.set(this.getValue());
		}
		if(this.getMin() != null) {
			string.setMin(this.getMin());
		}
		if(this.getMax() != null) {
			string.setMax(this.getMax());
		}
	}
	
	@Override
	public void writeToProtobuf(IotsysServicePb.IotObjectProto.Builder protoBuilder) {
		super.writeToProtobuf(protoBuilder);
		if(this.getValue() != null) {
			protoBuilder.setStringValue(this.getValue());
		}
		if(this.getMin() != null) {
			protoBuilder.setIntMin(this.getMin());
		}
		if(this.getMax() != null) {
			protoBuilder.setIntMax(this.getMax());
		}
	}
	
}
