package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.knx;

import java.util.logging.Logger;

import com.sun.tracing.dtrace.ArgsAttributes;

import obix.Int;
import obix.Obj;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.ComplexSunblindActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.SunblindActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.ComplexSunblindActuatorImpl;


public class ComplexSunblindActuatorImplKnx extends ComplexSunblindActuatorImpl {
	
	private static final Logger log = Logger.getLogger(ComplexSunblindActuatorImplKnx.class.getName());
	
	//CalimeroNG
	private GroupAddress statusPositionBlindGA;
	private GroupAddress statusPositionSlatGA;
	private GroupAddress positonBlindValueGA;
	private GroupAddress positonSlatValueGA;
	private KNXConnector knxConnector;
	
	public ComplexSunblindActuatorImplKnx(KNXConnector knxConnector, GroupAddress statusPositionBlind, GroupAddress statusPositionSlat, final GroupAddress positonBlindValue, final GroupAddress positonSlatValue){
	super();
	this.statusPositionBlindGA = statusPositionBlind;
	this.statusPositionSlatGA = statusPositionSlat;
	this.positonBlindValueGA = positonBlindValue;
	this.positonSlatValueGA = positonSlatValue;
	this.knxConnector = knxConnector;
	

	
	if((this.statusPositionBlindGA == null) || (this.statusPositionSlatGA == null)){
		
		log.info("For ComplexSunblindActuatorImplKnx the Parameter statusPositionBlind and statusPositionSlat have to be set.");
		
	}
	
	else {
		
		// watchDog for Blind Position
		knxConnector.addWatchDog(statusPositionBlind, new KNXWatchDog() {
			@Override
			public void notifyWatchDog(byte[] apdu) {			
				try {						
					DPTXlator8BitUnsigned x = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_SCALING);// DPT_VALUE_1_UCOUNT);// DPT_SCALING);
					
					x.setData(apdu);
					
					log.finer("New Value"+x.getValueUnsigned(2));
					log.finer("Old Value"+(short)ComplexSunblindActuatorImplKnx.this.positonBlindValue.get()); // value.get());
					if(x.getValueUnsigned(2) != (short)ComplexSunblindActuatorImplKnx.this.positonBlindValue.get()){
						
						ComplexSunblindActuatorImplKnx.this.positonBlindValue.set(x.getValueUnsigned(2));
						//SimpleHVACvalveActuatorImplKnx.this.value.set(x.getValueUnsigned(2));	// getValueUnscaled());
						log.info("Updated to value: "+(short)ComplexSunblindActuatorImplKnx.this.positonBlindValue.get());
					}				
				} catch (KNXException e) {
					e.printStackTrace();
				}
			}
		});
		
		// watchDog for Slat Position
		knxConnector.addWatchDog(statusPositionSlat, new KNXWatchDog() {
			@Override
			public void notifyWatchDog(byte[] apdu) {			
				try {						
					DPTXlator8BitUnsigned x = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_SCALING);// DPT_VALUE_1_UCOUNT);// DPT_SCALING);
					
					x.setData(apdu);
					
					log.finer("New Value"+x.getValueUnsigned(2));
					log.finer("Old Value"+(short)ComplexSunblindActuatorImplKnx.this.positonSlatValue.get()); // value.get());
					if(x.getValueUnsigned(2) != (short)ComplexSunblindActuatorImplKnx.this.positonSlatValue.get()){
						
						ComplexSunblindActuatorImplKnx.this.positonSlatValue.set(x.getValueUnsigned(2));	// getValueUnscaled());
						log.info("Updated to value: "+(short)ComplexSunblindActuatorImplKnx.this.positonSlatValue.get());
					}
										
				} catch (KNXException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
//		if(status == null){
//			// add watch dog on switching group address
//			knxConnector.addWatchDog(positonBlindValue, new KNXWatchDog() {
//				@Override
//				public void notifyWatchDog(byte[] apdu) {			
//					try {						
//						
//						DPTXlator8BitUnsigned x = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_SCALING);
//						
//						x.setData(apdu);
//						
//						if(x.getValueUnscaled() != (short)ComplexSunblindActuatorImplKnx.this.positonBlindValue.get()){
//	//							pos positonBlindValue.get()){
//							ComplexSunblindActuatorImplKnx.this.positonBlindValue.set(x.getValueUnscaled());
//						}
//						
//					} catch (KNXException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		}
	}

	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.	
		
		//System.out.println("+++++++++++++++++++++++++++++");
		super.writeObject(input);
		
		if (input instanceof ComplexSunblindActuator){
		
		//	System.out.println("DAD+++++++++++++++++++++++++++++");
			
//			ComplexSunblindActuator in = (ComplexSunblindActuator) input;
//			
//			log.info("+++++New Value"+in.positonBlindValue().get());
//			log.info("+++++Old Value"+(short)ComplexSunblindActuatorImplKnx.this.positonSlatValue.get());
			
			knxConnector.write(positonBlindValueGA, (int)this.positonBlindValue().get(), ProcessCommunicator.SCALING);
			knxConnector.write(positonSlatValueGA, (int)this.positonSlatValue().get(), ProcessCommunicator.SCALING);
			
		}
		else if(input instanceof Int){
			
		//	System.out.println("in da else Int ++++++++++++++++");
			
			if(input.getHref() == null){
			//	System.out.println("getHref() == null  ++++++++++++++++");
				
				if(input.getInvokedHref() != null && input.getInvokedHref().length() > 0){
					String resourceUriPath = input.getInvokedHref().substring(input.getInvokedHref().lastIndexOf('/') + 1);
					
					if(ComplexSunblindActuator.POSITION_BLIND_HIGH_CONTRACT_HREF.equals(resourceUriPath)){
						knxConnector.write(positonBlindValueGA, (int)this.positonBlindValue().get(), ProcessCommunicator.SCALING);
						//this.positonBlindValue.set(((Int) input).get());
					}
					else if (ComplexSunblindActuator.POSITION_SLAT_CONTRACT_HREF.equals(resourceUriPath)){
						knxConnector.write(positonSlatValueGA, (int)this.positonSlatValue().get(), ProcessCommunicator.SCALING);
					//	this.positonSlatValue.set(((Int) input).get());
					}

				}
			}
			else {
			//	System.out.println("href is do +++++++++");
				if (ComplexSunblindActuator.POSITION_BLIND_HIGH_CONTRACT_HREF.equals(input.getHref().toString())){
					//this.positonBlindValue.set(((Int) input).get());
					knxConnector.write(positonBlindValueGA, (int)this.positonBlindValue().get(), ProcessCommunicator.SCALING);
			
				}
				else if (ComplexSunblindActuator.POSITION_SLAT_CONTRACT_HREF.equals(input.getHref().toString())){
					//this.positonSlatValue.set(((Int) input).get());
					knxConnector.write(positonSlatValueGA, (int)this.positonSlatValue().get(), ProcessCommunicator.SCALING);
		
				}
			}
		}
		
		
		
		//unterscheiden was sich veraendert hat
		//new value old value changed oder bit stzen in ImplKNX

//		
//		super.writeObject(input);
//		
//		knxConnector.write(positonBlindValueGA, (int)this.positonBlindValue().get(), ProcessCommunicator.SCALING);
		
		
	}

	public void refreshObject(){
//		if(status != null){			
//			int value = knxConnector.readInt(status,ProcessCommunicator.SCALING);		
//			this.positonBlindValue().set(value);
//		}		
	}	

}
