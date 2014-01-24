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
import obix.List;
import obix.Obj;

import com.google.apphosting.api.IotsysServicePb;

public class IotList extends IotObject {

	private static final long serialVersionUID = 1L;

	private String elementType = null;
	private Integer minItems = null;
	private Integer maxItems = null;
	
	public void setElementType(String type) {
		this.elementType = type;
	}

	public String getElementType() {
		return elementType;
	}

	public void setMinItems(Integer min) {
		this.minItems = min;
	}

	public Integer getMinItems() {
		return minItems;
	}

	public void setMaxItems(Integer max) {
		this.maxItems = max;
	}

	public Integer getMaxItems() {
		return maxItems;
	}
	
	@Override
	protected boolean merge(IotObject object) {
		if(object instanceof IotList) {
			IotList other = (IotList) object;
			this.setElementType(other.getElementType());
			this.setMinItems(other.getMinItems());
			this.setMaxItems(other.getMaxItems());
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public void initialize(Obj obj) {
		if(!obj.isList()) {
			throw new IllegalArgumentException("obj is not of type List");
		}
		super.initialize(obj);
		List list = (List) obj;
		this.setMinItems(list.getMin());
		this.setMaxItems(list.getMax());
		if(list.getOf() != null) {
			this.setElementType(list.getOf().toString());
		}
	}
	
	@Override
	public void initialize(IotsysServicePb.IotObjectProto protobuf) {
		super.initialize(protobuf);
		if(protobuf.hasIntMin()) {
			this.setMinItems((int)protobuf.getIntMin());
		}
		if(protobuf.hasIntMax()) {
			this.setMaxItems((int)protobuf.getIntMax());
		}
		if(protobuf.hasElementType()) {
			this.setElementType(protobuf.getElementType());
		}
	}
	
	@Override
	public void writeToObj(Obj obj) {
		super.writeToObj(obj);
		if(!obj.isList()) {
			return;
		}
		List list = (List) obj;
		if(this.getMinItems() != null) {
			list.setMin(this.getMinItems());
		}
		if(this.getMaxItems() != null) {
			list.setMax(this.getMaxItems());
		}
		if(this.getElementType() != null) {
			list.setOf(new Contract(this.getElementType()));
		}
	}
	
	@Override
	public void writeToProtobuf(IotsysServicePb.IotObjectProto.Builder protoBuilder) {
		super.writeToProtobuf(protoBuilder);
		if(this.getMinItems() != null) {
			protoBuilder.setIntMin(this.getMinItems());
		}
		if(this.getMaxItems() != null) {
			protoBuilder.setIntMax(this.getMaxItems());
		}
		if(this.getElementType() != null) {
			protoBuilder.setElementType(this.getElementType());
		}
	}
	
}
