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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl;

import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.AirDamperActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.FanSpeedActuator;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Uri;

public class FanSpeedActuatorImpl extends ActuatorImpl implements
		FanSpeedActuator {
	protected Int fanSpeedSetpointValue = new Int(0);
	protected Bool enabledValue = new Bool(false);

	private static final Logger log = Logger
			.getLogger(FanSpeedActuatorImpl.class.getName());

	public FanSpeedActuatorImpl() {
		setIs(new Contract(FanSpeedActuator.CONTRACT));
		fanSpeedSetpointValue.setWritable(true);
		Uri fanSpeedSetpointValueUri = new Uri(
				FanSpeedActuator.FAN_SPEED_SETPOINT_CONTRACT_HREF);
		fanSpeedSetpointValue.setHref(fanSpeedSetpointValueUri);
		fanSpeedSetpointValue
				.setName(FanSpeedActuator.FAN_SPEED_SETPOINT_CONTRACT_NAME);
		fanSpeedSetpointValue.setUnit(new Uri(
				FanSpeedActuator.FAN_SPEED_SETPOINT_CONTRACT_UNIT));

		enabledValue.setHref(new Uri(FanSpeedActuator.ENBALED_CONTRACT_HREF));
		enabledValue.setName(FanSpeedActuator.ENABLED_CONTRACT_NAME);
		enabledValue.setWritable(true);
		
		add(fanSpeedSetpointValue);
		add(enabledValue);
	}

	public void writeObject(Obj input) {
		String resourceUriPath = "";
		if (input.getHref() == null) {
			resourceUriPath = input.getInvokedHref().substring(
					input.getInvokedHref().lastIndexOf('/') + 1);
		} else {
			resourceUriPath = input.getHref().get();
		}
		if (input instanceof FanSpeedActuator) {
			FanSpeedActuator in = (FanSpeedActuator) input;
			log.finer("Writing on FanSpeedActuator: "
					+ in.fanSpeedSetpointValue().get() + ","
					+ in.enabled().get());

			this.fanSpeedSetpointValue.set(in.fanSpeedSetpointValue().get());
			this.enabledValue.set(in.enabled().get());

		} else if (input instanceof Int) {

			if (FanSpeedActuator.FAN_SPEED_SETPOINT_CONTRACT_HREF
					.equals(resourceUriPath)) {
				this.fanSpeedSetpointValue.set(((Int) input).get());
			} else if (FanSpeedActuator.ENBALED_CONTRACT_HREF
					.equals(resourceUriPath)) {
				this.enabledValue.set(((Int) input).get());
			}

		} else if (input instanceof Bool) {

			if (FanSpeedActuator.FAN_SPEED_SETPOINT_CONTRACT_HREF
					.equals(resourceUriPath)) {
				this.fanSpeedSetpointValue.set(((Bool) input).get());
			} else if (FanSpeedActuator.ENBALED_CONTRACT_HREF
					.equals(resourceUriPath)) {
				this.enabledValue.set(((Bool) input).get());
			}

		}
	}

	@Override
	public Int fanSpeedSetpointValue() {
		log.finer("Returning this value is now: "
				+ this.fanSpeedSetpointValue.getInt());
		return this.fanSpeedSetpointValue;
	}

	public Bool enabled() {
		return enabledValue;
	}
}
