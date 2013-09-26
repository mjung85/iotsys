package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.devices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
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

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl.ProbabilityCodeImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl.WeatherForecastCrawlerImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl.WeatherForecastLocationImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl.WeatherForecastRecordImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl.WeatherForecastUpdateInImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl.WeatherSymbolImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;

public class WeatherForecastCrawlerYR_NO extends WeatherForecastCrawlerImpl {

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
						
		try 
		{
			// parse response 
			Document doc = connector.getWeatherForecastAsXML(this.getServiceURL());
			
			if (doc != null)
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
				ArrayList<Obj> data = new ArrayList<Obj>();
				Hashtable<Date, WeatherForecastRecordImpl> hashTable = new Hashtable<Date, WeatherForecastRecordImpl>(); 
				WeatherForecastRecordImpl forecast;
				
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

						// check if a weather forecast record already exists for 
						// the given timestamp -- if not create a new one
						if ((forecast = hashTable.get(to)) == null)
						{
							forecast = new WeatherForecastRecordImpl();
							forecast.precipitation().setNull(true);
							forecast.symbol().setNull(true);
							
							// append the record to the update array list
							data.add(forecast);
							
							// add the record to the hash table
							hashTable.put(to, forecast);
							
							/*
							 * set timestamp
							 */
							String utcOffset = time.getAttribute("to");
							 
							// strip date
							utcOffset = utcOffset.substring(utcOffset.indexOf('T'));
							
							// 'Z' means utc
							utcOffset = utcOffset.replaceAll("Z", "+00:00");

							// strip time (note that either '+' or '-' is present)
							utcOffset = utcOffset.substring(utcOffset.lastIndexOf('+') + utcOffset.lastIndexOf('-') + 1);
							
							forecast.timestamp().set(to.getTime(), TimeZone.getTimeZone("GMT" + utcOffset));
						}

						// check the time element's type
						if (from.equals(to))
						{			
							// parse probability code
							int temperatureProbability = -1;
							int windProbability = -1;
							int probabilityCode;
							
							tmp = location.getElementsByTagName("temperatureProbability");
							
							if (tmp.getLength() >= 1)
								temperatureProbability = Integer.parseInt(((Element) tmp.item(0)).getAttribute("value"));

							tmp = location.getElementsByTagName("windProbability");
							
							if (tmp.getLength() >= 1)
								windProbability = Integer.parseInt(((Element) tmp.item(0)).getAttribute("value"));

							forecast.probabilityCode().setNull(temperatureProbability == -1 && windProbability == -1);

							probabilityCode = (temperatureProbability > windProbability) ? temperatureProbability : windProbability;
							forecast.probabilityCode().set(ProbabilityCodeImpl.GetByID(probabilityCode));
							
							// parse temperature
							tmp = location.getElementsByTagName("temperature");
							
							if (tmp.getLength() >= 1)
								forecast.temperature().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));

							forecast.temperature().setNull(tmp.getLength() == 0);
							
							// parse wind speed
							tmp = location.getElementsByTagName("windSpeed");
							
							if (tmp.getLength() >= 1)
								forecast.windSpeed().set(Integer.parseInt(((Element) tmp.item(0)).getAttribute("beaufort")));

							forecast.windSpeed().setNull(tmp.getLength() == 0);
							
							// parse humidity
							tmp = location.getElementsByTagName("humidity");
							
							if (tmp.getLength() >= 1)
								forecast.humidity().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));

							forecast.humidity().setNull(tmp.getLength() == 0);
							
							// parse pressure
							tmp = location.getElementsByTagName("pressure");
							
							if (tmp.getLength() >= 1)
								forecast.pressure().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));

							forecast.pressure().setNull(tmp.getLength() == 0);
							
							// parse cloudiness
							tmp = location.getElementsByTagName("cloudiness");
							
							if (tmp.getLength() >= 1)
								forecast.cloudiness().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));

							forecast.cloudiness().setNull(tmp.getLength() == 0);
							
							// parse fog
							tmp = location.getElementsByTagName("fog");
							
							if (tmp.getLength() >= 1)
								forecast.fog().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));

							forecast.fog().setNull(tmp.getLength() == 0);
						}
						else // time element describes period of time
						{
							/*
							 * there may exist multiple time periods; former periods are shorter and should be preferred
							 */
							
							// parse probability code
							int symbolProbability = -1;
							int probabilityCode = ProbabilityCodeImpl.GetByName(forecast.probabilityCode().get());
							
							tmp = location.getElementsByTagName("symbolProbability");
							
							if (tmp.getLength() >= 1)
								symbolProbability = Integer.parseInt(((Element) tmp.item(0)).getAttribute("value"));

							forecast.probabilityCode().setNull(forecast.probabilityCode().isNull() && symbolProbability == -1);

							probabilityCode = (probabilityCode > symbolProbability) ? probabilityCode : symbolProbability;
							forecast.probabilityCode().set(ProbabilityCodeImpl.GetByID(probabilityCode));

							// parse precipitation
							if (forecast.precipitation().isNull() == true)
							{
								tmp = location.getElementsByTagName("precipitation");
								
								if (tmp.getLength() >= 1)
									forecast.precipitation().set(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));

								forecast.precipitation().setNull(tmp.getLength() == 0);
							}
							
							// parse symbol
							if (forecast.symbol().isNull() == true)
							{
								tmp = location.getElementsByTagName("symbol");
								
								if (tmp.getLength() >= 1)
									forecast.symbol().set(WeatherSymbolImpl.GetByID(Integer.parseInt(((Element) tmp.item(0)).getAttribute("number"))));
	
								forecast.symbol().setNull(tmp.getLength() == 0);
							}
						}
					}
				}
				
				// update the forecast array
				forecasts.update(new WeatherForecastUpdateInImpl(data));
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
		
	@Override
	public String getServiceURL() {		
		return SERVICE_URL + 
				"lat=" + location.getLatitude() + 
				";lon=" + location.getLongitude() + 
				";msl=" + location.getHeight();
	}
}
