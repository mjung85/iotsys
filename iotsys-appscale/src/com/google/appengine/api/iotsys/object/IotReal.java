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
import obix.Real;
import obix.Uri;

import com.google.apphosting.api.IotsysServicePb;

public class IotReal extends IotNumericDatapoint<Double> {

	private static final long serialVersionUID = 1L;

	private Integer precision;

	
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	public Integer getPrecision() {
		return precision;
	}
	
	@Override
	protected boolean merge(IotObject object) {
		if(object instanceof IotReal) {
			IotReal other = (IotReal) object;
			this.setValue(other.getValue());
			this.setMin(other.getMin());
			this.setMax(other.getMax());
			this.setPrecision(other.getPrecision());
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public void initialize(Obj obj) {
		if(!obj.isReal()) {
			throw new IllegalArgumentException("obj is not of type Real");
		}
		super.initialize(obj);
		Real real = (Real) obj;
		this.setValue(real.get());
		
		this.setMin(real.getMin());
		this.setMax(real.getMax());
		if(real.getUnit() != null) {
			this.setUnit(real.getUnit().get());
		}
		this.setPrecision(real.getPrecision());
	}
	
	@Override
	public void initialize(IotsysServicePb.IotObjectProto protobuf) {
		super.initialize(protobuf);
		if(protobuf.hasRealValue()) {
			this.setValue(protobuf.getRealValue());
		}
		this.setMin(protobuf.getRealMin());
		this.setMax(protobuf.getRealMax());
		if(protobuf.hasNumericUnit()) {
			this.setUnit(protobuf.getNumericUnit());
		}
		if(protobuf.hasPrecision()) {
			this.setPrecision(protobuf.getPrecision());
		}
	}
	
	@Override
	public void writeToObj(Obj obj) {
		super.writeToObj(obj);
		if(!obj.isReal()) {
			return;
		}
		Real real = (Real) obj;
		if(this.getValue() != null) {
			real.set(this.getValue());
		}
		if(this.getMin() != null) {
			real.setMin(this.getMin());
		}
		if(this.getMax() != null) {
			real.setMax(this.getMax());
		}
		if(this.getUnit() != null) {
			real.setUnit(new Uri(this.getUnit()));
		}
		if(this.getPrecision() != null) {
			real.setPrecision(this.getPrecision());
		}
	}
	
	@Override
	public void writeToProtobuf(IotsysServicePb.IotObjectProto.Builder protoBuilder) {
		super.writeToProtobuf(protoBuilder);
		if(this.getValue() != null) {
			protoBuilder.setRealValue(this.getValue());
		}
		if(this.getMin() != null) {
			protoBuilder.setRealMin(this.getMin());
		}
		if(this.getMax() != null) {
			protoBuilder.setRealMax(this.getMax());
		}
		if(this.getUnit() != null) {
			protoBuilder.setNumericUnit(this.getUnit());
		}
		if(this.getPrecision() != null) {
			protoBuilder.setPrecision(this.getPrecision());
		}
	}
	
}
