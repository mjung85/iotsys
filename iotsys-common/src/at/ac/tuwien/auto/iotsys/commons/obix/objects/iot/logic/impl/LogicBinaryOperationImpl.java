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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.impl;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.LogicBinaryOperation;

public class LogicBinaryOperationImpl extends Obj implements LogicBinaryOperation {
	protected Bool input1 = new Bool();
	protected Bool input2 = new Bool();
	protected Bool enabled = new Bool();
	protected Bool result = new Bool();
	protected obix.Enum logicOperationType = new obix.Enum();

	public LogicBinaryOperationImpl() {
		setIs(new Contract(LogicBinaryOperation.CONTRACT));

		input1.setName("input1");
		input1.setDisplayName("Input 1");
		input1.setHref(new Uri("input1"));

		input2.setName("input2");
		input2.setDisplayName("Input 2");
		input2.setHref(new Uri("input2"));

		enabled.setName("enabled");
		enabled.setDisplayName("Enabled");
		enabled.setHref(new Uri("enabled"));

		input1.setWritable(true);
		input2.setWritable(true);
		enabled.setWritable(true);

		result.setName("result");
		result.setDisplayName("Result");
		result.setHref(new Uri("result"));

		logicOperationType.setName("logicOperationType");
		logicOperationType.setDisplayName("Logic Operation Type");
		logicOperationType.setRange(new Uri("/enums/logicOperationTypes"));
		logicOperationType.set(LogicBinaryOperation.BIN_OP_AND);
		logicOperationType.setHref(new Uri("logicOperationType"));

		this.add(input1);
		this.add(input2);
		this.add(enabled);
		this.add(result);
		this.add(logicOperationType);
	}

	@Override
	public Bool input1() {
		return input1;
	}

	@Override
	public Bool input2() {
		return input2;
	}

	@Override
	public Bool enabled() {
		return enabled;
	}

	@Override
	public obix.Enum logicOperationType() {
		return logicOperationType;
	}

	@Override
	public void writeObject(Obj input) {
		String resourceUriPath = "";
		if (input.getHref() == null) {
			resourceUriPath = input.getInvokedHref().substring(
					input.getInvokedHref().lastIndexOf('/') + 1);
		} else {
			resourceUriPath = input.getHref().get();
		}

		if (input instanceof LogicBinaryOperation) {
			LogicBinaryOperation in = (LogicBinaryOperation) input;
			this.input1.set(in.input1().get());
			this.input2.set(in.input2().get());
			this.logicOperationType.set(in.logicOperationType().get());
			this.enabled.set(in.enabled().get());
		} else if (input instanceof Real) {

			if ("input1".equals(resourceUriPath)) {
				input1.set(((Real) input).get());
			} else if ("input2".equals(resourceUriPath)) {
				input2.set(((Real) input).get());
			} else if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Real) input).get());
			}

		} else if (input instanceof Bool) {

			if ("input1".equals(resourceUriPath)) {
				input1.set(((Bool) input).get());
			} else if ("input2".equals(resourceUriPath)) {
				input2.set(((Bool) input).get());
			} else if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Bool) input).get());
			}

		} else if (input instanceof Int) {

			if ("input1".equals(resourceUriPath)) {
				input1.set(((Int) input).get());
			} else if ("input2".equals(resourceUriPath)) {
				input2.set(((Int) input).get());
			} else if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Int) input).get());
			}

		}
		else if (input instanceof obix.Enum){
			this.logicOperationType.set( ((obix.Enum) input).get() );
		}

		// perform control logic
		if (enabled.get()) {
			if (LogicBinaryOperation.BIN_OP_AND.equals(logicOperationType.get())) {
				result.set(input1.get() && input2.get());
			} else if (LogicBinaryOperation.BIN_OP_OR.equals(logicOperationType.get())) {
				result.set(input1.get() || input2.get());
			} else if (LogicBinaryOperation.BIN_OP_XOR.equals(logicOperationType.get())) {
				result.set(input1.get() ^ input2.get());
			} else if (LogicBinaryOperation.BIN_OP_NAND.equals(logicOperationType.get())) {
				result.set(!(input1.get() && input2.get()));
			}  else if (LogicBinaryOperation.BIN_OP_NOR.equals(logicOperationType.get())) {
				result.set(!(input1.get() || input2.get()));
			}
		}
	}

	@Override
	public void refreshObject() {
		// Nothing to do for logic objects
	}

	@Override
	public Bool result() {
		return result;
	}
}