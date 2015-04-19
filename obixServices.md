# Introduction #

This page details how you can use oBIX services in your custom devices.
To learn how to interact with the services via HTTP, please refer to the [HTTP Interaction guide](HTTPinteraction#Using_oBIX_services.md).
If you are interested in the implementation of these services, please refer to [oBIX services developer guide](obixServicesDev.md).


# History #

To add a history to an Object, you can call ObjectBroker.addHistoryToDatapoints(Obj, int)
and pass in the Obj you want to add a history to, and an Integer specifying the maximum
number of history records to keep for the specified object:
```
// Add a history to the device "virtualTemp1". Keep at most 100 history records at a time.
objectBroker.addHistoryToDatapoints(virtualTemp1, 100);
```


The built-in connectors allow you to add histories to devices through the devices.xml config file.
You can read more on [how to create a technology connector](connectorhowto.md) to learn how to use this yourself.


# Alarms #

To set up automatic alarming for your devices, you must first define an alarm condition.
Implement an `AlarmCondition` that checks an `AlarmSource` for an alarming condition:
```
AlarmCondition condition = new AlarmCondition() {
	protected boolean checkAlarmCondition(AlarmSource source) {
		// return true if AlarmSource is in alarm state
		return ((Obj) source).getInt() < 2000;
	}
};
```

Generic, reusable `AlarmConditon`s can be defined.
For example, you can use an IntAlarmCondition to check if an int value is inside an acceptable range.
AlarmSources with values outside that range will be considered to be in alarm state.

```
AlarmCondition condition = new IntRangeAlarmCondition(10l, 20l);
```

The condition of `AlarmCondition`s can be flipped, for example if you want to specify a range of values that is not allowed:
```
condition.setFlipped(true);
```

After you have defined your alarm condition, you can attach an alarm observer to an Obj.
This will monitor the Obj and check the alarm condition every time it updates.
```
AlarmObserver observer = new DefaultAlarmObserver(condition);
source.attach(observer);
```

You can set the type of alarm that is being generated if the alarm condition occurs:
```
observer.setAcked(true).setStateful(false);
```

If you want to generate an alarm for a device object when one of its children meets a certain criteria,
you can set the target of the alarm observer.
Generated alarms show the set target as the alarm source. By default the target is the observed object.

```
AlarmObserver observer = new DefaultAlarmObserver(new IntRangeAlarmCondition(0l, 2000l));
fanSpeed.attach(observer);

// when fanSpeed is in alarm condition, the generated alarm lists this as source, not the fanSpeed object
observer.setTarget(this);
```


Usually generated alarms are collected by the default `AlarmSubject` at `/alarms`.
However, you can create your own `AlarmSubject`:
```
AlarmSubject subject = new AlarmSubjectImpl(objectBroker);
subject.setHref(new Uri("myAlarms"));

// Set the alarm subject the alarm observer reports generated alarms to
observer.setAlarmSubject(subject);
```


# Feeds #

Feed objects are used to define a topic for feeds of events.
Feeds are [used with watches](HTTPinteraction#Watching_Feeds.md) to subscribe to a stream of events such as alarms.

Creating your own Feed is simple.
You should also set the type of events that your Feed will contain using the `of` attribute:
```
Feed feed = new Feed();
feed.setOf(new Contract("MyContract"));

// Optionally set a maximum number of events to keep
feed.setMaxEvents(100);
```

To add events to the Feed, use the `addEvent` method.
Events are oBIX objects:
```
MyContract event = new MyContractImpl();
feed.addEvent(event);
```


Optionally, you can define a Filter to act on the events of a feed.
Filters can be passed in as oBIX objects when subscribing to feeds or when querying feeds.

To create a new Filter, create a new `Obj` class that implements the `FeedFilter` interface.
Don't forget the contract definition to the `ContractRegistry`.
You can add your contract definition to `at.ac.tuwien.auto.iotsys.gateway.obix.object.ContractInit`.

```
class MyFilter extends Obj implements FeedFilter {
	public static final String CONTRACT = "MyFilter";
	private Abstime start = new Abstime("start");
	private Abstime end   = new Abstime("end");
	
	public MyFilter() {
		setIs(new Contract(CONTRACT));
		add(start);
		add(end);
	}

	@Override
	public FeedFilter getFilter(Obj filter) {
		// construct a new filter out of the passed in Obj
		MyFilter myFilter = new MyFilter();
		myFilter.start = (Abstime) filter.get("start");
		myFilter.start = (Abstime) filter.get("end"); 
		return myFilter;
	}

	@Override
	public List<Obj> query(Feed feed) {
		// Logic to query a feed using this filter
		
		List<Obj> events = feed.getEvents();
		// filter out events not meeting the filter criteria

		return events;
	}

	@Override
	public List<Obj> poll(List<Obj> unpolledEvents) {
		// Called by the WatchService.
		// filter out events not meeting the filter criteria
		// events should usually be returned newest-first when polling
		return unpolledEvents;
	}
}
```


If no filter is specified when polling or querying a feed, it's default filter is used.
This filter is also used to construct new Filters out of passed in oBIX objects.
Therefore you should set your feed's default filter to your own custom filter:

```
MyFilter filter = new MyFilter();
feed.setDefaultFilter(filter);
feed.setIn(MyFilter.CONTRACT);
```