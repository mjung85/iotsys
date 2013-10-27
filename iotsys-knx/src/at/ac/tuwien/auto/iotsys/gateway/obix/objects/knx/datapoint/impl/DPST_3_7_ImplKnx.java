package at.ac.tuwien.auto.iotsys.gateway.obix.objects.knx.datapoint.impl;

import java.util.logging.Logger;

import obix.Int;
import obix.Obj;
import obix.contracts.impl.NilImpl;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl.DPST_3_7_Impl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.parameter.ParameterDimming;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;

public class DPST_3_7_ImplKnx extends DPST_3_7_Impl
{
	private static final Logger log = Logger.getLogger(DPST_3_7_ImplKnx.class.getName());

	private GroupAddress groupAddress;
	private KNXConnector connector;

	public DPST_3_7_ImplKnx(KNXConnector connector, GroupAddress groupAddress, String name, String displayName, String display)
	{
		super(name, displayName, display);

		this.groupAddress = groupAddress;
		this.connector = connector;
	}

	public DPST_3_7_ImplKnx(KNXConnector connector, DataPointInit dataPointInit)
	{
		this(connector, dataPointInit.getGroupAddress(), dataPointInit.getName(), dataPointInit.getDisplayName(), dataPointInit.getDisplay());
	}

	@Override
	protected Obj doIncrease(Obj in)
	{
		return doRun(in, ProcessCommunicator.BOOL_INCREASE);
	}

	@Override
	protected Obj doDecrease(Obj in)
	{
		return doRun(in, ProcessCommunicator.BOOL_DECREASE);
	}

	private Obj doRun(Obj in, boolean control)
	{
		try
		{
			if (in instanceof ParameterDimming)
			{
				ParameterDimming p = (ParameterDimming) in;
				Int value = p.value();

				if (value.get() > ParameterDimming.MAX_VALUE)
					value.set(ParameterDimming.MAX_VALUE, false);
				else if (value.get() < ParameterDimming.MIN_VALUE)
					value.set(ParameterDimming.MIN_VALUE, false);

				int stepCode = 0;

				if (value.get() > 0)
					stepCode = (int) Math.round(((Math.log((float) ParameterDimming.MAX_VALUE / value.get()) / Math.log(2)) + 1));

				log.fine("dimming with step code" + stepCode);

				if (connector.getProcessCommunicator() != null)
				{
					connector.getProcessCommunicator().write(groupAddress, control, (byte) stepCode);
				}
				else
				{
					log.severe("Process communicator is not available!");
				}
			}
		}
		catch (KNXFormatException e)
		{

			e.printStackTrace();
		}
		catch (KNXException e)
		{

			e.printStackTrace();
		}
		return new NilImpl();
	}
}
