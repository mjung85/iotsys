package obix;

import java.util.LinkedList;

import obix.contracts.Alarm;
import at.ac.tuwien.auto.iotsys.gateway.obix.observer.Subject;

public interface AlarmSource extends Subject {
	public void setOffNormal(Alarm alarm);
	public void setToNormal(Alarm alarm);
	
	public void alarmAcknowledged(Alarm alarm);
	
	public boolean inAlarmState();
	public LinkedList<Alarm> getAlarms();
}
