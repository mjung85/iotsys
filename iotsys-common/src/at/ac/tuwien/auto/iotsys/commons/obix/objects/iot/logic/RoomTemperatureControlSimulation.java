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
	
	
//	public static final String seasonContract = "<str name='season' href='season' val='winter'/>";
//	public Str season();  
//	
//	
//	public static final String winterImpactContract = "<real name='winterImpact' href='winterImpact' val='0'/>";
//	public Real winterImpact();
//	
//	public static final String fallImpactContract = "<real name='fallImpact' href='fallImpact' val='0'/>";
//	public Real fallImpact();
//	
//	public static final String summerImpactContract = "<real name='summerImpact' href='summerImpact' val='0'/>";
//	public Real summerImpact();
//	
//	public static final String heatingImpactContract = "<real name='heatingImpact' href='heatingImpact' val='0'/>";
//	public Real heatingImpact();
//	
//	public static final String coolingImpactContract = "<real name='coolingImpact' href='coolingImpact' val='0'/>";
//	public Real coolingImpact();
//	
//	public static final String boilerActiveContract = "<bool name='boilerActive' href='boilerActive' val='false'/>";
//	public Bool boilerActive();
//	
//	public static final String heatPumpActiveContract = "<bool name='heatPumpActive' href='heatPumpActive' val='false'/>";
//	public Bool heatPumpActive();
//	
//	public static final String fanInActiveContract = "<bool name='fanInActive' href='fanInActive' val='false'/>";
//	public Bool fanInActive();
//	
//	public static final String coolerActiveContract = "<bool name='coolerActive' href='coolerActive' val='false'/>";
//	public Bool coolerActive();
//	
//	public static final String coolPumpActiveContract = "<bool name='coolPumpActive' href='coolPumpActive' val='false'/>";
//	public Bool coolPumpActive();
//		
//	public static final String tempContract = "<real name='temp' href='temp' val='0'/>";
//	public Real temp();

}
