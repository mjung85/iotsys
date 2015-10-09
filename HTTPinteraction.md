

# Introduction #

For the interaction with the gateway you can test the HTTP interface with a simple HTTP client (e.g. a browser like IE, Firefox, ...). In the case you want to make write requests and queries to histories you need an application or a plugin for your browser.

Below are some example interactions and screenshots are based on Firefox browser and for all non-GET requests the HTTPRequester add-on is used.

# Query oBIX Lobby #
<img src='https://iotsys.googlecode.com/hg/misc/img/http/http_obix_lobby.png' />

The oBIX lobby provides an entry point to all supported oBIX objects provided by the server.

**Request:**
```
HTTP GET http://localhost:8080/obix
```
**Response:**
```
<obj href="obix/">
  <ref name="about" href="obix/about"/>
  <ref href="watchService" is="obix:WatchService"/>
  <ref href="alarms" is="obix:AlarmSubject"/>
  <ref href="VirtualDevices/virtualIndoorBrightnessSensor" is="iot:IndoorBrightnessSensor"/>
  <ref href="VirtualDevices/virtualOutsideTemperatureSensor" is="iot:OutsideTemperatureSensor"/>
  <ref href="VirtualDevices/virtualPresence" is="iot:PresenceDetectorSensor"/>
  <ref href="VirtualDevices/virtualPushButton" is="iot:PushButton"/>
  <ref href="VirtualDevices/virtualRoomRelativeHumiditySensor" is="iot:RoomRelativeHumiditySensor"/>
  <ref href="VirtualDevices/virtualRoomTemperatureSensor" is="iot:RoomTemperatureSensor"/>
  <ref href="VirtualDevices/virtualTemperatureSensor" is="iot:TemperatureSensor"/>
  <ref href="VirtualDevices/virtualSunIntensitySensor" is="iot:SunIntensitySensor"/>
  <ref href="VirtualDevices/virtualSwitchingSensor" is="iot:SwitchingSensor"/>
  <ref href="VirtualDevices/smartmeter" is="iot:SmartMeter"/>
  <ref href="VirtualDevices/virtualSimpleHVACvalveActuator" is="iot:SimpleHVACvalveActuator"/>
  <ref href="VirtualDevices/vComplexSunBlind" is="iot:ComplexSunblindActuator"/>
  <ref href="VirtualDevices/sunblindMiddleA" is="iot:SunblindActuator"/>
  <ref href="VirtualDevices/sunblindMiddleB" is="iot:SunblindActuator"/>
  <ref href="VirtualDevices/virtualFanSpeed" is="iot:FanSpeedActuator"/>
  <ref href="VirtualDevices/virtualBrightnessActuator" is="iot:BrightnessActuator"/>
  <ref href="VirtualDevices/virtualHVACvalveActuatorImpl" is="HVACvalveActuator"/>
  <ref href="VirtualDevices/virtualAirDamperActuatorImpl" is="iot:AirDamperActuator"/>
  <ref href="VirtualDevices/virtualLight" is="iot:LightSwitchActuator"/>
  <ref href="VirtualDevices/virtualPumpActuatorImpl" is="iot:Pump"/>
  <ref href="VirtualDevices/virtualDimmingActuatorImpl" is="iot:DimmingActuator"/>
  <ref href="VirtualDevices/virtualCoolerActuatorImpl" is="iot:Cooler"/>
  <ref href="VirtualDevices/virtualBoilerActuatorImpl" is="iot:Boiler"/>
</obj>

```

# Query sensor/actuator #
Simple make a GET on the oBIX object identified through the URL.

**Request:**
```
HTTP GET http://localhost:8080/VirtualDevices/virtualLight/
```
**Response:**
```
<obj href="/VirtualDevices/virtualLight/" is="iot:LightSwitchActuator">
  <bool name="value" href="value" val="false" writable="true"/>
</obj>
```

It is also possible to directly request a data point.

**Request:**
```
HTTP GET http://localhost:8080/VirtualDevices/virtualLight/value
```
**Response:**
```
<bool name="value" href="value/" val="false" writable="true"/>
```

# Modify actuator #
<img src='https://iotsys.googlecode.com/hg/misc/img/http/http_put.png' />

An object can be modified following the RESTful interaction patterns using a HTTP PUT call. The PUT can be either on the full object or only on the data point.

**Request:**
```
HTTP PUT http://localhost:8080/VirtualDevices/virtualLight/

Payload:
<obj href="/VirtualDevices/virtualLight/" is="iot:LightSwitchActuator">
  <bool name="value" href="value" val="true" writable="true"/>
</obj>
```

**Response:**
```
<obj href="/VirtualDevices/virtualLight/" is="iot:LightSwitchActuator">
  <bool name="value" href="value" val="true" writable="true"/>
</obj>
```

Put on a simple data point.

