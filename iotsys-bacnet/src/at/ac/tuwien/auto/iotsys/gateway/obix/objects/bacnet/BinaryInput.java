package at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet;

import obix.Bool;
import obix.IObj;

public interface BinaryInput extends IObj {
	
	public static final String CONTRACT="iot:BinaryInput";
	
	public static final String valueContract = "<bool name='value' href='value' val='false'/>";
	public Bool value();
}
