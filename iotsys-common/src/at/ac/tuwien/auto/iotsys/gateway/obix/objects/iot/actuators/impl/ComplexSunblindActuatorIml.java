package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.ComplexSunblindActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.SunblindActuator;



public class ComplexSunblindActuatorIml extends ActuatorImpl implements ComplexSunblindActuator{

	
	protected Bool moveDownValue = new Bool(false);	
	protected Bool moveUpValue = new Bool(false);
	protected Bool stopStepUpDownValue = new Bool(false);
	protected Bool dedicatedStopValue  = new Bool(false);
	
	private static final Logger log = Logger.getLogger(SunblindActuatorImpl.class.getName());
	
	
	public ComplexSunblindActuatorIml(){
		setIs(new Contract(SunblindActuator.CONTRACT));
		
		//moveDownValue
		moveDownValue.setWritable(true);
		Uri moveDownValueUri = new Uri(SunblindActuator.MOVE_DOWN_CONTRACT_HREF);	
		moveDownValue.setHref(moveDownValueUri);
		moveDownValue.setName(SunblindActuator.MOVE_DOWN_CONTRACT_NAME);			
		add(moveDownValue);
		
		//moveUpValue
		moveUpValue.setWritable(true);
		Uri moveUpValueUri = new Uri(SunblindActuator.MOVE_UP_CONTRACT_HREF);	
		moveUpValue.setHref(moveUpValueUri);
		moveUpValue.setName(SunblindActuator.MOVE_UP_CONTRACT_NAME);			
		add(moveUpValue);
						
	}
	
	//gehoert noch ausgemistet
	
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.		
		boolean newMoveDownValue = false;
		boolean newMoveUpValue = false;
		
		
		if(input instanceof SunblindActuator){
			SunblindActuator in = (SunblindActuator) input;
			log.finer("Writing on SunblindActuator: " + in.moveDownValue().get());
			
			newMoveDownValue = in.moveDownValue().get();
			
			newMoveUpValue = in.moveUpValue().get();
		}
		
		else if(input instanceof Bool){
			
			
			if(input.getHref() == null){
				if(input.getInvokedHref() != null && input.getInvokedHref().length() > 0){
					String resourceUriPath = input.getInvokedHref().substring(input.getInvokedHref().lastIndexOf('/') + 1);
					
					if(SunblindActuator.MOVE_DOWN_CONTRACT_HREF.equals(resourceUriPath)){
						newMoveDownValue = ((Bool) input).get();
					}
					else if (SunblindActuator.MOVE_UP_CONTRACT_HREF.equals(resourceUriPath)){
						newMoveUpValue = ((Bool) input).get();
					}

				}
			}
			else{
				if (SunblindActuator.MOVE_DOWN_CONTRACT_HREF.equals(input.getHref().toString())){
					newMoveDownValue = ((Bool) input).get();
			
				}
				else if (SunblindActuator.MOVE_UP_CONTRACT_HREF.equals(input.getHref().toString())){
					newMoveUpValue = ((Bool) input).get();
		
				}

			}
		}
		
		if(moveDownValue.get() != newMoveDownValue)
			this.moveDownValue.set(newMoveDownValue);
		if(moveUpValue.get() != newMoveUpValue)
			this.moveUpValue.set(newMoveUpValue);

	}

	
	
	
	
	
	@Override
	public Int positionBlindHighValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Int positonSlatValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
