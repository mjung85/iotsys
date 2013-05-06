package obix.contracts;

import obix.*;

/**
 * Watch
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public interface Watch
  extends IObj
{

  public static final String leaseContract = "<reltime name='lease' val='PT0S' writable='true' min='PT0S'/>";
  public Reltime lease();

  public static final String addContract = "<op name='add' in='obix:WatchIn' out='obix:WatchOut'/>";
  public Op add();

  public static final String removeContract = "<op name='remove' in='obix:WatchIn' out='obix:obj'/>";
  public Op remove();

  public static final String pollChangesContract = "<op name='pollChanges' in='obix:obj' out='obix:WatchOut'/>";
  public Op pollChanges();

  public static final String pollRefreshContract = "<op name='pollRefresh' in='obix:obj' out='obix:WatchOut'/>";
  public Op pollRefresh();

  public Op delete();

}
