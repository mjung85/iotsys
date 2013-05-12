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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.impl;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.Comparator;

public class ComparatorImpl extends Obj implements Comparator {
	protected Real input1 = new Real();
	protected Real input2 = new Real();
	protected Bool enabled = new Bool();
	protected Bool result = new Bool();
	protected obix.Enum compareType = new obix.Enum();

	public ComparatorImpl() {
		setIs(new Contract(Comparator.CONTRACT));

		input1.setName("input1");
		input1.setHref(new Uri("input1"));

		input2.setName("input2");
		input2.setHref(new Uri("input2"));

		enabled.setName("enabled");
		enabled.setHref(new Uri("enabled"));

		input1.setWritable(true);
		input2.setWritable(true);
		enabled.setWritable(true);

		result.setName("result");
		result.setHref(new Uri("result"));

		compareType.setName("compareType");
		compareType.setRange(new Uri("/enums/compareTypes"));
		compareType.set("eq");

		this.add(input1);
		this.add(input2);
		this.add(enabled);
		this.add(result);
		this.add(compareType);

	}

	@Override
	public Real input1() {
		return input1;
	}

	@Override
	public Real input2() {
		return input2;
	}

	@Override
	public Bool enabled() {
		return enabled;
	}

	@Override
	public obix.Enum compareType() {
		return compareType;
	}

	@Override
	public void writeObject(Obj input) {
		if (input instanceof Comparator) {
			Comparator in = (Comparator) input;
			this.input1.set(in.input1().get());
			this.input2.set(in.input2().get());
			this.compareType.set(in.compareType().get());
			this.enabled.set(in.enabled().get());
		} else if (input instanceof Real) {
			if (input.getHref() == null) {
				String resourceUriPath = input.getInvokedHref().substring(
						input.getInvokedHref().lastIndexOf('/') + 1);

				if ("input1".equals(resourceUriPath)) {
					input1.set(((Real) input).get());
				} else if ("input2".equals(resourceUriPath)) {
					input2.set(((Real) input).get());
				} else if("enabled".equals(resourceUriPath)){
					enabled.set(((Real) input).get());
				}
			}
		} else if (input instanceof Bool) {
			if (input.getHref() == null) {
				String resourceUriPath = input.getInvokedHref().substring(
						input.getInvokedHref().lastIndexOf('/') + 1);

				if ("input1".equals(resourceUriPath)) {
					input1.set(((Bool) input).get());
				} else if ("input2".equals(resourceUriPath)) {
					input2.set(((Bool) input).get());
				} else if("enabled".equals(resourceUriPath)){
					enabled.set(((Bool) input).get());
				}
			}
		}
		else if (input instanceof Int) {
			if (input.getHref() == null) {
				String resourceUriPath = input.getInvokedHref().substring(
						input.getInvokedHref().lastIndexOf('/') + 1);

				if ("input1".equals(resourceUriPath)) {
					input1.set(((Int) input).get());
				} else if ("input2".equals(resourceUriPath)) {
					input2.set(((Int) input).get());
				} else if("enabled".equals(resourceUriPath)){
					enabled.set(((Int) input).get());
				}
			}
		}

		// perform control logic
		if (enabled.get()) {
			if (Comparator.COMPARE_TYPE_EQ.equals(compareType.get())) {

				if (input1.get() == input2.get()) {
					result.set(true);
				} else {
					result.set(false);
				}
			} else if (Comparator.COMPARE_TYPE_GT.equals(compareType.get())) {

				if (input1.get() > input2.get()) {
					result.set(true);
				} else {
					result.set(false);
				}
			} else if (Comparator.COMPARE_TYPE_GTE.equals(compareType.get())) {

				if (input1.get() >= input2.get()) {
					result.set(true);
				} else {
					result.set(false);
				}
			} else if (Comparator.COMPARE_TYPE_LT.equals(compareType.get())) {

				if (input1.get() < input2.get()) {
					result.set(true);
				} else {
					result.set(false);
				}
			} else if (Comparator.COMPARE_TYPE_LTE.equals(compareType.get())) {

				if (input1.get() <= input2.get()) {
					result.set(true);
				} else {
					result.set(false);
				}
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
