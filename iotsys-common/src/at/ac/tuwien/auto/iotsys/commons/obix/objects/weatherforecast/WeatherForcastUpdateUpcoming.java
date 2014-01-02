package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

import obix.IObj;
import obix.List;

public interface WeatherForcastUpdateUpcoming  extends IObj{
	
public static final String CONTRACT = "iot:WeatherForecastUpdateUpcoming";
	
	public static final String dataContract = "<list name='data' of='iot:WeatherForecastUpcoming'/>";
	public List data();
	

}
