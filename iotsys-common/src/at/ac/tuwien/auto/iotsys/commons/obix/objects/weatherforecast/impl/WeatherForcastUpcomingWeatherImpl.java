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
	private Abstime weatherTimestamp = new Abstime("timestamp");
	private Enum weatherProbabilitycode = new Enum("probabilityCode", "");
	private Real weatherTemperature = new Real("temperature");
	private Real weatherHumidity = new Real("humidity");
	private Real weatherPressure = new Real("pressure");
	private Real weatherPrecipitation = new Real("precipitation");
	private Real weatherCloudiness = new Real("cloudiness");
	private Real weatherFog = new Real("fog");
	private Int weatherWindspeed = new Int("windSpeed");
	
	
	public WeatherForcastUpcomingWeatherImpl(){
		setIs(new Contract(WeatherForcastUpcomingWeather.CONTRACT));
		
		weatherProbabilitycode.setRange(new Uri(ProbabilityCodeImpl.CONTRACT));
		
		weatherTemperature.setUnit(new Uri("obix:units/celsius"));
		weatherHumidity.setUnit(new Uri("obix:units/percent"));
		weatherPressure.setUnit(new Uri("obix:units/hectopascal")); //100*kg^1*m^-1*s^-2
		weatherPrecipitation.setUnit(new Uri("obix:units/millimeter")); //0.001*m^1
		weatherCloudiness.setUnit(new Uri("obix:units/percent"));
		weatherFog.setUnit(new Uri("obix:units/percent"));
		weatherWindspeed.setUnit(new Uri("obix:units/beaufort"));
		
		
		//weatherTemperature.set(8.8);
		
		
		
		add(weatherTimestamp);
		add(weatherProbabilitycode);
		add(weatherTemperature);
		add(weatherHumidity);
		add(weatherPressure);
		add(weatherPrecipitation);
		add(weatherCloudiness);
		add(weatherFog);
		add(weatherWindspeed);

		dataRecords = new ArrayList<WeatherForecastRecordImpl>();
		
	}
	
	public WeatherForcastUpcomingWeatherImpl(WeatherForcastUpcomingWeather rec) {
		this();
		
		setAll(rec);
	}
	
	public void setAll(WeatherForcastUpcomingWeather rec) {
		weatherTimestamp.set(rec.weatherTimestamp().getMillis(), rec.weatherTimestamp().getTimeZone());
		
		if (ProbabilityCodeImpl.GetByName(rec.weatherProbabilitycode().get()) != ProbabilityCodeImpl.ID_UNKNOWN) {
			weatherProbabilitycode.set(rec.weatherProbabilitycode().get());
		}
		else {
			weatherProbabilitycode.set(ProbabilityCodeImpl.NAME_UNKNOWN);
		}
		
		weatherTemperature.set(rec.weatherTemperature().get());
		//weatherTemperature.set(8.8);
		weatherHumidity.set(rec.weatherHumidity().get());
		weatherPressure.set(rec.weatherPressure().get());
		weatherPrecipitation.set(rec.weatherPrecipitation().get());
		weatherCloudiness.set(rec.weatherCloudiness().get());
		weatherFog.set(rec.weatherFog().get());
		weatherWindspeed.set(rec.weatherWindspeed().get());
		
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
	public Abstime weatherTimestamp() {
		// TODO Auto-generated method stub
		return weatherTimestamp;
	}

	@Override
	public Enum weatherProbabilitycode() {
		// TODO Auto-generated method stub
		return weatherProbabilitycode;
	}

	@Override
	public Real weatherTemperature() {
		// TODO Auto-generated method stub
		System.out.println("Weather Temp upcoming impl: "+weatherTemperature);
		return weatherTemperature;
	}

	@Override
	public Real weatherHumidity() {
		// TODO Auto-generated method stub
		return weatherHumidity;
	}

	@Override
	public Real weatherPressure() {
		// TODO Auto-generated method stub
		return weatherPressure;
	}

	@Override
	public Real weatherPrecipitation() {
		// TODO Auto-generated method stub
		return weatherPrecipitation;
	}

	@Override
	public Real weatherCloudiness() {
		// TODO Auto-generated method stub
		return weatherCloudiness;
	}

	@Override
	public Real weatherFog() {
		// TODO Auto-generated method stub
		return weatherFog;
	}

	@Override
	public Int weatherWindspeed() {
		// TODO Auto-generated method stub
		return weatherWindspeed;
	}


}
