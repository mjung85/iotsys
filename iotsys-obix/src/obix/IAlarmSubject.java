package obix;

import obix.contracts.Alarm;
import obix.contracts.AlarmSubject;

public interface IAlarmSubject extends AlarmSubject {
	public void addAlarm(Alarm alarm);
}
