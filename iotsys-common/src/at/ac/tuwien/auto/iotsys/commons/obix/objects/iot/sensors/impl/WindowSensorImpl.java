package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sensors.impl;

import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.sensors.WindowSensor;

public class WindowSensorImpl extends Obj implements WindowSensor {
	protected Bool value = new Bool(false);
	
	public WindowSensorImpl(){
		setIs(new Contract(WindowSensor.CONTRACT));
		value.setWritable(false);
		value.setDisplayName("Open");
		Uri valueUri = new Uri("value");
	
		value.setHref(valueUri);
		value.setName("value");
		add(value);
	}
	
	public void writeObject(Obj input){
	}
	@Override
	public Bool value() {
		return value;
	}

}
