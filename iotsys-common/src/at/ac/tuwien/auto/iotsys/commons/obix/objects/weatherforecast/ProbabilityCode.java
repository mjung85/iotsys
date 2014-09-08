package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast;

public interface ProbabilityCode {
	public static final String CONTRACT = "iot:probabilityCode";
	
	public static final String NAME = "probabilityCode";
	
	public static final int ID_UNKNOWN = -1;
	public static final int ID_HIGHLY_PROBABLE = 0;
	public static final int ID_PROBABLE = 1;
	public static final int ID_UNCERTAIN = 2;
	
	public static final String NAME_UNKNOWN = "unknown";
	public static final String NAME_HIGHLY_PROBABLE = "highly-probable";
	public static final String NAME_PROBABLE = "probable";
	public static final String NAME_UNCERTAIN = "uncertain";
}
