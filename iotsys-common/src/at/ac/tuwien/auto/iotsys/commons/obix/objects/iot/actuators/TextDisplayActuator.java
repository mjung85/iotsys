package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators;

import obix.Str;

public interface TextDisplayActuator extends Actuator{
	
	
	public static final String CONTRACT="iot:TextDisplayActuator";
	
	public static final String TEXTDISPLAY_CONTRACT_NAME="textDisplayValue";
	public static final String TEXTDISPLAY_CONTRACT_HREF="textDisplayValue";
	public static final String SWITCH_ON_OFF_CONTRACT = "<str name='"+TEXTDISPLAY_CONTRACT_NAME+"' href='"+TEXTDISPLAY_CONTRACT_HREF+"' val='null'/>";	
	public Str textDisplayValue();
	
}
