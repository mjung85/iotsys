package at.ac.tuwien.auto.iotsys.commons.obix.objects;

import java.util.List;

import obix.Contract;
import obix.Feed;
import obix.IAlarmSubject;
import obix.Int;
import obix.Obj;
import obix.Op;
import obix.Uri;
import obix.contracts.Alarm;
import obix.contracts.AlarmFilter;
import obix.contracts.AlarmQueryOut;
import obix.contracts.AlarmSubject;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.AlarmFilterImpl;
import at.ac.tuwien.auto.iotsys.obix.OperationHandler;

public class AlarmSubjectImpl extends Obj implements IAlarmSubject {
	private static IAlarmSubject defaultSubject;
	private static int alarmID = 0;
	
	private ObjectBroker broker;
	private Int count;
	private Op query;
	private Feed feed;
	private static obix.List alarmdb;
	
	/**
	 * @return the default instance of an AlarmSubject
	 */
	public static IAlarmSubject defaultAlarmSubject() {
		return defaultSubject;
	}
	
	public static void setDefaultAlarmSubject(IAlarmSubject subject) {
		defaultSubject = subject;
	}
	
	
	public AlarmSubjectImpl(final ObjectBroker broker) {
		this.broker = broker;
		
		this.setHref(new Uri("alarms"));
		this.setIs(new Contract(AlarmSubject.CONTRACT));
		
		if (alarmdb == null) {
			alarmdb = new obix.List("alarmdb", new Contract(Alarm.CONTRACT));
			alarmdb.setHref(new Uri("alarmdb"));
			
			broker.addObj(alarmdb, false);
		}
		
		add(count());
		add(query());
		add(feed());
		
		if (defaultSubject == null)
			defaultSubject = this;
	}
	
	public Int count() {
		if (count == null) {
			count = new Int("count");
			count.set(0);
			count.setHref(new Uri("count"));
		}
		return count;
	}

	public Op query() {
		if (query == null) {
			query = new Op("query", new Contract(AlarmFilter.CONTRACT), new Contract(AlarmQueryOut.CONTRACT));
			query.setHref(new Uri("query"));
			query.setOperationHandler(new OperationHandler() {
				public Obj invoke(Obj in) {
					return query(in);
				}
			});
		}
		return query;
	}

	public Feed feed() {
		if (feed == null) {
			feed = new Feed("feed", new Contract(AlarmFilter.CONTRACT), new Contract(Alarm.CONTRACT));
			feed.setHref(new Uri("feed"));
		}
		return feed;
	}

	public synchronized void addAlarm(final Alarm alarm) {
		AlarmImpl alarmObj = (AlarmImpl) alarm;
		
		alarmObj.setHref(new Uri(String.valueOf(alarmID++)));
		alarmdb.add(alarmObj);
		alarmObj.getNormalizedHref();
		alarmObj.setHref(new Uri(alarmObj.getFullContextPath()));
		
		feed.addEvent((Obj) alarm);
		count.set(count.get()+1);
		
		broker.addObj((Obj)alarm, false);
	}
	
	public Obj query(Obj in) {
		AlarmFilterImpl filter = new AlarmFilterImpl(in);
		List<Obj> results = filter.query(feed);
		return new AlarmQueryOutImpl(results);
	}
}
