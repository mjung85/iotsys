package obix.contracts;

import obix.*;

/**
 * AckAlarmOut
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public interface AckAlarmOut
  extends IObj
{

  public static final String alarmContract = "<obj name='alarm' is='obix:AckAlarm obix:Alarm'/>";
  public AckAlarm alarm();

}
