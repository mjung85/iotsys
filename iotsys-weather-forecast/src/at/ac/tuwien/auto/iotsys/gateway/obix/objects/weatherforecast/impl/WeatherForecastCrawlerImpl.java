package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import java.util.logging.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastCrawler;

import obix.*;

public class WeatherForecastCrawlerImpl extends Obj implements WeatherForecastCrawler {

	private static final Logger log = Logger.getLogger(WeatherForecastCrawlerImpl.class.getName());
	
	protected WeatherForecastConnector connector;
	
	protected WeatherForecastLocationImpl location;
	protected WeatherForecastImpl forecasts;

	public WeatherForecastCrawlerImpl(WeatherForecastLocationImpl location, WeatherForecastConnector connector) 
				throws FactoryConfigurationError, ParserConfigurationException {
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
		
		// initialize forecasts array
		this.forecasts = new WeatherForecastImpl();
		this.forecasts.setName("forecasts");
		this.forecasts.setHref(new Uri("forecasts"));
				
		add(this.location);
		add(this.forecasts);
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
	}
}
