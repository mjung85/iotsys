package at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.impl;

import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.contracts.impl.RangeImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumCompareTypes;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.enumeration.EnumStringCompareTypes;

public class EnumStringCompareTypesImpl extends RangeImpl implements EnumCompareTypes
{
	public EnumStringCompareTypesImpl()
	{
		super(new Uri(EnumStringCompareTypes.HREF));
	}

	protected void initValues()
	{
		addElement(new ObjElement(EnumStringCompareTypes.KEY_EQ));
		addElement(new ObjElement(EnumStringCompareTypes.KEY_STARTS_WITH));
		addElement(new ObjElement(EnumStringCompareTypes.KEY_ENDS_WITH));
		addElement(new ObjElement(EnumStringCompareTypes.KEY_CONTAINS));
	}
}
