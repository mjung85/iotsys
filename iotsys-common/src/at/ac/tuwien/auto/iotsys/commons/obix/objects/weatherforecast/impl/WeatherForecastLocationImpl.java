package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.Crawler;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForecastLocation;
import obix.*;

public class WeatherForecastLocationImpl extends Obj implements
		WeatherForecastLocation {

	public static final double MAX_LATITUDE = 90;
	public static final double MIN_LATITUDE = -90;
	public static final double MAX_LONGITUDE = 180;
	public static final double MIN_LONGITUDE = -180;
	
	private Real latitude = new Real("latitude");
	private Real longitude = new Real("longitude");
	private Int height = new Int("height");
	private Str description = new Str("description", "");
	
	public WeatherForecastLocationImpl(String description, double latitude, double longitude, long height) {
		setIs(new Contract(WeatherForecastLocation.CONTRACT));
		
		setDescription(description);
		setLatitude(latitude);
		setLongitude(longitude);
		setHeight(height);

		this.description.setHref(new Uri("description"));
		this.latitude.setHref(new Uri("latitude"));
		this.longitude.setHref(new Uri("longitude"));
		this.height.setHref(new Uri("height"));
		
		this.latitude.setUnit(new Uri("obix:units/degree"));
		this.longitude.setUnit(new Uri("obix:units/degree"));
		this.height.setUnit(new Uri("obix:units/meter"));

		add(this.description);
		add(this.latitude);
		add(this.longitude);
		add(this.height);
	}
	
	public WeatherForecastLocationImpl(String description, double latitude, double longitude) {
		this(description, latitude, longitude, 0);
	}
	
	public WeatherForecastLocationImpl() {
		this("", 0, 0, 0);
	}
	
	public WeatherForecastLocationImpl(WeatherForecastLocationImpl loc) throws NullPointerException {
		this(loc.getDescription(), loc.getLatitude(), loc.getLongitude(), loc.getHeight());
	}

	@Override
	public Real latitude() {
		return latitude;
	}

	@Override
	public Real longitude() {
		return longitude;
	}

	@Override
	public Int height() {
		return height;
	}
	
	@Override
	public Str description() {
		return description;
	}
	
	public String getDescription() {
		return description.get();
	}
	
	public void setDescription(String description) {
		this.description.set(description);
	}
	
	public double getLatitude() {
		return latitude.get();
	}
	
	public void setLatitude(double latitude) {
		if (latitude <= MAX_LATITUDE && latitude >= MIN_LATITUDE)
			this.latitude.set(latitude);
	}
	
	public double getLongitude() {
		return longitude.get();
	}
	
	public void setLongitude(double longitude) {
		if (longitude <= MAX_LONGITUDE && longitude >= MIN_LONGITUDE)
			this.longitude.set(longitude);
	}
	
	public long getHeight() {
		return height.get();
	}
	
	public void setHeight(long height) {
		this.height.set(height);
	}
	
	public WeatherForecastLocationImpl clone() {
		return new WeatherForecastLocationImpl(this);
	}
	
	@Override
	public void writeObject(Obj input) {
		if (input instanceof WeatherForecastLocation ) {
			WeatherForecastLocation newLocation = (WeatherForecastLocation) input;
			
			setLatitude(newLocation.latitude().get());
			setLongitude(newLocation.longitude().get());
			setHeight(newLocation.height().get());
			setDescription(newLocation.description().get());
			
			Obj parent = this.getParent();
			if (parent instanceof Crawler) {
				((Crawler) parent).reset();
			}
		}
	}
}
