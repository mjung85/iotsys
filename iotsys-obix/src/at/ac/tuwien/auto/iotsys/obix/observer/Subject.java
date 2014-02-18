package at.ac.tuwien.auto.iotsys.obix.observer;

/**
 * Each oBIX object should be a subject that a observer can be attached to.
 *
 */
public interface Subject {
	public void attach(Observer observer);
	public void detach(Observer observer);
	public void notifyObservers();
	public Object getCurrentState();
}
