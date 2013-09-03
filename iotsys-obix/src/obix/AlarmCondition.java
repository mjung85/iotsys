package obix;

public abstract class AlarmCondition {
	private boolean isFlipped = false;
	
	/**
	 * @param source the alarm source to check for an alarm condition
	 * @return <code>true</code> if the given alarm source is in alarm condition, <code>false</code> otherwise
	 */
	public boolean inAlarmCondition(AlarmSource source) {
		return isFlipped ^ checkAlarmCondition(source);
	}
	
	/**
	 * @param source the alarm source to check for an alarm condition
	 * @return <code>true</code> if given alarm source is in alarm condition (without flipping the result), <code>false</code> otherwise
	 */
	protected abstract boolean checkAlarmCondition(AlarmSource source);
	
	public AlarmCondition setFlipped(boolean flipped) {
		this.isFlipped = flipped;
		return this;
	}
}
