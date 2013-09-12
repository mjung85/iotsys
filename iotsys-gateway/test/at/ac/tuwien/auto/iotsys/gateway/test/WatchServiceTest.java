package at.ac.tuwien.auto.iotsys.gateway.test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.post;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.element.Node;

public class WatchServiceTest extends AbstractGatewayTest {
	@Test
	public void testLobbyHasWatchService() {
		expect().body(hasXPath("/obj/ref[@href='watchService' and @is='obix:WatchService']")).
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
			 "		<uri val='/testDevicesWatch/switchWatch1/value' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@val and @href='/testDevicesWatch/switchWatch1/value']")).
		post(watchHref + "/add");
	}
	
	@Test
	public void testWatchAddMultipleObjs() {
		String watchHref = makeWatch();
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/testDevicesWatch/switchWatch1/value' />" +
			 "		<uri val='/testDevicesWatch/nonExistingObject/value' />" +
			 "		<uri val='/testDevicesWatch/switchWatch2/value' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@href='/testDevicesWatch/switchWatch1/value']")).
		body(hasXPath("/obj/list/bool[@href='/testDevicesWatch/switchWatch2/value']")).
		body(hasXPath("/obj/list/err[@href='/testDevicesWatch/nonExistingObject/value']")).
		post(watchHref + "/add");
	}
	
	@Test
	public void testWatchPollChanges() {
		String watchHref = makeWatch();
		
		String addUri = watchHref + "/add";
		String pollUri = watchHref + "/pollChanges";
		
		given().param("data", "<obj is='obix:WatchIn'>"+
				 "	<list name='hrefs'>" +
				 "		<uri val='/testDevicesWatch/switchWatch1/value' />" +
				 "		<uri val='/testDevicesWatch/switchWatch2/value' />" +
				 "	</list>" +
				 "</obj>").post(addUri);
		
		// No changes yet
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(not(hasXPath("/obj/list/bool"))).
		when().post(pollUri);
		
		
		// Reset objects
		given().body("<bool val='false' />").put("/testDevicesWatch/switchWatch1/value");
		given().body("<bool val='false' />").put("/testDevicesWatch/switchWatch2/value");
		given().param("Content-Type", "text/plain").post(pollUri);
		
		
		// Make single change
		given().body("<bool val='true' />").put("/testDevicesWatch/switchWatch1/value");
		given().param("Content-Type", "text/plain").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@val='true' and @href='/testDevicesWatch/switchWatch1/value']")).
		when().post(pollUri);
		
		
		// Make multiple changes
		given().body("<bool val='false' />").put("/testDevicesWatch/switchWatch1/value");
		given().body("<bool val='true' />").put("/testDevicesWatch/switchWatch2/value");
		 
		given().param("Content-Type", "text/plain").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@val='false' and @href='/testDevicesWatch/switchWatch1/value']")).
		body(hasXPath("/obj/list/bool[@val='true' and @href='/testDevicesWatch/switchWatch2/value']")).
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
				 "		<uri val='/testDevicesWatch/switchWatch1/value' />" +
				 "		<uri val='/testDevicesWatch/switchWatch2/value' />" +
				 "	</list>" +
				 "</obj>").post(addUri);
		
		// Reset objects
		given().body("<bool val='false' />").put("/testDevicesWatch/switchWatch1/value");
		given().body("<bool val='false' />").put("/testDevicesWatch/switchWatch2/value");
		given().param("Content-Type", "text/plain").post(pollUri);
		
		// Report all 
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@href='/testDevicesWatch/switchWatch1/value']")).
		body(hasXPath("/obj/list/bool[@href='/testDevicesWatch/switchWatch2/value']")).
		when().post(pollUri);
		
		// Make single change
		given().body("<bool val='true' />").put("/testDevicesWatch/switchWatch1/value");
		
		// Report all 
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/bool[@href='/testDevicesWatch/switchWatch1/value']")).
		body(hasXPath("/obj/list/bool[@href='/testDevicesWatch/switchWatch2/value']")).
		when().post(pollUri);
		
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
			 "		<uri val='/testDevicesWatch/switchWatch1/value' />" +
			 "		<uri val='/testDevices/brightnessSensor1' />" +
			 "		<uri val='/testDevicesWatch/switchWatch2/value' />" +
			 "	</list>" +
			 "</obj>").
		post(watchHref + "/add");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/testDevicesWatch/switchWatch1/value' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:Nil']")).
		post(watchHref + "/remove");
		
		expect().
		body(not(hasXPath("//bool[@href='/testDevicesWatch/switchWatch1/value']"))).
		body(hasXPath("//bool[@href='/testDevicesWatch/switchWatch2/value']")).
		body(hasXPath("//obj[@href='/testDevices/brightnessSensor1']")).
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
		given().body("<int val='1' />").put("/testDevicesWatch/fanSpeedWatchAdd/fanSpeedSetpoint");
		given().body("<int val='2' />").put("/testDevicesWatch/fanSpeedWatchAdd/fanSpeedSetpoint");
		given().body("<int val='3' />").put("/testDevicesWatch/fanSpeedWatchAdd/fanSpeedSetpoint");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/testDevicesWatch/fanSpeedWatchAdd/fanSpeedSetpoint/history/feed' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/feed[@href='/testDevicesWatch/fanSpeedWatchAdd/fanSpeedSetpoint/history/feed' and @of='obix:HistoryRecord']")).
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val='3']")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val='2']")).
		body(hasXPath("/obj/list/feed/obj[3]/int[@val='1']")).
		post(watchHref + "/add");
	}
	
	@Test
	public void testWatchFeedPollRefresh() {
		String watchHref = makeWatch();
		String pollUri = watchHref + "/pollRefresh";
		String pollChangesUri = watchHref + "/pollChanges";
		
		// initial data for feed
		given().body("<int val='1' />").put("/testDevicesWatch/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		given().body("<int val='2' />").put("/testDevicesWatch/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		given().body("<int val='3' />").put("/testDevicesWatch/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/testDevicesWatch/fanSpeedWatchPollRefresh/fanSpeedSetpoint/history/feed' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/feed[@href='/testDevicesWatch/fanSpeedWatchPollRefresh/fanSpeedSetpoint/history/feed' and @of='obix:HistoryRecord']")).
		post(watchHref + "/add");
		
		// additional data for feed
		given().body("<int val='4' />").put("/testDevicesWatch/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		given().body("<int val='5' />").put("/testDevicesWatch/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		given().body("<int val='6' />").put("/testDevicesWatch/fanSpeedWatchPollRefresh/fanSpeedSetpoint");
		
		
		// pollRefresh returns all events
		expect().
		body(hasXPath("/obj/list/feed[count(obj) = 6]")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val='6']")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val='5']")).
		body(hasXPath("/obj/list/feed/obj[3]/int[@val='4']")).
		body(hasXPath("/obj/list/feed/obj[4]/int[@val='3']")).
		body(hasXPath("/obj/list/feed/obj[5]/int[@val='2']")).
		body(hasXPath("/obj/list/feed/obj[6]/int[@val='1']")).
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
		given().body("<int val='1' />").put("/testDevicesWatch/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		given().body("<int val='2' />").put("/testDevicesWatch/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		given().body("<int val='3' />").put("/testDevicesWatch/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/testDevicesWatch/fanSpeedWatchPollChanges/fanSpeedSetpoint/history/feed' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/feed[@href='/testDevicesWatch/fanSpeedWatchPollChanges/fanSpeedSetpoint/history/feed' and @of='obix:HistoryRecord']")).
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val='3']")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val='2']")).
		body(hasXPath("/obj/list/feed/obj[3]/int[@val='1']")).
		post(addUri);
		
		// pollChanges has no new events
		expect().
		body(not(hasXPath("/obj/list/feed"))).
		post(pollChangesUri);
		
		// additional data for feed
		given().body("<int val='4' />").put("/testDevicesWatch/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		given().body("<int val='5' />").put("/testDevicesWatch/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		given().body("<int val='6' />").put("/testDevicesWatch/fanSpeedWatchPollChanges/fanSpeedSetpoint");
		
		expect().
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val='6']")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val='5']")).
		body(hasXPath("/obj/list/feed/obj[3]/int[@val='4']")).
		post(pollChangesUri);
		
		// pollChanges has no new events
		expect().
		body(not(hasXPath("/obj/list/feed"))).
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
			 "		<uri val='/testDevicesWatch/fanSpeedWatchExtent'/>" + 
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/obj[@href='/testDevicesWatch/fanSpeedWatchExtent']")).
		post(addUri);
		
		given().body("<int val='42' />").put("/testDevicesWatch/fanSpeedWatchExtent/fanSpeedSetpoint");
		
		expect().
		body(hasXPath("/obj/list/obj[@href='/testDevicesWatch/fanSpeedWatchExtent']")).
		body(hasXPath("/obj/list/obj/int[@val=42]")).
		post(pollChangesUri);
	}
	
	
	
	@Test
	public void testWatchHistoryFeedFiltered() throws InterruptedException {
		String watchHref = makeWatch();
		String addUri = watchHref + "/add";
		String pollRefreshUri = watchHref + "/pollRefresh";
		String pollChangesUri = watchHref + "/pollChanges";
		
		// initial data for feed
		given().body("<int val='1' />").put("/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='2' />").put("/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='3' />").put("/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='4' />").put("/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='5' />").put("/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint");
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint/history/feed'>" +
			 "			<obj is='obix:HistoryFilter'>" +
			 "				<int name='limit' val='3' />" +
			 "			</obj>" +
			 "		</uri>" + 
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		body(hasXPath("/obj/list/feed[@href='/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint/history/feed' and @of='obix:HistoryRecord']")).
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val=5]")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val=4]")).
		body(hasXPath("/obj/list/feed/obj[3]/int[@val=3]")).
		post(addUri);
		
		// pollChanges has no new events
		expect().
		body(not(hasXPath("/obj/list/feed"))).
		post(pollChangesUri);
		
		expect().
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val=5]")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val=4]")).
		body(hasXPath("/obj/list/feed/obj[3]/int[@val=3]")).
		post(pollRefreshUri);
		
		// additional data for feed
		given().body("<int val='6' />").put("/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='7' />").put("/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='8' />").put("/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint");
		given().body("<int val='9' />").put("/testDevicesWatch/fanSpeedWatchFilter/fanSpeedSetpoint");
		
		expect().
		body(hasXPath("/obj/list/feed[count(obj) = 3]")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val=9]")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val=8]")).
		body(hasXPath("/obj/list/feed/obj[3]/int[@val=7]")).
		post(pollChangesUri);
	}

	@Test
	public void testHistoryFeedFuture() {
		String watchHref = makeWatch();
		String addUri = watchHref + "/add";
		String pollChangesUri = watchHref + "/pollChanges";
		String pollRefreshUri = watchHref + "/pollRefresh";
		Calendar calendar = Calendar.getInstance();
		String futureDate;
		
		
		String appendEvent = "<obj is='obix:HistoryAppendIn'>" +
			"	<list name='data' of='obix:HistoryRecord'>" +
			"		<obj>" +
			"			<abstime name='timestamp' val='%s'/>" +
			"			<int name='value' val='%d'/>" +
			"		</obj>" +
			"	</list>" +
			"</obj>";
		
		
		calendar.add(Calendar.HOUR, 1);
		futureDate = DatatypeConverter.printDateTime(calendar);
		
		given().
		param("data", "<obj is='obix:WatchIn'>"+
			 "	<list name='hrefs'>" +
			 "		<uri val='/testDevicesHistory/brightnessHistoryFeedFuture/value/history/feed' />" +
			 "	</list>" +
			 "</obj>").
		expect().
		body(hasXPath("/obj[@is='obix:WatchOut']")).
		post(addUri);
		
		
		given().param("data", String.format(appendEvent, futureDate, 5)).
		post("/testDevicesHistory/brightnessHistoryFeedFuture/value/history/append");
		
		given().body("<int val='1' />").put("/testDevicesHistory/brightnessHistoryFeedFuture/value");
		
		expect().
		body(hasXPath("/obj/list/feed")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val=5]")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val=1]")).
		post(pollChangesUri);
		
		given().body("<int val='2' />").put("/testDevicesHistory/brightnessHistoryFeedFuture/value");
		
		expect().
		body(hasXPath("/obj/list/feed")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val=2]")).
		post(pollChangesUri);
		
		calendar.add(Calendar.MINUTE, -20);
		futureDate = DatatypeConverter.printDateTime(calendar);
		
		given().body("<int val='3' />").put("/testDevicesHistory/brightnessHistoryFeedFuture/value");
		given().param("data", String.format(appendEvent, futureDate, 4)).
			post("/testDevicesHistory/brightnessHistoryFeedFuture/value/history/append");
		
		expect().
		body(hasXPath("/obj/list/feed")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val=4]")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val=3]")).
		post(pollChangesUri);
		
		expect().
		body(hasXPath("/obj/list/feed")).
		body(hasXPath("/obj/list/feed/obj[1]/int[@val=5]")).
		body(hasXPath("/obj/list/feed/obj[2]/int[@val=4]")).
		body(hasXPath("/obj/list/feed/obj[3]/int[@val=3]")).
		body(hasXPath("/obj/list/feed/obj[4]/int[@val=2]")).
		body(hasXPath("/obj/list/feed/obj[5]/int[@val=1]")).
		post(pollRefreshUri);
	}
}
