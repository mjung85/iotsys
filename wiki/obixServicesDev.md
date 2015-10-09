# Introduction #

This page details the implementation of oBIX services.
For information on how to use the services from a user's perspective, please refer to [oBIX services user guide](obixServices.md).

# History #

Histories are implemented as Observers.
`HistoryImpl` is an Obj implementing the `Observer` interface and attaches itself to the observed object upon creation.

The History object is added as a hidden child of the observed object.
It can be reached by appending "/history" to the end of the observed object's URI.
A reference to the history is added to the observed object's parent so that it can be discovered.
The `HistoryHelper` adds Histories recursively to an object and it's children if they are value type objects.

The History's update method is called when the observed object has changed.
When this happens, a new `HistoryRecord` is created that contains information about the change (time and value).
This `HistoryRecord` is then added to its feed.

# Watches #

The `WatchServiceImpl` offers an operation to create new `WatchImpl` instances.

Watches are used to observe multiple objects and subscribe to feeds.
A watch keeps a collection `EventObserver`s.
They are stored in a Hashtable, using the URI of the observed object as key.

`EventObserver`s keep a list of changes to the object they observe.
This list can be polled, which resets the list.

When adding a URI to a watch, a new `EventObserver` is created and added to the Hashtable.
If the URI points to a feed, a `FeedObserver` is created and attached, otherwise an `ObjObserver`.
A filter can be specified when subscribing to feeds. This Filter is added to the `FeedObserver`.

When the `pollChanges` operation of the Watch is invoked, the Hashtable of observers is traversed.
If the observer reports changes, the object is included in the `WatchOut` result.
Objects in the output need to have their href adjusted to their full path.
Also, names have to be stripped, as multiple changed objects could have the same name, but names have to be unique among the children of an object (the list in `WatchOut`).
To not mess with the actual objects, they are copied and modified.

The `pollRefresh` operation iterates through the list of observed objects.
Their current state is added to the result and the list of changes their observers have collected is cleared.
For feeds, all events, filtered by the specified `FeedFilter`, even those that have already been polled before, are part of the result.

Watches expire if they aren't used in a certain amount of time (the lease).
A timer is scheduled to delete the watch.
If there is any action on the watch (read, written, invoked), the timer is canceled and starts anew.
The lease time is determined by the "lease" object that is a child of the watch object.
This lease object can be written to, so when resetting the timer, the delay is equal to the value of the lease object.


# Feeds #

`Feed`s are implemented as normal `Obj`s, only extended by their feed specific facets (in & of) and a list in which the events of the feed are stored.
The events are kept in a `LinkedList`, new events are added with the `addEvent(Obj)` method that adds the event to the front of the list.
If the number of events in the list exceeds the set number of maximum events, the last event is removed.
The list of all events is returned by the `getEvents()` method.


`FeedFilter`s can be used to extract events from a Feed.
`FeedObserver` use those filters to only return a list of unpolled events that meet the filter criteria.
Every feed has a default filter assigned. By default, this filter doesn't filter out anything, returns everything.

A `FeedFilter` is also used in the query operations of Histories and Alarm Subjects.
They use the Feed as a backing store for events, and query the feed with the filter.

A `FeedFilter` has separate methods for querying a Feed, and polling a Feed.
Usually, you want to return events newest-first when polling a Feed.
When querying, you might want to return events in the order they were added.


# Alarms #

In oBIX, an Alarm can be a `StatefulAlarm` and an `AckAlarm`, in addition to being an `Alarm`.
The class `AlarmImpl` implements all three interfaces.
The constructor takes boolean arguments on whether the alarm should be stateful and/or acknowledgeable.

`Obj`s keep track of alarms they are the source of in order to provide correct status information (unackedAlarm, unacked, alarm).
When an Alarm is generated, it calls the source  `Obj`'s `setOffNormal(Alarm)` method.
When an Alarm is acknowledged, it calls the source `Obj`'s `alarmAcknowledged(Alarm)` method.
When an Alarm becomes inactive (the alarm condition that caused it subsided), it calls the source `Obj`'s `setToNormal(Alarm)` method.

Alarms are collected in `AlarmSubject`s, where a feed of the alarms is provided and can be queried.
A alarm can be added to a `AlarmSubject` by calling its `addAlarm(Alarm)` method.
Added alarms are then registered in the global alarm database at "/alarmdb".
The `AlarmFilter` used to query and filter the alarms has been extended beyond the oBIX specification to have more powerful filter capabilities, like filtering by the alarm source.
More information is found on the AlarmFilter is found [in the HTTP interaction guide](HTTPinteraction#Managing_alarms.md).


To automatically generate alarms when an object changes into an alarming state, an `AlarmObserver` is used.
`AlarmObserver`s are used in conjunction with `AlarmCondition`s.
`AlarmCondition`s encapsulate the checking of the alarm source for an alarming condition.
If the condition is met, an alarm is generated and added to an `AlarmSubject`.
When the observed object is in alarm condition, and still is in alarm condition afte it updates, no new alarm is generated.
When the alarm condition is left, the observed object is set back to normal, and the normalTimestamp of `StatefulAlarm`s is set.

The `DefaultAlarmObserver` is an implementation of an `AlarmObserver` that generates `AlarmImpl`s and adds them to the default `AlarmSubject` at "/alarms".