package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl;

import java.util.ArrayList;
import java.util.TimeZone;

import obix.Abstime;
import obix.Contract;
import obix.Enum;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForcastUpcomingWeather;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastRecord;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastUpdateIn;


public class WeatherForcastUpcomingWeatherImpl extends Obj implements WeatherForcastUpcomingWeather {

	
	private Str tz = new Str("tz", "UTC");
	
	
	private ArrayList<WeatherForecastRecordImpl> dataRecords = null;
	private Abstime timestamp = new Abstime("timestamp");
	private Enum probabilitycode = new Enum("probabilityCode", "");
	private Real temperature = new Real("temperature");
	private Real humidity = new Real("humidity");
	private Real pressure = new Real("pressure");
	private Real precipitation = new Real("precipitation");
	private Real cloudiness = new Real("cloudiness");
	private Real fog = new Real("fog");
	private Int  windSpeed = new Int("windSpeed");
	private Real dewpointTemperature = new Real("dewpointTemperature");
	private Str windDirection = new Str("windDirection");
	
	
	public WeatherForcastUpcomingWeatherImpl(){
		setIs(new Contract(WeatherForcastUpcomingWeather.CONTRACT));
		
		probabilitycode.setRange(new Uri(ProbabilityCodeImpl.CONTRACT));
		
		temperature.setUnit(new Uri("obix:units/celsius"));
		humidity.setUnit(new Uri("obix:units/percent"));
		pressure.setUnit(new Uri("obix:units/hectopascal")); //100*kg^1*m^-1*s^-2
		precipitation.setUnit(new Uri("obix:units/millimeter")); //0.001*m^1
		cloudiness.setUnit(new Uri("obix:units/percent"));
		fog.setUnit(new Uri("obix:units/percent"));
		windSpeed.setUnit(new Uri("obix:units/beaufort"));
		dewpointTemperature.setUnit(new Uri("obix:units/celsius"));
		windDirection.setStr("null");
		
		add(timestamp);
		add(probabilitycode);
		add(temperature);
		add(humidity);
		add(pressure);
		add(precipitation);
		add(cloudiness);
		add(fog);
		add(windSpeed);
		add(windDirection);
		add(dewpointTemperature);

		dataRecords = new ArrayList<WeatherForecastRecordImpl>();
		
	}
	
	public WeatherForcastUpcomingWeatherImpl(WeatherForcastUpcomingWeather rec) {
		this();
		
		setAll(rec);
	}
	
	public void setAll(WeatherForcastUpcomingWeather rec) {
		timestamp.set(rec.timestamp().getMillis(), rec.timestamp().getTimeZone());
		
		if (ProbabilityCodeImpl.GetByName(rec.probabilitycode().get()) != ProbabilityCodeImpl.ID_UNKNOWN) {
			probabilitycode.set(rec.probabilitycode().get());
		}
		else {
			probabilitycode.set(ProbabilityCodeImpl.NAME_UNKNOWN);
		}
		
		temperature.set(rec.temperature().get());
		humidity.set(rec.humidity().get());
		pressure.set(rec.pressure().get());
		precipitation.set(rec.precipitation().get());
		cloudiness.set(rec.cloudiness().get());
		fog.set(rec.fog().get());
		windSpeed.set(rec.windspeed().get());
		windDirection.set(rec.windDirection().get());
		dewpointTemperature.set(rec.dewpointTemperature().get());
		
	}
	
	

		
//		while (dataRecords.size() > 0 && now > dataRecords.get(0).timestamp().get())
//			dataRecords.remove(0);
//				
//		// update count
//		count.set(dataRecords.size(), false);
//		
//		// update start, end
//		if (dataRecords.size() > 0)
//		{
//			start.set(dataRecords.get(0).timestamp().getMillis(), TimeZone.getTimeZone(tz.get()));
//			end.set(dataRecords.get(dataRecords.size()-1).timestamp().getMillis(), TimeZone.getTimeZone(tz.get()));
//		}
//		
//		start.setNull(dataRecords.size() == 0);
//		end.setNull(dataRecords.size() == 0);
//		
//		return new WeatherForecastUpdateOutImpl(added, updated, dataRecords.size(), 
//					new Abstime(start.getMillis(), start.getTimeZone()), 
//					new Abstime(end.getMillis(), end.getTimeZone()));
		
//	}
	

//	public void setAlll() {
//		weatherTimestamp.set(rec.weatherTimestamp().getMillis(), rec.weatherTimestamp().getTimeZone());
//		
//		if (ProbabilityCodeImpl.GetByName(rec.weatherProbabilitycode().get()) != ProbabilityCodeImpl.ID_UNKNOWN) {
//			weatherProbabilitycode.set(rec.weatherProbabilitycode().get());
//		}
//		else {
//			weatherProbabilitycode.set(ProbabilityCodeImpl.NAME_UNKNOWN);
//		}
		
		//weatherTemperature.set(rec.weatherTemperature().get());
//		weatherTemperature.set(9.8);
//		weatherHumidity.set(weatherHumidity.get());
//		weatherPressure.set(rec.weatherPressure().get());
//		weatherPrecipitation.set(rec.weatherPrecipitation().get());
//		weatherCloudiness.set(rec.weatherCloudiness().get());
//		weatherFog.set(rec.weatherFog().get());
//		weatherWindspeed.set(rec.weatherWindspeed().get());
		
//	}
//	
	
	@Override
	public Abstime timestamp() {
		return timestamp;
	}

	@Override
	public Enum probabilitycode() {
		return probabilitycode;
	}

	@Override
	public Real temperature() {
		System.out.println("Weather Temp upcoming impl: "+temperature);
		return temperature;
	}

	@Override
	public Real humidity() {
		return humidity;
	}

	@Override
	public Real pressure() {
		return pressure;
	}

	@Override
	public Real precipitation() {
		return precipitation;
	}

	@Override
	public Real cloudiness() {
		return cloudiness;
	}

	@Override
	public Real fog() {
		return fog;
	}

	@Override
	public Int windspeed() {
		return  windSpeed;
	}

	@Override
	public Real dewpointTemperature() {
		return dewpointTemperature;
	}

	@Override
	public Str windDirection() {
		return windDirection;
	}


}
