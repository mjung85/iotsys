package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.knx;

import java.util.logging.Logger;

import obix.Obj;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorString;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.TextDisplayActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;

public class TextDisplayActuatorImplKnx extends TextDisplayActuatorImpl{
	private GroupAddress status;
	private GroupAddress textDisplay;
	private KNXConnector knxConnector;
	
	public static final Logger knxBus = KNXConnector.knxBus;
	
	public TextDisplayActuatorImplKnx(KNXConnector knxConnector, GroupAddress status, final GroupAddress textDisplay){
		super();
		this.status = status;
		this.textDisplay = textDisplay;
		this.knxConnector = knxConnector;
		if(status == null){
			// add watchdog on switching group address
			knxConnector.addWatchDog(textDisplay, new KNXWatchDog() {
				@Override
				public void notifyWatchDog(byte[] apdu) {			
					try {
						DPTXlatorString x = new DPTXlatorString(DPTXlatorString.DPT_STRING_8859_1);									
						x.setData(apdu);
						
						if(x.getValue().equals(TextDisplayActuatorImplKnx.this.value.get())){
							TextDisplayActuatorImplKnx.this.value.set(x.getValue());
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
		knxConnector.write(textDisplay, this.textDisplayValue().get());
		
//		CsvCreator.instance.writeLine("" + System.currentTimeMillis() + ";" + switching.toString() + ";" + this.value().get());
	}
	
	public void refreshObject(){
		if(status != null){
			String value = knxConnector.readString(status);		
			this.textDisplayValue().set(value);
		}	
	}
}
