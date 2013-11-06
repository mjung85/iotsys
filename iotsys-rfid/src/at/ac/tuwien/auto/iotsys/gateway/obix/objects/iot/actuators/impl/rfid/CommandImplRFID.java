package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.rfid;


import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.RfidConnector;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.ActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.rfid.EventImpRfidTAG;
import obix.Contract;
import obix.Enum;
import obix.Str;
import obix.Uri;
import obix.Bool;
import obix.Obj;
public class CommandImplRFID extends ActuatorImpl implements CommandRFID {
	private static final Logger log = Logger.getLogger(EventImpRfidTAG.class.getName());
	
	
	private RfidConnector connector;
	private String hexAddress;
	
	protected Bool continousRead = new Bool(false);
	protected Bool singleSelect = new Bool(false);
	protected Bool multiSelect = new Bool(false);
	protected Bool abortContinousRead = new Bool(false);
	protected Bool reset = new Bool(false);
	
	public CommandImplRFID(RfidConnector connector, final String hexAddress)
	{
		setIs(new Contract(CommandRFID.CONTRACT));

		this.connector = connector;
		

		setIs(new Contract(CommandRFID.CONTRACT));
		continousRead.setWritable(true);
		Uri continousReadUri = new Uri(CommandRFID.RFID_CONTINOUS_HREF);
		continousRead.setHref(continousReadUri);
		continousRead.setName(CommandRFID.RFID_CONTINOUS_NAME);			

		setIs(new Contract(CommandRFID.CONTRACT));
		abortContinousRead.setWritable(true);
		Uri abortContinousReadUri = new Uri(CommandRFID.RFID_ABORT_CONTINOUS_HREF);
		abortContinousRead.setHref(abortContinousReadUri);
		abortContinousRead.setName(CommandRFID.RFID_ABORT_CONTINOUS_NAME);
		
		setIs(new Contract(CommandRFID.CONTRACT));
		singleSelect.setWritable(true);
		Uri singleSelectUri = new Uri(CommandRFID.RFID_S_SELECT_HREF);
		singleSelect.setHref(singleSelectUri);
		singleSelect.setName(CommandRFID.RFID_S_SELECT_NAME);			

		setIs(new Contract(CommandRFID.CONTRACT));
		multiSelect.setWritable(true);
		Uri multiSelectUri = new Uri(CommandRFID.RFID_M_SELECT_HREF);
		multiSelect.setHref(multiSelectUri);
		multiSelect.setName(CommandRFID.RFID_M_SELECT_NAME);			

		
		this.add(singleSelect);
		this.add(multiSelect);
		this.add(abortContinousRead);
		this.add(continousRead);
	}
	
	public void initialize(){
		super.initialize();
		// But stuff here that should be executed after object creation
	}
	
	public void writeObject(Obj input){
		String resourceUriPath = "";
		if (input.getHref() == null) {
			resourceUriPath = input.getInvokedHref().substring(
					input.getInvokedHref().lastIndexOf('/') + 1);
		} else {
			resourceUriPath = input.getHref().get();
		}
		
		if (input instanceof Bool) 
		{
			
			log.info("RFID Command");
			
			if(singleSelect.getHref().get().equals(resourceUriPath))
			{
				this.singleSelect.set(((Bool)input).get());
				
				log.info("Writing on RFID Device: "
						+ this.singleSelect().get()
				);
				
				connector.sendCommand("s");
				
			} else if(multiSelect.getHref().get().equals(resourceUriPath)){
				
				this.multiSelect.set(((Bool)input).get());
				
				log.info("Writing on RFID Device: "
						+ this.multiSelect().get()
				);
				
				connector.sendCommand("m");
				
			} else if(continousRead.getHref().get().equals(resourceUriPath)){
				
				this.continousRead.set(((Bool)input).get());
				
				log.info("Writing on RFID Device: "
						+ this.continousRead().get()
				);
				
				connector.sendCommand("c");
				
			} else if(abortContinousRead.getHref().get().equals(resourceUriPath)){
				
				this.abortContinousRead.set(((Bool)input).get());
				
				log.info("Writing on RFID Device: "
						+ this.abortContinousRead().get()
				);
				
				connector.sendCommand(".");
			}
		}
	}

	public void refreshObject(){
	
		if(singleSelect.get() == true){
			
			this.singleSelect().set(false);
			log.info("singleSelect is: " + singleSelect );
			
		} else if(abortContinousRead.get() == true){
			
			this.abortContinousRead().set(false);
			log.info("abortContinousRead is: " + abortContinousRead );
			this.continousRead().set(false);
			log.info("continousRead is: " + continousRead );
			
		} else if(multiSelect.get() == true){
			
			this.multiSelect().set(false);
			log.info("multiSelect is: " + multiSelect );
			
		}
	}

	@Override
	public Bool continousRead() 
	{
		return this.continousRead;
	}

	@Override
	public Bool abortContinousRead() 
	{
		return this.abortContinousRead;
	}

	@Override
	public Bool singleSelect() 
	{
		return this.singleSelect;
	}

	@Override
	public Bool multiSelect() 
	{
		return this.multiSelect;
	}

	@Override
	public Bool reset() 
	{
		return this.reset;
	}


}
