package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet;

import obix.IObj;
import obix.Real;

public interface AnalogOutput extends IObj {
	
	public static final String CONTRACT="iot:AnalogOutput";
	
	public static final String valueContract = "<real name='value' href='value' val='0'/>";
	public Real value();
}
