package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

import obix.*;

public interface WeatherForecast extends IObj {
	
	public static final String CONTRACT = "iot:WeatherForecast";
	
	public static final String countContract = "<int name='count' val='0' min='0'/>";
	public Int count();

	public static final String startContract = "<abstime name='start' val='1969-12-31T19:00:00.000-05:00' null='true'/>";
	public Abstime start();

	public static final String endContract = "<abstime name='end' val='1969-12-31T19:00:00.000-05:00' null='true'/>";
	public Abstime end();
	  
	public static final String tzContract = "<str name='tz' null='true'/>";
	public Str tz();

	public static final String queryContract = "<op name='query' href='query' in='iot:WeatherForecastFilter' out='iot:WeatherForecastQueryResult'/>";
	public Op query();
	
	public static final String updateContract = "<op name='update' href='query' in='iot:WeatherForecastUpdateIn' out='iot:WeatherForecastUpdateOut'/>";
	public Op update();

	public static final String feedContract = "<feed name='feed' in='iot:WeatherForecastFilter' of='iot:WeatherForecastRecord'/>";
	public Feed feed();
}
