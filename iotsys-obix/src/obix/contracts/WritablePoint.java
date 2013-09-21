package obix.contracts;

import obix.*;

/**
 * WritablePoint
 * 
 * @author obix.tools.Obixc
 * @creation 24 May 06
 * @version $Revision$ $Date$
 */
public interface WritablePoint extends IObj, Point
{
	public static final String CONTRACT = "obix:WritablePoint";

	public static final String writePointContract = "<op name='writePoint' in='" + WritePointIn.CONTRACT + "' out='" + Point.CONTRACT + "'/>";

	public Op writePoint();

}
