package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import java.util.logging.Logger;

import obix.Obj;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;

public class DPST_3_7_ImplKnx extends DPST_3_7_Impl
{
	private static final Logger log = Logger.getLogger(DPST_3_7_ImplKnx.class.getName());

	private GroupAddress groupAddress;

	private KNXConnector connector;

	private boolean readFlag = false;
	
	// Divide the 100% range into 8 intervals
	private final int STEP_SIZE = 4; // 2^(STEP_SIZE-1) Intervals
	
	// current interval we are in
	// TODO this needs to be synced with possible other updates with different step size!!
	private int curInterval = 1;
	
	private double intervalSize = 100 / (Math.pow(2,STEP_SIZE-1)); 
	
	// if more group addresses are needed just add more constructor parameters.
	public DPST_3_7_ImplKnx(KNXConnector connector, GroupAddress groupAddress)
	{
		super();

		this.groupAddress = groupAddress;
		this.connector = connector;
		
		if (readFlag)
			this.createWatchDog();
	}

	public void createWatchDog()
	{
		connector.addWatchDog(groupAddress, new KNXWatchDog()
		{
			@Override
			public void notifyWatchDog(byte[] apdu)
			{
//				try
//				{
//					DPTXlator3BitControlled x = new DPTXlator3BitControlled(DPTXlator3BitControlled.DPT_CONTROL_DIMMING);
//
//					x.setData(apdu, 0);
//
//					// String[] a = x.getAllValues();
//
//					log.fine("Value for " + DPST_3_7_ImplKnx.this.getHref() + " now " + x.getValueSigned());
//					value.set(x.getValueSigned());
//				}
//				catch (KNXException e)
//				{
//					e.printStackTrace();
//				}
			}
		});
	}

	@Override
	public void refreshObject()
	{
		// here we need to read from the bus, only if the read flag is set at
		// the data point
		if (readFlag)
		{
			int value = connector.readInt(groupAddress, ProcessCommunicator.UNSCALED);
			this.value().set(value);
		}
	}

	@Override
	public void writeObject(Obj obj)
	{
		// always pass the writeObject call to the super method (triggers oBIX
		// related internal services like watches, alarms, ...)
		// also the internal instance variables get updated		
		
		super.writeObject(obj);
		
		

		// now write this.value to the KNX bus
		//connector.write(groupAddress, (int) this.value().get(), ProcessCommunicator.UNSCALED);
		try {
			// if the new val is outside the current interval increase or decrease the current value
			// use step size 8
			log.info("interval size is: " + intervalSize);
			while(this.value().get() > this.curInterval * intervalSize){
				// increase the current interval			
				connector.getProcessCommunicator().write(groupAddress, ProcessCommunicator.BOOL_INCREASE,(byte) STEP_SIZE);
				this.curInterval += 1;
			}
			
			// current value is below the lower interval bounds
			while(this.value().get() < (this.curInterval) * intervalSize){
				// increase the current interval			
				connector.getProcessCommunicator().write(groupAddress, ProcessCommunicator.BOOL_DECREASE,(byte) STEP_SIZE);
				this.curInterval -= 1;
			}		
			
		} catch (KNXFormatException e) {
			
			e.printStackTrace();
		} catch (KNXException e) {
			
			e.printStackTrace();
		}
	}
}
