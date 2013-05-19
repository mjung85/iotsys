package obix.contracts;

import obix.*;

/**
 * StatefulAlarm
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public interface StatefulAlarm
  extends IObj, Alarm
{

  public static final String normalTimestampContract = "<abstime name='normalTimestamp' val='1969-12-31T19:00:00.000-05:00' null='true'/>";
  public Abstime normalTimestamp();

}
