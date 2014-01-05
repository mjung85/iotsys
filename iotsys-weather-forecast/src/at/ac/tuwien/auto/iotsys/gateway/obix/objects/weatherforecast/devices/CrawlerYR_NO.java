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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.devices;

import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.HistoryImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForcastObject;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.UpcomingWeatherImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastLocationImpl;
import at.ac.tuwien.auto.iotsys.gateway.weatherforecast.crawler.impl.CrawlerImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;

public class CrawlerYR_NO extends CrawlerImpl {
	private static final String SERVICE_URL = "http://api.yr.no/weatherapi/locationforecast/1.8/?";
	private static final Logger log = Logger.getLogger(CrawlerYR_NO.class
			.getName());

	public CrawlerYR_NO(String name, WeatherForecastLocationImpl location,
			WeatherForecastConnector connector)
			throws FactoryConfigurationError, ParserConfigurationException {
		super(location, connector);

		setName(name);
		setHref(new Uri(name));
	}

	@Override
	public void initialize() {
		// create generic history object in oBIX base class
		super.initialize();
	}

	@Override
	public void refreshObject() {
		log.info("Refreshing weather forecast.");
		super.refreshObject();
		
		// history object is created in the super class (initialize method).
		HistoryImpl forecast = (HistoryImpl) upcoming.getChildByHref(new Uri("forecast"));
		
		List<WeatherForcastObject> weatherList = connector
				.getWeatherForecast(this.getServiceURL());

		// FIXME weather forecast should be limited to 10 entries
		for (int i = 0; i < weatherList.size() && i < 10; i++) {
			UpcomingWeatherImpl upcomingWeather = new UpcomingWeatherImpl();
			upcomingWeather.timestamp().set(weatherList.get(i).getTimestamp(),
					TimeZone.getDefault());
			// set temperature
			if (weatherList.get(i).getTemperature() != Double.NaN) {
				upcomingWeather.temperature().set(
						weatherList.get(i).getTemperature());
			} else {
				upcomingWeather.temperature().setNull(true);
			}

			// set windDirection
			if (weatherList.get(i).getWindDirection() != null) {
				upcomingWeather.windDirection().set(
						weatherList.get(i).getWindDirection());
			} else {
				upcomingWeather.windDirection().setNull(true);
			}

			// set windSpeed
			if (weatherList.get(i).getWindSpeed() != Double.NaN) {
				upcomingWeather.windspeed().set(
						weatherList.get(i).getWindSpeed());
			} else {
				upcomingWeather.windspeed().setNull(true);
			}

			// set humidity
			if (weatherList.get(i).getHumidity() != Double.NaN) {
				upcomingWeather.humidity()
						.set(weatherList.get(i).getHumidity());
			} else {
				upcomingWeather.humidity().setNull(true);
			}

			// set pressure
			if (weatherList.get(i).getPressure() != Double.NaN) {
				upcomingWeather.pressure()
						.set(weatherList.get(i).getPressure());
			} else {
				upcomingWeather.pressure().setNull(true);
			}

			// set cloudiness
			if (weatherList.get(i).getCloudiness() != Double.NaN) {
				upcomingWeather.cloudiness().set(
						weatherList.get(i).getCloudiness());
			} else {
				upcomingWeather.cloudiness().setNull(true);
			}

			// set fog
			if (weatherList.get(i).getFog() != Double.NaN) {
				upcomingWeather.fog().set(weatherList.get(i).getFog());
			} else {
				upcomingWeather.fog().setNull(true);
			}

			// set dewpointTemperature
			if (weatherList.get(i).getDewpointTemperature() != Double.NaN) {
				upcomingWeather.dewpointTemperature().set(
						weatherList.get(i).getDewpointTemperature());
			} else {
				upcomingWeather.dewpointTemperature().setNull(true);
			}
			
			if(i == 0){ // first forecast is upcoming weather
				upcoming.setAll(upcomingWeather);
			}
			
			if(forecast != null){
				// by having more weather forecast entries than the max size of the history
				// this should implicitly clear the whole forecast FIXME
				upcomingWeather.unsetHrefs();
				forecast.addObjToHistory(upcomingWeather, upcomingWeather.timestamp());
			}
		}
	}

	@Override
	public String getServiceURL() {
		return SERVICE_URL + "lat=" + location.getLatitude() + ";lon="
				+ location.getLongitude() + ";msl=" + location.getHeight();
	}
}
