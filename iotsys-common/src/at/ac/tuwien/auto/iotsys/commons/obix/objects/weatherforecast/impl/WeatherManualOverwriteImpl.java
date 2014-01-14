package at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl;

import obix.Bool;
import obix.Int;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.contracts.impl.RangeImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.WeatherManualOverwrite;

public class WeatherManualOverwriteImpl extends RangeImpl implements WeatherManualOverwrite{

	public WeatherManualOverwriteImpl(Uri href)
	{
		super(href);
	}

	public Obj getValue(obix.Enum in)
	{
		if (in != null)
		{
			Obj obj = this.getByHref(this.getChildHref(in.get()));

			if (obj != null)
			{
				return obj;
			}
		}
		return null;
	}

	public String getName(Obj value)
	{
		for (Obj child : this.list())
		{
			if (child instanceof Bool)
			{
				if (child.getBool() == value.getBool())
				{
					return child.getName();
				}
			}
			else if (child instanceof Int)
			{
				if (child.getInt() == value.getInt())
				{
					return child.getName();
				}
			}
		}
		return null;
	}
	
	protected void initValues()
	{
		addElement(new IntElement(WeatherManualOverwrite.NAME_OFF, WeatherManualOverwrite.ID_OFF));
		addElement(new IntElement(WeatherManualOverwrite.NAME_STORM_ALARM, WeatherManualOverwrite.ID_STORM_ALARM));
		addElement(new IntElement(WeatherManualOverwrite.NAME_STORM_WARNING, WeatherManualOverwrite.ID_STORM_WARNING));		
	}
}
