package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

import obix.Enum;
import obix.IObj;

public interface WeatherControl extends IObj{
	public static final String CONTRACT = "iot:weatherControl";

	public Enum manualOverwrite();
}
