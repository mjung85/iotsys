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

package at.ac.tuwien.auto.iotsys.gateway.weatherforecast.crawler.impl;

import java.util.logging.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherObject;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.UpcomingWeatherImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastLocationImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.HistoryImpl;
import obix.*;

public class WeatherObjImpl extends Obj implements WeatherObject {
	private static final Logger log = Logger.getLogger(WeatherObjImpl.class.getName());
	
	protected WeatherForecastConnector connector;
	
	protected WeatherForecastLocationImpl location;
	protected UpcomingWeatherImpl upcoming;

	public WeatherObjImpl(WeatherForecastLocationImpl location, WeatherForecastConnector connector) 
				throws FactoryConfigurationError, ParserConfigurationException {
		
		setIs(new Contract(WeatherObject.CONTRACT));
		
		// store connector
		if (connector != null)
			this.connector = connector;
		else
			// use default connector
			this.connector = new WeatherForecastConnector();
		
		// store location
		if (location != null)
			this.location = location.clone();
		else
			// use default location
			this.location = new WeatherForecastLocationImpl();

		this.location.setName("location");
		this.location.setHref(new Uri("location"));
		this.location.setWritable(false);
		
		//objectBroker.addHistoryToDatapoints(this.location,100);
		
		this.location.setHidden(false);
		
		add(this.location);

		this.upcoming = new UpcomingWeatherImpl();
		this.upcoming.setName("upcoming");
		this.upcoming.setHref(new Uri("upcoming"));

		add(this.upcoming);	
	}
	
	public Obj location() {
		return location;
	}
	
	/*
	 * Override this method to return the crawler-specific service URL using 
	 * the crawler's location.
	 */
	@Override
	public String getServiceURL() {
		return "";
	}
	
	@Override
	public void initialize(){
		super.initialize();
		new HistoryImpl("forecast", upcoming, 10, false, false);
	}
	
	/*
	 * Override this method to refresh the crawler object using the 
	 * crawler-specific web service.
	 */
	@Override
	public void refreshObject(){
		this.upcoming.temperature().setReal(7.7);
	}

	@Override
	public Obj upcoming() {
		return upcoming;
	}

}
