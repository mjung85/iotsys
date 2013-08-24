package at.ac.tuwien.auto.iotsys.gateway.obix.observer;

import java.util.TimeZone;

import obix.Abstime;
import obix.AlarmSource;
import obix.IAlarmSubject;
import obix.contracts.Alarm;
import obix.contracts.StatefulAlarm;

/**
 * Observes an AlarmSource for alarming conditions and generates Alarms
 *
 */
public abstract class AlarmObserver implements Observer {
	private AlarmSource source;
	private IAlarmSubject alarmSubject;
	private Alarm currentAlarm;
	private boolean stateful, acked;
	private boolean flipped;
	
	public AlarmObserver(IAlarmSubject alarmSubject, boolean stateful, boolean acked) {
		this.alarmSubject = alarmSubject;
		this.stateful = stateful;
		this.acked = acked;
		this.flipped = false;
	}
	
	public abstract boolean inAlarmCondition(AlarmSource source);
	public abstract Alarm generateAlarm(AlarmSource source);
	
	private void notifyAlarmSubject(Alarm alarm) {
		if (alarmSubject != null)
			alarmSubject.addAlarm(alarm);
	}
	
	@Override
	public void update(Object state) {
		if (flipped ^ inAlarmCondition(source)) {
			if (currentAlarm == null) {
				currentAlarm = generateAlarm(source);
				notifyAlarmSubject(currentAlarm);
			}
			source.setOffNormal(currentAlarm);
		} else {
			source.setToNormal(currentAlarm);
			if (currentAlarm instanceof StatefulAlarm) {
				// set normal timestamp
				Abstime normalTimestamp = ((StatefulAlarm) currentAlarm).normalTimestamp();
				normalTimestamp.set(System.currentTimeMillis(), TimeZone.getDefault());
				normalTimestamp.setNull(false);
			}
			
			currentAlarm = null;
		}
	}

	@Override
	public void setSubject(Subject object) {
		if (object instanceof AlarmSource) {
			if (source != null && source != object) source.detach(this);
			
			source = (AlarmSource) object;
		}
	}

	@Override
	public Subject getSubject() {
		return (Subject)source;
	}

	public boolean isStateful() {
		return stateful;
	}

	public AlarmObserver setStateful(boolean stateful) {
		this.stateful = stateful;
		return this;
	}

	public boolean isAcked() {
		return acked;
	}

	public AlarmObserver setAcked(boolean acked) {
		this.acked = acked;
		return this;
	}
	
	public AlarmObserver setFlipped(boolean flipped) {
		this.flipped = flipped;
		return this;
	}

}
