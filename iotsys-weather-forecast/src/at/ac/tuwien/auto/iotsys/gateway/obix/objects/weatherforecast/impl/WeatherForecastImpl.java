package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import java.util.ArrayList;
import java.util.TimeZone;

import at.ac.tuwien.auto.iotsys.obix.OperationHandler;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecast;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastFilter;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastQueryResult;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastRecord;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastUpdateIn;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastUpdateOut;

import obix.*;

public class WeatherForecastImpl extends Obj implements WeatherForecast {
	
	private ArrayList<WeatherForecastRecordImpl> dataRecords = null;
	
	private Int count = new Int("count");
	private Abstime start = new Abstime("start");
	private Abstime end = new Abstime("end");
	private Str tz = new Str("tz", "UTC");
	private Op query = new Op("query", 
		new Contract(WeatherForecastFilter.CONTRACT),
		new Contract(WeatherForecastQueryResult.CONTRACT));
	private Op update = new Op("update", 
			new Contract(WeatherForecastUpdateIn.CONTRACT),
			new Contract(WeatherForecastUpdateOut.CONTRACT));
	private Feed feed = new Feed("feed",
		new Contract(WeatherForecastFilter.CONTRACT),
		new Contract(WeatherForecastRecord.CONTRACT));
	
	public WeatherForecastImpl() {		
		setIs(new Contract(WeatherForecast.CONTRACT));
		
		count.setHref(new Uri("count"));
		start.setHref(new Uri("start"));
		end.setHref(new Uri("end"));
		tz.setHref(new Uri("tz"));
				
		start.set(0, TimeZone.getTimeZone(tz.get()));
		end.set(0, TimeZone.getTimeZone(tz.get()));
				
		query.setHref(new Uri("query"));
		query.setOperationHandler(new OperationHandler() {
			public Obj invoke(Obj in) {
				return query(in);
			}
		});
		
		update.setHref(new Uri("update"));
		update.setOperationHandler(new OperationHandler() {
			public Obj invoke(Obj in) {
				return update(in);
			}
		});
		
		feed.setHref(new Uri("feed"));
		
		add(count);
		add(start);
		add(end);
		add(tz);
		add(query);
		add(update);
		add(feed);
		
		dataRecords = new ArrayList<WeatherForecastRecordImpl>();
	}
	
	public Obj query(Obj in) {
		long limit = dataRecords.size();
		boolean startNull = false;
		boolean endNull = false;
		ArrayList<Obj> results = new ArrayList<Obj>();
		WeatherForecastFilter filter = (WeatherForecastFilter) in;
		
		if (filter.limit().isNull() == false)
			limit = filter.limit().get();
		
		startNull = filter.start().isNull();
		endNull = filter.end().isNull();
		
		for (WeatherForecastRecordImpl rec : dataRecords)
		{			
			if (results.size() >= limit)
				break;
			
			if (startNull == false && rec.timestamp().get() < filter.start().get())
				continue;
			
			if (endNull == false && rec.timestamp().get() > filter.end().get())
				break;
			/*
			// update the timestamp's timezone if necessary
			if (rec.timestamp().getTz() != tz.get())
				rec.timestamp().set(rec.timestamp().get(), TimeZone.getTimeZone(tz.get()));
			*/
			results.add(rec);
		}
		
		return new WeatherForecastQueryResultImpl(results);
	}
	
	public Obj update(Obj in) {
		int updated = 0;
		int added = 0;
		boolean found;
		WeatherForecastUpdateIn update = (WeatherForecastUpdateIn) in;
						
		for (Obj o : update.data().list())
		{			
			WeatherForecastRecord rec = (WeatherForecastRecord) o;
			
			// store records with same timezone only
			rec.timestamp().set(rec.timestamp().get(), TimeZone.getTimeZone(tz.get()));
			
			found = false;
			
			for (int i=0; i < dataRecords.size() && found == false; i++)
			{
				if (rec.timestamp().get() < dataRecords.get(i).timestamp().get())
				{
					// insert record
					dataRecords.add(i, new WeatherForecastRecordImpl(rec));
					
					added++;
					found = true;
				}
				else if (rec.timestamp().get() == dataRecords.get(i).timestamp().get())
				{
					// update record
					dataRecords.get(i).setAll(rec);
										
					updated++;
					found = true;
				}
			}
			
			if (found == false)
			{
				// append record
				dataRecords.add(new WeatherForecastRecordImpl(rec));
				
				added++;
			}
		}
		
		// remove outdated forecasts
		long now = System.currentTimeMillis();
		
		while (dataRecords.size() > 0 && now > dataRecords.get(0).timestamp().get())
			dataRecords.remove(0);
				
		// update count
		count.set(dataRecords.size(), false);
		
		// update start, end
		if (dataRecords.size() > 0)
		{
			start.set(dataRecords.get(0).timestamp().getMillis(), TimeZone.getTimeZone(tz.get()));
			end.set(dataRecords.get(dataRecords.size()-1).timestamp().getMillis(), TimeZone.getTimeZone(tz.get()));
		}
		
		start.setNull(dataRecords.size() == 0);
		end.setNull(dataRecords.size() == 0);
		
		return new WeatherForecastUpdateOutImpl(added, updated, dataRecords.size(), 
					new Abstime(start.getMillis(), start.getTimeZone()), 
					new Abstime(end.getMillis(), end.getTimeZone()));
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
	public Str tz() {
		return tz;
	}

	@Override
	public Op query() {
		return query;
	}
	
	@Override
	public Op update() {
		return update;
	}

	@Override
	public Feed feed() {
		return feed;
	}

	public void clearForecasts() {
		dataRecords.clear();
		
		count.set(0, false);
		start.setNull(true);
		end.setNull(true);
	}
}
