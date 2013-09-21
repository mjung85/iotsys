package at.ac.tuwien.auto.iotsys.commons.obix.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import obix.Abstime;
import obix.Feed;
import obix.Int;
import obix.Obj;
import obix.contracts.AlarmFilter;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.AlarmFilterImpl;
import at.ac.tuwien.auto.iotsys.obix.FeedFilter;

public class AlarmFilterImpl extends Obj implements AlarmFilter, FeedFilter {
	
	private Int limit = new Int();
	private Abstime start = new Abstime();
	private Abstime end = new Abstime();
	

	public AlarmFilterImpl() {
		add(limit);
		add(start);
		add(end);
	}
	
	public AlarmFilterImpl(Obj filter) {
		this();
		
		if (filter instanceof AlarmFilter) {
			AlarmFilter alarmFilter = (AlarmFilter) filter;
			limit.set(alarmFilter.limit());
			start.set(alarmFilter.start());
			end.set(alarmFilter.end());
			
			limit.setNull(alarmFilter.limit().isNull());
			start.setNull(alarmFilter.start().isNull());
			end.setNull(alarmFilter.end().isNull());
		}
	}
	
	public Int limit() {
		return limit;
	}

	public Abstime start() {
		return start;
	}

	public Abstime end() {
		return end;
	}

	
	@Override
	public List<Obj> query(Feed feed) {
		ArrayList<AlarmImpl> alarms = filterRecords(feed.getEvents());
		
		while (limit.get() > 0 & alarms.size() > limit.get())
			alarms.remove(alarms.size()-1);
		
		return new ArrayList<Obj>(alarms);
	}

	@Override
	public List<Obj> poll(List<Obj> events) {
		ArrayList<AlarmImpl> alarms = filterRecords(events);
		Collections.reverse(alarms);
		
		while (limit.get() > 0 & alarms.size() > limit.get())
			alarms.remove(alarms.size()-1);
		
		return new ArrayList<Obj>(alarms);
	}
	
	
	private ArrayList<AlarmImpl> filterRecords(List<Obj> events) {
		ArrayList<AlarmImpl> filteredRecords = new ArrayList<AlarmImpl>();

		// sort alarms
		Collections.sort(events, new Comparator<Obj>() {
			public int compare(Obj obj1, Obj obj2) {
				AlarmImpl r1 = (AlarmImpl) obj1;
				AlarmImpl r2 = (AlarmImpl) obj2;
				return r1.timestamp().compareTo(r2.timestamp());
			}
		});
		
		for (Obj event : events) {
			if (!(event instanceof AlarmImpl))
				continue;
			
			AlarmImpl record = (AlarmImpl) event;
			
			if (start.get() != end.get()) {
				if (!start.isNull() && record.timestamp().get() < start.get()) {
					continue;
				}

				if (!end.isNull() && record.timestamp().get() > end.get()) {
					continue;
				}
			}

			filteredRecords.add(record);
		}
		
		return filteredRecords;
	}

	@Override
	public FeedFilter getFilter(Obj filter) {
		return new AlarmFilterImpl(filter);
	}


}