**Request:**
```
HTTP PUT http://localhost:8080/VirtualDevices/virtualLight/value

Payload:
<bool name="value" href="value" val="true" writable="true"/>
```

**Response:**
```
<bool name="value" href="value/" val="true" writable="true"/>
```



# Using oBIX services #

## Using an oBIX watch to monitor an object ##
Since HTTP requires the request/response cycle you can create a per-client watch that monitors your resources and only contains the changes since your last request, but still you need to poll for updates. For creating a watch you have to call the `make` operation of the `watchService`.

**Request**
```
HTTP POST http://localhost:8080/watchService/make
```

**Response:**
```
<obj href="/watch0" is="obix:Watch">
  <op name="add" href="/watch0/add" in="obix:WatchIn" out="obix:WatchOut"/>
  <op name="remove" href="/watch0/remove" in="obix:WatchIn" out="obix:Nil"/>
  <reltime name="lease" href="/watch0/lease" val="PT60S" writable="true"/>
  <op name="pollChanges" href="/watch0/pollChanges" in="obix:WatchIn" out="obix:WatchOut"/>
  <op name="pollRefresh" href="/watch0/pollRefresh" in="obix:WatchIn" out="obix:WatchOut"/>
  <op name="delete" href="/watch0/delete" in="obix:Nil" out="obix:Nil"/>
</obj>
```

Add the data point you want to observe by calling the `add` operation of the created `watch0`, for example the current state of the light switch.

**Request:**
```
HTTP POST http://localhost:8080/watch0/add

Payload:
<obj is="obix:WatchIn">
  <list name="hrefs">
   <uri val="/VirtualDevices/virtualLight/value" />
  </list>
</obj>
```

**Response:**

The response contains the latest value of the observed object.
```
<obj is="obix:WatchOut">
  <list>
  <bool name="value" href="value" val="false" writable="true"/>
  </list>
</obj>

<obj is="obix:WatchOut">
  <list>
  <bool href="/VirtualDevices/virtualLight/value" val="false" writable="true"/>
  </list>
</obj>
```

Afterwards the latest changes can be polled using the `pollChanges` operation of `watch0`.

**Request:**
```
HTTP POST http://localhost:8080/watch0/pollChanges
```

Depending on the changes that happened since the last request for the monitored object the `WatchOut` response contains the latest value or is empty.

The `pollRefresh` operation returns every object added to the watch with their current values, regardless if they have changed since the last poll or not.

### Watching Feeds ###
Clients can subscribe to events by adding a feed’s href to a watch, optionally passing an input parameter which is typed via the feed’s in attribute.
The object returned from Watch.add is a list of historic events (or the empty list if no event history is available). Subsequent calls to pollChanges returns the list of events which have occurred since the last poll.
The `pollRefresh` operation returns all events as if the `pollRefresh` was an `add` operation.

**Request:**
```
HTTP POST http://localhost:8080/watch1/add

Payload:
<obj is="obix:WatchIn">
 <list name="hrefs">
  <uri val="/VirtualDevices/virtualBrightnessActuator/value/history/feed">
    <obj is="obix:HistoryFilter">
      <int name="limit" val="3" />
    </obj>
  </uri>
 </list>
</obj>
```

**Response:**
```
<obj is="obix:WatchOut">
  <list>
    <feed href="/VirtualDevices/virtualBrightnessActuator/value/history/feed" in="obix:HistoryFilter" of="obix:HistoryRecord">
      <obj>
        <abstime val="2013-09-13T10:48:30.269+02:00" tz="Europe/Vienna"/>
        <int val="42"/>
      </obj>
      <obj>
        <abstime val="2013-09-13T10:48:28.254+02:00" tz="Europe/Vienna"/>
        <int val="84"/>
      </obj>
      <obj>
        <abstime val="2013-09-13T10:48:23.729+02:00" tz="Europe/Vienna"/>
        <int val="33"/>
      </obj>
    </feed>
  </list>
</obj>
```

After making a few changes to `/VirtualDevices/virtualBrightnessActuator/value`,
`pollChanges` returns the historical values that have occured since the last poll.
The amount of records returned is limited by the HistoryFilter passed in when the feed was added to the watch.
If there are no records, the feed is not included in the `WatchOut` object.

**Request:**
```
HTTP POST http://localhost:8080/watch1/pollChanges
```

**Response:**
```
<obj is="obix:WatchOut">
  <list>
    <feed href="/VirtualDevices/virtualBrightnessActuator/value/history/feed" in="obix:HistoryFilter" of="obix:HistoryRecord">
      <obj>
        <abstime val="2013-09-13T10:53:32.122+02:00" tz="Europe/Vienna"/>
        <int val="5"/>
      </obj>
      <obj>
        <abstime val="2013-09-13T10:53:31.251+02:00" tz="Europe/Vienna"/>
        <int val="4"/>
      </obj>
      <obj>
        <abstime val="2013-09-13T10:53:30.223+02:00" tz="Europe/Vienna"/>
        <int val="3"/>
      </obj>
    </feed>
  </list>
</obj>
```

