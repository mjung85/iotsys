package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.knx;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import obix.Obj;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.SimpleHVACvalveActuatorImpl;

public class SimpleHVACvalveActuatorImplKnx extends SimpleHVACvalveActuatorImpl{
	
	//CalimeroNG
	private GroupAddress status;
	private GroupAddress valuePosition;
	
	private KNXConnector knxConnector;
	private static final Logger log = Logger.getLogger(SimpleHVACvalveActuatorImplKnx.class.getName());
	
//	public String getHexString(byte[] b) {
//		
//		   StringBuffer sb = new StringBuffer();
//		   for (int i = 0; i < b.length; i++){
//		      if (i > 0)
//		         sb.append(':');
//		      sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
//		   }
//		   return sb.toString();
//		   
//		}
	
	public SimpleHVACvalveActuatorImplKnx(KNXConnector knxConnector, GroupAddress status, final GroupAddress valuePosition){
	super();
	this.status = status;
	this.valuePosition = valuePosition;
	this.knxConnector = knxConnector;
	
	if(status == null){
		// add watch dog on switching group address
		knxConnector.addWatchDog(valuePosition, new KNXWatchDog() {
			@Override
			public void notifyWatchDog(byte[] apdu) {			
				try {						
					DPTXlator8BitUnsigned x = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_SCALING);// DPT_VALUE_1_UCOUNT);// DPT_SCALING);
					
					x.setData(apdu);
					
		
//					System.out.println("++++++++++++++++++++++++++++++SimpleHVAC+++++");
//					System.out.println("+++++DEBUB APDU:"+ getHexString(apdu));
//					System.out.println("getDate: "+getHexString(x.getData()));
					
					
					log.finer("New Value"+x.getValueUnsigned(2));
					log.finer("Old Value"+(short)SimpleHVACvalveActuatorImplKnx.this.value.get());
					if(x.getValueUnsigned(2) != (short)SimpleHVACvalveActuatorImplKnx.this.value.get()){
						
						SimpleHVACvalveActuatorImplKnx.this.value.set(x.getValueUnsigned(2));	// getValueUnscaled());
						log.info("Updated to value: "+(short)SimpleHVACvalveActuatorImplKnx.this.value.get());
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
	knxConnector.write(valuePosition, (int)this.value().get(), ProcessCommunicator.SCALING);// UNSCALED);
}

	public void refreshObject(){
//		if(status != null){			
//			int value = knxConnector.readInt(status,ProcessCommunicator.UNSCALED);// SCALING);		
//			this.value().set(value);
//		}		
		
		if(status != null){			
			int value = knxConnector.readInt(status,ProcessCommunicator.SCALING); //UNSCALED);		
			this.value().set(value);
		}	
		
	}	

}
