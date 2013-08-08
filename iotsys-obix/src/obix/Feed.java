/*
 * This code licensed to public domain
 */
package obix;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import at.ac.tuwien.auto.iotsys.commons.DefaultFeedFilter;
import at.ac.tuwien.auto.iotsys.commons.FeedFilter;

/**
 * Feed models a event feed topic.
 * 
 * @author Brian Frank
 * @creation 30 Mar 06
 * @version $Revision$ $Date$
 */
public class Feed extends Obj {

	// //////////////////////////////////////////////////////////////
	// Construction
	// //////////////////////////////////////////////////////////////

	/**
	 * Construct named Feed with in and of contracts.
	 */
	public Feed(String name, Contract in, Contract of) {
		super(name);
		setIn(in);
		setOf(of);
	}

	/**
	 * Construct named Feed.
	 */
	public Feed(String name) {
		this(name, null, null);
	}

	/**
	 * Construct unnamed Feed.
	 */
	public Feed() {
		this(null, null, null);
	}

	// //////////////////////////////////////////////////////////////
	// Feed
	// //////////////////////////////////////////////////////////////

	/**
	 * Get input contract.
	 */
	public Contract getIn() {
		return in;
	}

	/**
	 * Set the input contract.
	 */
	public void setIn(Contract in) {
		this.in = (in != null) ? in : Contract.Obj;
	}

	/**
	 * Get of contract.
	 */
	public Contract getOf() {
		return of;
	}

	/**
	 * Set the of contract.
	 */
	public void setOf(Contract of) {
		this.of = (of != null) ? of : Contract.Obj;
	}

	// //////////////////////////////////////////////////////////////
	// Obj
	// //////////////////////////////////////////////////////////////

	/**
	 * Return "feed".
	 */
	public String getElement() {
		return "feed";
	}

	/**
	 * Return BinObix.FEED.
	 */
	public int getBinCode() {
		return obix.io.BinObix.FEED;
	}

	// //////////////////////////////////////////////////////////////
	// Events
	// //////////////////////////////////////////////////////////////

	/**
	 * Return all events in the feed
	 */
	public List<Obj> getEvents() {
		synchronized (events) {
			return new ArrayList<Obj>(events);
		}
	}

	/**
	 * Add an event to the feed
	 * 
	 * @param event
	 *            Event to be added
	 */
	public void addEvent(Obj event) {
		synchronized (events) {
			events.addFirst(event);
			while (maxEvents > 0 && events.size() > maxEvents)
				removeEvent(events.getLast());

			latestEvent = event;
			notifyObservers();
		}
	}

	/**
	 * Set the maximum number of events to store in the feed
	 * 
	 * @param maxEvents
	 *            Maximum number of events. If 0, an unlimited number of events
	 *            are stored.
	 */
	public void setMaxEvents(int maxEvents) {
		this.maxEvents = maxEvents;
	}

	/**
	 * Remove event from feed
	 * 
	 * @param event
	 *            Event to remove from the feed
	 */
	public void removeEvent(Obj event) {
		this.events.remove(event);
	}

	public Obj getLatestEvent() {
		return latestEvent;
	}
	
	public void setDefaultFilter(FeedFilter filter) {
		this.defaultFilter = filter;
	}
	
	public FeedFilter getDefaultFilter() {
		return defaultFilter;
	}

	// //////////////////////////////////////////////////////////////
	// Fields
	// //////////////////////////////////////////////////////////////

	private Contract in;
	private Contract of;
	private LinkedList<Obj> events = new LinkedList<Obj>();
	private FeedFilter defaultFilter = new DefaultFeedFilter();
	private int maxEvents = 0;
	private Obj latestEvent;

}
