package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import java.util.logging.Logger;

import obix.Obj;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator3BitControlled;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;

public class DPST_3_7_ImplKnx extends DPST_3_7_Impl
{
	private static final Logger log = Logger.getLogger(DPST_3_7_ImplKnx.class.getName());

	private GroupAddress groupAddress;

	private KNXConnector connector;

	private boolean readFlag = false;

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
				try
				{
					DPTXlator3BitControlled x = new DPTXlator3BitControlled(DPTXlator3BitControlled.DPT_CONTROL_DIMMING);

					x.setData(apdu, 0);

					// String[] a = x.getAllValues();

					log.fine("Value for " + DPST_3_7_ImplKnx.this.getHref() + " now " + x.getValueSigned());
					value.set(x.getValueSigned());
				}
				catch (KNXException e)
				{
					e.printStackTrace();
				}
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
		connector.write(groupAddress, (int) this.value().get(), ProcessCommunicator.UNSCALED);
	}
}
