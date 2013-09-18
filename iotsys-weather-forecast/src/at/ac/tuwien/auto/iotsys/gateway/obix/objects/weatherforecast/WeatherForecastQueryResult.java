package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast;

import obix.*;

public interface WeatherForecastQueryResult extends IObj {
	
	public static final String CONTRACT = "iot:WeatherForecastQueryResult";

	public static final String countContract = "<int name='count' val='0' min='0'/>";
	public Int count();

	public static final String startContract = "<abstime name='start' val='1969-12-31T19:00:00.000-05:00' null='true'/>";
	public Abstime start();

	public static final String endContract = "<abstime name='end' val='1969-12-31T19:00:00.000-05:00' null='true'/>";
	public Abstime end();

	public static final String dataContract = "<list name='data' of='iot:WeatherForecastRecord'/>";
	public List data();
}
