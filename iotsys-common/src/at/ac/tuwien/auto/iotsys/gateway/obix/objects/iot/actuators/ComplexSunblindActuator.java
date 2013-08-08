package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators;

import obix.Int;

public interface ComplexSunblindActuator {

	public static final String CONTRACT="iot:ComplexSunblindActuator";
	
	public static final String POSITION_BLIND_HIGH_CONTRACT_NAME="posBlind";
	public static final String POSITION_BLIND_HIGH_CONTRACT_HREF="posBlind";	
	public static final String POSITION_BLIND_HIGH_CONTRACT = "<bool name='"+POSITION_BLIND_HIGH_CONTRACT_NAME+"' href='"+POSITION_BLIND_HIGH_CONTRACT_HREF+"' val='false'/>";
	public Int positionBlindHighValue();
	
	public static final String POSITION_SLAT_CONTRACT_NAME="posSlat";
	public static final String POSITION_SLAT_CONTRACT_HREF="posSlat";	
	public static final String POSITION_SLAT_CONTRACT = "<bool name='"+POSITION_SLAT_CONTRACT_NAME+"' href='"+POSITION_SLAT_CONTRACT_HREF+"' val='false'/>";
	public Int positonSlatValue();
	
}
