package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.rfid;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.Actuator;
import obix.Bool;

public interface CommandRFID extends Actuator{
	public static final String CONTRACT = "iot:RFIDOperation";
	
	public static final String RFID_CONTINOUS_NAME = "Continous Read";
	public static final String RFID_CONTINOUS_HREF = "continous_read";
	public static final String continousReadContract = "<real name='" + RFID_CONTINOUS_NAME + "' href='"+ RFID_CONTINOUS_HREF +"' val='false'/>";	
	
	public static final String RFID_ABORT_CONTINOUS_NAME = "Abort Continous Read";
	public static final String RFID_ABORT_CONTINOUS_HREF = "abort_continous_read";	
	public static final String abortContinousReadContract  = "<real name='" + RFID_ABORT_CONTINOUS_NAME + "' href='"+ RFID_ABORT_CONTINOUS_HREF +"' val='false'/>";	
	
	public static final String RFID_S_SELECT_NAME = "Single Select";
	public static final String RFID_S_SELECT_HREF = "single_select";	
	public static final String cmdSelectContract  = "<real name='" + RFID_S_SELECT_NAME + "' href='"+ RFID_S_SELECT_HREF +"' val='false'/>";	

	public static final String RFID_M_SELECT_NAME = "Multi Select";
	public static final String RFID_M_SELECT_HREF = "multi_select";	
	public static final String cmdMultiSelectContract  = "<real name='" + RFID_M_SELECT_NAME + "' href='"+ RFID_M_SELECT_HREF +"' val='false'/>";	


	public static final String RFID_RESET_NAME = "Reset";
	public static final String RFID_RESET_HREF = "reset";	
	public static final String cmdResetContract  = "<real name='" + RFID_RESET_NAME + "' href='"+ RFID_RESET_HREF +"' val='false'/>";	
	
	
	public Bool continousRead();
	public Bool abortContinousRead();
	public Bool singleSelect();
	public Bool multiSelect();
	public Bool reset();
}



