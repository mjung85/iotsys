package obix.contracts;

import obix.*;

/**
 * HistoryAppendOut
 *
 * @author    Robert Horvath
 * @creation  10 July 13
 * @version   $Revision$ $Date$
 */
public interface HistoryAppendOut
  extends IObj
{

  public static final String numAddedContract = "<int name='numAdded' val='0' min='0'/>";
  public Int numAdded();
  
  public static final String newCountContract = "<int name='newCount' val='0' min='0'/>";
  public Int newCount();

  public static final String newStartContract = "<abstime name='newStart' null='true'/>";
  public Abstime newStart();

  public static final String newEndContract = "<abstime name='newEnd' null='true'/>";
  public Abstime newEnd();
  
}
