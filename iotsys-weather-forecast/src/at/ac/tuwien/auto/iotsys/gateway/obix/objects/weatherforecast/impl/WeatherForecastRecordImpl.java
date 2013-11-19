package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastRecord;
import obix.*;
import obix.Enum;

public class WeatherForecastRecordImpl extends Obj implements
		WeatherForecastRecord {
	
	private Abstime timestamp = new Abstime("timestamp");
	private Enum probabilityCode = new Enum("probabilityCode", "");
	private Real temperature = new Real("temperature");
	private Real humidity = new Real("humidity");
	private Real pressure = new Real("pressure");
	private Real precipitation = new Real("precipitation");
	private Real cloudiness = new Real("cloudiness");
	private Real fog = new Real("fog");
	private Int windSpeed = new Int("windSpeed");
	private Enum symbol = new Enum("symbol", "");
	
	public WeatherForecastRecordImpl() {
		setIs(new Contract(WeatherForecastRecord.CONTRACT));
		
		probabilityCode.setRange(new Uri(ProbabilityCodeImpl.CONTRACT));
		symbol.setRange(new Uri(WeatherSymbolImpl.CONTRACT));

		temperature.setUnit(new Uri("obix:units/celsius"));
		humidity.setUnit(new Uri("obix:units/percent"));
		pressure.setUnit(new Uri("obix:units/hectopascal")); //100*kg^1*m^-1*s^-2
		precipitation.setUnit(new Uri("obix:units/millimeter")); //0.001*m^1
		cloudiness.setUnit(new Uri("obix:units/percent"));
		fog.setUnit(new Uri("obix:units/percent"));
		windSpeed.setUnit(new Uri("obix:units/beaufort"));
		
		add(timestamp);
		add(probabilityCode);
		add(temperature);
		add(humidity);
		add(pressure);
		add(precipitation);
		add(cloudiness);
		add(fog);
		add(windSpeed);
		add(symbol);
	}

	public WeatherForecastRecordImpl(WeatherForecastRecord rec) {
		this();
		
		setAll(rec);
	}
	
	public void setAll(WeatherForecastRecord rec) {
		timestamp.set(rec.timestamp().getMillis(), rec.timestamp().getTimeZone());
		
		if (ProbabilityCodeImpl.GetByName(rec.probabilityCode().get()) != ProbabilityCodeImpl.ID_UNKNOWN) {
			probabilityCode.set(rec.probabilityCode().get());
		}
		else {
			probabilityCode.set(ProbabilityCodeImpl.NAME_UNKNOWN);
		}
		
		temperature.set(rec.temperature().get());
		humidity.set(rec.humidity().get());
		pressure.set(rec.pressure().get());
		precipitation.set(rec.precipitation().get());
		cloudiness.set(rec.cloudiness().get());
		fog.set(rec.fog().get());
		windSpeed.set(rec.windSpeed().get());
		
		if (WeatherSymbolImpl.GetByName(rec.symbol().get()) != WeatherSymbolImpl.ID_UNKNOWN) {
			symbol.set(rec.symbol().get());
		}
		else {
			symbol.set(WeatherSymbolImpl.NAME_UNKNOWN);
		}
	}

	@Override
	public Abstime timestamp() {
		return timestamp;
	}

	@Override
	public Enum probabilityCode() {
		return probabilityCode;
	}

	@Override
	public Real temperature() {
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
	public Int windSpeed() {
		return windSpeed;
	}

	@Override
	public Enum symbol() {
		return symbol;
	}

}
