package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastRecord;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastUpdateIn;
import obix.*;

public class WeatherForecastUpdateInImpl extends Obj implements
		WeatherForecastUpdateIn {
	
	private List data = new List("data", new Contract(WeatherForecastRecord.CONTRACT));
	
	public WeatherForecastUpdateInImpl(java.util.List<Obj> newForecasts) {
		setIs(new Contract(WeatherForecastUpdateIn.CONTRACT));
		
		data.setNull(false);
		
		if (newForecasts != null)
		{
			for (Obj o : newForecasts)
			{
				WeatherForecastRecordImpl forecast = (WeatherForecastRecordImpl) o;
				
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
