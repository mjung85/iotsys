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

package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl;

import obix.Abstime;
import obix.Contract;
import obix.Enum;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.UpcomingWeather;

public class UpcomingWeatherImpl extends Obj implements UpcomingWeather {
	
	private Str tz = new Str("tz", "UTC");

	private Abstime timestamp = new Abstime("timestamp");
	private Enum probabilitycode = new Enum("probabilityCode", "");
	private Real temperature = new Real("temperature");
	private Real humidity = new Real("humidity");
	private Real pressure = new Real("pressure");
	private Real precipitation = new Real("precipitation");
	private Real cloudiness = new Real("cloudiness");
	private Real fog = new Real("fog");
	private Int  windSpeed = new Int("windSpeed");
	private Real dewpointTemperature = new Real("dewpointTemperature");
	private Str windDirection = new Str();
	
	
	public UpcomingWeatherImpl(){
		setIs(new Contract(UpcomingWeather.CONTRACT));
		
		probabilitycode.setRange(new Uri(ProbabilityCodeImpl.CONTRACT));
		probabilitycode.setHref(new Uri("probabiltityCode"));
		temperature.setUnit(new Uri("obix:units/celsius"));
		temperature.setHref(new Uri("temperature"));
		humidity.setUnit(new Uri("obix:units/percent"));
		humidity.setHref(new Uri("humidity"));
		pressure.setUnit(new Uri("obix:units/hectopascal")); //100*kg^1*m^-1*s^-2
		pressure.setHref(new Uri("pressure"));
		precipitation.setUnit(new Uri("obix:units/millimeter")); //0.001*m^1
		precipitation.setHref(new Uri("precipitation"));
		cloudiness.setUnit(new Uri("obix:units/percent"));
		cloudiness.setHref(new Uri("cloudiness"));
		fog.setUnit(new Uri("obix:units/percent"));
		fog.setHref(new Uri("fog"));
		windSpeed.setUnit(new Uri("obix:units/beaufort"));
		windSpeed.setHref(new Uri("windspeed"));
		dewpointTemperature.setUnit(new Uri("obix:units/celsius"));
		dewpointTemperature.setHref(new Uri("dewpointTemperature"));
		windDirection.setName("windDirection");
		
		add(timestamp);
		//add(probabilitycode);
		add(temperature);
		add(humidity);
		add(pressure);
		add(precipitation);
		add(cloudiness);
		add(fog);
		add(windSpeed);
		add(windDirection);
		add(dewpointTemperature);
	}
	
	public UpcomingWeatherImpl(UpcomingWeather rec) {
		this();
		
		setAll(rec);
	}
	
	public void setAll(UpcomingWeather rec) {
		timestamp.set(rec.timestamp().getMillis(), rec.timestamp().getTimeZone());
		
		if (ProbabilityCodeImpl.GetByName(rec.probabilitycode().get()) != ProbabilityCodeImpl.ID_UNKNOWN) {
			probabilitycode.set(rec.probabilitycode().get());
		}
		else {
			probabilitycode.set(ProbabilityCodeImpl.NAME_UNKNOWN);
		}
		
		temperature.set(rec.temperature().get());
		humidity.set(rec.humidity().get());
		pressure.set(rec.pressure().get());
		precipitation.set(rec.precipitation().get());
		cloudiness.set(rec.cloudiness().get());
		fog.set(rec.fog().get());
		windSpeed.set(rec.windspeed().get());
		windDirection.set(rec.windDirection().get());
		dewpointTemperature.set(rec.dewpointTemperature().get());
	}
	
	@Override
	public Abstime timestamp() {
		return timestamp;
	}

	@Override
	public Enum probabilitycode() {
		return probabilitycode;
	}

	@Override
	public Real temperature() {
		return temperature;
	}

	@Override
	public Real humidity() {
		return humidity;
	}

	@Override
	public Real pressure() {
		return pressure;
	}

	@Override
	public Real precipitation() {
		return precipitation;
	}

	@Override
	public Real cloudiness() {
		return cloudiness;
	}

	@Override
	public Real fog() {
		return fog;
	}

	@Override
	public Int windspeed() {
		return  windSpeed;
	}

	@Override
	public Real dewpointTemperature() {
		return dewpointTemperature;
	}

	@Override
	public Str windDirection() {
		return windDirection;
	}
	
	// workaround for encoding of history object
	public void unsetHrefs(){
		this.cloudiness.setHref(null);
		this.dewpointTemperature.setHref(null);
		this.fog.setHref(null);
		this.humidity.setHref(null);
		this.precipitation.setHref(null);
		this.pressure.setHref(null);
		this.probabilitycode.setHref(null);
		this.temperature.setHref(null);
		this.tz.setHref(null);
		this.windDirection.setHref(null);
		this.windSpeed.setHref(null);
	}
	
	@Override
	public void refreshObject(){
		if(this.getParent() != null){
			this.getParent().refreshObject();
		}
	}
}
