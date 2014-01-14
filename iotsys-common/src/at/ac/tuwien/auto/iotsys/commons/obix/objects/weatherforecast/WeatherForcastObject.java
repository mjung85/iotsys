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
package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

import java.util.TimeZone;

public class WeatherForcastObject {
	
	private long timestamp;
	private TimeZone timeZone;
	private String probabilityCode;
	private double temperature;
	private String WindDirection;
	private Integer windSpeed;
	private double humidity;
	private double pressure;
	private double precipitation;
	private double cloudiness;
	private double fog;
	private double lowClouds;
	private double mediumClouds;
	private double highClouds;
	private double dewpointTemperature;
	private String symbol;
	private int symbolProbability;
	private int windProbability;
	private int temperatureProbability;
	
	public String getWindDirection() {
		return WindDirection;
	}
	public void setWindDirection(String windDirection) {
		WindDirection = windDirection;
	}
	
	public int getWindProbability() {
		return windProbability;
	}
	public void setWindProbability(int windProbability) {
		this.windProbability = windProbability;
	}
	public int getTemperatureProbability() {
		return temperatureProbability;
	}
	public void setTemperatureProbability(int temperatureProbability) {
		this.temperatureProbability = temperatureProbability;
	}

	public int getSymbolProbability() {
		return symbolProbability;
	}
	public void setSymbolProbability(int symbolProbability) {
		this.symbolProbability = symbolProbability;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public TimeZone getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	public String getProbabilityCode() {
		return probabilityCode;
	}
	public void setProbabilityCode(String probabilityCode) {
		this.probabilityCode = probabilityCode;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public double getHumidity() {
		return humidity;
	}
	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}
	public double getPressure() {
		return pressure;
	}
	public void setPressure(double pressure) {
		this.pressure = pressure;
	}
	public double getPrecipitation() {
		return precipitation;
	}
	public void setPrecipitation(double precipitation) {
		this.precipitation = precipitation;
	}
	public double getCloudiness() {
		return cloudiness;
	}
	public void setCloudiness(double cloudiness) {
		this.cloudiness = cloudiness;
	}
	public double getFog() {
		return fog;
	}
	public void setFog(double fog) {
		this.fog = fog;
	}
	public double getLowClouds() {
		return lowClouds;
	}
	public void setLowClouds(double lowClouds) {
		this.lowClouds = lowClouds;
	}
	public double getMediumClouds() {
		return mediumClouds;
	}
	public void setMediumClouds(double mediumClouds) {
		this.mediumClouds = mediumClouds;
	}
	public double getHighClouds() {
		return highClouds;
	}
	public void setHighClouds(double highClouds) {
		this.highClouds = highClouds;
	}
	public double getDewpointTemperature() {
		return dewpointTemperature;
	}
	public void setDewpointTemperature(double dewpointTemperature) {
		this.dewpointTemperature = dewpointTemperature;
	}
	public int getWindSpeed() {
		return windSpeed;
	}
	public void setWindSpeed(Integer windSpeed) {
		this.windSpeed = windSpeed;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
