package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherSymbol;

/*
 * symbols according to http://om.yr.no/forklaring/symbol/
 */
public class WeatherSymbolImpl implements WeatherSymbol {
		
	
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
