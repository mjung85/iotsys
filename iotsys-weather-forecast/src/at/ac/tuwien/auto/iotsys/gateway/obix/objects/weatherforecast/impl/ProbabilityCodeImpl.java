package at.ac.tuwien.auto.iotsys.gateway.obix.objects.weatherforecast.impl;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.ProbabilityCode;

public class ProbabilityCodeImpl implements ProbabilityCode {
	
	
	
	public static int GetByName(String name) {
		int id;
		
		if (name == null)
			name = NAME_UNKNOWN;
		
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
