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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.objects;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumWeatherManualOverwrite;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherControl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.ManualOverwrite;
import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;
import obix.Enum;
import obix.Obj;
import obix.Uri;

public class WeatherControlImpl extends Obj implements WeatherControl {

	protected Enum manualOverwrite = new Enum();

	private WeatherForecastConnector connector;

	public WeatherControlImpl(String name, WeatherForecastConnector connector) {
		this.setHref(new Uri(name));
		this.setName(name);
		this.connector = connector;

		this.manualOverwrite.setName("manualOverwrite");
		this.manualOverwrite.setHref(new Uri("manualOverwrite"));
		this.manualOverwrite.setRange(new Uri(EnumWeatherManualOverwrite.HREF));
		this.manualOverwrite.setWritable(true);
		this.manualOverwrite.setNull(true);
		this.add(manualOverwrite);
	}

	@Override
	public Enum manualOverwrite() {
		// TODO Auto-generated method stub
		return manualOverwrite;
	}

	@Override
	public void writeObject(Obj input) {
		if (input instanceof obix.Enum) {
			obix.Enum in = (obix.Enum) input;

			manualOverwrite.set(in.get());
			if(in.get().equals("off")){
				connector.setManualOverwrite(ManualOverwrite.OFF);
			}
			
			if(in.get().equals("Storm alarm")){
				connector.setManualOverwrite(ManualOverwrite.STORM_ALARM);
			}
			
			if(in.get().equals("Storm warning")){
				connector.setManualOverwrite(ManualOverwrite.STORM_WARNING);
			}
		}
	}

	@Override
	public void refreshObject() {

	}
}
