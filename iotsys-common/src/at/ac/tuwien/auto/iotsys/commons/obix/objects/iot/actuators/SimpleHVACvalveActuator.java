package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators;

import obix.Int;

public interface SimpleHVACvalveActuator extends Actuator
{

	public static final String CONTRACT="iot:SimpleHVACvalveActuator";
	
	public static final String VALVE_POSITION_CONTRACT_NAME="valvePosition";
	public static final String VALVE_POSITION_CONTRACT_HREF="valvePosition";
	public static final String VALVE_POSITION_CONTRACT_UNIT="obix:units/percent";
	public static final String VALVE_POSITION_CONTRACT = "<int name='"+VALVE_POSITION_CONTRACT_NAME+"' href='"+VALVE_POSITION_CONTRACT_HREF+"' val='0'/>";
	public Int value();
	
}
