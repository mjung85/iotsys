package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.knx;

import obix.Obj;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.SimpleHVACvalveActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;

public class SimpleHVACvalveActuatorImplKnx extends SimpleHVACvalveActuatorImpl{
	
	//CalimeroNG
	private GroupAddress status;
	private GroupAddress valuePosition;
	
	private KNXConnector knxConnector;
	
	public SimpleHVACvalveActuatorImplKnx(KNXConnector knxConnector, GroupAddress status, final GroupAddress valuePosition){
	super();
	this.status = status;
	this.valuePosition = valuePosition;
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
		knxConnector.addWatchDog(valuePosition, new KNXWatchDog() {
			@Override
			public void notifyWatchDog(byte[] apdu) {			
				try {						
					
					DPTXlator8BitUnsigned x = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_SCALING);
					
					x.setData(apdu);
																			
					if(x.getValueUnscaled() != (short)SimpleHVACvalveActuatorImplKnx.this.value.get()){
						SimpleHVACvalveActuatorImplKnx.this.value.set(x.getValueUnscaled());
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
	knxConnector.write(valuePosition, (int)this.value().get(), ProcessCommunicator.SCALING);
}

	public void refreshObject(){
		if(status != null){			
			int value = knxConnector.readInt(status,ProcessCommunicator.SCALING);		
			this.value().set(value);
		}		
	}	

}
