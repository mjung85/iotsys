package at.ac.tuwien.auto.iotsys.gateway.obix.observer;

import obix.contracts.Alarm;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.AlarmImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.AlarmSubjectImpl;

/**
 * The Default Alarm Observer generates Alarms that are
 * reported to the default Alarm Subject
 */
public abstract class DefaultAlarmObserver extends AlarmObserver {
	
	public DefaultAlarmObserver(boolean stateful, boolean acked) {
		super(AlarmSubjectImpl.defaultAlarmSubject(), stateful, acked);
	}
	
	public DefaultAlarmObserver() {
		this(true, true);
	}

	public Alarm generateAlarm() {
		return new AlarmImpl(getTarget(), isStateful(), isAcked());
	}

}
