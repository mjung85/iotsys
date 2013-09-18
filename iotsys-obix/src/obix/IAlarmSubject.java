package obix;

import obix.contracts.Alarm;
import obix.contracts.AlarmSubject;

public interface IAlarmSubject extends AlarmSubject {
	/**
	 * Add an alarm to this AlarmSubject
	 * @param alarm Alarm to add
	 */
	public void addAlarm(Alarm alarm);
}
