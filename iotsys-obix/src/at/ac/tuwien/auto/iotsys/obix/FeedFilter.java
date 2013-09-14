package at.ac.tuwien.auto.iotsys.obix;

import java.util.List;

import obix.Feed;
import obix.Obj;

public interface FeedFilter {
	public FeedFilter getFilter(Obj filter);
	
	public List<Obj> query(Feed feed);
	public List<Obj> poll(List<Obj> events);
}
	