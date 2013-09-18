package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast;

import obix.*;

public interface WeatherForecastCrawler extends IObj {

	public static final String CONTRACT = "iot:WeatherForecastCrawler";

	public static final String locationContract = "<obj name='location' is='iot:WeatherForecastLocation'/>";
	public Obj location();

	public static final String forecastsContract = "<obj name='forecasts' is='iot:WeatherForecast'/>";
	public Obj forecasts();
	
	public static final String setLocationContract = "<op name='setLocation' href='setLocation' in='iot:WeatherForecastLocation' out='obix:Nil'/>";
	public Op setLocation();
}
