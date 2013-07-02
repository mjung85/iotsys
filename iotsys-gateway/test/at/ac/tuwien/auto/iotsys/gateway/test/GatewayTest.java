package at.ac.tuwien.auto.iotsys.gateway.test;
import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.auto.iotsys.gateway.IoTSySGateway;

import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.element.Node;

public class GatewayTest {
	private static IoTSySGateway gateway;
	
	@BeforeClass
	public static void setUp() {
		gateway = new IoTSySGateway();
		gateway.startGateway();
	}
	
	@AfterClass
	public static void tearDown() {
		gateway.stopGateway();
	}
	
	@Test
	public void testLobbyHasAbout() {
		expect().body(Matchers.hasXPath("/obj/ref[@href='about']")).
		when().get("/obix");
	}
	
	@Test
	public void testLobbyHasWatchService() {
		expect().body(Matchers.hasXPath("/obj/ref[@href='/watchService']")).
		when().get("/obix");
	}
	
	@Test
	public void testQuerySensor() {
		expect().body(Matchers.hasXPath("//bool[@name='value']")).
		when().get("/virtualLight1");
	}
	
	@Test
	public void testModifyValue() {
		given().body("<bool val='false' />").
		expect().body(Matchers.hasXPath("//bool[@name='value' and @val='false']")).
		when().put("/virtualLight1/value");
		
		given().body("<bool val='true' />").
		expect().body(Matchers.hasXPath("//bool[@name='value' and @val='true']")).
		when().put("/virtualLight1/value");
		
		given().body("<int val='42' />").
		expect().body(Matchers.hasXPath("//int[@val='42']")).
		when().put("/simFanSpeedIn/fanSpeedSetpoint");
	}
	
	@Test
	public void testMakeWatch() {
		given().
		param("Content-Type", "text/plain").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:Watch']")).
		body(Matchers.hasXPath("/obj/reltime[@name='lease']")).
		body(Matchers.hasXPath("/obj/op[@name='add' and @in='obix:WatchIn' and @out='obix:WatchOut']")).
		body(Matchers.hasXPath("/obj/op[@name='remove' and @in='obix:WatchIn']")).
		body(Matchers.hasXPath("/obj/op[@name='pollChanges' and @out='obix:WatchOut']")).
		body(Matchers.hasXPath("/obj/op[@name='pollRefresh' and @out='obix:WatchOut']")).
		body(Matchers.hasXPath("/obj/op[@name='delete']")).
		when().post("/watchService/make");
	}
	
