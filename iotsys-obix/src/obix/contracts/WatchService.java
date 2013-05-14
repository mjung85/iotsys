package obix.contracts;

import obix.*;

/**
 * WatchService
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public interface WatchService
  extends IObj
{

  public static final String makeContract = "<op name='make' in='obix:Nil' out='obix:Watch'/>";
  public Op make();

}
