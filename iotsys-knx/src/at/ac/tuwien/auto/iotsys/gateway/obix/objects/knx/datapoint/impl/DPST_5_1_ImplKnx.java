package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.impl.DPST_5_1_Impl;

public class DPST_5_1_ImplKnx extends DPST_5_1_Impl
{
	private GroupAddress groupAddress;

	private KNXConnector connector;

	private boolean readFlag = true; // TODO need to be set based on ETS
										// configuration

	// if more group addresses are needed just add more constructor parameters.
	public DPST_5_1_ImplKnx(KNXConnector connector, GroupAddress groupAddress, String name, String displayName, String display, boolean writable)
	{
		super(name, displayName, display, writable);

		this.groupAddress = groupAddress;
		this.connector = connector;

		// if it is not possible to read from the group address --> create a
		// watchdog that monitors the communicaiton

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
//					DPTXlator2ByteFloat x = new DPTXlator2ByteFloat(DPTXlator2ByteFloat.DPT_TEMPERATURE);
//
//					x.setData(apdu, 0);
//
//					// String[] a = x.getAllValues();
//
//					log.fine("Temperature for " + DPST_5_1_ImplKnx.this.getHref() + " now " + x.getValueFloat(1));
//					value.set(x.getValueFloat(1));
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
			// TODO read from KNX bus
		}
	}
}
