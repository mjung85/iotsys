package obix.contracts;

import obix.*;

/**
 * Alarm
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public interface Alarm
  extends IObj
{

  public Ref source();

  public Abstime timestamp();

}
