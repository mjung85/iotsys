package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import java.util.logging.Logger;

import obix.Obj;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl.DPT_5_A_Impl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;

public class DPT_5_A_ImplKnx extends DPT_5_A_Impl
{
	private static final Logger log = Logger.getLogger(DPT_5_A_ImplKnx.class.getName());

	private GroupAddress groupAddress;
	private KNXConnector connector;

	// if more group addresses are needed just add more constructor parameters.
	public DPT_5_A_ImplKnx(KNXConnector connector, GroupAddress groupAddress, String name, String displayName, String display, boolean writable, boolean readable)
	{
		super(name, displayName, display, writable, readable);

		this.groupAddress = groupAddress;
		this.connector = connector;

		this.createWatchDog();
	}

	public DPT_5_A_ImplKnx(KNXConnector connector, DataPointInit dataPointInit)
	{
		this(connector, dataPointInit.getGroupAddress(), dataPointInit.getName(), dataPointInit.getDisplayName(), dataPointInit.getDisplay(), dataPointInit.isWritable(), dataPointInit.isReadable());
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
					DPTXlator8BitUnsigned x = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_VALUE_1_UCOUNT);
					ProcessCommunicatorImpl.extractGroupASDU(apdu, x);

					log.info("8Bit Unsigned for " + DPT_5_A_ImplKnx.this.getHref() + " now " + x.getValueUnsigned());

					value().setNull(false);
					value().set(x.getValueUnsigned());
					
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
		// here we need to read from the bus, only if the read flag is set at the data point
		if (this.value().isReadable())
		{
			int value = connector.readInt(groupAddress, DPTXlator8BitUnsigned.DPT_VALUE_1_UCOUNT.getID());
			
			this.value().set(value);
			this.value().setNull(false);
		}

		// run refresh from super class
		super.refreshObject();
	}

	@Override
	public void writeObject(Obj obj)
	{
		if (this.value().isWritable())
		{
			// always pass the writeObject call to the super method (triggers, oBIX related internal services like watches, alarms, ...)
			// also the internal instance variables get updated
			super.writeObject(obj);

			// set isNull to false
			this.value().setNull(false);
			
			// now write this.value to the KNX bus
			connector.write(groupAddress, this.value().get());
		}
	}
}
