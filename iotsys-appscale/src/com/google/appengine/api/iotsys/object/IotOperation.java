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
import obix.Obj;
import obix.Op;

import com.google.appengine.api.iotsys.IotsysConnectionProxy;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.apphosting.api.IotsysServicePb;

public class IotOperation extends IotObject {

	private static final long serialVersionUID = 1L;

	private String inType;
	private String outType;

	public void setIn(String type) {
		this.inType = type;
	}

	public String getIn() {
		return inType;
	}

	public void setOut(String type) {
		this.outType = type;
	}

	public String getOut() {
		return outType;
	}

	/**
	 * Invokes the operation that this object represents on the gateway by
	 * sending a POST request with this objects communication information (host,
	 * port, href, format, protocol) and the given object as input to the
	 * operation
	 * 
	 * @param input
	 *            Input for the operation
	 * @return Output of the operation
	 * @throws CommunicationException
	 *             If the gateway is unreachable or returns an "obix:Error"
	 *             object
	 */
	public IotObject invoke(IotObject input) throws CommunicationException {
		IotObject response = IotsysConnectionProxy.getInstance()
				.sendPostRequest(this.getHost(), this.getPort(),
						this.getHref(), this.getFormat(), this.getProtocol(),
						input);
		if (response instanceof IotError) {
			throw new CommunicationException(response.getDisplay());
		}
		return response;
	}

	@Override
	protected boolean merge(IotObject object) {
		if (object instanceof IotOperation) {
			IotOperation other = (IotOperation) object;
			this.setIn(other.getIn());
			this.setOut(other.getOut());
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void initialize(Obj obj) {
		if (!obj.isOp()) {
			throw new IllegalArgumentException("obj is not of type Op");
		}
		super.initialize(obj);
		Op op = (Op) obj;
		if (op.getIn() != null) {
			this.setIn(op.getIn().toString());
		}
		if (op.getOut() != null) {
			this.setOut(op.getOut().toString());
		}
	}

	@Override
	public void initialize(IotsysServicePb.IotObjectProto protobuf) {
		super.initialize(protobuf);
		if (protobuf.hasStringValue()) {
			this.setIn(protobuf.getStringValue());
		}
		if (protobuf.hasElementType()) {
			this.setOut(protobuf.getElementType());
		}
	}

	@Override
	public void writeToObj(Obj obj) {
		super.writeToObj(obj);
		if (!obj.isOp()) {
			return;
		}
		Op op = (Op) obj;
		if (this.getIn() != null) {
			op.setIn(new Contract(this.getIn()));
		}
		if (this.getOut() != null) {
			op.setOut(new Contract(this.getOut()));
		}
	}

	@Override
	public void writeToProtobuf(
			IotsysServicePb.IotObjectProto.Builder protoBuilder) {
		super.writeToProtobuf(protoBuilder);
		if (this.getIn() != null) {
			protoBuilder.setStringValue(this.getIn());
		}
		if (this.getOut() != null) {
			protoBuilder.setElementType(this.getOut());
		}
	}

}
