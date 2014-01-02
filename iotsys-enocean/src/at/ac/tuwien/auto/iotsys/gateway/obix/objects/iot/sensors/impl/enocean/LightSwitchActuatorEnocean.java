package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.enocean;

import obix.Bool;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.Actuator;

public interface LightSwitchActuatorEnocean extends Actuator {
	public static final String CONTRACT="iot:LightSwitchActuatorEnocean";
	
	public static final String VALUE_CONTRACT_NAME = "value";
	public static final String VALUE_CONTRACT_HERF = "Toggle";
	public static final String switchContract = "<bool name='" + VALUE_CONTRACT_NAME + "' href='" + VALUE_CONTRACT_HERF + "' val='false'/>";
	
	public static final String LRN_CONTRACT_NAME = "lrn";
	public static final String LRN_CONTRACT_HERF = "LRN";
	public static final String lrnContract = "<bool name='" + LRN_CONTRACT_NAME + "' href='" + LRN_CONTRACT_HERF + "' val='false'/>";
	public Bool value();
	public Bool lrn();
	
}