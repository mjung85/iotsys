package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

import obix.Enum;
import obix.*;

public interface WeatherForecastRecord extends IObj {
	
	public static final String CONTRACT = "iot:WeatherForecastRecord";

	public static final String timestampContract = "<abstime name='timestamp' val='1969-12-31T19:00:00.000-05:00' null='true'/>";
	public Abstime timestamp();

	public static final String probabilityCodeContract = "<enum name='probabilityCode' null='true' range='" + ProbabilityCode.CONTRACT + "'/>";
	public Enum probabilityCode();

	public static final String temperatureContract = "<real name='temperature' null='true'/>";
	public Real temperature();

	public static final String humidityContract = "<real name='humidity' min='0' max='100' null='true'/>";
	public Real humidity();

	public static final String pressureContract = "<real name='pressure' min='0' null='true'/>";
	public Real pressure();

	public static final String precipitationContract = "<real name='precipitation' min='0' null='true'/>";
	public Real precipitation();

	public static final String cloudinessContract = "<real name='cloudiness' min='0' max='100' null='true'/>";
	public Real cloudiness();
	
	public static final String fogContract = "<real name='fog' min='0' max='100' null='true'/>";
	public Real fog();

	public static final String windSpeedContract = "<int name='windSpeed' min='0' max='12' null='true'/>";
	public Int windSpeed();
	
	public static final String windDirection = "<str name='windDirection' val='null'/>";
	public Str windDirection();
	
	public static final String symbolContract = "<enum name='symbol' null='true' range='" + WeatherSymbol.CONTRACT + "'/>";
	public Enum symbol();
	
	public static final String dewpointTemperatureContract = "<real name='dewpointTemperature' null='true'/>";
	public Real dewpointTemperature();
}
