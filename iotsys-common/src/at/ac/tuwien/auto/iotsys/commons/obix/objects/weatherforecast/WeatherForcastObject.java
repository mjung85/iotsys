package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;


import java.util.Date;


public class WeatherForcastObject {
	
	private long timestamp;
	private String probabilityCode;
	private double temperature;
	private String WindDirection;
	private int windSpeed;
	private double humidity;
	private double pressure;
	private double precipitation;
	private double cloudiness;
	private double fog;
	private double lowClouds;
	private double mediumClouds;
	private double highClouds;
	private double dewpointTemperature;
	private String symbol;
	private int symbolProbability;
	private int windProbability;
	private int temperatureProbability;
	
	public String getWindDirection() {
		return WindDirection;
	}
	public void setWindDirection(String windDirection) {
		WindDirection = windDirection;
	}
	
	public int getWindProbability() {
		return windProbability;
	}
	public void setWindProbability(int windProbability) {
		this.windProbability = windProbability;
	}
	public int getTemperatureProbability() {
		return temperatureProbability;
	}
	public void setTemperatureProbability(int temperatureProbability) {
		this.temperatureProbability = temperatureProbability;
	}

	public int getSymbolProbability() {
		return symbolProbability;
	}
	public void setSymbolProbability(int symbolProbability) {
		this.symbolProbability = symbolProbability;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getProbabilityCode() {
		return probabilityCode;
	}
	public void setProbabilityCode(String probabilityCode) {
		this.probabilityCode = probabilityCode;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public double getHumidity() {
		return humidity;
	}
	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}
	public double getPressure() {
		return pressure;
	}
	public void setPressure(double pressure) {
		this.pressure = pressure;
	}
	public double getPrecipitation() {
		return precipitation;
	}
	public void setPrecipitation(double precipitation) {
		this.precipitation = precipitation;
	}
	public double getCloudiness() {
		return cloudiness;
	}
	public void setCloudiness(double cloudiness) {
		this.cloudiness = cloudiness;
	}
	public double getFog() {
		return fog;
	}
	public void setFog(double fog) {
		this.fog = fog;
	}
	public double getLowClouds() {
		return lowClouds;
	}
	public void setLowClouds(double lowClouds) {
		this.lowClouds = lowClouds;
	}
	public double getMediumClouds() {
		return mediumClouds;
	}
	public void setMediumClouds(double mediumClouds) {
		this.mediumClouds = mediumClouds;
	}
	public double getHighClouds() {
		return highClouds;
	}
	public void setHighClouds(double highClouds) {
		this.highClouds = highClouds;
	}
	public double getDewpointTemperature() {
		return dewpointTemperature;
	}
	public void setDewpointTemperature(double dewpointTemperature) {
		this.dewpointTemperature = dewpointTemperature;
	}
	public int getWindSpeed() {
		return windSpeed;
	}
	public void setWindSpeed(int windSpeed) {
		this.windSpeed = windSpeed;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	

}
