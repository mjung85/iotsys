package obix.contracts;

import obix.*;

/**
 * PointAlarm
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public interface PointAlarm
  extends IObj, Alarm
{

  public Obj alarmValue();

}
