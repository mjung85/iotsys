package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators;

import obix.Int;

public interface ComplexSunblindActuator extends Actuator {

	public static final String CONTRACT="iot:ComplexSunblindActuator";
	
	public static final String POSITION_BLIND_HIGH_CONTRACT_NAME="positonBlindValue";
	public static final String POSITION_BLIND_HIGH_CONTRACT_HREF="positonBlindValue";
	public static final String POSITION_BLIND_HIGH_CONTRACT_UNIT="obix:units/percent";
	public static final String POSITION_BLIND_HIGH_CONTRACT = "<int name='"+POSITION_BLIND_HIGH_CONTRACT_NAME+"' href='"+POSITION_BLIND_HIGH_CONTRACT_HREF+"' val='0'/>";
	public Int positonBlindValue();
	
	public static final String POSITION_SLAT_CONTRACT_NAME="positonSlatValue";
	public static final String POSITION_SLAT_CONTRACT_HREF="positonSlatValue";	
	public static final String POSITION_SLAT_CONTRACT_UNIT="obix:units/percent";
	public static final String POSITION_SLAT_CONTRACT = "<int name='"+POSITION_SLAT_CONTRACT_NAME+"' href='"+POSITION_SLAT_CONTRACT_HREF+"' val='0'/>";
	public Int positonSlatValue();
	
}
