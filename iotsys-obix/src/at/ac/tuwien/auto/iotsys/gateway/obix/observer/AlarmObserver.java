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
	private AlarmSource source, target;
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
	
	/**
	 * @param source the alarm source to be checked for an alarm condition
	 * @return <code>true</code> if <code>source</code> is currently in an alarm condition, <code>false</code> otherwise
	 */
	public abstract boolean inAlarmCondition(AlarmSource source);
	public abstract Alarm generateAlarm();
	
	private void notifyAlarmSubject(Alarm alarm) {
		if (alarmSubject != null)
			alarmSubject.addAlarm(alarm);
	}
	
	@Override
	public synchronized void update(Object state) {
		if (flipped ^ inAlarmCondition(source)) {
			this.setOffNormal();
		} else if(currentAlarm != null) {
			this.setNormal();
		}
	}
	
	/**
	 * Called when the observed alarm source goes into alarm condition.
	 * Generates an alarm and notifies the alarm subject.
	 */
	public void setOffNormal() {
		if (currentAlarm == null) {
			currentAlarm = generateAlarm();
			notifyAlarmSubject(currentAlarm);
		}
		
		getTarget().setOffNormal(currentAlarm);
	}
	
	/**
	 * Called when the observed alarm source goes out of alarm condition.
	 * Sets the normal timestamp on stateful alarms.
	 */
	public void setNormal() {
		getTarget().setToNormal(currentAlarm);
		
		if (currentAlarm instanceof StatefulAlarm) {
			setNormalTimestamp((StatefulAlarm) currentAlarm);
		}
		
		currentAlarm = null;
	}
	
	private void setNormalTimestamp(StatefulAlarm alarm) {
		Abstime normalTimestamp = ((StatefulAlarm) currentAlarm).normalTimestamp();
		if (normalTimestamp != null) {
			normalTimestamp.set(System.currentTimeMillis(), TimeZone.getDefault());
			normalTimestamp.setNull(false);
		}
	}
	
	public AlarmSource getTarget() {
		if (target == null)
			return source;
		
		return target;
	}
	
	public AlarmObserver setTarget(AlarmSource target) {
		this.target = target;
		return this;
	}

	public void setSubject(Subject object) {
		if (object instanceof AlarmSource) {
			if (source != null && source != object) source.detach(this);
			
			source = (AlarmSource) object;
		}
	}

	public Subject getSubject() {
		return (Subject)source;
	}

	public IAlarmSubject getAlarmSubject() {
		return alarmSubject;
	}

	public AlarmObserver setAlarmSubject(IAlarmSubject alarmSubject) {
		this.alarmSubject = alarmSubject;
		return this;
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
