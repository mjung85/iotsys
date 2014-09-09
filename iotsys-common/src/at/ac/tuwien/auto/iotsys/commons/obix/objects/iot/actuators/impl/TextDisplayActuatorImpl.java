package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl;

import java.util.logging.Logger;
import obix.Contract;
import obix.Obj;
import obix.Str;
import obix.Uri;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.TextDisplayActuator;

public class TextDisplayActuatorImpl extends ActuatorImpl implements TextDisplayActuator{
	
	private static final Logger log = Logger.getLogger(TextDisplayActuatorImpl.class.getName());
	protected Str value = new Str("null");
	
	
	public TextDisplayActuatorImpl(){
		setIs(new Contract(TextDisplayActuator.CONTRACT));
		value.setWritable(true);
		//value.setDisplayName("On/Off");
		Uri valueUri = new Uri(TextDisplayActuator.TEXTDISPLAY_CONTRACT_HREF);
	
		value.setHref(valueUri);
		value.setName(TextDisplayActuator.TEXTDISPLAY_CONTRACT_NAME);			
		
		add(value);
	}
	
	public void writeObject(Obj input){	
		System.out.println("in der writeObject");
		if(input instanceof TextDisplayActuator){
			TextDisplayActuator in = (TextDisplayActuator) input;
			System.out.println("in der if");
			log.finer("Writing on TextDisplayActuator: "+ in.textDisplayValue().get());
			this.value.set(in.textDisplayValue().get());		
		}
		else if(input instanceof Str){
			this.value.set(((Str) input).get());
		}		
	}
	
	@Override
	public Str textDisplayValue() {
		log.finer("Returning this value is now: "
				+ this.value.getStr());
		return this.value;
	}

}
