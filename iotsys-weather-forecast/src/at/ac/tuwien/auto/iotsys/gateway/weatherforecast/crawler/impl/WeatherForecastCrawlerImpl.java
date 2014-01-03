package at.ac.tuwien.auto.iotsys.gateway.weatherforecast.crawler.impl;

import java.util.logging.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastCrawler;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastRecord;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForcastUpcomingWeatherImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastLocationImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;

import obix.*;

public class WeatherForecastCrawlerImpl extends Obj implements WeatherForecastCrawler {

	private static final Logger log = Logger.getLogger(WeatherForecastCrawlerImpl.class.getName());
	
	protected WeatherForecastConnector connector;
	
	//protected WeatherForecastImpl weatherNow;
	protected WeatherForecastLocationImpl location;
	protected WeatherForecastImpl forecasts;
	protected WeatherForcastUpcomingWeatherImpl upcoming;

	public WeatherForecastCrawlerImpl(WeatherForecastLocationImpl location, WeatherForecastConnector connector) 
				throws FactoryConfigurationError, ParserConfigurationException {
		
		//Obj root = null;
		//root.add(new Ref(WeatherForecastCrawler.CONTRACT));
				
		//		String.valueOf(this.location.)), devRoot.getHref()));
		
		
		setIs(new Contract(WeatherForecastCrawler.CONTRACT));
		
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
		this.location.setWritable(true);
		
		//objectBroker.addHistoryToDatapoints(this.location,100);
		
		this.location.setHidden(true);
		
		Ref refLocation = new Ref();
		refLocation.setHref(new Uri("location"));
		add(refLocation);
		
		add(this.location);
		
		
		
		// initialize forecasts array
		this.forecasts = new WeatherForecastImpl();
		this.forecasts.setName("forecasts");
		this.forecasts.setHref(new Uri("forecasts"));
		
	
		this.forecasts.setHidden(true);
				
		
		
		Ref refForcast = new Ref();
		
		refForcast.setHref(new Uri("forecasts"));
		
		add(refForcast);
		
		add(this.forecasts);
		
		
		this.upcoming = new WeatherForcastUpcomingWeatherImpl();
		this.upcoming.setName("upcoming");
		this.upcoming.setHref(new Uri("upcoming"));
		
		
		
	
		
		
		add(this.upcoming);
		
		
//		WeatherForecastRecord rec = new WeatherForecastRecordImpl();
//		
//		WeatherForecastRecordImpl upcommingForcast = new WeatherForecastRecordImpl(rec);
//		
//		add(upcommingForcast);
		
//		this.weatherNow = new WeatherForecastImpl();
//		this.weatherNow.setName("weatherNow");
//		this.weatherNow.setHref(new Uri("weatherNow"));
//		add(this.weatherNow);
		
	}
	
	public Obj location() {
		return location;
	}
	
	public Obj forecasts() {
		return forecasts;
	}
	

	/*
	 * Resets the crawler, i.e., clears the forecasts array and triggers a 
	 * refresh after the crawler's location has been modified.
	 */
	@Override
	public void reset() {	
		// clear forecasts array
		this.forecasts.clearForecasts();
		
		// trigger object refresh
		setLastRefresh(0);
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
	}
	
	/*
	 * Override this method to refresh the crawler object using the 
	 * crawler-specific web service.
	 */
	@Override
	public void refreshObject(){
		
		this.upcoming.temperature().setReal(7.7);
		
	}
}
