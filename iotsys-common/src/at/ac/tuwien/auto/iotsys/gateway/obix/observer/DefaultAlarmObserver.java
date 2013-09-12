package at.ac.tuwien.auto.iotsys.gateway.obix.observer;

import obix.AlarmCondition;
import obix.contracts.Alarm;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.AlarmImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.AlarmSubjectImpl;

/**
 * The Default Alarm Observer generates Alarms that are
 * reported to the default Alarm Subject
 */
public class DefaultAlarmObserver extends AlarmObserver {
	
	/**
	 * A Observer that observes an alarm source for an alarm condition and 
	 * reports generated Alarms to the default Alarm Subject.
	 * @param alarmCondition the alarm condition to watch for
	 * @param stateful <code>true</code> if stateful alarms should be generated, <code>false</code> otherwise
	 * @param acked <code>true</code> if acknowledgeable alarms should be generated, <code>false</code> otherwise
	 */
	public DefaultAlarmObserver(AlarmCondition alarmCondition, boolean stateful, boolean acked) {
		super(AlarmSubjectImpl.defaultAlarmSubject(), alarmCondition, stateful, acked);
	}
	
	/**
	 * A Observer that observes an alarm source for an alarm condition and 
	 * reports generated Alarms to the default Alarm Subject.
	 * By default, generates stateful, acknowledgeable alarms.
	 * @param alarmCondition the alarm condition to watch for
	 */
	public DefaultAlarmObserver(AlarmCondition alarmCondition) {
		this(alarmCondition, true, true);
	}
	
	@Override
	public Alarm generateAlarm() {
		return new AlarmImpl(getTarget(), isStateful(), isAcked());
	}
}
