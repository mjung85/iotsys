package at.ac.tuwien.auto.iotsys.gateway.obix.observer;

import obix.AlarmSource;
import obix.Int;

/**
 * The IntAlarmObserver observes Int objects.
 * If the value of the Int object is outside a specified range, an alarm is generated.
 */
public class IntAlarmObserver extends DefaultAlarmObserver {
	private Long min, max;
	
	/**
	 * Defines a range of normal values
	 * @param min The minimum allowed value 
	 * @param max The maximum allowed value
	 */
	public IntAlarmObserver(Long min, Long max) {
		this.min = min;
		this.max = max;
	}

	public boolean inAlarmCondition(AlarmSource source) {
		long val = ((Int)source).get();
		
		if (min == null && max == null)
			return false; // always in unbounded range
		
		if (min == null) return val > max;
		if (max == null) return val < min;
		
		return (val < min) || (val > max);
	}
}
