package at.ac.tuwien.auto.iotsys.gateway.obix.objects;

import obix.Contract;
import obix.Obj;
import obix.contracts.AckAlarm;
import obix.contracts.AckAlarmOut;

public class AckAlarmOutImpl extends Obj implements AckAlarmOut {
	private AlarmImpl alarm;
	
	public AckAlarmOutImpl(AlarmImpl alarm) {
		add(alarm);
		setIs(new Contract(AckAlarmOut.CONTRACT));
	}
	
	@Override
	public AckAlarm alarm() {
		return alarm;
	}

}