	@Test
	public void testWatchAddSingleObj() {
		String watch = given().param("Content-Type", "text/plain").post("/watchService/make").asString();
		Node watchNode = XmlPath.from(watch).get("/obj");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/virtualLight1/value' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:WatchOut']")).
		body(Matchers.hasXPath("/obj/list/bool[@val='true' and @href='/virtualLight1/value']")).
		post(watchNode.getAttribute("href") + "/add");
	}
	
	@Test
	public void testWatchAddMultipleObjs() {
		String watch = given().param("Content-Type", "text/plain").post("/watchService/make").asString();
		Node watchNode = XmlPath.from(watch).get("/obj");
		
		System.out.println(given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/virtualLight1/value' />" +
			 "		<uri val='/nonExistingObject/value' />" +
			 "		<uri val='/virtualLight2/value' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:WatchOut']")).
		body(Matchers.hasXPath("/obj/list/bool[@href='/virtualLight1/value']")).
		body(Matchers.hasXPath("/obj/list/bool[@href='/virtualLight2/value']")).
		body(Matchers.hasXPath("/obj/list/err[@href='/nonExistingObject/value']")).
		post(watchNode.getAttribute("href") + "/add").asString());
	}
	
	@Test
	public void testWatchPollChanges() {
		String watch = given().param("Content-Type", "text/plain").post("/watchService/make").asString();
		Node watchNode = XmlPath.from(watch).get("/obj");
		
		String addUri = watchNode.getAttribute("href") + "/add";
		String pollUri = watchNode.getAttribute("href") + "/pollChanges";
		
		System.out.println(
		given().param("data", "<obj is='obix:WatchIn'>"+
				 "	<list name='hrefs'>" +
				 "		<uri val='/virtualLight1/value' />" +
				 "		<uri val='/virtualLight2/value' />" +
				 "	</list>" +
				 "</obj>").post(addUri).asString());
		
		// No changes yet
		given().param("Content-Type", "text/plain").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:WatchOut']")).
		body(Matchers.not(Matchers.hasXPath("/obj/list/bool"))).
		when().post(pollUri);
		
		
		// Reset objects
		given().body("<bool val='false' />").put("/virtualLight1/value");
		given().body("<bool val='false' />").put("/virtualLight2/value");
		given().param("Content-Type", "text/plain").post(pollUri);
		
		
		// Make single change
		given().body("<bool val='true' />").put("/virtualLight1/value");
		given().param("Content-Type", "text/plain").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:WatchOut']")).
		body(Matchers.hasXPath("/obj/list/bool[@val='true' and @href='/virtualLight1/value']")).
		when().post(pollUri);
		
		
		// Make multiple changes
		given().body("<bool val='false' />").put("/virtualLight1/value");
		given().body("<bool val='true' />").put("/virtualLight2/value");
		 
		given().param("Content-Type", "text/plain").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:WatchOut']")).
		body(Matchers.hasXPath("/obj/list/bool[@val='false' and @href='/virtualLight1/value']")).
		body(Matchers.hasXPath("/obj/list/bool[@val='true' and @href='/virtualLight2/value']")).
		when().post(pollUri);
	}
	
	@Test
	public void testWatchPollRefresh() {
		String watch = given().param("Content-Type", "text/plain").post("/watchService/make").asString();
		Node watchNode = XmlPath.from(watch).get("/obj");
		
		String addUri = watchNode.getAttribute("href") + "/add";
		String pollUri = watchNode.getAttribute("href") + "/pollRefresh";
		String pollChangesUri = watchNode.getAttribute("href") + "/pollChanges";
		
		System.out.println(
		given().param("data", "<obj is='obix:WatchIn'>"+
				 "	<list name='hrefs'>" +
				 "		<uri val='/virtualLight1/value' />" +
				 "		<uri val='/virtualLight2/value' />" +
				 "	</list>" +
				 "</obj>").post(addUri).asString());
		
		// Reset objects
		given().body("<bool val='false' />").put("/virtualLight1/value");
		given().body("<bool val='false' />").put("/virtualLight2/value");
		given().param("Content-Type", "text/plain").post(pollUri);
		
		// Report all 
		given().param("Content-Type", "text/plain").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:WatchOut']")).
		body(Matchers.hasXPath("/obj/list/bool[@href='/virtualLight1/value']")).
		body(Matchers.hasXPath("/obj/list/bool[@href='/virtualLight2/value']")).
		when().post(pollUri);
		
		// Make single change
		given().body("<bool val='true' />").put("/virtualLight1/value");
		// Report all 
		given().param("Content-Type", "text/plain").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:WatchOut']")).
		body(Matchers.hasXPath("/obj/list/bool[@href='/virtualLight1/value']")).
		body(Matchers.hasXPath("/obj/list/bool[@href='/virtualLight2/value']")).
		when().post(pollUri);
		
		// 
		given().param("Content-Type", "text/plain").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:WatchOut']")).
		body(Matchers.not(Matchers.hasXPath("/obj/list/bool"))).
		when().post(pollChangesUri);
	}
	
	
	@Test
	public void testHistoryObj() {
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:History']")).
		body(Matchers.hasXPath("/obj/int[@name='count']")).
		body(Matchers.hasXPath("/obj/abstime[@name='start']")).
		body(Matchers.hasXPath("/obj/abstime[@name='end']")).
		body(Matchers.hasXPath("/obj/op[@name='query' and @in='obix:HistoryFilter' and @out='obix:HistoryQueryOut']")).
		body(Matchers.hasXPath("/obj/op[@name='rollup' and @in='obix:HistoryRollupIn' and @out='obix:HistoryRollupOut']")).
		when().get("/virtualLight1/value/history");
	}
	
	
	@Test
	public void testHistoryQuery() {
		given().
		param("data", "<obj is='obix:HistoryFilter'>" + 
				"	<int name='limit' val='2'/>" +
				"	<abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Berlin'/>" +
				"	<abstime name='end' null='true' tz='Europe/Berlin'/>" +
				"</obj>").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:HistoryQueryOut']")).
		body(Matchers.hasXPath("/obj/int[@name='count' and @val='0']")).
		body(Matchers.hasXPath("/obj/abstime[@name='start']")).
		body(Matchers.hasXPath("/obj/abstime[@name='end']")).
		body(Matchers.hasXPath("/obj/list[@of='obix:HistoryRecord']")).
		post("/simBoiler/enabled/history/query");
		
		// some changes
		given().body("<bool val='false' />").put("/simBoiler/enabled");
		given().body("<bool val='true' />").put("/simBoiler/enabled");
		given().body("<bool val='false' />").put("/simBoiler/enabled");
		
		// check history
		given().
		param("data", "<obj is='obix:HistoryFilter'>" + 
				"	<int name='limit' val='2'/>" +
				"	<abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Berlin'/>" +
				"	<abstime name='end' null='true' tz='Europe/Berlin'/>" +
				"</obj>").
		expect().
		body(Matchers.hasXPath("/obj/int[@name='count' and @val='2']")).
		body(Matchers.hasXPath("/obj/list[@of='obix:HistoryRecord']")).
		body(Matchers.hasXPath("/obj/list/obj[1]/bool[@val='true']")).
		body(Matchers.hasXPath("/obj/list/obj[2]/bool[@val='false']")).
		post("/simBoiler/enabled/history/query");
	}
	
	@Test
	public void testHistoryRollup() {
		given().body("<int val='50' />").put("/simFanSpeedIn/fanSpeedSetpoint");
		given().body("<int val='100' />").put("/simFanSpeedIn/fanSpeedSetpoint");
		
		given().
		param("data", "<obj is='obix:HistoryRollupIn'> " +
				"  <int name='limit' val='2'/>" +
				"  <abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Berlin'/>" +
				"  <abstime name='end' null='true' />" +
				"  <reltime name='interval' val='PT15M'/>" +
				"</obj>").
		expect().
		body(Matchers.hasXPath("/obj[@is='obix:HistoryRollupOut']")).
		body(Matchers.hasXPath("/obj/int[@name='count']")).
		body(Matchers.hasXPath("/obj/abstime[@name='start']")).
		body(Matchers.hasXPath("/obj/abstime[@name='end']")).
		body(Matchers.hasXPath("/obj/list[@of='obix:HistoryRollupRecord']")).
		body(Matchers.hasXPath("/obj/list/obj/int[@name='count' and @val='2']")).
		body(Matchers.hasXPath("/obj/list/obj/abstime[@name='start']")).
		body(Matchers.hasXPath("/obj/list/obj/abstime[@name='end']")).
		body(Matchers.hasXPath("/obj/list/obj/real[@name='min' and @val='50.0']")).
		body(Matchers.hasXPath("/obj/list/obj/real[@name='max' and @val='100.0']")).
		body(Matchers.hasXPath("/obj/list/obj/real[@name='avg' and @val='75.0']")).
		body(Matchers.hasXPath("/obj/list/obj/real[@name='sum' and @val='150.0']")).
		post("/simFanSpeedIn/fanSpeedSetpoint/history/rollup");
	}
	
}
