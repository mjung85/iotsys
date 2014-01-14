package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

public interface WeatherManualOverwrite {
	public static final String CONTRACT = "iot:WeatherManualOverwrite";
	public static final String NAME = "weatherSymbol";

	public static final int ID_OFF = -1;
	public static final int ID_STORM_WARNING = 1;
	public static final int ID_STORM_ALARM = 2;

	public static final String NAME_OFF = "unknown";
	public static final String NAME_STORM_WARNING = "sun";
	public static final String NAME_STORM_ALARM  = "fair";
	
}