## Query the history of an object ##
Every object that is configured to hold a history for its basic data points can be queried using the history object. For testing make some `PUT` requests to an object with an `int` or `real` datapoint and history enabled. In the default configuration you can use the `virtualBrightnessActuator`.

**Request:**
```
HTTP GET http://localhost:8080/VirtualDevices/virtualBrightnessActuator/value/history/
```

The response depends on your test interactions.

**Response:**
```
<obj name="history" href="/VirtualDevices/virtualBrightnessActuator/value/history/" is="obix:History">
  <int name="count" href="count" val="6"/>
  <abstime name="start" href="start" val="2013-03-31T15:37:22.076+02:00" null="true" tz="Europe/Berlin"/>
  <abstime name="end" href="end" val="2013-03-31T15:37:24.960+02:00" null="true" tz="Europe/Berlin"/>
  <str name="tz" href="tz" val="Europe/Berlin"/>
  <feed name="feed" href="feed" in="obix:HistoryFilter" of="obix:HistoryRecord"/>
  <op name="query" href="query" in="obix:HistoryFilter" out="obix:HistoryQueryOut"/>
  <op name="rollup" href="rollup" in="obix:HistoryRollupIn" out="obix:HistoryRollupOut"/>
  <op name="append" href="append" in="obix:HistoryAppendIn" out="obix:HistoryAppendOut"/>
</obj>
```

### Filter the history ###
Using the `query` operation a filter can be performed on the history.

**Request:**
```
HTTP POST http://localhost:8080/VirtualDevices/virtualBrightnessActuator/value/history/query

Payload:
<obj is="obix:HistoryFilter"> 
  <int name="limit" val="2"/>
  <abstime name="start" val="2013-03-31T15:30:00+02:00" tz="Europe/Berlin"/>
  <abstime name="end" val="2013-03-31T15:45:00+02:00" tz="Europe/Berlin"/>
</obj>
```

**Response:**
```
<obj is="obix:HistoryQueryOut">
  <int name="count" href="count" val="2"/>
  <abstime name="start" href="start" val="2013-03-31T15:37:22.076+02:00" tz="Europe/Berlin"/>
  <abstime name="end" href="end" val="2013-03-31T15:37:22.640+02:00" tz="Europe/Berlin"/>
  <list of="obix:HistoryRecord">
  <obj>
    <abstime val="2013-03-31T15:37:22.076+02:00" tz="Europe/Berlin"/>
    <int val="100"/>
  </obj>
  <obj>
    <abstime val="2013-03-31T15:37:22.640+02:00" tz="Europe/Berlin"/>
    <int val="100"/>
  </obj>
  </list>
</obj>
```

### Query a history rollup ###
Using the `rollup` operation the historic values can be grouped within a provided interval and basic statistical indicators can be calculated.

**Request:**
```
HTTP POST http://localhost:8080/VirtualDevices/virtualBrightnessActuator/value/history/rollup

Payload:
<obj is="obix:HistoryRollupIn"> 
  <int name="limit" val="10"/>
  <abstime name="start" val="2013-03-31T15:30:00+02:00" tz="Europe/Berlin"/>
  <abstime name="end" val="2013-03-31T15:45:00+02:00" tz="Europe/Berlin"/>
  <reltime name="interval" val="PT15M"/>
</obj>
```

**Response:**
```
<obj is="obix:HistoryRollupOut">
  <int name="count" href="count" val="1"/>
  <abstime name="start" href="start" val="2013-03-31T15:30:00.000+02:00" tz="Europe/Berlin"/>
  <abstime name="end" href="end" val="2013-03-31T15:45:00.000+02:00" tz="Europe/Berlin"/>
  <list of="obix:HistoryRecord">
  <obj>
    <int name="count" href="count" val="6"/>
    <abstime name="start" href="start" val="2013-03-31T15:30:00.000+02:00" tz="Europe/Berlin"/>
    <abstime name="end" href="end" val="2013-03-31T15:45:00.000+02:00" tz="Europe/Berlin"/>
    <real name="min" href="min" val="100.0"/>
    <real name="max" href="max" val="100.0"/>
    <real name="avg" href="avg" val="100.0"/>
    <real name="sum" href="sum" val="600.0"/>
  </obj>
  </list>
</obj>
```

### Appending history records ###
Using the `append` operation historical records can be appended to a history.


