package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet;

import obix.Bool;
import obix.IObj;

public interface BinaryOutput extends IObj {
	
	public static final String CONTRACT="iot:BinaryOutput";
	
	public static final String valueContract = "<bool name='value' href='value' val='false'/>";
	public Bool value();
}
