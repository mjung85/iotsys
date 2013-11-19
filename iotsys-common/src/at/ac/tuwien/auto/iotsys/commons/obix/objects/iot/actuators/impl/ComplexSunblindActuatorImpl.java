package at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl;

import java.util.logging.Logger;



import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.ComplexSunblindActuator;



public class ComplexSunblindActuatorImpl extends ActuatorImpl implements ComplexSunblindActuator{

	
	protected Int positonBlindValue = new Int(0);
	protected Int positonSlatValue = new Int(0);

	private static final Logger log = Logger.getLogger(SimpleHVACvalveActuatorImpl.class.getName());

	
	
	public ComplexSunblindActuatorImpl() {
		
		setIs(new Contract(ComplexSunblindActuator.CONTRACT));
		positonBlindValue.setWritable(true);
		Uri positonBlindUri = new Uri(ComplexSunblindActuator.POSITION_BLIND_HIGH_CONTRACT_HREF);
		positonBlindValue.setHref(positonBlindUri);
		positonBlindValue.setName(ComplexSunblindActuator.POSITION_BLIND_HIGH_CONTRACT_NAME);
		this.positonBlindValue.setMin(0);
		this.positonBlindValue.setMax(100);
		positonBlindValue.setUnit(new Uri(ComplexSunblindActuator.POSITION_BLIND_HIGH_CONTRACT_UNIT));
		add(positonBlindValue);
		
		positonSlatValue.setWritable(true);
		Uri positonSlatUri = new Uri(ComplexSunblindActuator.POSITION_SLAT_CONTRACT_HREF);
		positonSlatValue.setHref(positonSlatUri);
		positonSlatValue.setName(ComplexSunblindActuator.POSITION_SLAT_CONTRACT_NAME);
		this.positonSlatValue.setMin(0);
		this.positonSlatValue.setMax(100);
		positonSlatValue.setUnit(new Uri(ComplexSunblindActuator.POSITION_SLAT_CONTRACT_UNIT));
		add(positonSlatValue);
		
		
	}
	
	
	
	public void writeObject(Obj input){		
			
				
				if(input instanceof ComplexSunblindActuator){
					ComplexSunblindActuator in = (ComplexSunblindActuator) input;
					this.positonBlindValue.set(in.positonBlindValue().get());
					this.positonSlatValue.set(in.positonSlatValue().get());
				}
				else if(input instanceof Int){

					if(input.getHref() == null){
						if(input.getInvokedHref() != null && input.getInvokedHref().length() > 0){
							String resourceUriPath = input.getInvokedHref().substring(input.getInvokedHref().lastIndexOf('/') + 1);
							
							if(ComplexSunblindActuator.POSITION_BLIND_HIGH_CONTRACT_HREF.equals(resourceUriPath)){
								this.positonBlindValue.set(((Int) input).get());
							}
							else if (ComplexSunblindActuator.POSITION_SLAT_CONTRACT_HREF.equals(resourceUriPath)){
								this.positonSlatValue.set(((Int) input).get());
							}

						}
					}
					else{
						if (ComplexSunblindActuator.POSITION_BLIND_HIGH_CONTRACT_HREF.equals(input.getHref().toString())){
							this.positonBlindValue.set(((Int) input).get());
					
						}
						else if (ComplexSunblindActuator.POSITION_SLAT_CONTRACT_HREF.equals(input.getHref().toString())){
							this.positonSlatValue.set(((Int) input).get());
				
						}

					}
				}
				
	}



	@Override
	public Int positonBlindValue() {
		log.finer("Returning this value is now: " + this.positonBlindValue.getInt());
		return this.positonBlindValue;
	}



	@Override
	public Int positonSlatValue() {
		log.finer("Returning this value is now: " + this.positonSlatValue.getInt());
		return this.positonSlatValue;
	}


}
