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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.LedsActuator;

public class LedsActuatorImpl extends ActuatorImpl implements LedsActuator {
	protected Bool red = new Bool(false);
	protected Bool blue = new Bool(false);
	protected Bool green = new Bool(false);

	private static final Logger log = Logger
			.getLogger(LedsActuatorImpl.class.getName());

	public LedsActuatorImpl() {
		setIs(new Contract(LedsActuator.CONTRACT));
		
		red.setWritable(true);
		red.setHref(new Uri(LedsActuator.LED_RED_CONTRACT_HREF));
		red.setName(LedsActuator.LED_RED_CONTRACT_NAME);

		blue.setWritable(true);
		blue.setHref(new Uri(LedsActuator.LED_BLUE_CONTRACT_HREF));
		blue.setName(LedsActuator.LED_BLUE_CONTRACT_NAME);
		
		green.setWritable(true);
		green.setHref(new Uri(LedsActuator.LED_GREEN_CONTRACT_HREF));
		green.setName(LedsActuator.LED_GREEN_CONTRACT_NAME);
		
		add(red);
		add(blue);
		add(green);
	}

	public void writeObject(Obj input) {
		String resourceUriPath = "";
		if (input.getHref() == null) {
			resourceUriPath = input.getInvokedHref().substring(
					input.getInvokedHref().lastIndexOf('/') + 1);
		} else {
			resourceUriPath = input.getHref().get();
		}
		if (input instanceof LedsActuator) {
			LedsActuator in = (LedsActuator) input;
			log.finer("Writing on LedsActuator: Blue: "
					+ in.blue().get() + ", Red: "
					+ in.red().get() + ", Green: " + in.green().get());

			this.blue.set(in.blue().get());
			this.red.set(in.red().get());
			this.green.set(in.green().get());

		} else if (input instanceof Bool) {

			if (LedsActuator.LED_BLUE_CONTRACT_HREF
					.equals(resourceUriPath)) {
				this.blue.set(((Bool) input).get());
			} else if (LedsActuator.LED_RED_CONTRACT_HREF
					.equals(resourceUriPath)) {
				this.red.set(((Bool) input).get());
			} else if (LedsActuator.LED_GREEN_CONTRACT_HREF
					.equals(resourceUriPath)) {
				this.green.set(((Bool) input).get());
			}
		}
	}

	@Override
	public Bool blue() {
		return this.blue;
	}

	@Override
	public Bool red() {
		return this.red;
	}

	@Override
	public Bool green() {
		return this.green;
	}
}