**Request:**
```
HTTP POST http://localhost:8080/VirtualDevices/virtualBrightnessActuator/value/history/append

Payload:
<obj is="obix:HistoryAppendIn">
  <int name="limit" val="10"/>
  <abstime name="start" val="2013-03-31T15:30:00+02:00" tz="Europe/Berlin"/>
  <abstime name="end" val="2013-03-31T15:45:00+02:00" tz="Europe/Berlin"/>
  <reltime name="interval" val="PT15M"/>
</obj>

<obj is="obix:HistoryAppendIn">
  <list name="data" of="obix:HistoryRecord">
  <obj>
    <abstime name="timestamp" val="2013-03-31T16:00:00+02:00"/>
    <int name="value" val="42"/>
  </obj>
  </list>
</obj>
```

**Response:**
```
<obj is="obix:HistoryAppendOut">
  <int name="numAdded" href="numAdded" val="1"/>
  <int name="newCount" href="newCount" val="7"/>
  <abstime name="newStart" href="newStart" val="2013-03-31T15:30:00+02:00" tz="Europe/Berlin"/>
  <abstime name="newEnd" href="newEnd" val="2013-03-31T16:00:00+02:00" tz="Europe/Berlin"/>
</obj>
```

### History Feed ###
Every history object contains a feed object that can be observed using the watch service.
The feed can be filtered using `HistoryFilter` objects the same way as when querying the history.
[Watching Feeds](HTTPinteraction#Watching_Feeds.md) contains an interaction example.

## Managing alarms ##
oBIX specifies a feature that allows objects to generate alarms.
An alarm indicates a condition which requires notification of either a user or another application.

Alarms are grouped in `AlarmSubject`s.
The default `AlarmSubject` is found at `/alarms`.

**Request:**
```
HTTP GET http://localhost:8080/alarms/
```

**Response:**
```
<obj href="/alarms/" is="obix:AlarmSubject">
  <int name="count" href="count" val="1"/>
  <op name="query" href="query" in="obix:AlarmFilter" out="obix:AlarmQueryOut"/>
  <feed name="feed" href="feed" in="obix:AlarmFilter" of="obix:Alarm"/>
</obj>
```

The `count` specifies how many alarms have been collected by the `AlarmSubject`, the alarms might already have been acknowledged.
Querying and watching the feed works the same way as it does with [histories](HTTPinteraction#Query_the_history_of_an_object.md).

The contract for the AlarmFilter is:
```
<obj href="obix:AlarmFilter">
  <int name="limit" null="true"/>
  <abstime name="start" null="true"/>
  <abstime name="end" null="true"/>
</obj>
```


**Request:**
```
HTTP POST /alarms/query
```

**Response:**
```
<obj is="obix:AlarmQueryOut">
  <int name="count" val="1"/>
  <abstime name="start" val="2013-09-13T11:52:37.168+02:00" tz="Europe/Vienna"/>
  <abstime name="end" val="2013-09-13T11:52:37.168+02:00" tz="Europe/Vienna"/>
  <list name="data" of="obix:Alarm">
    <obj href="/alarmdb/0" is="obix:Alarm obix:StatefulAlarm obix:AckAlarm">
      <ref name="source" href="/VirtualDevices/virtualTemperatureSensor"/>
      <abstime name="timestamp" val="2013-09-13T11:52:37.168+02:00" tz="Europe/Vienna"/>
      <abstime name="normalTimestamp" val="1970-01-01T01:00:00.000+01:00" null="true" tz="Europe/Vienna"/>
      <abstime name="ackTimestamp" val="1970-01-01T01:00:00.000+01:00" null="true" tz="Europe/Vienna"/>
      <str name="ackUser" val="" null="true"/>
      <op name="ack" href="/alarmdb/0/ack" in="obix:AckAlarmIn" out="obix:AckAlarmOut"/>
    </obj>
  </list>
</obj>
```

`StatefulAlarm`s have a `normalTimestamp` that indicates when the alarm source has returned back to normal condition.
`AckAlarm`s can be acknowledged by using the `ack` operation.

**Request:**
```
HTTP POST /alarmdb/0/ack/

Payload:
<obj is="obix:AckAlarmIn">
  <str name="ackUser" val="steve"/>
</obj>
```

**Response:**
```
<obj is="obix:AckAlarmOut">
  <obj href="/alarmdb/0" is="obix:Alarm obix:StatefulAlarm obix:AckAlarm">
    <ref name="source" href="/VirtualDevices/virtualTemperatureSensor"/>
    <abstime name="timestamp" val="2013-09-13T13:44:37.565+02:00" tz="Europe/Vienna"/>
    <abstime name="normalTimestamp" val="1970-01-01T01:00:00.000+01:00" null="true" tz="Europe/Vienna"/>
    <abstime name="ackTimestamp" val="2013-09-13T13:46:32.533+02:00" tz="Europe/Vienna"/>
    <str name="ackUser" val="steve"/>
    <op name="ack" href="ack" in="obix:AckAlarmIn" out="obix:AckAlarmOut"/>
  </obj>
</obj>
```