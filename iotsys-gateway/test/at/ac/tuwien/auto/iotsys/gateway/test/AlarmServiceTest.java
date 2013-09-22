package at.ac.tuwien.auto.iotsys.gateway.test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.post;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import groovyx.net.http.ContentType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import obix.Int;
import obix.Status;
import obix.Uri;

import org.junit.Test;

import at.ac.tuwien.auto.iotsys.commons.alarms.IntRangeAlarmCondition;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.AlarmImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.AlarmSubjectImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.BrightnessActuatorImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.observer.DefaultAlarmObserver;
import at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBrokerImpl;

import com.jayway.restassured.path.xml.XmlPath;

public class AlarmServiceTest extends AbstractGatewayTest {
	private int getAlarmCount() {
		return getAlarmCount("/alarms");
	}
	
	private int getAlarmCount(String alarmSubjectHref) {
		String count = XmlPath.from(get(alarmSubjectHref).asString()).get("obj.int.@val");
		return Integer.parseInt(count);
	}
	
	private XmlPath getLatestAlarm() {
		XmlPath lastAlarm = new XmlPath(post("/alarms/query").asString());
		int alarms = lastAlarm.getInt("obj.list.obj.size()");
		lastAlarm.setRoot(("obj.list.obj[" + (alarms-1) + "]"));
		
		return lastAlarm;
	}
	
	@Test
	public void testAlarmSubject() {
		expect().
		body(hasXPath("/obj[@is='obix:AlarmSubject']")).
		body(hasXPath("/obj/int[@name='count']")).
		body(hasXPath("/obj/op[@name='query' and @in='obix:AlarmFilter' and @out='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/feed[@name='feed' and @in='obix:AlarmFilter' and @of='obix:Alarm']")).
		when().get("/alarms");
	}

	@Test
	public void testAlarmGeneration() {
		int initialAlarmCount = getAlarmCount();
		
		String fanspeedPut = "<obj is='iot:FanSpeedActuator'>"
				+ "	<int name='fanSpeedSetpointValue' href='fanSpeedSetpoint' val='%d' />"
				+ " <bool name='enabled' href='enabled' val='%b' />"
				+ "</obj>";
		
		
		given().body(String.format(fanspeedPut, 1000, false)).
		put("/testDevicesAlarm/fanSpeed1");
		assertEquals(initialAlarmCount, getAlarmCount());
		
		given().body(String.format(fanspeedPut, 3000, true)).
		put("/testDevicesAlarm/fanSpeed1");
		assertEquals(initialAlarmCount, getAlarmCount());
		
		given().body(String.format(fanspeedPut, 500, true)).
		put("/testDevicesAlarm/fanSpeed1");
		assertEquals(initialAlarmCount + 1, getAlarmCount());
	}
	
	@Test
	public void testRepeatedAlarmGeneration() {
		int initialAlarmCount = getAlarmCount();
		
		String fanspeedPut = "<obj is='iot:FanSpeedActuator'>"
				+ "	<int name='fanSpeedSetpointValue' href='fanSpeedSetpoint' val='%d' />"
				+ " <bool name='enabled' href='enabled' val='%b' />"
				+ "</obj>";
		
		// off normal
		given().body(String.format(fanspeedPut, 1000, true)).
		put("/testDevicesAlarm/fanSpeed2");
		assertEquals(initialAlarmCount + 1, getAlarmCount());
		
		// staying off normal
		given().body(String.format(fanspeedPut, 200, true)).
		put("/testDevicesAlarm/fanSpeed2");
		assertEquals(initialAlarmCount + 1, getAlarmCount());
		
		// to normal
		given().body(String.format(fanspeedPut, 3000, true)).
		put("/testDevicesAlarm/fanSpeed2");
		assertEquals(initialAlarmCount + 1, getAlarmCount());
		
		// off normal again, new alarm generated
		given().body(String.format(fanspeedPut, 500, true)).
		put("/testDevicesAlarm/fanSpeed2");
		assertEquals(initialAlarmCount + 2, getAlarmCount());
	}
	
