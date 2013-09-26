package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast;

import obix.*;

public interface WeatherForecastLocation extends IObj {

	public static final String CONTRACT = "iot:WeatherForecastLocation";

	public static final String descriptionContract = "<str name='description'/>";
	public Str description();
	
	public static final String latitudeContract = "<real name='latitude' val='0' min='-90' max='90'/>";
	public Real latitude();

	public static final String longitudeContract = "<real name='longitude' val='0' min='-180' max='180'/>";
	public Real longitude();

	public static final String heightContract = "<int name='height' val='0'/>";
	public Int height();
}
