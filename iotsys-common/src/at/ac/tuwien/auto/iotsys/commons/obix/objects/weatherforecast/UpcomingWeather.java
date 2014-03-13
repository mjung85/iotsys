package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

import obix.Abstime;
import obix.IObj;
import obix.Int;
import obix.Obj;
import obix.Enum;
import obix.Real;
import obix.Str;

public interface UpcomingWeather extends IObj {

	public static final String CONTRACT = "iot:UpcomingWeather";
		
	public static final String WEATHER_TIMESTAMP = "<abstime name='timestamp' val='1969-12-31T19:00:00.000-05:00'tz='null'/>";
	public Abstime timestamp();
	
	public static final String WEATHER_PROBABILITYCODE = "<enum name='probabilityCode' val='null' range='iot:probabilityCode'/>";
	public Enum probabilitycode();
	
	public static final String WEATHER_TEMPERATURE = "<real name='temperature' val='0' unit='obix:units/celsius'/>";
	public Real temperature();
	
	public static final String WEATHER_HUMIDITY = "<real name='humidity' val='0' unit='obix:units/percent'/>";
	public Real humidity();
	
	public static final String WEATHER_PRESSURE = "<real name='pressure' val='0' unit='obix:units/hectopascal'/>";
	public Real pressure();
	
	public static final String WEATHER_PRECIPITATION = "<real name='precipitation' val='0' unit='obix:units/millimeter'/>";
	public Real precipitation();
	
	public static final String WEATHER_CLOUDINESS = "<real name='cloudiness' val='0' unit='obix:units/percent'/>";
	public Real cloudiness();
	
	public static final String WEATHER_FOG = "<real name='fog' val='0' unit='obix:units/percent'/>";
	public Real fog();
	
	public static final String WEATHER_WINDSPEED = "<int name='windSpeed' val='0' unit='obix:units/beaufort'/>";
	public Int windspeed();
	
	public static final String DEWPOINTTEMPERATURE = "<real name='dewpointTemperature' null='true'/>";
	public Real dewpointTemperature();
	
	public static final String WINDDIRECTION = "<str name='windDirection' val='null'/>";
	public Str windDirection();
}
