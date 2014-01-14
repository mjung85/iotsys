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

package at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

//import obix.WeatherForcastObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForcastObject;

public class WeatherForecastConnector implements Connector {
	
	private static final Logger log = Logger.getLogger(WeatherForecastConnector.class.getName());
	
	private ManualOverwrite overwrite = ManualOverwrite.OFF;
	
	private HttpURLConnection httpConnection = null;
	private DocumentBuilder docBuilder = null;
	
	public WeatherForecastConnector() throws FactoryConfigurationError, ParserConfigurationException {
		this.httpConnection = null;
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		
		this.docBuilder = docBuilderFactory.newDocumentBuilder();
	}
	
	public void connect() {
		// nothing to do
	}
	
	public void disconnect() {
		// nothing to do
	}
	
	// create a bad weather front
	public void setManualOverwrite(ManualOverwrite overwrite){
		this.overwrite = overwrite;
	}
	
	public ManualOverwrite getManualOverwrite(){
		return this.overwrite;
	}
	
	public Document getWeatherForecastAsXML(String serviceURL) throws IOException, MalformedURLException, SAXException
	{ 
        log.info("Retrieving weather forecast from " + serviceURL + ".");
        
        Document result = null;
        
        if (docBuilder != null)
        {
			connectToURL(serviceURL);
			
			if (httpConnection.getResponseCode() == 200)						
				result = docBuilder.parse(httpConnection.getInputStream());
			
	        disconnectFromURL();
        } 
        return result;
	}
	
