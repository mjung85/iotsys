package at.ac.tuwien.auto.iotsys.obix;

import java.util.Collections;
import java.util.List;

import obix.Feed;
import obix.Obj;

public class DefaultFeedFilter implements FeedFilter {
	public List<Obj> query(Feed feed) {
		List<Obj> events = feed.getEvents();
		Collections.reverse(events);
		return events;
	}
	
	public List<Obj> poll(List<Obj> events) {
		return events;
	}

	@Override
	public FeedFilter getFilter(Obj filter) {
		return new DefaultFeedFilter();
	}
}
