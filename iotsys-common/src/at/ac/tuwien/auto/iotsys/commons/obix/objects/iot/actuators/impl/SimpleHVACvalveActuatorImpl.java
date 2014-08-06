package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.SimpleHVACvalveActuator;


public class SimpleHVACvalveActuatorImpl extends ActuatorImpl implements SimpleHVACvalveActuator{

	
	protected Int value = new Int(0);

	private static final Logger log = Logger
			.getLogger(SimpleHVACvalveActuatorImpl.class.getName());

	
	
	public SimpleHVACvalveActuatorImpl() {
		
		setIs(new Contract(SimpleHVACvalveActuator.CONTRACT));
		value.setWritable(true);
		Uri valveUri = new Uri("value");
		
		value.setHref(valveUri);
		value.setName("value");
		this.value.setMin(0);
		this.value.setMax(100);
		value.setUnit(new Uri(SimpleHVACvalveActuator.VALVE_POSITION_CONTRACT_UNIT));
		
		add(value);
		
	}
	
	
	
	public void writeObject(Obj input){		
		
		// A write on this object was received, update the according data point.		
				
				if(input instanceof SimpleHVACvalveActuator){
					SimpleHVACvalveActuator in = (SimpleHVACvalveActuator) input;
					this.value.set(in.value().get());
				}
				else if(input instanceof Int){
					this.value.set(((Int) input).get());
				}
				else if(input instanceof Bool){
					this.value.set( ((Bool) input).get());
				}
				else if(input instanceof Real){
					this.value.set( ((Real) input).get());
				}
	}


	@Override
	public Int value() {
		log.finer("Returning this value is now: " + this.value.getInt());
		return this.value;
	}

	
	
	
	
	

}
