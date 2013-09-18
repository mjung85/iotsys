package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastQueryResult;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastRecord;
import obix.*;

public class WeatherForecastQueryResultImpl extends Obj implements
		WeatherForecastQueryResult {

	private Int count = new Int("count");
	private Abstime start = new Abstime("start");
	private Abstime end = new Abstime("end");
	private List data = new List("data", new Contract(WeatherForecastRecord.CONTRACT));
	
	public WeatherForecastQueryResultImpl(java.util.List<Obj> forecasts) {
		setIs(new Contract(WeatherForecastQueryResult.CONTRACT));
		
		data.setNull(false);
		
		if (forecasts != null) {
			for(Obj obj : forecasts) {
				WeatherForecastRecordImpl forecast = (WeatherForecastRecordImpl) obj;
				
				data.add(forecast);
				if (data.size() == 1 || start.get() > forecast.timestamp().get())
					start.set(forecast.timestamp().get(), forecast.timestamp().getTimeZone());
				
				if (data.size() == 1 || end.get() < forecast.timestamp().get())
					end.set(forecast.timestamp().get(), forecast.timestamp().getTimeZone());
			}
		}
		
		if (data.size() == 0) {
			start.setNull(true);
			end.setNull(true);
		}
		
		count.setSilent(data.size());
		count.setNull(false);
		
		add(count);
		add(start);
		add(end);
		add(data);
	}

	@Override
	public Int count() {
		return count;
	}

	@Override
	public Abstime start() {
		return start;
	}

	@Override
	public Abstime end() {
		return end;
	}

	@Override
	public List data() {
		return data;
	}

}
