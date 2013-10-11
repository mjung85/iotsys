package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl.DPT_3_Impl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;

public class DPT_3_ImplKnx extends DPT_3_Impl
{
//	private static final Logger log = Logger.getLogger(DPT_3_ImplKnx.class.getName());
//
//	private GroupAddress groupAddress;
//	private KNXConnector connector;

	public DPT_3_ImplKnx(KNXConnector connector, GroupAddress groupAddress, String name, String displayName, String display)
	{
		super(name, displayName, display);

//		this.groupAddress = groupAddress;
//		this.connector = connector;
	}
	
	public DPT_3_ImplKnx(KNXConnector connector, DataPointInit dataPointInit)
	{
		this(connector, dataPointInit.getGroupAddress(), dataPointInit.getName(), dataPointInit.getDisplayName(), dataPointInit.getDisplay());
	}
}
