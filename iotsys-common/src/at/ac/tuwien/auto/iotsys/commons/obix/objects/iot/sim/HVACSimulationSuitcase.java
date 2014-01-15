package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sim;

import obix.Bool;
import obix.Int;
import obix.Real;
import obix.Str;

public interface HVACSimulationSuitcase {

	public static final String CONTRACT="iot:HVACSimulationSuitcase";
	
	public static final String enabledContract = "<bool name='enabled' href='enabled' val='false'/>";
	public Bool enabled();
	
	public static final String seasonContract = "<str name='season' href='season' val='winter'/>";
	public Str season();  
	
	public static final String springImpactContract = "<real name='springImpact' href='springImpact' val='0'/>";
	public Real springImpact(); 
	
	public static final String winterImpactContract = "<real name='winterImpact' href='winterImpact' val='0'/>";
	public Real winterImpact();
	
	public static final String fallImpactContract = "<real name='fallImpact' href='fallImpact' val='0'/>";
	public Real fallImpact();
	
	public static final String summerImpactContract = "<real name='summerImpact' href='summerImpact' val='0'/>";
	public Real summerImpact();
	
	public static final String heatingImpactContract = "<real name='heatingImpact' href='heatingImpact' val='0'/>";
	public Real heatingImpact();
	
	public static final String coolingImpactContract = "<real name='coolingImpact' href='coolingImpact' val='0'/>";
	public Real coolingImpact();
	
	public static final String boilerActiveContract = "<bool name='boilerActive' href='boilerActive' val='false'/>";
	public Bool boilerActive();
	
	public static final String coolerActiveContract = "<bool name='coolerActive' href='coolerActive' val='false'/>";
	public Bool coolerActive();
	
	public static final String fanInActiveContract = "<bool name='fanInActive' href='fanInActive' val='false'/>";
	public Bool fanInActive();
	
	public static final String fanOutActiveContract = "<bool name='fanInActive' href='fanInActive' val='false'/>";
	public Bool fanOutActive();
	
	public static final String valveInPositionContract = "<int name='valveInPosition' href='valveInPosition' val='0'/>";
	public Int valveInPosition();
	
	public static final String valveOutPositionContract = "<int name='valveOutPosition' href='valveOutPosition' val='0'/>";
	public Int valveOutPosition();
	
	public static final String tempOutsideContract = "<real name='tempOutside' href='tempOutside' val='0'/>";
	public Real tempOutside();
	
	public static final String windowClosedContract = "<bool name='windowClosed' href='windowClosed' val='false'/>";
	public Bool windowClosed();
	
	public static final String tempContract = "<real name='temp' href='temp' val='0'/>";
	public Real temp();
	
	public static final String tempOutsideOffset = "<real name='tempOutsideOffset' href='tempOutsideOffset' val='0'/>";
	public Real tempOutsideOffset();
	
	public static final String comfortModeActiveContract = "<bool name='comfortModeActive' href='comfortModeActive' val='false'/>";
	public Bool comfortModeActive(); 
	
	public static final String standbyModeActiveContract = "<bool name='standbyModeActive' href='standbyModeActive' val='false'/>";
	public Bool standbyModeActive();
	
	public static final String doorOpenerActiveContract = "<bool name='doorOpenerActive' href='doorOpenerActive' val='false'/>";
	public Bool doorOpenerActive();

}
