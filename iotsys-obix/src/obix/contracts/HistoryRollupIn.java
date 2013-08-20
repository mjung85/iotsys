package obix.contracts;

import obix.*;

/**
 * HistoryRollupIn
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public interface HistoryRollupIn
  extends IObj, HistoryFilter
{

  public Reltime interval();

}
