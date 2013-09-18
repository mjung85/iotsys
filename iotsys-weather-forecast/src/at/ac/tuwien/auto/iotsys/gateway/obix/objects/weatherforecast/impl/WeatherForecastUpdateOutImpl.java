package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastUpdateOut;
import obix.*;

public class WeatherForecastUpdateOutImpl extends Obj implements
		WeatherForecastUpdateOut {
	
	private Int numAdded = new Int("num-added");
	private Int numUpdated = new Int("num-updated");
	private Int newCount = new Int("new-count");
	private Abstime newStart = new Abstime("new-start");
	private Abstime newEnd = new Abstime("new-end");
	
	public WeatherForecastUpdateOutImpl(int added, int updated, int count, Abstime start, Abstime end) {
		setIs(new Contract(WeatherForecastUpdateOut.CONTRACT));
		
		numAdded.setSilent(added);
		numAdded.setNull(false);
		
		numUpdated.setSilent(updated);
		numUpdated.setNull(false);
		
		newCount.setSilent(count);
		newCount.setNull(false);
		
		if (start != null)
			newStart.set(start.getMillis(), start.getTimeZone());

		newStart.setNull(start == null);
		
		if (end != null)
			newEnd.set(end.getMillis(), end.getTimeZone());

		newEnd.setNull(end == null);
		
		add(numAdded);
		add(numUpdated);
		add(newCount);
		add(newStart);
		add(newEnd);
	}

	@Override
	public Int numAdded() {
		return numAdded;
	}

	@Override
	public Int numUpdated() {
		return numUpdated;
	}

	@Override
	public Int newCount() {
		return newCount;
	}

	@Override
	public Abstime newStart() {
		return newStart;
	}

	@Override
	public Abstime newEnd() {
		return newEnd;
	}

}
