package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic;

import obix.Bool;
import obix.Str;

public interface StringComparator {
	public static final String COMPARE_TYPE_EQ = "eq";
	public static final String COMPARE_TYPE_STARTS_WITH = "startsWith";
	public static final String COMPARE_TYPE_ENDS_WITH = "endsWith";
	public static final String COMPARE_TYPE_CONTAINS = "contains";
	
	public static final String CONTRACT = "iot:StringComparator";
	
	public static final String input1Contract = "<str name='input1' href='input1' val='0' writable='true'/>";
	public Str input1();
	
	public static final String input2Contract = "<str name='input2' href='input2' val='0' writable='true'/>";
	public Str input2();
	
	public static final String resultContract = "<bool name='result' href='result' val='false'/>";
	public Bool result();
	
	public static final String enabledContract = "<bool name='enabled' href='enable' val='false'/>";
	public Bool enabled();
	
	public static final String caseSensitive = "<bool name='caseSensitive' href='caseSensitive' val='false'/>";
	public Bool caseSensitive();
	
	public static final String compareTypeContract = "<enum name='compareType' href='compareType' range='/enums/stringCompareType'/>";
	
	public obix.Enum compareType();	
}
