package at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.impl;

import java.util.logging.Logger;

import obix.Bool;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.DPT_1;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.datapoint.DPT_16;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.encoding.impl.EncodingImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.encoding.impl.EncodingsImpl;

public class DPT_16_Impl extends DatapointImpl implements DPT_16 {

	private static final Logger log = Logger.getLogger(DPT_16_Impl.class.getName());

	private Str value = new Str();

	public DPT_16_Impl(String name, String displayName, String display, boolean writable, boolean readable)
	{
		super(name, displayName, display);

		this.addIs(new Contract(DPT_16.CONTRACT));

		this.value.setName("value");
		this.value.setHref(new Uri("value"));
		this.value.setWritable(writable);
		this.value.setReadable(readable);
		this.value.setNull(true);
		this.add(value);
	}

	@Override
	public boolean isValueWritable()
	{
		return value.isWritable();
	}

	@Override
	public boolean isValueReadable()
	{
		return value.isReadable();
	}

	@Override
	public Str value()
	{
		return value;
	}

	@Override
	public void writeObject(Obj input)
	{
		if (this.value.isWritable())
		{
			if (input instanceof DPT_1)
			{
				DPT_16 in = (DPT_16) input;
				log.info("Writing on data point.");
				this.value.set(in.value().get());
			}
			else if (input instanceof Str)
			{
				this.value.set(((Str) input).get());
			}
			else if (input instanceof Real)
			{
				this.value.set(((Real) input).get());
			}
			else if (input instanceof Int)
			{
				this.value.set(((Int) input).get());
			}
			else if (input instanceof Bool){
				this.value.set(((Bool) input).get());
			}
			else if (input instanceof obix.Enum)
			{
				// set value from encoding
				if (input.isWritable())
				{
					obix.Enum in = (obix.Enum) input;

					if (in.getRange() != null)
					{
						EncodingImpl encoding = EncodingsImpl.getInstance().getEncoding(in.getRange().getPath());

						if (encoding != null)
						{
							Obj value = encoding.getValue(in);

							if (value != null)
							{
								this.value.set(((Bool) value).get());
							}
						}
					}
				}
			}
		}
	}

}
