package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

public interface WeatherManualOverwrite {
	public static final String CONTRACT = "iot:WeatherManualOverwrite";
	public static final String NAME = "weatherManualOverwrite";

	public static final int ID_OFF = -1;
	public static final int ID_STORM_WARNING = 1;
	public static final int ID_STORM_ALARM = 2;

	public static final String NAME_OFF = "off";
	public static final String NAME_STORM_WARNING = "Storm warning";
	public static final String NAME_STORM_ALARM  = "Storm alarm";
	
}
