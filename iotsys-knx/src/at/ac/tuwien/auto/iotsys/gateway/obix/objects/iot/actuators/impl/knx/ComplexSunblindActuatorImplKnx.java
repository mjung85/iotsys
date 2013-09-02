package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.knx;

import obix.Obj;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.ComplexSunblindActuatorImpl;


public class ComplexSunblindActuatorImplKnx extends ComplexSunblindActuatorImpl {
	
	//CalimeroNG
	private GroupAddress status;
	private GroupAddress positonBlindValueGA;
	private GroupAddress positonSlatValueGA;
	private KNXConnector knxConnector;
	
	public ComplexSunblindActuatorImplKnx(KNXConnector knxConnector, GroupAddress status, final GroupAddress positonBlindValue, final GroupAddress positonSlatValue){
	super();
	this.status = status;
	this.positonBlindValueGA = positonBlindValue;
	this.positonSlatValueGA = positonSlatValue;
	this.knxConnector = knxConnector;
	
//	if(status == null){
//		// add watch dog on switching group address
//		knxConnector.addWatchDog(brightness, new KNXWatchDog() {
//			@Override
	
//			public void notifyWatchDog(byte[] apdu) {			
//				try {						
//					DPTXlator8BitUnsigned x = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_VALUE_1_UCOUNT);
//				
//					x.setData(apdu);
//																			
//					if(x.getValueUnscaled() != (short)BrightnessActuatorImplKnx.this.value.get()){
//						BrightnessActuatorImplKnx.this.value.set(x.getValueUnscaled());
//					}
//					
//				} catch (KNXException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
	if(status == null){
		// add watch dog on switching group address
		knxConnector.addWatchDog(positonBlindValue, new KNXWatchDog() {
			@Override
			public void notifyWatchDog(byte[] apdu) {			
				try {						
					
					DPTXlator8BitUnsigned x = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_SCALING);
					
					x.setData(apdu);
					
					if(x.getValueUnscaled() != (short)ComplexSunblindActuatorImplKnx.this.positonBlindValue.get()){
//							pos positonBlindValue.get()){
						ComplexSunblindActuatorImplKnx.this.positonBlindValue.set(x.getValueUnscaled());
					}
					
				} catch (KNXException e) {
					e.printStackTrace();
				}
			}
		});
	}
}

public void writeObject(Obj input){
	// A write on this object was received, update the according data point.	
	super.writeObject(input);				
	knxConnector.write(positonBlindValueGA, (int)this.positonBlindValue().get(), ProcessCommunicator.SCALING);
}

	public void refreshObject(){
		if(status != null){			
			int value = knxConnector.readInt(status,ProcessCommunicator.SCALING);		
			this.positonBlindValue().set(value);
		}		
	}	

}