	@Test
	public void testAlarmObject() {
		String fanspeedPut = "<obj is='iot:FanSpeedActuator'>"
				+ "	<int name='fanSpeedSetpointValue' href='fanSpeedSetpoint' val='%d' />"
				+ " <bool name='enabled' href='enabled' val='%b' />"
				+ "</obj>";
		
		// off normal
		given().body(String.format(fanspeedPut, 1000, true)).
		put("/testDevicesAlarm/fanSpeed3");
		String alarmHref = getLatestAlarm().get("@href");
		
		expect().
		body(hasXPath("/obj/ref[@name='source' and @href='/testDevicesAlarm/fanSpeed3']")).
		body(hasXPath("/obj/abstime[@name='timestamp']")).
		when().get(alarmHref);
	}
	
	
	@Test
	public void testAckAlarm() {
		String ackUser = "Ford";
		BrightnessActuatorImpl source = new BrightnessActuatorImpl();
		source.value().attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(10l, 20l))
										.setAcked(true).setStateful(false));
		int alarms = getAlarmCount();
		
		// generate alarm
		source.value().set(25l);
		assertEquals(++alarms, getAlarmCount());
		String alarmHref = getLatestAlarm().get("@href");

		expect().
		body(hasXPath("/obj/op[@name='ack' and @href='ack' and @in='obix:AckAlarmIn' and @out='obix:AckAlarmOut']")).
		body(hasXPath("/obj/str[@name='ackUser' and @null='true']")).
		body(hasXPath("/obj/abstime[@name='ackTimestamp' and @null='true']")).
		when().get(alarmHref + "/");
		
		given().
		param("data", "<obj is='obix:AckAlarmIn'>"
				+ " <str name='ackUser' val='" + ackUser + "'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AckAlarmOut']")).
		body(hasXPath("/obj/obj[@href='" + alarmHref + "']")).
		body(hasXPath("/obj/obj/str[@name='ackUser' and not(@null) and @val='" + ackUser + "']")).
		body(hasXPath("/obj/obj/abstime[@name='ackTimestamp' and not(@null='true')]")).
		when().post(alarmHref + "/ack");
		long expectedAckTimestamp = System.currentTimeMillis();
		
		String response = expect().
		body(hasXPath("/obj/op[@name='ack' and @href='ack' and @in='obix:AckAlarmIn' and @out='obix:AckAlarmOut']")).
		body(hasXPath("/obj/str[@name='ackUser' and @val='" + ackUser + "']")).
		body(hasXPath("/obj/abstime[@name='ackTimestamp' and not(@null='true')]")).
		when().get(alarmHref + "/").asString();
		
		long ackTimestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime.find { it.@name='ackTimestamp' }.@val")).getTimeInMillis();
		assertEquals(expectedAckTimestamp, ackTimestamp, 1000);
	}
	
	@Test
	public void testAckAlarmWithNullUser() {
		BrightnessActuatorImpl source = new BrightnessActuatorImpl();
		source.value().attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(10l, 20l))
										.setAcked(true).setStateful(false));
		int alarms = getAlarmCount();
		
		// generate alarm
		source.value().set(25l);
		assertEquals(++alarms, getAlarmCount());
		String alarmHref = getLatestAlarm().get("@href");

		expect().
		body(hasXPath("/obj/op[@name='ack' and @href='ack' and @in='obix:AckAlarmIn' and @out='obix:AckAlarmOut']")).
		body(hasXPath("/obj/str[@name='ackUser' and @null='true']")).
		body(hasXPath("/obj/abstime[@name='ackTimestamp' and @null='true']")).
		when().get(alarmHref + "/");
		
		given().
		param("data", "<obj is='obix:AckAlarmIn'>"
				+ "	<str name='ackUser' null='true'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AckAlarmOut']")).
		body(hasXPath("/obj/obj[@href='" + alarmHref + "']")).
		body(hasXPath("/obj/obj/str[@name='ackUser' and not(@null='true')]")).
		body(hasXPath("/obj/obj/abstime[@name='ackTimestamp' and not(@null='true')]")).
		when().post(alarmHref + "/ack");
		long expectedAckTimestamp = System.currentTimeMillis();
		
		String response = expect().
		body(hasXPath("/obj/op[@name='ack' and @href='ack' and @in='obix:AckAlarmIn' and @out='obix:AckAlarmOut']")).
		body(hasXPath("/obj/str[@name='ackUser' and not(@null='true')]")).
		body(hasXPath("/obj/abstime[@name='ackTimestamp' and not(@null='true')]")).
		when().get(alarmHref + "/").asString();
		
		long ackTimestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime.find { it.@name='ackTimestamp' }.@val")).getTimeInMillis();
		assertEquals(expectedAckTimestamp, ackTimestamp, 1000);
	}

	@Test
	public void testStatefulAlarm() {
		BrightnessActuatorImpl source = new BrightnessActuatorImpl();
		source.value().attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(10l, 20l))
										.setAcked(false).setStateful(true));
		int alarms = getAlarmCount();
		
		// generate alarm
		source.value().set(30l);
		assertEquals(alarms+1, getAlarmCount());
		String alarmHref = getLatestAlarm().get("@href");
		
		expect().
		body(hasXPath("/obj/abstime[@name='normalTimestamp' and @null='true']")).
		when().get(alarmHref);
		
		// to normal
		source.value().set(15l);
		long expectedNormalTimestamp = System.currentTimeMillis();
		
		String response = expect().
		body(hasXPath("/obj/abstime[@name='normalTimestamp' and not(@null)]")).
		when().get(alarmHref).asString();
		
		long normalTimestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime.find { it.@name='normalTimestamp' }.@val")).getTimeInMillis();
		assertEquals(expectedNormalTimestamp, normalTimestamp, 500);
		
		
		// changing alarm-state again does not influence recorded normal timestamp
		// off normal and back to normal
		source.value().set(2l);
		source.value().set(12l);
		
		response = expect().
		body(hasXPath("/obj/abstime[@name='normalTimestamp' and not(@null='true')]")).
		when().get(alarmHref).asString();
		
		normalTimestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime.find { it.@name='normalTimestamp' }.@val")).getTimeInMillis();
		assertEquals(expectedNormalTimestamp, normalTimestamp, 1000);
		
		// new alarm has been generated
		assertFalse(alarmHref.equals(getLatestAlarm().get("@href")));
	}
	
	
	@Test
	public void testAlarmSourceStatus() {
		
		BrightnessActuatorImpl source = new BrightnessActuatorImpl();
		source.setHref(new Uri("/testAlarmSourceStatus"));
		ObjectBrokerImpl.getInstance().addObj(source);
		Int val = source.value();
		
		val.attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(10l, 20l).setFlipped(true), true, false)
							.setTarget(source));
		val.attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(21l, 30l).setFlipped(true), false, true)
							.setTarget(source));
		
		long statefulAlarmValue = 15;
		long ackedAlarmValue = 25;
		long noAlarmValue = 0;
		int alarms = getAlarmCount();
		
		assertEquals(Status.ok, source.getStatus());

		source.setOverridden(true);
		assertEquals(Status.overridden, source.getStatus());
		
		source.setDown(true);
		assertEquals(Status.down, source.getStatus());
		
		source.setFaulty(true);
		assertEquals(Status.fault, source.getStatus());
		
		source.setDisabled(true);
		assertEquals(Status.disabled, source.getStatus());
		
		source.setFaulty(false);
		assertEquals(Status.disabled, source.getStatus());
		
		source.setDisabled(false);
		assertEquals(Status.down, source.getStatus());
		
		source.setStatus(Status.ok);
		assertEquals(Status.ok, source.getStatus());
		
		
		// generate alarm
		val.set(statefulAlarmValue);
		assertTrue(source.inAlarmState());
		assertEquals(++alarms, getAlarmCount());
		assertEquals(Status.alarm, source.getStatus());
		
		source.setFaulty(true);
		assertEquals(Status.fault, source.getStatus());
		
		source.setFaulty(false);
		assertEquals(Status.alarm, source.getStatus());
		
		source.setDisabled(true);
		assertEquals(Status.disabled, source.getStatus());
		source.setDown(true);
		assertEquals(Status.disabled, source.getStatus());
		source.setDisabled(false);
		assertEquals(Status.down, source.getStatus());
		source.setDown(false);
		assertEquals(Status.alarm, source.getStatus());
		
		// unset alarm
		val.set(noAlarmValue);
		assertEquals(Status.ok, source.getStatus());
		
		// generate AckAlarm
		val.set(ackedAlarmValue);
		assertEquals(++alarms, getAlarmCount());
		assertEquals(Status.unackedAlarm, source.getStatus());
		
		// ack the alarm
		String alarmHref;
		
		alarmHref = getLatestAlarm().get("@href");
		given().param("data", "<obj is='obix:AckAlarmIn'>"
				+ " <str name='ackUser' val='someUser'/>"
				+ "</obj>").
		post(alarmHref + "/ack");
		
		
		assertEquals(Status.alarm, source.getStatus());
		// unset alarm 
		val.set(noAlarmValue);
		assertEquals(Status.ok, source.getStatus());
		
		// generate AckAlarm
		val.set(ackedAlarmValue);
		assertEquals(++alarms, getAlarmCount());
		assertEquals(Status.unackedAlarm, source.getStatus());
		alarmHref = getLatestAlarm().get("@href");
		
		val.set(noAlarmValue);
		assertEquals(Status.unacked, source.getStatus());
		
		val.set(statefulAlarmValue);
		assertEquals(++alarms, getAlarmCount());
		assertEquals(Status.alarm, source.getStatus());
		
		val.set(noAlarmValue);
		assertEquals(Status.unacked, source.getStatus());
		
		given().param("data", "<obj is='obix:AckAlarmIn'>"
				+ " <str name='ackUser' val='someUser'/>"
				+ "</obj>").
		post(alarmHref + "/ack");
		
		assertEquals(Status.ok, source.getStatus());
	}
	
	
	@Test
	public void testCustomAlarmSubject() {
		AlarmSubjectImpl subject = new AlarmSubjectImpl(ObjectBrokerImpl.getInstance());
		String subjectHref = "/customAlarmSubject";
		subject.setHref(new Uri(subjectHref));
		ObjectBrokerImpl.getInstance().addObj(subject);
		
		BrightnessActuatorImpl source = new BrightnessActuatorImpl();
		Int val = source.value();
		
		val.attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(10l, 20l).setFlipped(true), true, false).setTarget(source).setAlarmSubject(subject));
		
		int alarms = getAlarmCount();
		val.set(15l);
		
		assertEquals(alarms, getAlarmCount()); // was not added to default alarm subject
		assertEquals(1, getAlarmCount(subjectHref)); // custom subject has 1 alarm registered
	}
	
	@Test
	public void testQueryAlarmSubject() throws ParseException {
		AlarmSubjectImpl subject = new AlarmSubjectImpl(ObjectBrokerImpl.getInstance());
		String subjectHref = "/queryAlarmSubject";
		subject.setHref(new Uri(subjectHref));
		ObjectBrokerImpl.getInstance().addObj(subject);
		
		// add some alarm data
		BrightnessActuatorImpl source = new BrightnessActuatorImpl();
		source.setHref(new Uri("/some/alarm/source"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		TimeZone tz = TimeZone.getTimeZone("Europe/Vienna");
		dateFormat.setTimeZone(tz);
		
		AlarmImpl alarm10 = new AlarmImpl(source, true, true);
		alarm10.timestamp().set(dateFormat.parse("2013-09-10 10:00").getTime(), tz);
		subject.addAlarm(alarm10);
		
		AlarmImpl alarm11 = new AlarmImpl(source, true, false);
		alarm11.timestamp().set(dateFormat.parse("2013-09-10 11:00").getTime(), tz);
		subject.addAlarm(alarm11);
		
		AlarmImpl alarm12 = new AlarmImpl(source, false, true);
		alarm12.timestamp().set(dateFormat.parse("2013-09-10 12:00").getTime(), tz);
		subject.addAlarm(alarm12);
		
		AlarmImpl alarm13 = new AlarmImpl(source, false, false);
		alarm13.timestamp().set(dateFormat.parse("2013-09-10 13:00").getTime(), tz);
		subject.addAlarm(alarm13);
		
		
		// query the alarm subject
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='4']")).
		body(hasXPath("/obj/abstime[@name='start']")).
		body(hasXPath("/obj/abstime[@name='end']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		when().post(subjectHref + "/query");
		
		// only alarms in time interval [11:00,12:00]
		given().contentType(ContentType.XML).
		body("<obj is='obix:AlarmFilter'>"
				+ "  <abstime name='start' val='2013-09-10T11:00:00+02:00'/>"
				+ "  <abstime name='end'   val='2013-09-10T12:00:00+02:00'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='2']")).
		body(hasXPath("/obj/abstime[@name='start']")).
		body(hasXPath("/obj/abstime[@name='end']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		body(hasXPath("/obj/list/obj[@href='" + alarm11.getFullContextPath() + "']")).
		body(hasXPath("/obj/list/obj[@href='" + alarm12.getFullContextPath() + "']")).
		when().post(subjectHref + "/query");
		
		// limit to 2 alarms
		given().contentType(ContentType.XML).
		body("<obj is='obix:AlarmFilter'>"
				+ "  <int name='limit' val='2'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='2']")).
		body(hasXPath("/obj/abstime[@name='start']")).
		body(hasXPath("/obj/abstime[@name='end']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		body(hasXPath("/obj/list/obj[@href='" + alarm10.getFullContextPath() + "']")).
		body(hasXPath("/obj/list/obj[@href='" + alarm11.getFullContextPath() + "']")).
		when().post(subjectHref + "/query");
	}
	
	@Test
	public void testQueryAlarmSource() {
		// add some alarm data
		String source1href = "/queryAlarmSource/source1";
		String source2href = "/queryAlarmSource/source2";
		
		BrightnessActuatorImpl source1 = new BrightnessActuatorImpl();
		source1.setHref(new Uri(source1href));
		Int val1 = source1.value();
		val1.attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(10l, 20l).setFlipped(true), false, false).setTarget(source1));
		
		BrightnessActuatorImpl source2 = new BrightnessActuatorImpl();
		source2.setHref(new Uri(source2href));
		Int val2 = source2.value();
		val2.attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(10l, 20l).setFlipped(true), false, false).setTarget(source2));
		
		ObjectBrokerImpl.getInstance().addObj(source1);
		ObjectBrokerImpl.getInstance().addObj(source2);
		
		// generate alarms
		val1.set(15l);
		val2.set(17l);
		
		// query the alarm subject by source
		given().contentType(ContentType.XML).
		body("<obj is='obix:AlarmFilter'>"
				+ "  <uri name='source' val='" + source1href + "'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='1']")).
		body(hasXPath("/obj/abstime[@name='start']")).
		body(hasXPath("/obj/abstime[@name='end']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		body(hasXPath("/obj/list/obj/ref[@name='source' and @href='" + source1.getFullContextPath() + "']")).
		body(not(hasXPath("/obj/list/obj/ref[@name='source' and @href='" + source2.getFullContextPath() + "']"))).
		when().post("/alarms/query");
	}
	
	
	@Test
	public void testQueryUnackedAlarms() {
		AlarmSubjectImpl subject = new AlarmSubjectImpl(ObjectBrokerImpl.getInstance());
		String subjectHref = "/queryUnackedAlarms";
		subject.setHref(new Uri(subjectHref));
		ObjectBrokerImpl.getInstance().addObj(subject);
		
		// add some alarm data
		BrightnessActuatorImpl source = new BrightnessActuatorImpl();
		source.setHref(new Uri("/bright/alarm/source"));
		
		AlarmImpl alarmUnacked = new AlarmImpl(source, false, true);
		subject.addAlarm(alarmUnacked);
		
		AlarmImpl alarmStateful = new AlarmImpl(source, true, false);
		subject.addAlarm(alarmStateful);
		
		AlarmImpl alarmAcked = new AlarmImpl(source, false, true);
		subject.addAlarm(alarmAcked);
		alarmAcked.ack("Trillian");
		
		
		// unacked = null, return all alarms
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='3']")).
		body(hasXPath("/obj/abstime[@name='start']")).
		body(hasXPath("/obj/abstime[@name='end']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		body(hasXPath("/obj/list/obj[@href='" + alarmUnacked.getFullContextPath() + "']")).
		body(hasXPath("/obj/list/obj[@href='" + alarmStateful.getFullContextPath() + "']")).
		body(hasXPath("/obj/list/obj[@href='" + alarmAcked.getFullContextPath() + "']")).
		when().post(subjectHref + "/query");
		
		// unacked = true, return unacked AckAlarms
		given().contentType(ContentType.XML).
		body("<obj is='obix:AlarmFilter'>"
				+ "  <bool name='unacked' val='true'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='1']")).
		body(hasXPath("/obj/abstime[@name='start']")).
		body(hasXPath("/obj/abstime[@name='end']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		body(hasXPath("/obj/list/obj[@href='" + alarmUnacked.getFullContextPath() + "']")).
		body(not(hasXPath("/obj/list/obj[@href='" + alarmStateful.getFullContextPath() + "']"))).
		body(not(hasXPath("/obj/list/obj[@href='" + alarmAcked.getFullContextPath() + "']"))).
		when().post(subjectHref + "/query");
		
		// unacked = false, return acked AckAlarms
		given().contentType(ContentType.XML).
		body("<obj is='obix:AlarmFilter'>"
				+ "  <bool name='unacked' val='false'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='1']")).
		body(hasXPath("/obj/abstime[@name='start']")).
		body(hasXPath("/obj/abstime[@name='end']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		body(not(hasXPath("/obj/list/obj[@href='" + alarmUnacked.getFullContextPath() + "']"))).
		body(not(hasXPath("/obj/list/obj[@href='" + alarmStateful.getFullContextPath() + "']"))).
		body(    hasXPath("/obj/list/obj[@href='" + alarmAcked.getFullContextPath() + "']")).
		when().post(subjectHref + "/query");
	}
	
	@Test
	public void testQueryActiveAlarms() {
		AlarmSubjectImpl subject = new AlarmSubjectImpl(ObjectBrokerImpl.getInstance());
		String subjectHref = "/queryActiveAlarmsSubject";
		subject.setHref(new Uri(subjectHref));
		ObjectBrokerImpl.getInstance().addObj(subject);

		// add some alarm data
		String source1href = "/queryActiveAlarms/source1";
		String source2href = "/queryActiveAlarms/source2";
		
		BrightnessActuatorImpl source1 = new BrightnessActuatorImpl();
		source1.setHref(new Uri(source1href));
		Int val1 = source1.value();
		val1.attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(10l, 20l).setFlipped(true), true, false).setTarget(source1).setAlarmSubject(subject));
		
		BrightnessActuatorImpl source2 = new BrightnessActuatorImpl();
		source2.setHref(new Uri(source2href));
		Int val2 = source2.value();
		val2.attach(new DefaultAlarmObserver(new IntRangeAlarmCondition(10l, 20l).setFlipped(true), true, false).setTarget(source2).setAlarmSubject(subject));
		
		ObjectBrokerImpl.getInstance().addObj(source1);
		ObjectBrokerImpl.getInstance().addObj(source2);
		
		// source 1 into alarm condition
		long valueAlarming = 15l;
		long valueNormal = 30l;
		val1.set(valueAlarming);
		val2.set(valueAlarming);
		val2.set(valueNormal); // to normal
		
		// query the alarm subject for active alarms (StatefulAlarms that have not returned to normal)
		given().contentType(ContentType.XML).
		body("<obj is='obix:AlarmFilter'>"
				+ "  <bool name='active' val='true'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='1']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		body(hasXPath("/obj/list/obj/ref[@name='source' and @href='" + source1.getFullContextPath() + "']")).
		body(not(hasXPath("/obj/list/obj/ref[@name='source' and @href='" + source2.getFullContextPath() + "']"))).
		when().post(subjectHref + "/query");
		
		// inactive alarms
		given().contentType(ContentType.XML).
		body("<obj is='obix:AlarmFilter'>"
				+ "  <bool name='active' val='false'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='1']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		body(not(hasXPath("/obj/list/obj/ref[@name='source' and @href='" + source1.getFullContextPath() + "']"))).
		body(hasXPath("/obj/list/obj/ref[@name='source' and @href='" + source2.getFullContextPath() + "']")).
		when().post(subjectHref + "/query");
		
		// val1 to normal too
		val1.set(valueNormal);
		
		given().contentType(ContentType.XML).
		body("<obj is='obix:AlarmFilter'>"
				+ "  <bool name='active' val='true'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AlarmQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='0']")).
		body(hasXPath("/obj/list[@name='data' and @of='obix:Alarm']")).
		body(not(hasXPath("/obj/list/obj/ref[@name='source' and @href='" + source1.getFullContextPath() + "']"))).
		body(not(hasXPath("/obj/list/obj/ref[@name='source' and @href='" + source2.getFullContextPath() + "']"))).
		when().post(subjectHref + "/query");
	}
}
