package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast;

import obix.*;

public interface WeatherForecastUpdateOut extends IObj {

	public static final String CONTRACT = "iot:WeatherForecastUpdateOut";
		
	public static final String numAddedContract = "<int name='num-added'/>";
	public Int numAdded();
	
	public static final String numUpdatedContract = "<int name='num-updated'/>";
	public Int numUpdated();
	
	public static final String newCountContract = "<int name='new-count'/>";
	public Int newCount();

	public static final String newStartContract = "<abstime name='new-start' null='true'/>";
	public Abstime newStart();

	public static final String newEndContract = "<abstime name='new-end' null='true'/>";
	public Abstime newEnd();
}