	public List<WeatherForcastObject> getWeatherForecast(String serviceURL){
		
		
		ArrayList<WeatherForcastObject> resultWeatherList = new ArrayList<WeatherForcastObject>();
		
		if(overwrite == ManualOverwrite.STORM_ALARM){
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
			long now = System.currentTimeMillis();
			long threeHours = 1000 * 60 * 60 * 3;
			WeatherForcastObject weatherObject = new WeatherForcastObject();
			weatherObject.setCloudiness(100);
			weatherObject.setDewpointTemperature(0);
			weatherObject.setFog(0);
			weatherObject.setHighClouds(100);
			weatherObject.setMediumClouds(100);
			weatherObject.setPrecipitation(100);
			weatherObject.setPressure(1024);
			weatherObject.setTemperatureProbability(100);
			weatherObject.setTemperature(10);
			weatherObject.setWindDirection("W");
			weatherObject.setWindProbability(100);
			weatherObject.setWindSpeed(11);
		
			weatherObject.setTimestamp(now);		
			weatherObject.setTimeZone(TimeZone.getTimeZone("CET"));	
			
			for(int i = 0; i < 10 ; i++){
				weatherObject.setTimestamp(now);
				resultWeatherList.add(weatherObject);
				now += threeHours;
			}					
		}
		else{
			log.info("Retrieving weather forecast from " + serviceURL + ".");
			try {
				Document doc = getWeatherForecastAsXML(serviceURL);
				
				if (doc != null)
				{
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
					
					NodeList elements = doc.getElementsByTagName("location");
					
					for (int i=0; i < elements.getLength(); i++)
					{
						Element location = (Element) elements.item(i);
						
						if (location != null)
						{
							Date from;
							Date to;
							NodeList tmp;
							Element time = (Element) location.getParentNode();
	
							try
							{
								// Z in xs:dateTime means UTC time!
								from = dateFormat.parse(time.getAttribute("from").replaceAll("Z", "+00:00"));
								to = dateFormat.parse(time.getAttribute("to").replaceAll("Z", "+00:00"));			
							}
							catch (ParseException pe)
							{
								log.log(Level.WARNING, pe.getMessage());
								
								// ignore time element
								continue;
							}
				
							if (from.equals(to))
							{		
								WeatherForcastObject weatherObject = new WeatherForcastObject();
								String utcOffset = time.getAttribute("to");		 
								// strip date
								utcOffset = utcOffset.substring(utcOffset.indexOf('T'));
								// 'Z' means utc
								utcOffset = utcOffset.replaceAll("Z", "+00:00");
								// strip time (note that either '+' or '-' is present)
								utcOffset = utcOffset.substring(utcOffset.lastIndexOf('+') + utcOffset.lastIndexOf('-') + 1);
								
								weatherObject.setTimestamp(to.getTime());		
								weatherObject.setTimeZone(TimeZone.getTimeZone("GMT" + utcOffset));			
								//parse temperatureProbability
								tmp = location.getElementsByTagName("temperatureProbability");
								
								if (tmp.getLength() >= 1){
									weatherObject.setTemperatureProbability(Integer.parseInt(((Element) tmp.item(0)).getAttribute("value")));
								}
								
								//parese windProbability
								tmp = location.getElementsByTagName("windProbability");
								if (tmp.getLength() >= 1){
									weatherObject.setWindProbability(Integer.parseInt(((Element) tmp.item(0)).getAttribute("value")));
								}
								
								// parse temperature
								tmp = location.getElementsByTagName("temperature");
								if (tmp.getLength() >= 1){
									weatherObject.setTemperature(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
								}
								else{
									weatherObject.setTemperature(Double.NaN);
								}
									
								// parse windDirection 
								tmp = location.getElementsByTagName("windDirection");
								if (tmp.getLength() >= 1){
									weatherObject.setWindDirection(((Element) tmp.item(0)).getAttribute("name")); 
								}
								else{
									weatherObject.setWindDirection(null);
									
								}
								
								// parse wind speed
								tmp = location.getElementsByTagName("windSpeed");
								if (tmp.getLength() >= 1){
									weatherObject.setWindSpeed(Integer.parseInt(((Element) tmp.item(0)).getAttribute("beaufort")));
								}
								else{	
									weatherObject.setWindSpeed(null);
								}
								
								// parse humidity
								tmp = location.getElementsByTagName("humidity");
								if (tmp.getLength() >= 1){
									weatherObject.setHumidity(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
								}
								else{	
									weatherObject.setHumidity(Double.NaN);
								}
	
								// parse pressure
								tmp = location.getElementsByTagName("pressure");
								if (tmp.getLength() >= 1){
									weatherObject.setPressure(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
								}
								else{	
									weatherObject.setPressure(Double.NaN);
								}
								
								// parse cloudiness
								tmp = location.getElementsByTagName("cloudiness");
								if (tmp.getLength() >= 1){
									weatherObject.setCloudiness(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
								}
								else{
									weatherObject.setCloudiness(Double.NaN);
								}
								
								// parse fog
								tmp = location.getElementsByTagName("fog");
								if (tmp.getLength() >= 1){
									weatherObject.setFog(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
								}
								else{
									weatherObject.setFog(Double.NaN);
								}
								
								// parse lowClouds
								tmp = location.getElementsByTagName("lowClouds");
								if (tmp.getLength() >= 1){
									weatherObject.setLowClouds(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
								}
								else{
									weatherObject.setLowClouds(Double.NaN);
								}
								
								// parse lowClouds
								tmp = location.getElementsByTagName("mediumClouds");
								if (tmp.getLength() >= 1){
									weatherObject.setHighClouds(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
								}
								else{
									weatherObject.setHighClouds(Double.NaN);
								}
								
								// parse MediumClouds
								tmp = location.getElementsByTagName("highClouds");
								if (tmp.getLength() >= 1){
									weatherObject.setMediumClouds(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
								}
								else{
									weatherObject.setMediumClouds(Double.NaN);
								}
								
								// parse dewpointTemperature 
								tmp = location.getElementsByTagName("dewpointTemperature");
								if (tmp.getLength() >= 1){
									weatherObject.setDewpointTemperature(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
								}
								else{
									weatherObject.setDewpointTemperature(Double.NaN);
								}
								
								resultWeatherList.add(weatherObject);
							}
						}			
					}
				}	
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}		
		}
		return resultWeatherList;
	}
	
	
	public WeatherForcastObject getUpcomingWeather (String serviceURL){
		
		log.info("Retrieving upcoming weather forecast from " + serviceURL + ".");
		
		List<WeatherForcastObject> weatherList = getWeatherForecast(serviceURL);
		
		return weatherList.get(0);
	}
		
	private void connectToURL(String serviceURL) throws IOException, MalformedURLException
	{
		log.info("Connecting to weather service.");
		
		httpConnection = (HttpURLConnection) (new URL(serviceURL)).openConnection();
		httpConnection.setRequestMethod("GET");
		httpConnection.setConnectTimeout(10000); // in milliseconds
		httpConnection.setDoInput(true); // use connection for input
				
		httpConnection.connect();
	}

	private void disconnectFromURL()
	{
		log.info("Disconnecting from weather service.");

		if (httpConnection != null)
			httpConnection.disconnect();
		
		httpConnection = null;
	}
}

enum ManualOverwrite{
	STORM_WARNING, STORM_ALARM, OFF
}