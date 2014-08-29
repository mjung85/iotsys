package at.ac.tuwien.auto.iotsys.gateway.test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import groovyx.net.http.ContentType;

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import com.jayway.restassured.path.xml.XmlPath;

public class HistoryServiceTest extends AbstractGatewayTest {
	@Test
	public void testHistoryObj() {
		expect().body(hasXPath("/obj[@is='obix:History']"))
				.body(hasXPath("/obj/int[@name='count']"))
				.body(hasXPath("/obj/abstime[@name='start']"))
				.body(hasXPath("/obj/abstime[@name='end']"))
				.body(hasXPath("/obj/str[@name='tz']"))
				.body(hasXPath("/obj/op[@name='query' and @in='obix:HistoryFilter' and @out='obix:HistoryQueryOut']"))
				.body(hasXPath("/obj/feed[@name='feed' and @in='obix:HistoryFilter' and @of='obix:HistoryRecord']"))
				.body(hasXPath("/obj/op[@name='rollup' and @in='obix:HistoryRollupIn' and @out='obix:HistoryRollupOut']"))
				.body(hasXPath("/obj/op[@name='append' and @in='obix:HistoryAppendIn' and @out='obix:HistoryAppendOut']"))
				.when()
				.get("/testDevicesHistory/brightnessHistoryQuery/value/history");
	}

