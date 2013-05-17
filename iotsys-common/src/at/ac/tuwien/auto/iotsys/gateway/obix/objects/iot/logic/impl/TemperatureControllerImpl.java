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
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.TemperatureController;

public class TemperatureControllerImpl extends Obj implements
		TemperatureController {
	protected Real setpoint = new Real();
	protected Real temperature = new Real();
	protected Real controlValue = new Real();
	protected Bool enabled = new Bool();
	protected Real tolerance = new Real();
	protected Bool saveEnergyEnabled = new Bool();
	protected Real saveEnergyFactor = new Real();

	private ControllerState controllerState = ControllerState.INACTIVE;

	public TemperatureControllerImpl() {
		setIs(new Contract(TemperatureController.CONTRACT));
		setpoint.setName("setpoint");
		setpoint.setHref(new Uri("setpoint"));
		setpoint.setUnit(new Uri("obix:units/celsius"));
		setpoint.setWritable(true);

		temperature.setName("temperature");
		temperature.setHref(new Uri("temperature"));
		temperature.setUnit(new Uri("obix:units/celsius"));
		temperature.setWritable(true);

		controlValue.setName("controlValue");
		controlValue.setHref(new Uri("controlValue"));
		controlValue.setMin(-100);
		controlValue.setMax(100);

		tolerance.setName("tolerance");
		tolerance.setHref(new Uri("tolerance"));
		tolerance.setUnit(new Uri("obix:units/celsius"));
		tolerance.setWritable(true);

		enabled.setName("enabled");
		enabled.setHref(new Uri("enabled"));
		enabled.setWritable(true);
		
		saveEnergyEnabled.setName("saveEnergyEnabled");
		saveEnergyEnabled.setHref(new Uri("saveEnergyEnabled"));
		saveEnergyEnabled.setWritable(true);
		
		saveEnergyFactor.setName("saveEnergyFactor");
		saveEnergyFactor.setHref(new Uri("saveEnergyFactor"));
		saveEnergyFactor.setWritable(true);
		
		this.add(setpoint);
		this.add(temperature);
		this.add(controlValue);
		this.add(tolerance);
		this.add(enabled);
		this.add(saveEnergyEnabled);
		this.add(saveEnergyFactor);		
	}

	@Override
	public Real setpoint() {
		return setpoint;
	}

	@Override
	public Real temperature() {
		return temperature;
	}

	@Override
	public Real controlValue() {
		return controlValue;
	}

	@Override
	public Bool enabled() {
		return enabled;
	}

	@Override
	public Real tolerance() {
		return tolerance;
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
		if (input instanceof TemperatureController) {
			TemperatureController in = (TemperatureController) input;
			this.setpoint.set(in.setpoint().get());
			this.temperature.set(in.temperature().get());
			this.enabled.set(in.enabled().get());
			this.tolerance.set(in.tolerance().get());
		} else if (input instanceof Real) {

			if ("setpoint".equals(resourceUriPath)) {
				setpoint.set(((Real) input).get());
			} else if ("temperature".equals(resourceUriPath)) {
				temperature.set(((Real) input).get());
			} else if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Real) input).get());
			} else if ("tolerance".equals(resourceUriPath)) {
				tolerance.set(((Real) input).get());
			}

		} else if (input instanceof Bool) {

			if ("setpoint".equals(resourceUriPath)) {
				setpoint.set(((Bool) input).get());
			} else if ("temperature".equals(resourceUriPath)) {
				temperature.set(((Bool) input).get());
			} else if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Bool) input).get());
			} else if ("tolerance".equals(resourceUriPath)) {
				tolerance.set(((Bool) input).get());
			}

		} else if (input instanceof Int) {

			if ("setpoint".equals(resourceUriPath)) {
				setpoint.set(((Int) input).get());
			} else if ("temperature".equals(resourceUriPath)) {
				temperature.set(((Bool) input).get());
			} else if ("enabled".equals(resourceUriPath)) {
				enabled.set(((Int) input).get());
			} else if ("tolerance".equals(resourceUriPath)) {
				tolerance.set(((Int) input).get());
			}

		}

		// perform control logic
		if (enabled.get()) {
			if (temperature.get() < setpoint.get() - tolerance.get()
					&& controllerState != ControllerState.HEATING) {
				// we need to heat!
				controlValue.set(100 * (saveEnergyEnabled.get()?(saveEnergyFactor().get()):(1)));
				controllerState = ControllerState.HEATING;
			} else if (temperature.get() > setpoint.get() + tolerance.get()
					&& controllerState != ControllerState.COOLING) {
				controlValue.set(-100 * (saveEnergyEnabled.get()?(saveEnergyFactor().get()):(1)));
				controllerState = ControllerState.COOLING;
			} else if (temperature.get() > setpoint.get()
					&& controllerState == ControllerState.HEATING) {
				// we have reached the target heating temp
				controlValue.set(0);
				controllerState = ControllerState.INACTIVE;
			} else if (temperature.get() < setpoint.get()
					&& controllerState == ControllerState.COOLING) {
				// we have reached the target cooling temp
				controlValue.set(0);
				controllerState = ControllerState.INACTIVE;
			}	
		} else if (controllerState == ControllerState.HEATING
				|| controllerState == ControllerState.COOLING) {
			// we need to stop the controller
			controlValue.set(0);
			controllerState = ControllerState.INACTIVE;
		}
	}

	@Override
	public Real saveEnergyFactor() {
		return saveEnergyFactor;
	}

	@Override
	public Bool saveEnergyEnabled() {
		return saveEnergyEnabled;
	}
}

enum ControllerState {
	INACTIVE, HEATING, COOLING
}
