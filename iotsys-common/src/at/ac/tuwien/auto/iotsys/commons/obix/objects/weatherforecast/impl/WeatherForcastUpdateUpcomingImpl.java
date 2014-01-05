package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl;

import obix.Contract;
import obix.List;
import obix.Obj;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.UpcomingWeather;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForcastUpdateUpcoming;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastUpdateIn;


public class WeatherForcastUpdateUpcomingImpl extends Obj implements WeatherForcastUpdateUpcoming{

	private List data = new List("data", new Contract(UpcomingWeather.CONTRACT));
	
	public WeatherForcastUpdateUpcomingImpl(java.util.List<Obj> newForecasts) {
		setIs(new Contract(WeatherForecastUpdateIn.CONTRACT));
		
		data.setNull(false);
		
		if (newForecasts != null)
		{
			for (Obj o : newForecasts)
			{
				UpcomingWeatherImpl forecast = (UpcomingWeatherImpl) o;
				
				data.add(forecast);
			}
		}
		
		add(data);
	}
	
	@Override
	public List data() {
		return data;
	}

}
