package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import java.util.logging.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast.WeatherForecastConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastCrawler;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastFilter;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastLocation;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastQueryResult;

import obix.*;
import obix.contracts.Nil;
import obix.contracts.impl.NilImpl;

public class WeatherForecastCrawlerImpl extends Obj implements WeatherForecastCrawler {

	private static final Logger log = Logger.getLogger(WeatherForecastCrawlerImpl.class.getName());
	
	protected WeatherForecastConnector connector;
	
	protected WeatherForecastLocationImpl location;
	protected WeatherForecastImpl forecasts;
	protected Op set;

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
		
		// initialize forecasts array
		this.forecasts = new WeatherForecastImpl();
		this.forecasts.setName("forecasts");
		this.forecasts.setHref(new Uri("forecasts"));
		
		this.set = new Op("setLocation", 
					new Contract(WeatherForecastLocation.CONTRACT),
					new Contract(Nil.CONTRACT));
		this.set.setHref(new Uri("setLocation"));
		this.set.setOperationHandler(new at.ac.tuwien.auto.iotsys.obix.OperationHandler() {
			public Obj invoke(Obj in) {
				setLocation(in);
				
				return new NilImpl();
			}
		});
		
		add(this.location);
		add(this.forecasts);
		add(this.set);
	}
	
	public Obj location() {
		return location;
	}
	
	public Obj forecasts() {
		return forecasts;
	}

	public Op setLocation() {
		return set;
	}
	
	public void setLocation(Obj location) {
		if (location != null)
		{
			WeatherForecastLocation l = (WeatherForecastLocation)location;
			
			this.location.description().set(l.description().get());
			this.location.latitude().set(l.latitude().get());
			this.location.longitude().set(l.longitude().get());
			this.location.height().set(l.height().get());
			
			// clear forecasts array
			this.forecasts.clearForecasts();
		}
	}
	
	public WeatherForecastLocationImpl getLocation() {
		return location;
	}
	
	/*
	 * Override this method to return the crawler-specific service URL using 
	 * the crawler's location
	 */
	public String getServiceURL() {
		return "";
	}
	
	@Override
	public void initialize(){
		super.initialize();	
	}
	
	@Override
	public void refreshObject(){
		log.finest("Refreshing weather forecast.");
	}
}
