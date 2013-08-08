package at.ac.tuwien.auto.iotsys.gateway.obix.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import obix.Feed;
import obix.Obj;
import at.ac.tuwien.auto.iotsys.commons.DefaultFeedFilter;
import at.ac.tuwien.auto.iotsys.commons.FeedFilter;

/**
 * Observes the state changes of an obix object. Holds a history 
 * of the changes until the events are polled or the max number of 
 * elements is exceeded.
 * 
 * This class also acts as a singleton subject, in order to allow CoAP updates on observed resources.
 *
 */
public class FeedObserver implements EventObserver<Obj> {
	private Feed subject;
	
	public static final int MAX_EVENTS = 50;
	private static final Object lock = new Object();
	private LinkedList<Obj> unpolledEvents = new LinkedList<Obj>();
	
	private FeedFilter filter;
	
	public FeedObserver(FeedFilter filter) {
		this.filter = filter;
		
		if (filter == null) {
			this.filter = new DefaultFeedFilter();
		}
	}
		
	@Override
	public void update(Object currentState) {
		synchronized(lock) {
			unpolledEvents.add(subject.getLatestEvent());
		}
	}
	
	/**
	 * @return the latest unpolled changes 
	 */
	public List<Obj> pollChanges() {
		List<Obj> ret = null;
		synchronized(lock) {
 			ret = new ArrayList<Obj>(filter.poll(unpolledEvents));
			clear();
		}
		return ret;
	}
	
	/**
	 * @return all events of the watched feed filtered by this observers filter 
	 */
	public List<Obj> pollRefresh() {
		synchronized(lock) {
			clear();
			unpolledEvents.addAll(subject.getEvents());
			Collections.reverse(unpolledEvents);
			return pollChanges();
		}
	}
	
	public void clear() {
		synchronized (lock) {
			unpolledEvents.clear();
		}
	}

	public Subject getSubject() {
		return subject;
	}
	
	public void setSubject(Subject subject) {
		if (!(subject instanceof Feed)) return;
		this.subject = (Feed) subject;
	}
	
	public FeedFilter getFilter() {
		return filter;
	}
}
