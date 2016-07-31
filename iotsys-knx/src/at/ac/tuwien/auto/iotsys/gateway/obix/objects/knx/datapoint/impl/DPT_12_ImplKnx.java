package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import java.util.logging.Logger;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator4ByteUnsigned;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorString;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import at.ac.tuwien.auto.calimero.Priority;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl.DPT_12_Impl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import obix.Obj;

public class DPT_12_ImplKnx extends DPT_12_Impl
{
	private static final Logger log = Logger.getLogger(DPT_9_ImplKnx.class.getName());

	private GroupAddress groupAddress;
	private KNXConnector connector;

	// if more group addresses are needed just add more constructor parameters.
	public DPT_12_ImplKnx(KNXConnector connector, GroupAddress groupAddress, String name, String displayName, String display, boolean writable, boolean readable)
	{
		super(name, displayName, display, writable, readable);

		this.groupAddress = groupAddress;
		this.connector = connector;

		this.createWatchDog();
	}

	public DPT_12_ImplKnx(KNXConnector connector, DataPointInit dataPointInit)
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
					DPTXlator4ByteUnsigned x = new DPTXlator4ByteUnsigned(DPTXlator4ByteUnsigned.DPT_VALUE_4_UCOUNT);
					ProcessCommunicatorImpl.extractGroupASDU(apdu, x);

					log.info("4Byte Int for " + DPT_12_ImplKnx.this.getHref() + " now " + x.getValueUnsigned());

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
			long value = connector.readUint(groupAddress);
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
			connector.write(groupAddress, (long)this.value().get());
		}
	}
}
