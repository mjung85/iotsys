package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic;

import obix.Bool;
import obix.IObj;
import obix.Real;
import obix.Str;

public interface RoomTemperatureControlSimulation extends IObj {
	
	public static final String CONTRACT="iot:InterworkingKNXBACnetSimulation";
	
	public static final String enabledContract = "<bool name='enabled' href='enabled' val='false'/>";
	public Bool enabled();
	
	public static final String roomTempSetPointContract = "<real name='roomTempSetPoint' href='roomTempSetPoint' val='0'/>";
	public Real roomTempSetPoint(); 
	
	public static final String roomCurrentTempContract = "<real name='roomCurrentTemp' href='roomCurrentTemp' val='0'/>";
	public Real roomCurrentTemp(); 
	
	public static final String toleranceContract = "<real name='tolerance' href='tolerance' val='0'/>";
	public Real tolerance();
	
	public static final String tempOutsideOffset = "<real name='tempOutsideOffset' href='tempOutsideOffset' val='0'/>";
	public Real tempOutsideOffset();
	
	public static final String windowOpenContract = "<bool name='windowOpen' href='windowOpen' val='false'/>";
	public Bool windowOpen(); 
	
	public static final String comfortModeActiveContract = "<bool name='comfortModeActive' href='comfortModeActive' val='false'/>";
	public Bool comfortModeActive(); 
	
	public static final String standbyModeActiveContract = "<bool name='standbyModeActive' href='standbyModeActive' val='false'/>";
	public Bool standbyModeActive();
	

}
