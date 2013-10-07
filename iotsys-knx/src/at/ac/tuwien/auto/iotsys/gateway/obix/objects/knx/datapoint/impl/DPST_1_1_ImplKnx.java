package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import java.util.logging.Logger;

import obix.Obj;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl.DPST_1_1_Impl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;

public class DPST_1_1_ImplKnx extends DPST_1_1_Impl
{
	private static final Logger log = Logger.getLogger(DPST_1_1_ImplKnx.class.getName());

	private GroupAddress groupAddress;
	private KNXConnector connector;
	private boolean readable;
	private boolean writable;

	public DPST_1_1_ImplKnx(KNXConnector connector, GroupAddress groupAddress, String name, String displayName, String display, boolean writable, boolean readable)
	{
		super(name, displayName, display, writable);

		this.groupAddress = groupAddress;
		this.connector = connector;
		this.writable = writable;
		this.readable = readable;

		// if it is not possible to read from the group address --> create a watchdog that monitors the communication
		if (!this.readable)
			this.createWatchDog();
	}

	public DPST_1_1_ImplKnx(KNXConnector connector, DataPointInit dataPointInit)
	{
		this(connector, dataPointInit.getGroupAddress(), dataPointInit.getName(), dataPointInit.getDisplayName(), dataPointInit.getDisplay(), dataPointInit.isWritable(), dataPointInit.isReadable());
	}

	public void createWatchDog()
	{
		if(connector != null && groupAddress != null){
			connector.addWatchDog(groupAddress, new KNXWatchDog()
			{
				@Override
				public void notifyWatchDog(byte[] apdu)
				{
					try
					{
						DPTXlatorBoolean x = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_SWITCH);
	
						x.setData(apdu, 0);
	
						log.fine("Switch for " + DPST_1_1_ImplKnx.this.getHref() + " now " + x.getValueBoolean());
	
						value.set(x.getValueBoolean());
					}
					catch (KNXException e)
					{
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public void refreshObject()
	{
		// here we need to read from the bus, only if the read flag is set at the data point
		if (this.readable)
		{
			boolean value = connector.readBool(groupAddress);
			this.value().set(value);
		}

		// run refresh-method from super class
		super.refreshObject();
	}

	@Override
	public void writeObject(Obj obj)
	{
		if (this.writable)
		{
			// always pass the writeObject call to the super method (triggers, oBIX related internal services like watches, alarms, ...)
			// also the internal instance variables get updated
			super.writeObject(obj);

			// now write this.value to the KNX bus
			connector.write(groupAddress, this.value().get());
		}
	}
}
