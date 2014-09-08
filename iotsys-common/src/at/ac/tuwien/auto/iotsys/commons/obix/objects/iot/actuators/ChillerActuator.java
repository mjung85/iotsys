package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators;

import obix.Bool;

public interface ChillerActuator {
	public static final String CONTRACT="iot:Chiller";
	
	public static final String CHILLER_ENABLED_NAME = "enabled";
	public static final String CHILLER_ENABLED_HREF = "enabled";
	
	public static final String CHILLER_CONTRACT = "<bool name='" + CHILLER_ENABLED_NAME + "' href='" + CHILLER_ENABLED_HREF + "' val='false'/>";
	public Bool enabled();
}
