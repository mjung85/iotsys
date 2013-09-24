package at.ac.tuwien.auto.iotsys.obix;

import java.util.List;

import obix.Feed;
import obix.Obj;

public interface FeedFilter {
	public FeedFilter getFilter(Obj filter);
	
	/**
	 * Queries the given feed with this filter
	 * @param feed the feed to be queried
	 * @return events of the feed that meet the filter criteria
	 */
	public List<Obj> query(Feed feed);
	
	/**
	 * Filters the given list of unpolled events
	 * @param unpolledEvents a list of yet unpolled events to be filtered
	 * @return unpolled events that meet the filter criteria
	 */
	public List<Obj> poll(List<Obj> unpolledEvents);
}
	