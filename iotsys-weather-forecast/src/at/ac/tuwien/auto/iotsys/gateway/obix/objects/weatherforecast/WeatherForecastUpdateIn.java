package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast;

import obix.*;

public interface WeatherForecastUpdateIn extends IObj {

	public static final String CONTRACT = "iot:WeatherForecastUpdateIn";
	
	public static final String dataContract = "<list name='data' of='iot:WeatherForecastRecord'/>";
	public List data();
}
