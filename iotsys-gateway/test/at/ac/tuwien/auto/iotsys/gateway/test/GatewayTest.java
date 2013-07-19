package at.ac.tuwien.auto.iotsys.gateway.test;
import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.post;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import javax.xml.bind.DatatypeConverter;

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
		gateway.startGateway("config/devices.test.xml");
	}
	
	@AfterClass
	public static void tearDown() {
		gateway.stopGateway();
	}
	
	
	@Test
	public void testLobbyHasAbout() {
		expect().body(hasXPath("/obj/ref[@href='about']")).
		when().get("/obix");
	}
	
	@Test
	public void testAbout() {
		expect().
		body(hasXPath("/obj[@is='obix:About']")).
		
		body(hasXPath("/obj/str[@name='obixVersion']")).
		body(hasXPath("/obj/str[@name='serverName']")).
		body(hasXPath("/obj/abstime[@name='serverTime']")).
		body(hasXPath("/obj/abstime[@name='serverBootTime']")).
		
		body(hasXPath("/obj/str[@name='vendorName']")).
		body(hasXPath("/obj/uri[@name='vendorUrl']")).
		
		body(hasXPath("/obj/str[@name='productName']")).
		body(hasXPath("/obj/str[@name='productVersion']")).
		body(hasXPath("/obj/uri[@name='productUrl']")).
		
		when().get("/obix/about");
	}
	
	
	////////////////////////////////////////////////////////////////
	// REST interaction
	////////////////////////////////////////////////////////////////
	
	@Test
	public void testQuerySensor() {
		expect().body(hasXPath("//real[@name='roomIllumination']")).
		expect().body(hasXPath("//real[@href='roomIllumination']")).
		when().get("/brightnessSensor1");
	}
	
	@Test
	public void testWriteSensor() {
		given().body("<real val='42.0' />").
		expect().body(hasXPath("//real[@name='roomIllumination']")).
		expect().body(hasXPath("//real[@href='roomIllumination']")).
		expect().body(hasXPath("//real[@val!='42.0']")).
		when().put("/brightnessSensor1");
	}
	
	@Test
	public void testModifyValue() {
		given().body("<bool val='false' />").
		expect().body(hasXPath("/bool[@name='value' and @val='false']")).
		when().put("/switch1/value");
		
		given().body("<bool val='true' />").
		expect().body(hasXPath("/bool[@name='value' and @val='true']")).
		when().put("/switch1/value");
	}
	
	@Test
	public void testModifyObject() {
		given().body(  "<obj href='/virtualLight1' is='iot:LightSwitchActuator'>"
					 + "	<bool name='value' href='value' val='true'/>"
					 + "</obj>").
		expect().body(hasXPath("/obj[@href='/switch2']")).
		expect().body(hasXPath("/obj/bool[@name='value' and @val='true']")).
		when().put("/switch2");
		
		
		given().body(  "<obj href='/virtualLight1' is='iot:LightSwitchActuator'>"
				 + "	<bool name='value' href='value' val='false'/>"
				 + "</obj>").
		expect().body(hasXPath("/obj[@href='/switch2']")).
		expect().body(hasXPath("/obj/bool[@name='value' and @val='false']")).
		when().put("/switch2");
	}
		
	////////////////////////////////////////////////////////////////
	// Watches
	////////////////////////////////////////////////////////////////
	
	@Test
	public void testLobbyHasWatchService() {
		expect().body(hasXPath("/obj/ref[@href='/watchService' and @is='obix:WatchService']")).
		when().get("/obix");
	}
	
	private String makeWatch() {
		String watch = post("/watchService/make").asString();
		Node watchNode = XmlPath.from(watch).get("/obj");
		return watchNode.getAttribute("href");
	}
	
	@Test
	public void testMakeWatch() {
		expect().
		body(hasXPath("/obj[@is='obix:Watch']")).
		body(hasXPath("/obj/reltime[@name='lease']")).
		body(hasXPath("/obj/op[@name='add' and @in='obix:WatchIn' and @out='obix:WatchOut']")).
		body(hasXPath("/obj/op[@name='remove' and @in='obix:WatchIn']")).
		body(hasXPath("/obj/op[@name='pollChanges' and @out='obix:WatchOut']")).
		body(hasXPath("/obj/op[@name='pollRefresh' and @out='obix:WatchOut']")).
		body(hasXPath("/obj/op[@name='delete']")).
		when().post("/watchService/make");
	}
	
	@Test
	public void testWatchAddSingleObj() {
		String watchHref = makeWatch();
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/switchWatch1/value' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@val and @href='/switchWatch1/value']")).
		post(watchHref + "/add");
	}
	
	@Test
	public void testWatchAddMultipleObjs() {
		String watchHref = makeWatch();
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/switchWatch1/value' />" +
			 "		<uri val='/nonExistingObject/value' />" +
			 "		<uri val='/switchWatch2/value' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@href='/switchWatch1/value']")).
		body(hasXPath("/obj/list/bool[@href='/switchWatch2/value']")).
		body(hasXPath("/obj/list/err[@href='/nonExistingObject/value']")).
		post(watchHref + "/add");
	}
	
	@Test
	public void testWatchPollChanges() {
		String watchHref = makeWatch();
		
		String addUri = watchHref + "/add";
		String pollUri = watchHref + "/pollChanges";
		
		given().param("data", "<obj is='obix:WatchIn'>"+
				 "	<list name='hrefs'>" +
				 "		<uri val='/switchWatch1/value' />" +
				 "		<uri val='/switchWatch2/value' />" +
				 "	</list>" +
				 "</obj>").post(addUri);
		
		// No changes yet
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(not(hasXPath("/obj/list/bool"))).
		when().post(pollUri);
		
		
		// Reset objects
		given().body("<bool val='false' />").put("/switchWatch1/value");
		given().body("<bool val='false' />").put("/switchWatch2/value");
		given().param("Content-Type", "text/plain").post(pollUri);
		
		
		// Make single change
		given().body("<bool val='true' />").put("/switchWatch1/value");
		given().param("Content-Type", "text/plain").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@val='true' and @href='/switchWatch1/value']")).
		when().post(pollUri);
		
		
		// Make multiple changes
		given().body("<bool val='false' />").put("/switchWatch1/value");
		given().body("<bool val='true' />").put("/switchWatch2/value");
		 
		given().param("Content-Type", "text/plain").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@val='false' and @href='/switchWatch1/value']")).
		body(hasXPath("/obj/list/bool[@val='true' and @href='/switchWatch2/value']")).
		when().post(pollUri);
	}
	
	@Test
	public void testWatchPollRefresh() {
		String watchHref = makeWatch();
		
		String addUri = watchHref + "/add";
		String pollUri = watchHref + "/pollRefresh";
		String pollChangesUri = watchHref + "/pollChanges";
		
		given().param("data", "<obj is='obix:WatchIn'>"+
				 "	<list name='hrefs'>" +
				 "		<uri val='/switchWatch1/value' />" +
				 "		<uri val='/switchWatch2/value' />" +
				 "	</list>" +
				 "</obj>").post(addUri);
		
		// Reset objects
		given().body("<bool val='false' />").put("/switchWatch1/value");
		given().body("<bool val='false' />").put("/switchWatch2/value");
		given().param("Content-Type", "text/plain").post(pollUri);
		
		// Report all 
		given().param("Content-Type", "text/plain").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@href='/switchWatch1/value']")).
		body(hasXPath("/obj/list/bool[@href='/switchWatch2/value']")).
		when().post(pollUri);
		
		// Make single change
		given().body("<bool val='true' />").put("/switchWatch1/value");
		// Report all 
		given().param("Content-Type", "text/plain").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@href='/switchWatch1/value']")).
		body(hasXPath("/obj/list/bool[@href='/switchWatch2/value']")).
		when().post(pollUri);
		
		// 
		given().param("Content-Type", "text/plain").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(not(hasXPath("/obj/list/bool"))).
		when().post(pollChangesUri);
	}
	
	@Test
	public void testWatchWriteLease() {
		String watchHref = makeWatch();
		
		given().body("<reltime href='lease' val='PT42S' />").
		expect().body(hasXPath("//reltime[@name='lease' and @val='PT42S']")).
		when().put(watchHref);
	}
	
	@Test
	public void testWatchLeaseExpiration() throws InterruptedException {
		String watchHref = makeWatch();
		
		given().body("<reltime href='lease' val='PT1S' />").
		expect().body(hasXPath("//reltime[@name='lease' and @val='PT1S']")).
		when().put(watchHref);
		
		// still accessible
		expect().body(hasXPath("/obj")).when().get(watchHref);
		
		Thread.sleep(1200);
		
		// watch expired
		expect().body(hasXPath("/err")).when().get(watchHref);
	}
	
	@Test
	public void testWatchRemove() {
		String watchHref = makeWatch();
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/switchWatch1/value' />" +
			 "		<uri val='/brightnessSensor1' />" +
			 "		<uri val='/switchWatch2/value' />" +
			 "	</list>" +
			 "</obj>").
		post(watchHref + "/add");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/switchWatch1/value' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:Nil']")).
		post(watchHref + "/remove");
		
		expect().
		body(not(hasXPath("//bool[@href='/switchWatch1/value']"))).
		body(hasXPath("//bool[@href='/switchWatch2/value']")).
		body(hasXPath("//obj[@href='/brightnessSensor1']")).
		post(watchHref + "/pollRefresh");
	}
	
	@Test
	public void testWatchDelete() {
		String watchHref = makeWatch();
		
		expect().body(hasXPath("/obj[@is='obix:Nil']")).
		post(watchHref + "/delete");
		
		expect().body(hasXPath("/err")).when().get(watchHref);
	}

	////////////////////////////////////////////////////////////////
	// Watching History Feed
	////////////////////////////////////////////////////////////////

	
	@Test
	public void testWatchAddFeed() {
		String watchHref = makeWatch();
		
		// initial data for feed
		given().body("<int val='1' />").put("/fanSpeedWatchAdd/fanSpeedSetpoint");
		given().body("<int val='2' />").put("/fanSpeedWatchAdd/fanSpeedSetpoint");
		given().body("<int val='3' />").put("/fanSpeedWatchAdd/fanSpeedSetpoint");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/fanSpeedWatchAdd/fanSpeedSetpoint/history/feed' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/feed[@href='/fanSpeedWatchAdd/fanSpeedSetpoint/history/feed' and @of='obix:HistoryRecord']")).
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		body(hasXPath("/obj/list/feed/obj/int[@val='1']")).
		body(hasXPath("/obj/list/feed/obj/int[@val='2']")).
		body(hasXPath("/obj/list/feed/obj/int[@val='3']")).
		post(watchHref + "/add");
	}
	
	@Test
	public void testWatchFeedPollRefresh() {
		String watchHref = makeWatch();
		String pollUri = watchHref + "/pollRefresh";
		String pollChangesUri = watchHref + "/pollChanges";
		
		// initial data for feed
		given().body("<int val='1' />").put("/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		given().body("<int val='2' />").put("/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		given().body("<int val='3' />").put("/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/fanSpeedWatchPollRefresh/fanSpeedSetpoint/history/feed' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/feed[@href='/fanSpeedWatchPollRefresh/fanSpeedSetpoint/history/feed' and @of='obix:HistoryRecord']")).
		post(watchHref + "/add");
		
		// additional data for feed
		given().body("<int val='4' />").put("/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		given().body("<int val='5' />").put("/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		given().body("<int val='6' />").put("/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		
		
		// pollRefresh returns all events
		expect().
		body(hasXPath("/obj/list/feed[count(obj) = 6]")).
		body(hasXPath("/obj/list/feed/obj/int[@val='1']")).
		body(hasXPath("/obj/list/feed/obj/int[@val='2']")).
		body(hasXPath("/obj/list/feed/obj/int[@val='3']")).
		body(hasXPath("/obj/list/feed/obj/int[@val='4']")).
		body(hasXPath("/obj/list/feed/obj/int[@val='5']")).
		body(hasXPath("/obj/list/feed/obj/int[@val='6']")).
		post(pollUri);
		
		// pollChanges has no unpolled events
		expect().
		body(not(hasXPath("/obj/list/feed"))).
		post(pollChangesUri);
		
		// pollRefresh returns all events
		expect().
		body(hasXPath("/obj/list/feed[count(obj) = 6]")).
		post(pollUri);
	}
	
	@Test
	public void testWatchFeedPollChanges() throws InterruptedException {
		String watchHref = makeWatch();
		String addUri = watchHref + "/add";
		String pollChangesUri = watchHref + "/pollChanges";
		
		// initial data for feed
		given().body("<int val='1' />").put("/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		given().body("<int val='2' />").put("/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		given().body("<int val='3' />").put("/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/fanSpeedWatchPollChanges/fanSpeedSetpoint/history/feed' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/feed[@href='/fanSpeedWatchPollChanges/fanSpeedSetpoint/history/feed' and @of='obix:HistoryRecord']")).
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		post(addUri);
		
		// pollChanges has no new events
		expect().
		body(not(hasXPath("/obj/list/feed"))).
		post(pollChangesUri);
		
		// additional data for feed
		given().body("<int val='4' />").put("/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		given().body("<int val='5' />").put("/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		given().body("<int val='6' />").put("/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		
		expect().
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		body(hasXPath("/obj/list/feed/obj/int[@val='4']")).
		body(hasXPath("/obj/list/feed/obj/int[@val='5']")).
		body(hasXPath("/obj/list/feed/obj/int[@val='6']")).
		post(pollChangesUri);
		
		// pollChanges has no new events
		expect().
		body(not(hasXPath("/obj/list/feed"))).
		post(pollChangesUri);
	}
	
	@Test
	public void testWatchFilteredFeed() throws InterruptedException {
		String watchHref = makeWatch();
		String addUri = watchHref + "/add";
		String pollRefreshUri = watchHref + "/pollRefresh";
		String pollChangesUri = watchHref + "/pollChanges";
		
		// initial data for feed
		given().body("<int val='1' />").put("/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='2' />").put("/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='3' />").put("/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='4' />").put("/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='5' />").put("/fanSpeedWatchFilter/fanSpeedSetpoint");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/fanSpeedWatchFilter/fanSpeedSetpoint/history/feed'>" +
			 "			<obj is='obix:HistoryFilter'>" +
			 "				<int name='limit' val='3' />" +
			 "			</obj>" +
			 "		</uri>" + 
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/feed[@href='/fanSpeedWatchFilter/fanSpeedSetpoint/history/feed' and @of='obix:HistoryRecord']")).
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		post(addUri);
		
		// pollChanges has no new events
		expect().
		body(not(hasXPath("/obj/list/feed"))).
		post(pollChangesUri);
		
		expect().
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		post(pollRefreshUri);
		
		// additional data for feed
		given().body("<int val='6' />").put("/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='7' />").put("/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='8' />").put("/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='9' />").put("/fanSpeedWatchFilter/fanSpeedSetpoint");
		
		expect().
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		post(pollChangesUri);
	}
	
	@Test
	public void testWatchExtent() throws InterruptedException {
		String watchHref = makeWatch();
		String addUri = watchHref + "/add";
		String pollChangesUri = watchHref + "/pollChanges";
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/fanSpeedWatchExtent'/>" + 
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/obj[@href='/fanSpeedWatchExtent']")).
		post(addUri);
		
		given().body("<int val='42' />").put("/fanSpeedWatchExtent/fanSpeedSetpoint");
		
		expect().
		body(hasXPath("/obj/list/obj[@href='/fanSpeedWatchExtent']")).
		body(hasXPath("/obj/list/obj/int[@val=42]")).
		post(pollChangesUri);
	}
	
	
	////////////////////////////////////////////////////////////////
	// History
	////////////////////////////////////////////////////////////////
	
	@Test
	public void testHistoryObj() {
		expect().
		body(hasXPath("/obj[@is='obix:History']")).
		body(hasXPath("/obj/int[@name='count']")).
		body(hasXPath("/obj/abstime[@name='start']")).
		body(hasXPath("/obj/abstime[@name='end']")).
		body(hasXPath("/obj/str[@name='tz']")).
		body(hasXPath("/obj/op[@name='query' and @in='obix:HistoryFilter' and @out='obix:HistoryQueryOut']")).
		body(hasXPath("/obj/feed[@name='feed' and @in='obix:HistoryFilter' and @of='obix:HistoryRecord']")).
		body(hasXPath("/obj/op[@name='rollup' and @in='obix:HistoryRollupIn' and @out='obix:HistoryRollupOut']")).
		body(hasXPath("/obj/op[@name='append' and @in='obix:HistoryAppendIn' and @out='obix:HistoryAppendOut']")).
		when().get("/switchHistory1/value/history");
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
		body(hasXPath("/obj[@is='obix:HistoryQueryOut']")).
		body(hasXPath("/obj/int[@name='count' and @val='0']")).
		body(hasXPath("/obj/abstime[@name='start' and @null='true']")).
		body(hasXPath("/obj/abstime[@name='end' and @null='true']")).
		body(hasXPath("/obj/list[@of='obix:HistoryRecord']")).
		post("/switchHistory1/value/history/query");
		
		// some changes
		given().body("<bool val='false' />").put("/switchHistory1/value");
		given().body("<bool val='true' />").put("/switchHistory1/value");
		given().body("<bool val='false' />").put("/switchHistory1/value");
		
		// check history
		given().
		param("data", "<obj is='obix:HistoryFilter'>" + 
				"	<int name='limit' val='2'/>" +
				"	<abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Berlin'/>" +
				"	<abstime name='end' null='true' tz='Europe/Berlin'/>" +
				"</obj>").
		expect().
		body(hasXPath("/obj/int[@name='count' and @val='2']")).
		body(hasXPath("/obj/abstime[@name='start' and not(@null)]")).
		body(hasXPath("/obj/abstime[@name='end' and not(@null)]")).
		body(hasXPath("/obj/list[@of='obix:HistoryRecord']")).
		body(hasXPath("/obj/list/obj[1]/bool[@val='true']")).
		body(hasXPath("/obj/list/obj[2]/bool[@val='false']")).
		post("/switchHistory1/value/history/query");
	}
	
	
	@Test
	public void testHistoryAppend() {
		String response;
		
		response = 
			given().param("data", "<obj is='obix:HistoryAppendIn'>" +
				"	<list name='data' of='obix:HistoryRecord'>" +
				"		<obj>" +
				"			<abstime name='timestamp' val='2013-07-10T10:15:00-05:00'/>" +
				"			<int name='value' val='2'/>" +
				"		</obj>" +
				"		<obj>" +
				"			<abstime name='timestamp' val='2013-07-10T10:30:00-05:00'/>" +
				"			<int val='3'/>" +
				"		</obj>" +
				"	</list>" +
				"</obj>").
			expect().
			body(hasXPath("/obj[@is='obix:HistoryAppendOut']")).
			body(hasXPath("/obj/int[@name='numAdded' and @val='2']")).
			body(hasXPath("/obj/int[@name='newCount' and @val='2']")).
			post("/brightnessHistory2/value/history/append").asString();
		
		long start = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime[0].@val")).getTimeInMillis();
		long   end = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime[1].@val")).getTimeInMillis();
		
		assertEquals(start, DatatypeConverter.parseDateTime("2013-07-10T10:15:00-05:00").getTimeInMillis());
		assertEquals(  end, DatatypeConverter.parseDateTime("2013-07-10T10:30:00-05:00").getTimeInMillis());
		
		
		
		response = 
			given().param("data", "<obj is='obix:HistoryAppendIn'>" +
				"	<list name='data' of='obix:HistoryRecord'>" +
				"		<obj>" +
				"			<abstime name='timestamp' val='2013-07-10T10:45:00-05:00'/>" +
				"			<int name='value' val='4'/>" +
				"		</obj>" +
				"	</list>" +
				"</obj>").
			expect().
			body(hasXPath("/obj[@is='obix:HistoryAppendOut']")).
			body(hasXPath("/obj/int[@name='numAdded' and @val='1']")).
			body(hasXPath("/obj/int[@name='newCount' and @val='3']")).
			post("/brightnessHistory2/value/history/append").asString();
		
		
		end = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime[1].@val")).getTimeInMillis();
		
		assertEquals(start, DatatypeConverter.parseDateTime("2013-07-10T10:15:00-05:00").getTimeInMillis());
		assertEquals(  end, DatatypeConverter.parseDateTime("2013-07-10T10:45:00-05:00").getTimeInMillis());
	}

	@Test
	public void testHistoryAppendOutOfOrderShouldFail() {
		given().param("data", "<obj is='obix:HistoryAppendIn'>" +
			"	<list name='data' of='obix:HistoryRecord'>" +
			"		<obj>" +
			"			<abstime name='timestamp' val='2013-07-10T10:30:00-05:00'/>" +
			"			<int val='3'/>" +
			"		</obj>" +
			"		<obj>" +
			"			<abstime name='timestamp' val='2013-07-10T10:15:00-05:00'/>" +
			"			<int name='value' val='2'/>" +
			"		</obj>" +
			"	</list>" +
			"</obj>").
		expect().
		body(hasXPath("/err")).
		post("/brightnessHistoryAppendOutOfOrder/value/history/append");
	}

	@Test
	public void testHistoryAppendBeforeLastShouldFail() {
		given().param("data", "<obj is='obix:HistoryAppendIn'>" +
			"	<list name='data' of='obix:HistoryRecord'>" +
			"		<obj>" +
			"			<abstime name='timestamp' val='2013-07-10T10:15:00-05:00'/>" +
			"			<int name='value' val='2'/>" +
			"		</obj>" +
			"	</list>" +
			"</obj>").
		expect().
		body(not(hasXPath("/err"))).
		post("/brightnessHistory3/value/history/append");
		
		given().param("data", "<obj is='obix:HistoryAppendIn'>" +
			"	<list name='data' of='obix:HistoryRecord'>" +
			"		<obj>" +
			"			<abstime name='timestamp' val='2013-07-05T10:00:00-05:00'/>" +
			"			<int name='value' val='1'/>" +
			"		</obj>" +
			"	</list>" +
			"</obj>").
		expect().
		body(hasXPath("/err")).
		post("/brightnessHistory3/value/history/append");
	}
	
	@Test
	public void testHistoryAppendNothing() {
		given().param("data", "<obj is='obix:HistoryAppendIn'>" +
			"	<list name='data' of='obix:HistoryRecord' />" +
			"</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:HistoryAppendOut']")).
		body(hasXPath("/obj/int[@name='numAdded' and @val='0']")).
		body(hasXPath("/obj/int[@name='newCount' and @val='0']")).
		body(hasXPath("/obj/abstime[@name='newStart' and @null='true']")).
		body(hasXPath("/obj/abstime[@name='newEnd' and @null='true']")).
		post("/brightnessHistoryAppendNothing/value/history/append");
		
		
		given().param("data", "<obj is='obix:HistoryAppendIn'>" +
			"	<list name='data' of='obix:HistoryRecord'>" +
			"		<obj>" +
			"			<abstime name='timestamp' val='2013-07-05T10:15:00+01:00'/>" +
			"			<int name='value' val='1'/>" +
			"		</obj>" +
			"	</list>" +
			"</obj>").
		expect().
		body(not(hasXPath("/err"))).
		post("/brightnessHistoryAppendNothing/value/history/append");
		
		given().param("data", "<obj is='obix:HistoryAppendIn'>" +
			"	<list name='data' of='obix:HistoryRecord' />" +
			"</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:HistoryAppendOut']")).
		body(hasXPath("/obj/int[@name='numAdded' and @val='0']")).
		body(hasXPath("/obj/int[@name='newCount' and @val='1']")).
		body(hasXPath("/obj/abstime[@name='newStart' and not(@null)]")).
		body(hasXPath("/obj/abstime[@name='newEnd' and not(@null)]")).
		post("/brightnessHistoryAppendNothing/value/history/append");
	}
	
	@Test
	public void testHistoryStartAndEnd() {
		expect().
		body(hasXPath("/obj[@is='obix:History']")).
		body(hasXPath("/obj/int[@name='count' and @val=0]")).
		body(hasXPath("/obj/abstime[@name='start' and @null='true']")).
		body(hasXPath("/obj/abstime[@name='end' and @null='true']")).
		when().get("/brightnessHistoryStartEnd/value/history");
		
		
		given().param("data", "<obj is='obix:HistoryAppendIn'>" +
			"	<list name='data' of='obix:HistoryRecord'>" +
			"		<obj>" +
			"			<abstime name='timestamp' val='2013-07-10T10:15:00+01:00'/>" +
			"			<int name='value' val='2'/>" +
			"		</obj>" +
			"	</list>" +
			"</obj>").
		expect().
		body(not(hasXPath("/err"))).
		post("/brightnessHistoryStartEnd/value/history/append");
		
		
		String response = expect().
			body(hasXPath("/obj[@is='obix:History']")).
			body(hasXPath("/obj/int[@name='count' and @val=1]")).
			body(not(hasXPath("/obj/abstime[@name='start'][@null]"))).
			body(not(hasXPath("/obj/abstime[@name='end'][@null]"))).
			when().get("/brightnessHistoryStartEnd/value/history").asString();
		
		long start = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime[0].@val")).getTimeInMillis();
		long end   = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime[1].@val")).getTimeInMillis();
		
		assertEquals(start, DatatypeConverter.parseDateTime("2013-07-10T10:15:00+01:00").getTimeInMillis());
		assertEquals(  end, DatatypeConverter.parseDateTime("2013-07-10T10:15:00+01:00").getTimeInMillis());
		
		given().param("data", "<obj is='obix:HistoryAppendIn'>" +
			"	<list name='data' of='obix:HistoryRecord'>" +
			"		<obj>" +
			"			<abstime name='timestamp' val='2013-07-10T10:45:00+01:00'/>" +
			"			<int name='value' val='5'/>" +
			"		</obj>" +
			"	</list>" +
			"</obj>").
		expect().
		body(not(hasXPath("/err"))).
		post("/brightnessHistoryStartEnd/value/history/append");
		
		response = expect().
			body(hasXPath("/obj[@is='obix:History']")).
			body(hasXPath("/obj/int[@name='count' and @val=2]")).
			body(not(hasXPath("/obj/abstime[@name='start' and @null]"))).
			body(not(hasXPath("/obj/abstime[@name='end' and @null]"))).
			when().get("/brightnessHistoryStartEnd/value/history").asString();
		
		start = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime[0].@val")).getTimeInMillis();
		end   = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime[1].@val")).getTimeInMillis();
		
		assertEquals(start, DatatypeConverter.parseDateTime("2013-07-10T10:15:00+01:00").getTimeInMillis());
		assertEquals(  end, DatatypeConverter.parseDateTime("2013-07-10T10:45:00+01:00").getTimeInMillis());
	}

	@Test
	public void testHistoryRollup() {
		given().body("<int val='50' />").put("/brightnessHistoryRollup/value");
		given().body("<int val='100' />").put("/brightnessHistoryRollup/value");
		
		given().
		param("data", "<obj is='obix:HistoryRollupIn'> " +
				"  <int name='limit' val='2'/>" +
				"  <abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Berlin'/>" +
				"  <abstime name='end' null='true' />" +
				"  <reltime name='interval' val='PT15M'/>" +
				"</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:HistoryRollupOut']")).
		body(hasXPath("/obj/int[@name='count']")).
		body(hasXPath("/obj/abstime[@name='start' and not(@null)]")).
		body(hasXPath("/obj/abstime[@name='end' and not(@null)]")).
		body(hasXPath("/obj/list[@of='obix:HistoryRollupRecord']")).
		body(hasXPath("/obj/list/obj/abstime[@name='start']")).
		body(hasXPath("/obj/list/obj/abstime[@name='end']")).
		body(hasXPath("/obj/list/obj/int[@name='count' and @val='2']")).
		body(hasXPath("/obj/list/obj/real[@name='min' and @val='50.0']")).
		body(hasXPath("/obj/list/obj/real[@name='max' and @val='100.0']")).
		body(hasXPath("/obj/list/obj/real[@name='avg' and @val='75.0']")).
		body(hasXPath("/obj/list/obj/real[@name='sum' and @val='150.0']")).
		post("/brightnessHistoryRollup/value/history/rollup");
	}
	
	
	@Test
	public void testHistoryRollupBoolShouldFail() {
		// Attempting to query a rollup on a non-numeric history such as a history of BoolPoints SHOULD result in an error.
		given().
		param("data", "<obj is='obix:HistoryRollupIn'> " +
				"  <int name='limit' val='2'/>" +
				"  <abstime name='start' val='2013-03-31T15:30:00+02:00' tz='Europe/Vienna'/>" +
				"  <reltime name='interval' val='PT15M'/>" +
				"</obj>").
		expect().
		body(hasXPath("/err")).
		post("/switch1/value/history/rollup");
	}
	
	@Test
	public void testHistoryRollupMultipleRecords() {
		given().param("data", "<obj is='obix:HistoryAppendIn'>" +
				"	<list name='data' of='obix:HistoryRecord'>" +
				"		<obj> <abstime name='timestamp' val='2005-03-16T12:00:00Z'/>" + 
				"		<real name='value' val='80'/></obj>" + 
				"		<obj> <abstime name='timestamp' val='2005-03-16T12:15:00Z'/>" + 
				"		<real name='value' val='82'/></obj>" + 
				"		<obj> <abstime name='timestamp' val='2005-03-16T12:30:00Z'/>" + 
				"		<real name='value' val='90'/> </obj>" + 
				"		<obj> <abstime name='timestamp' val='2005-03-16T12:45:00Z'/>" + 
				"		<real name='value' val='85'/> </obj>" + 
				"		<obj> <abstime name='timestamp' val='2005-03-16T13:00:00Z'/>" + 
				"		<real name='value' val='81'/> </obj>" + 
				"		<obj> <abstime name='timestamp' val='2005-03-16T13:15:00Z'/>" + 
				"		<real name='value' val='84'/> </obj>" + 
				"		<obj> <abstime name='timestamp' val='2005-03-16T13:30:00Z'/>" + 
				"		<real name='value' val='91'/> </obj>" + 
				"		<obj> <abstime name='timestamp' val='2005-03-16T13:45:00Z'/>" + 
				"		<real name='value' val='83'/> </obj>" + 
				"		<obj> <abstime name='timestamp' val='2005-03-16T14:00:00Z'/>" + 
				"		<real name='value' val='78'/> </obj>" +
				"	</list>" +
				"</obj>").
		expect().
		body(not(hasXPath("/err"))).
		post("/tempHistory/value/history/append");
		
		String response = 
			given().
			param("data", "<obj is='obix:HistoryRollupIn'> " +
					"  <abstime name='start' val='2005-03-16T12:00:00Z'/>" +
					"  <abstime name='end' val='2005-03-16T14:00:00Z' />" +
					"  <reltime name='interval' val='PT1H'/>" +
					"</obj>").
			expect().
			body(hasXPath("/obj[@is='obix:HistoryRollupOut']")).
			body(hasXPath("/obj/int[@name='count' and @val='2']")).
			body(hasXPath("/obj/abstime[@name='start']")).
			body(hasXPath("/obj/abstime[@name='end']")).
			
			body(hasXPath("/obj/list[@of='obix:HistoryRollupRecord']")).
			body(hasXPath("/obj/list/obj[1]/abstime[@name='start']")).
			body(hasXPath("/obj/list/obj[1]/abstime[@name='end']")).
			body(hasXPath("/obj/list/obj[1]/int[@name='count' and @val=4]")).
			body(hasXPath("/obj/list/obj[1]/real[@name='min' and @val=81]")).
			body(hasXPath("/obj/list/obj[1]/real[@name='max' and @val=90]")).
			body(hasXPath("/obj/list/obj[1]/real[@name='avg' and @val=84.5]")).
			body(hasXPath("/obj/list/obj[1]/real[@name='sum' and @val=338]")).
			
			body(hasXPath("/obj/list/obj[2]/abstime[@name='start']")).
			body(hasXPath("/obj/list/obj[2]/abstime[@name='end']")).
			body(hasXPath("/obj/list/obj[2]/int[@name='count' and @val=4]")).
			body(hasXPath("/obj/list/obj[2]/real[@name='min' and @val=78]")).
			body(hasXPath("/obj/list/obj[2]/real[@name='max' and @val=91]")).
			body(hasXPath("/obj/list/obj[2]/real[@name='avg' and @val=84]")).
			body(hasXPath("/obj/list/obj[2]/real[@name='sum' and @val=336]")).
			post("/tempHistory/value/history/rollup").asString();
		
		// Check timestamps
		long timestamp;
		
		timestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime[0].@val")).getTimeInMillis();
		assertEquals(timestamp, DatatypeConverter.parseDateTime("2005-03-16T12:00:00Z").getTimeInMillis());
		
		timestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.abstime[1].@val")).getTimeInMillis();
		assertEquals(timestamp, DatatypeConverter.parseDateTime("2005-03-16T14:00:00Z").getTimeInMillis());
		
		timestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.list.obj[0].abstime[0].@val")).getTimeInMillis();
		assertEquals(timestamp, DatatypeConverter.parseDateTime("2005-03-16T12:00:00Z").getTimeInMillis());
		
		timestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.list.obj[0].abstime[1].@val")).getTimeInMillis();
		assertEquals(timestamp, DatatypeConverter.parseDateTime("2005-03-16T13:00:00Z").getTimeInMillis());
		
		timestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.list.obj[1].abstime[0].@val")).getTimeInMillis();
		assertEquals(timestamp, DatatypeConverter.parseDateTime("2005-03-16T13:00:00Z").getTimeInMillis());
		
		timestamp = DatatypeConverter.parseDateTime(XmlPath.from(response).getString("obj.list.obj[1].abstime[1].@val")).getTimeInMillis();
		assertEquals(timestamp, DatatypeConverter.parseDateTime("2005-03-16T14:00:00Z").getTimeInMillis());
	}
	
	@Test
	public void testHistoryTimezones() {
		String history = expect().
		body(hasXPath("/obj[@is='obix:History']")).
		body(hasXPath("/obj/str[@name='tz']")).
		get("/HistoryTimezones/value/history").asString();
		
		String timezone = XmlPath.from(history).getString("obj.str.@val");
		
		// query and rollup have to use same timezone
		given().body("<int val='123' />").put("/HistoryTimezones/value");
		
		given().
		param("data", "<obj is='obix:HistoryFilter'>" + 
				"	<abstime name='start' val='2013-03-31T15:30:00-07:00' tz='America/Phoenix'/>" +
				"	<abstime name='end' null='true' />" +
				"</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:HistoryQueryOut']")).
		body(hasXPath("/obj/abstime[@name='start' and @tz='" + timezone + "']")).
		body(hasXPath("/obj/abstime[@name='end' and @tz='" + timezone + "']")).
		post("/HistoryTimezones/value/history/query");
		
		
		given().
		param("data", "<obj is='obix:HistoryRollupIn'> " +
				"  <int name='limit' val='2'/>" +
				"  <abstime name='start' val='2013-03-31T12:30:00+09:00' tz='Japan'/>" +
				"  <abstime name='end' null='true' />" +
				"  <reltime name='interval' val='PT2H'/>" +
				"</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:HistoryRollupOut']")).
		body(hasXPath("/obj/abstime[@name='start' and @tz='" + timezone + "']")).
		body(hasXPath("/obj/abstime[@name='end' and @tz='" + timezone + "']")).
		post("/HistoryTimezones/value/history/rollup");
	}
}
