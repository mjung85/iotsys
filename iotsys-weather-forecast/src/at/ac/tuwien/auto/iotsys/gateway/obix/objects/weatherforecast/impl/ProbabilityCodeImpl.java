package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

public class ProbabilityCodeImpl {
	
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
	
	public static int GetByName(String name) {
		int id;
		
		name = name.toLowerCase();
		
		if (name.equals(NAME_HIGHLY_PROBABLE))
			id = ID_HIGHLY_PROBABLE;
		else if (name.equals(NAME_PROBABLE))
			id = ID_PROBABLE;
		else if (name.equals(NAME_UNCERTAIN))
			id = ID_UNCERTAIN;
		else
			id = ID_UNKNOWN;

		return id;
	}
	
	public static String GetByID(int id) {
		String name;
		
		if (id == ID_HIGHLY_PROBABLE)
			name = NAME_HIGHLY_PROBABLE;
		else if (id == ID_PROBABLE)
			name = NAME_PROBABLE;
		else if (id == ID_UNCERTAIN)
			name = NAME_UNCERTAIN;
		else
			name = NAME_UNKNOWN;

		return name;
	}
}
