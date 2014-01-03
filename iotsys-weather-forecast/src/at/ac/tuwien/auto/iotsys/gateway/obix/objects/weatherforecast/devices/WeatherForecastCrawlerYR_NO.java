package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.devices;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import obix.Obj;
import obix.Uri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForcastObject;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.ProbabilityCodeImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForcastUpcomingWeatherImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForcastUpdateUpcomingImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastLocationImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastRecordImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastUpdateInImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherSymbolImpl;
import at.ac.tuwien.auto.iotsys.gateway.weatherforecast.crawler.impl.WeatherForecastCrawlerImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;

public class WeatherForecastCrawlerYR_NO extends WeatherForecastCrawlerImpl {
	double val = 0.2;
	
	private static final String SERVICE_URL = "http://api.yr.no/weatherapi/locationforecast/1.8/?";
	
	private static final Logger log = Logger.getLogger(WeatherForecastCrawlerYR_NO.class.getName());

	
	
	
	public WeatherForecastCrawlerYR_NO(String name, WeatherForecastLocationImpl location, WeatherForecastConnector connector) 
				throws FactoryConfigurationError, ParserConfigurationException {
		super(location, connector);
		
		setName(name);
		setHref(new Uri(name));
	}
	
	@Override
	public void initialize(){
		super.initialize();
	}
	
