package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastFilter;
import obix.*;

public class WeatherForecastFilterImpl extends Obj implements
		WeatherForecastFilter {
	
	private Int limit = new Int("limit");
	private Abstime start = new Abstime("start");
	private Abstime end = new Abstime("end");
	

	public WeatherForecastFilterImpl(int limit, Abstime start, Abstime end) {
		setIs(new Contract(WeatherForecastFilterImpl.CONTRACT));
		
		if (limit > 0)
			this.limit.setSilent(limit);

		this.limit.setNull(limit <= 0);
		
		if (start != null)
			this.start.set(start.getMillis(), start.getTimeZone());

		this.start.setNull(start == null);
		
		if (end != null)
			this.end.set(end.getMillis(), end.getTimeZone());
		
		this.end.setNull(end == null);
		
		add(this.limit);
		add(this.start);
		add(this.end);
	}

	@Override
	public Int limit() {
		return limit;
	}

	@Override
	public Abstime start() {
		return start;
	}

	@Override
	public Abstime end() {
		return end;
	}
}
