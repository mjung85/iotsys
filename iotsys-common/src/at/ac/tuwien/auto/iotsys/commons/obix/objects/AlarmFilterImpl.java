package at.ac.tuwien.auto.iotsys.commons.obix.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import obix.Abstime;
import obix.Bool;
import obix.Feed;
import obix.Int;
import obix.Obj;
import obix.Uri;
import obix.contracts.AlarmFilter;
import at.ac.tuwien.auto.iotsys.obix.FeedFilter;

public class AlarmFilterImpl extends Obj implements AlarmFilter, FeedFilter {
	
	private Int limit = new Int();
	private Abstime start = new Abstime();
	private Abstime end = new Abstime();
	private Uri source = new Uri();
	private Bool unacked = new Bool();
	private Bool active = new Bool();
	

	public AlarmFilterImpl() {
		add(limit);
		add(start);
		add(end);
		add(source);
		add(unacked);
		add(active);
	}
	
	public AlarmFilterImpl(Obj filter) {
		this();
		
		if (filter instanceof AlarmFilter) {
			AlarmFilter alarmFilter = (AlarmFilter) filter;
			limit.set(alarmFilter.limit());
			start.set(alarmFilter.start());
			end.set(alarmFilter.end());
			source.set(alarmFilter.source());
			unacked.set(alarmFilter.unacked());
			active.set(alarmFilter.active());
			
			limit.setNull(alarmFilter.limit().isNull());
			start.setNull(alarmFilter.start().isNull());
			end.setNull(alarmFilter.end().isNull());
			source.setNull(alarmFilter.source().isNull());
			unacked.setNull(alarmFilter.unacked().isNull());
			active.setNull(alarmFilter.active().isNull());
		} else {
			limit.setNull(true);
			start.setNull(true);
			end.setNull(true);
			source.setNull(true);
			unacked.setNull(true);
			active.setNull(true);
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

	
	public Uri source() {
		return source;
	}

	public Bool unacked() {
		return unacked;
	}
	

	public Bool active() {
		return active;
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
			
			if (!unacked.isNull()) {
				if (!record.isAcked()) continue;
				
				boolean unackedFilter = unacked.get();
				boolean recordIsUnacked = record.ackUser().isNull();
				
				if (unackedFilter != recordIsUnacked) {
					continue;
				}
			}
			
			if (!source.isNull()) {
				// get source of this record, compare it to given source uri. ;D
				Obj alarmSource = record.getRoot().getByHref(record.source().getHref());
				if (alarmSource == null) continue;
				
				Obj sourceFilter = record.getRoot().getByHref(source);
				if (alarmSource != sourceFilter)
					continue;
			}
			
			if (!active.isNull()) {
				if (!record.isStateful()) continue;
				Obj alarmSource = record.getRoot().getByHref(record.source().getHref());
				if (alarmSource == null) continue;
					
				if (active.get() != alarmSource.getAlarms().contains(record))
					continue;
			}
			
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