	@Override
	public void refreshObject(){
		log.finest("Refreshing weather forecast.");
		System.out.println("forcast crawler NO in refresh object");
			
		
		super.refreshObject();
		
		WeatherForcastObject weather = new WeatherForcastObject();

			List<WeatherForcastObject> weatherList = connector.getWeatherForecas(this.getServiceURL());

			System.out.println("WeatherList: "+weatherList.size());

			
			
			for(int i=0; i < weatherList.size();i++){

				if (i==0){
				
					
					// set temperature			
					if (weatherList.get(i).getTemperature() != Double.NaN){
						upcoming.temperature().set(weatherList.get(i).getTemperature());
					}
					else{
						upcoming.temperature().setNull(true);
					}
					
					// set windDirection			
					if (weatherList.get(i).getWindDirection() != null){
						upcoming.windDirection().set(weatherList.get(i).getWindDirection());	
					}
					else{
						upcoming.windDirection().setNull(true);
					}
					
					// set windSpeed			
					if (weatherList.get(i).getWindSpeed() != Double.NaN){
						upcoming.windspeed().set(weatherList.get(i).getWindSpeed());
					}
					else{
						upcoming.windspeed().setNull(true);
					}
					
					// set humidity			
					if (weatherList.get(i).getHumidity() != Double.NaN){
						upcoming.humidity().set(weatherList.get(i).getHumidity());
					}
					else{
						upcoming.humidity().setNull(true);
					}
					
					// set pressure			
					if (weatherList.get(i).getPressure() != Double.NaN){
						upcoming.pressure().set(weatherList.get(i).getPressure());
					}
					else{
						upcoming.pressure().setNull(true);
					}
					
					// set cloudiness 			
					if (weatherList.get(i).getCloudiness() != Double.NaN){
						upcoming.cloudiness().set(weatherList.get(i).getCloudiness());
					}
					else{
						upcoming.cloudiness().setNull(true);
					}
					
					// set fog 			
					if (weatherList.get(i).getFog() != Double.NaN){
						upcoming.fog().set(weatherList.get(i).getFog());
					}
					else{
						upcoming.fog().setNull(true);
					}
					
					// set dewpointTemperature  			
					if (weatherList.get(i).getDewpointTemperature() != Double.NaN){
						upcoming.dewpointTemperature().set(weatherList.get(i).getDewpointTemperature());
					}
					else{
						upcoming.dewpointTemperature().setNull(true);
					}

				}
				
				
				
				
				weather = weatherList.get(i);
				System.out.println("---------------------------------------");
				System.out.println("Timestamp: "+weather.getTimestamp());
				System.out.println("Temperatur: "+weather.getTemperature());
				System.out.println("WindDirection : "+weather.getWindDirection());
				System.out.println("WindSpeed: "+weather.getWindSpeed());
				System.out.println("Humidity: "+weather.getHumidity());
				System.out.println("Pressure: "+weather.getPressure());
				System.out.println("Cloudiness: "+weather.getCloudiness());
				System.out.println("Fog: "+weather.getFog());
				System.out.println("LowClouds: "+weather.getLowClouds());
				System.out.println("MediumClouds: "+weather.getMediumClouds());
				System.out.println("HighClouds: "+weather.getHighClouds());
				System.out.println("DewpointTemperature: "+weather.getDewpointTemperature());
				
				WeatherForecastRecordImpl forecast = new WeatherForecastRecordImpl();
				
				
				
				forecast.timestamp().set(weatherList.get(i).getTimestamp(), weatherList.get(i).getTimeZone());

				// set temperature			
				if (weatherList.get(i).getTemperature() != Double.NaN){
					forecast.temperature().set(weatherList.get(i).getTemperature());
				}
				else{
					forecast.temperature().setNull(true);
				}
				
				// set windDirection			
				if (weatherList.get(i).getWindDirection() != null){
					forecast.windDirection().set(weatherList.get(i).getWindDirection());	
				}
				else{
					forecast.windDirection().setNull(true);
				}
				
				// set windSpeed			
				if (weatherList.get(i).getWindSpeed() != Double.NaN){
					forecast.windSpeed().set(weatherList.get(i).getWindSpeed());
				}
				else{
					forecast.windSpeed().setNull(true);
				}
				
				// set humidity			
				if (weatherList.get(i).getHumidity() != Double.NaN){
					forecast.humidity().set(weatherList.get(i).getHumidity());
				}
				else{
					forecast.humidity().setNull(true);
				}
				
				// set pressure			
				if (weatherList.get(i).getPressure() != Double.NaN){
					forecast.pressure().set(weatherList.get(i).getPressure());
				}
				else{
					forecast.pressure().setNull(true);
				}
				
				// set cloudiness 			
				if (weatherList.get(i).getCloudiness() != Double.NaN){
					forecast.cloudiness().set(weatherList.get(i).getCloudiness());
				}
				else{
					forecast.cloudiness().setNull(true);
				}
				
				// set fog 			
				if (weatherList.get(i).getFog() != Double.NaN){
					forecast.fog().set(weatherList.get(i).getFog());
				}
				else{
					forecast.fog().setNull(true);
				}
				
				// set dewpointTemperature  			
				if (weatherList.get(i).getDewpointTemperature() != Double.NaN){
					forecast.dewpointTemperature().set(weatherList.get(i).getDewpointTemperature());
				}
				else{
					forecast.dewpointTemperature().setNull(true);
				}

			}
				
//		
//		try 
//		{
//			
//			//Document doc2 = connector.getUpcomingWeather(this.getServiceURL());
//			// parse response 
//			Document doc = connector.getWeatherForecastAsXML(this.getServiceURL());
//			
//			if (doc != null)
//			{
//				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
//				ArrayList<Obj> data = new ArrayList<Obj>();
//				Hashtable<Date, WeatherForecastRecordImpl> hashTable = new Hashtable<Date, WeatherForecastRecordImpl>(); 
//				WeatherForecastRecordImpl forecast;
//				WeatherForcastUpcomingWeatherImpl upcoming = null;
//				
//				NodeList elements = doc.getElementsByTagName("location");
//				
//				for (int i=0; i < elements.getLength(); i++)
//				{
//					Element location = (Element) elements.item(i);
//					
//					if (location != null)
//					{
//						Date from;
//						Date to;
//						NodeList tmp;
//						Element time = (Element) location.getParentNode();
//
//						try
//						{
//							// Z in xs:dateTime means UTC time!
//							from = dateFormat.parse(time.getAttribute("from").replaceAll("Z", "+00:00"));
//							to = dateFormat.parse(time.getAttribute("to").replaceAll("Z", "+00:00"));
//						}
//						catch (ParseException pe)
//						{
//							log.log(Level.WARNING, pe.getMessage());
//							
//							// ignore time element
//							continue;
//						}
//
//						// check if a weather forecast record already exists for 
//						// the given timestamp -- if not create a new one
//						if ((forecast = hashTable.get(to)) == null)
//						{
//							upcoming = new WeatherForcastUpcomingWeatherImpl();
//							forecast = new WeatherForecastRecordImpl();
//							forecast.precipitation().setNull(true);
//							forecast.symbol().setNull(true);
//							
//						//	upcoming.setAlll();
//							
//							// append the record to the update array list
//							data.add(forecast);
//							
//							// add the record to the hash table
//							hashTable.put(to, forecast);
//							
//							/*
//							 * set timestamp
//							 */
//							String utcOffset = time.getAttribute("to");
//							 
//							// strip date
//							utcOffset = utcOffset.substring(utcOffset.indexOf('T'));
//							
//							// 'Z' means utc
//							utcOffset = utcOffset.replaceAll("Z", "+00:00");
//
//							// strip time (note that either '+' or '-' is present)
//							utcOffset = utcOffset.substring(utcOffset.lastIndexOf('+') + utcOffset.lastIndexOf('-') + 1);
//							
//							forecast.timestamp().set(to.getTime(), TimeZone.getTimeZone("GMT" + utcOffset));
//						}
//
//						// check the time element's type
//						if (from.equals(to))
//						{			
//							// parse probability code
//							int temperatureProbability = -1;
//							int windProbability = -1;
//							int probabilityCode;
//							
//							tmp = location.getElementsByTagName("temperatureProbability");
//							
//							if (tmp.getLength() >= 1)
//								temperatureProbability = Integer.parseInt(((Element) tmp.item(0)).getAttribute("value"));
//
//							tmp = location.getElementsByTagName("windProbability");
//							
//							if (tmp.getLength() >= 1)
//								windProbability = Integer.parseInt(((Element) tmp.item(0)).getAttribute("value"));
//
//							forecast.probabilityCode().setNull(temperatureProbability == -1 && windProbability == -1);
//
//							probabilityCode = (temperatureProbability > windProbability) ? temperatureProbability : windProbability;
//							forecast.probabilityCode().set(ProbabilityCodeImpl.GetByID(probabilityCode));
//							
//							// parse temperature
//							tmp = location.getElementsByTagName("temperature");
//							
//							if (tmp.getLength() >= 1){
//								forecast.temperature().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
//								upcoming.weatherTemperature().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
//								upcoming.weatherTemperature().setReal(3.4);
//								
//								//val+=1;
//								//super.upcoming.weatherTemperature().setReal(val);
//								//System.out.println("in der if");
//							}
//							else{
//							forecast.temperature().setNull(tmp.getLength() == 0);
//							upcoming.weatherTemperature().setNull(tmp.getLength() == 0);
//							System.out.println("in der else");
//					
//							}
//							System.out.println("Weather Temp upcoming wetather crwaler YR NO: "+upcoming.weatherTemperature());
//							
//							// parse wind speed
//							tmp = location.getElementsByTagName("windSpeed");
//							
//							if (tmp.getLength() >= 1)
//								forecast.windSpeed().set(Integer.parseInt(((Element) tmp.item(0)).getAttribute("beaufort")));
//
//							forecast.windSpeed().setNull(tmp.getLength() == 0);
//							
//							// parse humidity
//							tmp = location.getElementsByTagName("humidity");
//							
//							if (tmp.getLength() >= 1)
//								forecast.humidity().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
//
//							forecast.humidity().setNull(tmp.getLength() == 0);
//							
//							// parse pressure
//							tmp = location.getElementsByTagName("pressure");
//							
//							if (tmp.getLength() >= 1)
//								forecast.pressure().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
//
//							forecast.pressure().setNull(tmp.getLength() == 0);
//							
//							// parse cloudiness
//							tmp = location.getElementsByTagName("cloudiness");
//							
//							if (tmp.getLength() >= 1)
//								forecast.cloudiness().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
//
//							forecast.cloudiness().setNull(tmp.getLength() == 0);
//							
//							// parse fog
//							tmp = location.getElementsByTagName("fog");
//							
//							if (tmp.getLength() >= 1)
//								forecast.fog().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
//
//							forecast.fog().setNull(tmp.getLength() == 0);
//						}
//						else // time element describes period of time
//						{
//							/*
//							 * there may exist multiple time periods; former periods are shorter and should be preferred
//							 */
//							
//							// parse probability code
//							int symbolProbability = -1;
//							int probabilityCode = ProbabilityCodeImpl.GetByName(forecast.probabilityCode().get());
//							
//							tmp = location.getElementsByTagName("symbolProbability");
//							
//							if (tmp.getLength() >= 1)
//								symbolProbability = Integer.parseInt(((Element) tmp.item(0)).getAttribute("value"));
//
//							forecast.probabilityCode().setNull(forecast.probabilityCode().isNull() && symbolProbability == -1);
//
//							probabilityCode = (probabilityCode > symbolProbability) ? probabilityCode : symbolProbability;
//							forecast.probabilityCode().set(ProbabilityCodeImpl.GetByID(probabilityCode));
//
//							// parse precipitation
//							if (forecast.precipitation().isNull() == true)
//							{
//								tmp = location.getElementsByTagName("precipitation");
//								
//								if (tmp.getLength() >= 1)
//									forecast.precipitation().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
//
//								forecast.precipitation().setNull(tmp.getLength() == 0);
//							}
//							
//							// parse symbol
//							if (forecast.symbol().isNull() == true)
//							{
//								tmp = location.getElementsByTagName("symbol");
//								
//								if (tmp.getLength() >= 1)
//									forecast.symbol().set(WeatherSymbolImpl.GetByID(Integer.parseInt(((Element) tmp.item(0)).getAttribute("number"))));
//	
//								forecast.symbol().setNull(tmp.getLength() == 0);
//							}
//						}
//					}
//				}
//				
//				// update the forecast array
//				forecasts.update(new WeatherForecastUpdateInImpl(data));
//				//wupcoming.update(new WeatherForcastUpdateUpcomingImpl(data));
//			}
//		}
//		catch (Exception e)
//		{
//			log.log(Level.SEVERE, e.getMessage(), e);
//		}
	}
		
	@Override
	public String getServiceURL() {		
		return SERVICE_URL + 
				"lat=" + location.getLatitude() + 
				";lon=" + location.getLongitude() + 
				";msl=" + location.getHeight();
	}
}
