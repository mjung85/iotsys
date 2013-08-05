package at.ac.tuwien.auto.iotsys.gateway.obix.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import obix.Abstime;
import obix.Contract;
import obix.Feed;
import obix.Obj;
import obix.Uri;
import obix.contracts.Alarm;
import obix.contracts.AlarmFilter;

public class AlarmFeed extends Feed {
	
	public AlarmFeed() {
		super("feed", new Contract(AlarmFilter.ALARM_FILTER_CONTRACT), new Contract(Alarm.ALARM_CONTRACT));
		setHref(new Uri("feed"));
	}
	
	@Override
	public List<Obj> query(List<Obj> events, Obj filter) {
		if (!(filter instanceof AlarmFilter))
			return new ArrayList<Obj>(events);
		
		return new ArrayList<Obj>(getRecords(filter, events));
	}
	
	public List<AlarmImpl> getRecords(Obj filter, List<Obj> events) {
		if (!(filter instanceof AlarmFilter))
			return null;
		
		AlarmFilter alarmFilter = (AlarmFilter) filter;
		LinkedList<AlarmImpl> results = new LinkedList<AlarmImpl>();
		
		// parameters
		long limit = (alarmFilter.limit().isNull()) ? 0 : alarmFilter.limit().getInt();
		Abstime start = alarmFilter.start();
		Abstime end = alarmFilter.end();
		
		for (Obj event : events) {
			if (!(event instanceof AlarmImpl)) continue;
			
			AlarmImpl alarm = (AlarmImpl) event;
			Abstime timestamp = alarm.timestamp();
			
			if (!start.isNull() && timestamp.get() < start.get()) continue;
			if (!end.isNull()   && timestamp.get() > end.get()) continue;
			
			results.add(alarm);
			
			if (limit != 0 && results.size() >= limit) break;
		}
		
		return results;
	}
}
