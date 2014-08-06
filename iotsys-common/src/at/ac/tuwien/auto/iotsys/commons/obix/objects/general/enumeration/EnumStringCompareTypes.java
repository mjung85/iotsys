package at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration;

import obix.contracts.Range;

public interface EnumStringCompareTypes extends Range {
	public static final String HREF = "/enums/stringCompareTypes";

	public static final String KEY_EQ = "eq";
	public static final String KEY_STARTS_WITH = "startsWith";
	public static final String KEY_ENDS_WITH = "endsWith";
	public static final String KEY_CONTAINS = "contains";
}
