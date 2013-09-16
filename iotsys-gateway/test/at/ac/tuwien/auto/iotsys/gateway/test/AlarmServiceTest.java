package at.ac.tuwien.auto.iotsys.gateway.test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.post;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.DatatypeConverter;

import obix.Int;
import obix.Status;

import org.junit.Test;

import at.ac.tuwien.auto.iotsys.commons.alarms.IntRangeAlarmCondition;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.actuators.impl.BrightnessActuatorImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.observer.DefaultAlarmObserver;

import com.jayway.restassured.path.xml.XmlPath;

public class AlarmServiceTest extends AbstractGatewayTest {
	private int getAlarmCount() {
		String count = XmlPath.from(get("/alarms").asString()).get("obj.int.@val");
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
				+ " <str name='ackUser' val='someUser'/>"
				+ "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:AckAlarmOut']")).
		body(hasXPath("/obj/obj[@href='" + alarmHref + "']")).
		body(hasXPath("/obj/obj/str[@name='ackUser' and not(@null) and @val='someUser']")).
		body(hasXPath("/obj/obj/abstime[@name='ackTimestamp' and not(@null)]")).
		when().post(alarmHref + "/ack");
		long expectedAckTimestamp = System.currentTimeMillis();
		
		String response = expect().
		body(hasXPath("/obj/op[@name='ack' and @href='ack' and @in='obix:AckAlarmIn' and @out='obix:AckAlarmOut']")).
		body(hasXPath("/obj/str[@name='ackUser' and @val='someUser']")).
		body(hasXPath("/obj/abstime[@name='ackTimestamp']")).
		when().get(alarmHref + "/").asString();
		
		long ackTimestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime.find { it.@name='ackTimestamp' }.@val")).getTimeInMillis();
		assertEquals(expectedAckTimestamp, ackTimestamp, 1000);
	}

	@Test
	public void testStatefulAlarmObj() {
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
		body(hasXPath("/obj/abstime[@name='normalTimestamp' and not(@null)]")).
		when().get(alarmHref).asString();
		
		normalTimestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime.find { it.@name='normalTimestamp' }.@val")).getTimeInMillis();
		assertEquals(expectedNormalTimestamp, normalTimestamp, 1000);
		
		// new alarm has been generated
		assertFalse(alarmHref.equals(getLatestAlarm().get("@href")));
	}
	
	
	@Test
	public void testAlarmSourceStatus() {
		
		BrightnessActuatorImpl source = new BrightnessActuatorImpl();
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
				+ " <str name='ackUser' source='someUser'/>"
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
				+ " <str name='ackUser' source='someUser'/>"
				+ "</obj>").
		post(alarmHref + "/ack");
		
		assertEquals(Status.ok, source.getStatus());
	}
}
