package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic;

import obix.Bool;
import obix.IObj;

public interface Application extends IObj {
	public static final String CONTRACT = "iot:application";
	
	public static final String enabledContract = "<bool name='enabled' href='enable' val='false'/>";
	public Bool enabled();

}
