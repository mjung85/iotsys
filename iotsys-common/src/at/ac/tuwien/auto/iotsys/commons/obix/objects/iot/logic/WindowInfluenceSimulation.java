package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic;

import obix.Bool;
import obix.IObj;
import obix.Real;

public interface WindowInfluenceSimulation extends IObj{

public static final String CONTRACT="iot:WindowInfluenceSimulation";
	
	public static final String enabledContract = "<bool name='enabled' href='enabled' val='false'/>";
	public Bool enabled();
	
	public static final String windowClosedContract = "<bool name='windowClosed' href='windowClosed' val='false'/>";
	public Bool windowClosed(); 
	
	public static final String comfortModeActiveContract = "<bool name='comfortModeActive' href='comfortModeActive' val='false'/>";
	public Bool comfortModeActive(); 
	
	public static final String standbyModeActiveContract = "<bool name='standbyModeActive' href='standbyModeActive' val='false'/>";
	public Bool standbyModeActive();
	
}
