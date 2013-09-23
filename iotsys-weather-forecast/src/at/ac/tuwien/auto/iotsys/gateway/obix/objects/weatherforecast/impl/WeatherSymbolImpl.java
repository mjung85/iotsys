package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

/*
 * symbols according to http://om.yr.no/forklaring/symbol/
 */
public class WeatherSymbolImpl {
		
	public static final String CONTRACT = "iot:weatherSymbol";
	
	public static final String NAME = "weatherSymbol";

	public static final int ID_UNKNOWN = -1;
	public static final int ID_SUN = 1;
	public static final int ID_FAIR = 2;
	public static final int ID_PARTLY_CLOUDY = 3;
	public static final int ID_CLOUDY = 4;
	public static final int ID_RAIN_SHOWERS = 5;
	public static final int ID_RAIN_SHOWERS_THUNDER = 6;
	public static final int ID_SLEET_SHOWERS = 7;
	public static final int ID_SNOW_SHOWERS = 8;
	public static final int ID_RAIN = 9;
	public static final int ID_HEAVY_RAIN = 10;
	public static final int ID_HEAVY_RAIN_THUNDER = 11;
	public static final int ID_SLEET = 12;
	public static final int ID_SNOW = 13;
	public static final int ID_SNOW_THUNDER = 14;
	public static final int ID_FOG = 15;
	public static final int ID_SLEET_SHOWERS_THUNDER = 20;
	public static final int ID_SNOW_SHOWERS_THUNDER = 21;
	public static final int ID_RAIN_THUNDER = 22;
	public static final int ID_SLEET_THUNDER = 23;

	public static final String NAME_UNKNOWN = "unknown";
	public static final String NAME_SUN = "sun";
	public static final String NAME_FAIR = "fair";
	public static final String NAME_PARTLY_CLOUDY = "partly cloudy";
	public static final String NAME_CLOUDY = "cloudy";
	public static final String NAME_RAIN_SHOWERS = "rain showers";
	public static final String NAME_RAIN_SHOWERS_THUNDER = "rain showers and thunder";
	public static final String NAME_SLEET_SHOWERS = "sleet showers";
	public static final String NAME_SNOW_SHOWERS = "snow showers";
	public static final String NAME_RAIN = "rain";
	public static final String NAME_HEAVY_RAIN = "heavy rain";
	public static final String NAME_HEAVY_RAIN_THUNDER = "heavy rain and thunder";
	public static final String NAME_SLEET = "sleet";
	public static final String NAME_SNOW = "snow";
	public static final String NAME_SNOW_THUNDER = "snow and thunder";
	public static final String NAME_FOG = "fog";
	public static final String NAME_SLEET_SHOWERS_THUNDER = "sleet showers and thunder";
	public static final String NAME_SNOW_SHOWERS_THUNDER = "snow showers and thunder";
	public static final String NAME_RAIN_THUNDER = "rain and thunder";
	public static final String NAME_SLEET_THUNDER = "sleet and thunder";
	
	public static int GetByName(String name) {
		int id;
		
		if (name == null)
			name = NAME_UNKNOWN;
		
		name = name.toLowerCase();
		
		if (name.equals(NAME_SUN))
			id = ID_SUN;
		else if (name.equals(NAME_FAIR))
			id = ID_FAIR;
		else if (name.equals(NAME_PARTLY_CLOUDY))
			id = ID_PARTLY_CLOUDY;
		else if (name.equals(NAME_CLOUDY))
			id = ID_CLOUDY;
		else if (name.equals(NAME_RAIN_SHOWERS))
			id = ID_RAIN_SHOWERS;
		else if (name.equals(NAME_RAIN_SHOWERS_THUNDER))
			id = ID_RAIN_SHOWERS_THUNDER;
		else if (name.equals(NAME_SLEET_SHOWERS))
			id = ID_SLEET_SHOWERS;
		else if (name.equals(NAME_SNOW_SHOWERS))
			id = ID_SNOW_SHOWERS;
		else if (name.equals(NAME_RAIN))
			id = ID_RAIN;
		else if (name.equals(NAME_HEAVY_RAIN))
			id = ID_HEAVY_RAIN;
		else if (name.equals(NAME_HEAVY_RAIN_THUNDER))
			id = ID_HEAVY_RAIN_THUNDER;
		else if (name.equals(NAME_SLEET))
			id = ID_SLEET;
		else if (name.equals(NAME_SNOW))
			id = ID_SNOW;
		else if (name.equals(NAME_SNOW_THUNDER))
			id = ID_SNOW_THUNDER;
		else if (name.equals(NAME_FOG))
			id = ID_FOG;
		else if (name.equals(NAME_SLEET_SHOWERS_THUNDER))
			id = ID_SLEET_SHOWERS_THUNDER;
		else if (name.equals(NAME_SNOW_SHOWERS_THUNDER))
			id = ID_SNOW_SHOWERS_THUNDER;
		else if (name.equals(NAME_RAIN_THUNDER))
			id = ID_RAIN_THUNDER;
		else if (name.equals(NAME_SLEET_THUNDER))
			id = ID_SLEET_THUNDER;
		else
			id = ID_UNKNOWN;

		return id;
	}
	
	public static String GetByID(int id) {
		String name;

		if (id == ID_SUN)
			name = NAME_SUN;
		else if (id == ID_FAIR)
			name = NAME_FAIR;
		else if (id == ID_PARTLY_CLOUDY)
			name = NAME_PARTLY_CLOUDY;
		else if (id == ID_CLOUDY)
			name = NAME_CLOUDY;
		else if (id == ID_RAIN_SHOWERS)
			name = NAME_RAIN_SHOWERS;
		else if (id == ID_RAIN_SHOWERS_THUNDER)
			name = NAME_RAIN_SHOWERS_THUNDER;
		else if (id == ID_SLEET_SHOWERS)
			name = NAME_SLEET_SHOWERS;
		else if (id == ID_SNOW_SHOWERS)
			name = NAME_SNOW_SHOWERS;
		else if (id == ID_RAIN)
			name = NAME_RAIN;
		else if (id == ID_HEAVY_RAIN)
			name = NAME_HEAVY_RAIN;
		else if (id == ID_HEAVY_RAIN_THUNDER)
			name = NAME_HEAVY_RAIN_THUNDER;
		else if (id == ID_SLEET)
			name = NAME_SLEET;
		else if (id == ID_SNOW)
			name = NAME_SNOW;
		else if (id == ID_SNOW_THUNDER)
			name = NAME_SNOW_THUNDER;
		else if (id == ID_FOG)
			name = NAME_FOG;
		else if (id == ID_SLEET_SHOWERS_THUNDER)
			name = NAME_SLEET_SHOWERS_THUNDER;
		else if (id == ID_SNOW_SHOWERS_THUNDER)
			name = NAME_SNOW_SHOWERS_THUNDER;
		else if (id == ID_RAIN_THUNDER)
			name = NAME_RAIN_THUNDER;
		else if (id == ID_SLEET_THUNDER)
			name = NAME_SLEET_THUNDER;
		else
			name = NAME_UNKNOWN;

		return name;
	}
}
