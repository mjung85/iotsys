package at.ac.tuwien.auto.iotsys.commons.alarms;

import obix.AlarmCondition;
import obix.AlarmSource;
import obix.Int;

/**
 * An IntRangeAlarmCondition checks if the value of an Int
 * is inside an allowed range.
 */
public class IntRangeAlarmCondition extends AlarmCondition {
	private Long min, max;
	
	/**
	 * Defines a range of allowed values.
	 * @param min The minimum allowed value. If <code>null</code>, there is no lower bound .
	 * @param max The maximum allowed value. If <code>null</code>, there is no upper bound.
	 */
	public IntRangeAlarmCondition(Long min, Long max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	protected boolean checkAlarmCondition(AlarmSource source) {
		if (!(source instanceof Int)) return false;
		long value = ((Int) source).get();
		
		if (min == null && max == null)
			return false; // always in unbounded range
		
		if (min == null) return value > max;
		if (max == null) return value < min;
		
		return (value < min) || (value > max);
	}

}
