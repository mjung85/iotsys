package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import java.util.logging.Logger;

import obix.Obj;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl.DPT_1_Impl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;

public class DPT_1_ImplKnx extends DPT_1_Impl
{
	private static final Logger log = Logger.getLogger(DPT_1_ImplKnx.class.getName());

	private GroupAddress groupAddress;
	private KNXConnector connector;

	public DPT_1_ImplKnx(KNXConnector connector, GroupAddress groupAddress, String name, String displayName, String display, boolean writable, boolean readable)
	{
		super(name, displayName, display, writable, readable);

		this.groupAddress = groupAddress;
		this.connector = connector;

		// if it is not possible to read from the group address --> create a watchdog that monitors the communication
		if (!this.value().isReadable())
			this.createWatchDog();
	}

	public DPT_1_ImplKnx(KNXConnector connector, DataPointInit dataPointInit)
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
					DPTXlatorBoolean x = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_BOOL);

					x.setData(apdu, 0);

					log.fine("Switch for " + DPT_1_ImplKnx.this.getHref() + " now " + x.getValueBoolean());

					value.set(x.getValueBoolean());
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
		// if (this.readable)
		// {
		// boolean value = connector.readBool(groupAddress);
		// this.value().set(value);
		// }
	}

	@Override
	public void writeObject(Obj obj)
	{
		if (this.value().isWritable())
		{
			// always pass the writeObject call to the super method (triggers, oBIX related internal services like watches, alarms, ...)
			// also the internal instance variables get updated
			super.writeObject(obj);

			// now write this.value to the KNX bus
			connector.write(groupAddress, this.value().get());
		}
	}
}
