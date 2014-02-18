package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.rfid;

import obix.Str;
import obix.Abstime;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sensors.Sensor;

public interface EventRFID extends Sensor {
	public static final String CONTRACT = "iot:RFIDEvent";

	
	public static final String RFID_TAG_NAME = "tag";
	public static final String RFID_TAG_HREF = "tag";
	public static final String tagContract = "<Str name='" + RFID_TAG_NAME + "' href='"+ RFID_TAG_HREF +"' val='none'/> ";	
	
	public static final String RFID_TIME_NAME = "time";
	public static final String RFID_TIME_HREF = "time";	
	public static final String timeContract  = "<Abstime name='" + RFID_TIME_NAME + "' href='"+ RFID_TIME_HREF +"' val='0'/>";	
	
	public Str rfidTag();
	public Abstime rfidTagTime();
}

