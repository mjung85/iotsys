package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

import obix.*;

public interface WeatherObject extends IObj {

	public static final String CONTRACT = "iot:WeatherForecast";

	public static final String locationContract = "<obj name='location' href='location' is='iot:WeatherForecastLocation'/>";
	public Obj location();
	
	public static final String upcomingContract = "<obj name='location' href='location' is='iot:UpcomingWeather'/>";
	public Obj upcoming();

	public String getServiceURL();
}
