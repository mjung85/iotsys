package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.WeatherForecastRecord;
import obix.*;
import obix.Enum;

public class WeatherForecastRecordImpl extends Obj implements
		WeatherForecastRecord {
	
	private Abstime timestamp = new Abstime("timestamp");
	private Enum probabilityCode = new Enum("probability-code");
	private Real temperature = new Real("temperature");
	private Real humidity = new Real("humidity");
	private Real pressure = new Real("pressure");
	private Real precipitation = new Real("precipitation");
	private Real cloudiness = new Real("cloudiness");
	private Real fog = new Real("fog");
	private Int windSpeed = new Int("wind-speed");
	private Enum symbol = new Enum("symbol");
	
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
