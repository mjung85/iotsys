package obix.contracts;

import obix.*;

/**
 * WritablePoint
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public interface WritablePoint
  extends IObj, Point
{

  public static final String writePointContract = "<op name='writePoint' in='obix:WritePointIn' out='obix:Point'/>";
  public Op writePoint();

}