	@Test
	public void testHistoryQuery() {
		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryFilter'>"
						+ "	<int name='limit' val='2'/>"
						+ "	<abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Berlin'/>"
						+ "	<abstime name='end' null='true' tz='Europe/Berlin'/>"
						+ "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryQueryOut']"))
				.body(hasXPath("/obj/int[@name='count' and @val='0']"))
				.body(hasXPath("/obj/abstime[@name='start' and @null='true']"))
				.body(hasXPath("/obj/abstime[@name='end' and @null='true']"))
				.body(hasXPath("/obj/list[@of='obix:HistoryRecord']"))
				.post("/testDevicesHistory/brightnessHistoryQuery/value/history/query");

		// some changes
		given().body("<int val='1' />").put(
				"/testDevicesHistory/brightnessHistoryQuery/value");
		given().body("<int val='2' />").put(
				"/testDevicesHistory/brightnessHistoryQuery/value");
		given().body("<int val='3' />").put(
				"/testDevicesHistory/brightnessHistoryQuery/value");

		// check history
		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryFilter'>"
						+ "	<int name='limit' val='2'/>"
						+ "	<abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Berlin'/>"
						+ "	<abstime name='end' null='true'/>" + "</obj>")
				.expect()
				.body(hasXPath("/obj/int[@name='count' and @val='2']"))
				.body(hasXPath("/obj/abstime[@name='start' and not(@null)]"))
				.body(hasXPath("/obj/abstime[@name='end' and not(@null)]"))
				.body(hasXPath("/obj/list[@of='obix:HistoryRecord']"))
				.body(hasXPath("/obj/list[count(obj) = 2]"))
				.body(hasXPath("/obj/list/obj[1]/int[@val=2]"))
				.body(hasXPath("/obj/list/obj[2]/int[@val=3]"))
				.post("/testDevicesHistory/brightnessHistoryQuery/value/history/query");
	}

	@Test
	public void testHistoryAppend() {
		String response;

		response = given()
				.contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T10:15:00-05:00'/>"
						+ "			<int name='value' val='2'/>"
						+ "		</obj>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T10:30:00-05:00'/>"
						+ "			<int val='3'/>" + "		</obj>" + "	</list>"
						+ "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryAppendOut']"))
				.body(hasXPath("/obj/int[@name='numAdded' and @val='2']"))
				.body(hasXPath("/obj/int[@name='newCount' and @val='2']"))
				.post("/testDevicesHistory/brightnessHistory2/value/history/append")
				.asString();

		long start = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[0].@val"))
				.getTimeInMillis();
		long end = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[1].@val"))
				.getTimeInMillis();

		assertEquals(start,
				DatatypeConverter.parseDateTime("2013-07-10T10:15:00-05:00")
						.getTimeInMillis());
		assertEquals(end,
				DatatypeConverter.parseDateTime("2013-07-10T10:30:00-05:00")
						.getTimeInMillis());

		response = given()
				.contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T10:45:00-05:00'/>"
						+ "			<int name='value' val='4'/>" + "		</obj>"
						+ "	</list>" + "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryAppendOut']"))
				.body(hasXPath("/obj/int[@name='numAdded' and @val='1']"))
				.body(hasXPath("/obj/int[@name='newCount' and @val='3']"))
				.post("/testDevicesHistory/brightnessHistory2/value/history/append")
				.asString();

		end = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[0].@val"))
				.getTimeInMillis();
		end = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[1].@val"))
				.getTimeInMillis();

		assertEquals(start,
				DatatypeConverter.parseDateTime("2013-07-10T10:15:00-05:00")
						.getTimeInMillis());
		assertEquals(end,
				DatatypeConverter.parseDateTime("2013-07-10T10:45:00-05:00")
						.getTimeInMillis());
	}

	@Test
	public void testHistoryAppendRecordsOutOfOrderShouldSort() {
		String response = given()
				.contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T12:30:00-05:00'/>"
						+ "			<int val='3'/>"
						+ "		</obj>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T12:15:00-05:00'/>"
						+ "			<int name='value' val='2'/>" + "		</obj>"
						+ "	</list>" + "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryAppendOut']"))
				.body(hasXPath("/obj/int[@name='numAdded' and @val='2']"))
				.body(hasXPath("/obj/int[@name='newCount' and @val='2']"))
				.post("/testDevicesHistory/brightnessHistoryAppendOutOfOrder/value/history/append")
				.asString();

		long start = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[0].@val"))
				.getTimeInMillis();
		long end = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[1].@val"))
				.getTimeInMillis();

		assertEquals(start,
				DatatypeConverter.parseDateTime("2013-07-10T12:15:00-05:00")
						.getTimeInMillis());
		assertEquals(end,
				DatatypeConverter.parseDateTime("2013-07-10T12:30:00-05:00")
						.getTimeInMillis());
	}

	@Test
	public void testHistoryAppendBeforeLastShouldFail() {
		given().param(
				"data",
				"<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T10:15:00-05:00'/>"
						+ "			<int name='value' val='2'/>" + "		</obj>"
						+ "	</list>" + "</obj>")
				.expect()
				.body(not(hasXPath("/err")))
				.post("/testDevicesHistory/brightnessHistory3/value/history/append");

		given().param(
				"data",
				"<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-05T10:00:00-05:00'/>"
						+ "			<int name='value' val='1'/>" + "		</obj>"
						+ "	</list>" + "</obj>")
				.expect()
				.body(hasXPath("/err"))
				.post("/testDevicesHistory/brightnessHistory3/value/history/append");
	}

	@Test
	public void testHistoryAppendNothing() {
		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord' />"
						+ "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryAppendOut']"))
				.body(hasXPath("/obj/int[@name='numAdded' and @val='0']"))
				.body(hasXPath("/obj/int[@name='newCount' and @val='0']"))
				.body(hasXPath("/obj/abstime[@name='newStart' and @null='true']"))
				.body(hasXPath("/obj/abstime[@name='newEnd' and @null='true']"))
				.post("/testDevicesHistory/brightnessHistoryAppendNothing/value/history/append");

		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-05T10:15:00+01:00'/>"
						+ "			<int name='value' val='1'/>" + "		</obj>"
						+ "	</list>" + "</obj>")
				.expect()
				.body(not(hasXPath("/err")))
				.post("/testDevicesHistory/brightnessHistoryAppendNothing/value/history/append");

		given().contentType(ContentType.XML)
		.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord' />"
						+ "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryAppendOut']"))
				.body(hasXPath("/obj/int[@name='numAdded' and @val='0']"))
				.body(hasXPath("/obj/int[@name='newCount' and @val='1']"))
				.body(hasXPath("/obj/abstime[@name='newStart' and not(@null)]"))
				.body(hasXPath("/obj/abstime[@name='newEnd' and not(@null)]"))
				.post("/testDevicesHistory/brightnessHistoryAppendNothing/value/history/append");
	}

	@Test
	public void testHistoryAppendFuture() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 1);
		String futureDate = DatatypeConverter.printDateTime(calendar);

		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>" + "			<abstime name='timestamp' val='"
						+ futureDate + "'/>" + "			<int name='value' val='3'/>"
						+ "		</obj>" + "	</list>" + "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryAppendOut']"))
				.body(hasXPath("/obj/int[@name='numAdded' and @val='1']"))
				.body(hasXPath("/obj/int[@name='newCount' and @val='1']"))
				.body(hasXPath("/obj/abstime[@name='newStart' and not(@null)]"))
				.body(hasXPath("/obj/abstime[@name='newEnd' and not(@null)]"))
				.post("/testDevicesHistory/brightnessHistoryAppendFuture/value/history/append");

		given().body("<int val='1' />").expect().body(not(hasXPath("/err")))
				.put("/testDevicesHistory/brightnessHistoryAppendFuture/value");
	}

	@Test
	public void testHistoryStartAndEnd() {
		expect().body(hasXPath("/obj[@is='obix:History']"))
				.body(hasXPath("/obj/int[@name='count' and @val=0]"))
				.body(hasXPath("/obj/abstime[@name='start' and @null='true']"))
				.body(hasXPath("/obj/abstime[@name='end' and @null='true']"))
				.when()
				.get("/testDevicesHistory/brightnessHistoryStartEnd/value/history");

		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T10:15:00+01:00'/>"
						+ "			<int name='value' val='2'/>" + "		</obj>"
						+ "	</list>" + "</obj>")
				.expect()
				.body(not(hasXPath("/err")))
				.post("/testDevicesHistory/brightnessHistoryStartEnd/value/history/append");

		String response = expect()
				.body(hasXPath("/obj[@is='obix:History']"))
				.body(hasXPath("/obj/int[@name='count' and @val=1]"))
				.body(not(hasXPath("/obj/abstime[@name='start'][@null]")))
				.body(not(hasXPath("/obj/abstime[@name='end'][@null]")))
				.when()
				.get("/testDevicesHistory/brightnessHistoryStartEnd/value/history")
				.asString();

		long start = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[0].@val"))
				.getTimeInMillis();
		long end = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[1].@val"))
				.getTimeInMillis();

		assertEquals(start,
				DatatypeConverter.parseDateTime("2013-07-10T10:15:00+01:00")
						.getTimeInMillis());
		assertEquals(end,
				DatatypeConverter.parseDateTime("2013-07-10T10:15:00+01:00")
						.getTimeInMillis());

		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T10:45:00+01:00'/>"
						+ "			<int name='value' val='5'/>" + "		</obj>"
						+ "	</list>" + "</obj>")
				.expect()
				.body(not(hasXPath("/err")))
				.post("/testDevicesHistory/brightnessHistoryStartEnd/value/history/append");

		response = expect()
				.body(hasXPath("/obj[@is='obix:History']"))
				.body(hasXPath("/obj/int[@name='count' and @val=2]"))
				.body(not(hasXPath("/obj/abstime[@name='start' and @null]")))
				.body(not(hasXPath("/obj/abstime[@name='end' and @null]")))
				.when()
				.get("/testDevicesHistory/brightnessHistoryStartEnd/value/history")
				.asString();

		start = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[0].@val"))
				.getTimeInMillis();
		end = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[1].@val"))
				.getTimeInMillis();

		assertEquals(start,
				DatatypeConverter.parseDateTime("2013-07-10T10:15:00+01:00")
						.getTimeInMillis());
		assertEquals(end,
				DatatypeConverter.parseDateTime("2013-07-10T10:45:00+01:00")
						.getTimeInMillis());
	}

	@Test
	public void testHistoryRollup() {
		given().body("<int val='50' />").put(
				"/testDevicesHistory/brightnessHistoryRollup/value");
		given().body("<int val='100' />").put(
				"/testDevicesHistory/brightnessHistoryRollup/value");

		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryRollupIn'> "
						+ "  <int name='limit' val='2'/>"
						+ "  <abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Berlin'/>"
						+ "  <abstime name='end' null='true' />"
						+ "  <reltime name='interval' val='PT15M'/>" + "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryRollupOut']"))
				.body(hasXPath("/obj/int[@name='count']"))
				.body(hasXPath("/obj/abstime[@name='start' and not(@null)]"))
				.body(hasXPath("/obj/abstime[@name='end' and not(@null)]"))
				.body(hasXPath("/obj/list[@of='obix:HistoryRollupRecord']"))
				.body(hasXPath("/obj/list/obj/abstime[@name='start']"))
				.body(hasXPath("/obj/list/obj/abstime[@name='end']"))
				.body(hasXPath("/obj/list/obj/int[@name='count' and @val='2']"))
				.body(hasXPath("/obj/list/obj/real[@name='min' and @val='50.0']"))
				.body(hasXPath("/obj/list/obj/real[@name='max' and @val='100.0']"))
				.body(hasXPath("/obj/list/obj/real[@name='avg' and @val='75.0']"))
				.body(hasXPath("/obj/list/obj/real[@name='sum' and @val='150.0']"))
				.post("/testDevicesHistory/brightnessHistoryRollup/value/history/rollup");
	}

	@Test
	public void testHistoryRollupBoolShouldFail() {
		// Attempting to query a rollup on a non-numeric history such as a
		// history of BoolPoints SHOULD result in an error.
		given().param(
				"data",
				"<obj is='obix:HistoryRollupIn'> "
						+ "  <int name='limit' val='2'/>"
						+ "  <abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Vienna'/>"
						+ "  <reltime name='interval' val='PT15M'/>" + "</obj>")
				.expect().body(hasXPath("/err"))
				.post("/testDevicesHistory/switch1/value/history/rollup");
	}

	@Test
	public void testHistoryRollupMultipleRecords() {
		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj> <abstime name='timestamp' val='2005-03-16T12:00:00Z'/>"
						+ "		<real name='value' val='80'/></obj>"
						+ "		<obj> <abstime name='timestamp' val='2005-03-16T12:15:00Z'/>"
						+ "		<real name='value' val='82'/></obj>"
						+ "		<obj> <abstime name='timestamp' val='2005-03-16T12:30:00Z'/>"
						+ "		<real name='value' val='90'/> </obj>"
						+ "		<obj> <abstime name='timestamp' val='2005-03-16T12:45:00Z'/>"
						+ "		<real name='value' val='85'/> </obj>"
						+ "		<obj> <abstime name='timestamp' val='2005-03-16T13:00:00Z'/>"
						+ "		<real name='value' val='81'/> </obj>"
						+ "		<obj> <abstime name='timestamp' val='2005-03-16T13:15:00Z'/>"
						+ "		<real name='value' val='84'/> </obj>"
						+ "		<obj> <abstime name='timestamp' val='2005-03-16T13:30:00Z'/>"
						+ "		<real name='value' val='91'/> </obj>"
						+ "		<obj> <abstime name='timestamp' val='2005-03-16T13:45:00Z'/>"
						+ "		<real name='value' val='83'/> </obj>"
						+ "		<obj> <abstime name='timestamp' val='2005-03-16T14:00:00Z'/>"
						+ "		<real name='value' val='78'/> </obj>" + "	</list>"
						+ "</obj>").expect().body(not(hasXPath("/err")))
				.post("/testDevicesHistory/tempHistory/value/history/append");

		String response = given()
				.contentType(ContentType.XML)
				.body("<obj is='obix:HistoryRollupIn'> "
						+ "  <abstime name='start' val='2005-03-16T12:00:00Z'/>"
						+ "  <abstime name='end' val='2005-03-16T14:00:00Z' />"
						+ "  <reltime name='interval' val='PT1H'/>" + "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryRollupOut']"))
				.body(hasXPath("/obj/int[@name='count' and @val='2']"))
				.body(hasXPath("/obj/abstime[@name='start']"))
				.body(hasXPath("/obj/abstime[@name='end']"))
				.

				body(hasXPath("/obj/list[@of='obix:HistoryRollupRecord']"))
				.body(hasXPath("/obj/list/obj[1]/abstime[@name='start']"))
				.body(hasXPath("/obj/list/obj[1]/abstime[@name='end']"))
				.body(hasXPath("/obj/list/obj[1]/int[@name='count' and @val=4]"))
				.body(hasXPath("/obj/list/obj[1]/real[@name='min' and @val=81]"))
				.body(hasXPath("/obj/list/obj[1]/real[@name='max' and @val=90]"))
				.body(hasXPath("/obj/list/obj[1]/real[@name='avg' and @val=84.5]"))
				.body(hasXPath("/obj/list/obj[1]/real[@name='sum' and @val=338]"))
				.

				body(hasXPath("/obj/list/obj[2]/abstime[@name='start']"))
				.body(hasXPath("/obj/list/obj[2]/abstime[@name='end']"))
				.body(hasXPath("/obj/list/obj[2]/int[@name='count' and @val=4]"))
				.body(hasXPath("/obj/list/obj[2]/real[@name='min' and @val=78]"))
				.body(hasXPath("/obj/list/obj[2]/real[@name='max' and @val=91]"))
				.body(hasXPath("/obj/list/obj[2]/real[@name='avg' and @val=84]"))
				.body(hasXPath("/obj/list/obj[2]/real[@name='sum' and @val=336]"))
				.post("/testDevicesHistory/tempHistory/value/history/rollup")
				.asString();

		// Check timestamps
		long timestamp;

		timestamp = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[0].@val"))
				.getTimeInMillis();
		assertEquals(timestamp,
				DatatypeConverter.parseDateTime("2005-03-16T12:00:00Z")
						.getTimeInMillis());

		timestamp = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[1].@val"))
				.getTimeInMillis();
		assertEquals(timestamp,
				DatatypeConverter.parseDateTime("2005-03-16T14:00:00Z")
						.getTimeInMillis());

		timestamp = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString(
						"obj.list.obj[0].abstime[0].@val")).getTimeInMillis();
		assertEquals(timestamp,
				DatatypeConverter.parseDateTime("2005-03-16T12:00:00Z")
						.getTimeInMillis());

		timestamp = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString(
						"obj.list.obj[0].abstime[1].@val")).getTimeInMillis();
		assertEquals(timestamp,
				DatatypeConverter.parseDateTime("2005-03-16T13:00:00Z")
						.getTimeInMillis());

		timestamp = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString(
						"obj.list.obj[1].abstime[0].@val")).getTimeInMillis();
		assertEquals(timestamp,
				DatatypeConverter.parseDateTime("2005-03-16T13:00:00Z")
						.getTimeInMillis());

		timestamp = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString(
						"obj.list.obj[1].abstime[1].@val")).getTimeInMillis();
		assertEquals(timestamp,
				DatatypeConverter.parseDateTime("2005-03-16T14:00:00Z")
						.getTimeInMillis());
	}

	@Test
	public void testHistoryTimezones() {
		String history = expect().body(hasXPath("/obj[@is='obix:History']"))
				.body(hasXPath("/obj/str[@name='tz']"))
				.get("/testDevicesHistory/HistoryTimezones/value/history")
				.asString();

		String timezone = XmlPath.from(history).getString("obj.str.@val");

		// append, query and rollup have to use same timezone
		String response = given()
				.contentType(ContentType.XML)
				.body("<obj is='obix:HistoryAppendIn'>"
						+ "	<list name='data' of='obix:HistoryRecord'>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T10:15:00-05:00' tz='America/Detroit'/>"
						+ "			<int name='value' val='2'/>"
						+ "		</obj>"
						+ "		<obj>"
						+ "			<abstime name='timestamp' val='2013-07-10T11:30:00-04:00' tz='America/Barbados'/>"
						+ "			<int val='3'/>" + "		</obj>" + "	</list>"
						+ "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryAppendOut']"))
				.body(hasXPath("/obj/abstime[@name='newStart' and @tz='"
						+ timezone + "']"))
				.body(hasXPath("/obj/abstime[@name='newEnd' and @tz='"
						+ timezone + "']"))
				.post("/testDevicesHistory/HistoryTimezones/value/history/append")
				.asString();

		long start = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[0].@val"))
				.getTimeInMillis();
		long end = DatatypeConverter.parseDateTime(
				XmlPath.from(response).getString("obj.abstime[1].@val"))
				.getTimeInMillis();

		assertEquals(start,
				DatatypeConverter.parseDateTime("2013-07-10T10:15:00-05:00")
						.getTimeInMillis());
		assertEquals(end,
				DatatypeConverter.parseDateTime("2013-07-10T11:30:00-04:00")
						.getTimeInMillis());

		given().contentType(ContentType.XML)
		.body("<obj is='obix:HistoryFilter'>"
						+ "	<abstime name='start' val='2013-03-31T15:30:00-07:00' tz='America/Phoenix'/>"
						+ "	<abstime name='end' null='true' />" + "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryQueryOut']"))
				.body(hasXPath("/obj/abstime[@name='start' and @tz='"
						+ timezone + "']"))
				.body(hasXPath("/obj/abstime[@name='end' and @tz='" + timezone
						+ "']"))
				.post("/testDevicesHistory/HistoryTimezones/value/history/query");

		given().contentType(ContentType.XML)
				.body("<obj is='obix:HistoryRollupIn'> "
						+ "  <int name='limit' val='2'/>"
						+ "  <abstime name='start' val='2013-03-31T12:30:00+09:00' tz='Japan'/>"
						+ "  <abstime name='end' null='true' />"
						+ "  <reltime name='interval' val='PT2H'/>" + "</obj>")
				.expect()
				.body(hasXPath("/obj[@is='obix:HistoryRollupOut']"))
				.body(hasXPath("/obj/abstime[@name='start' and @tz='"
						+ timezone + "']"))
				.body(hasXPath("/obj/abstime[@name='end' and @tz='" + timezone
						+ "']"))
				.post("/testDevicesHistory/HistoryTimezones/value/history/rollup");
	}

}
