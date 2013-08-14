package at.ac.tuwien.auto.iotsys.gateway.test;
import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.equalTo;
import static com.jayway.restassured.path.xml.XmlPath.from;

import static org.junit.Assert.*;
import org.junit.Test;

import at.ac.tuwien.auto.iotsys.gateway.util.ExiUtil;

public class GatewayTest extends AbstractGatewayTest {
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
		expect().body(hasXPath("/obj[@href='switch2']")).
		expect().body(hasXPath("/obj/bool[@name='value' and @val='true']")).
		when().put("/switch2");
		
		
		given().body(  "<obj href='/virtualLight1' is='iot:LightSwitchActuator'>"
				 + "	<bool name='value' href='value' val='false'/>"
				 + "</obj>").
		expect().body(hasXPath("/obj[@href='switch2']")).
		expect().body(hasXPath("/obj/bool[@name='value' and @val='false']")).
		when().put("/switch2");
	}
	
	
	@Test
	public void testAcceptJson() {
		given().header("Accept", "application/json").
		expect().
		body("tag", equalTo("obj")).
		body("is", equalTo("iot:LightSwitchActuator")).
		body("href", equalTo("switch1")).
		body("nodes[0].tag", equalTo("bool")).
		body("nodes[0].name", equalTo("value")).
		body("nodes[0].href", equalTo("value")).
		body("nodes[1].tag", equalTo("ref")).
		get("/switch1");
	}
	
	@Test
	public void testAcceptEXI() throws Exception {
		byte[] response = given().header("Accept", "application/exi").
		get("/switch1").asByteArray();
		
		ExiUtil exiUtil = ExiUtil.getInstance();
		String xml = exiUtil.decodeEXI(response);
		
		assertEquals("switch1", from(xml).get("obj.@href"));
		assertEquals("iot:LightSwitchActuator", from(xml).get("obj.@is"));
		assertEquals("value", from(xml).get("obj.bool.@name"));
		assertEquals("value", from(xml).get("obj.bool.@href"));
	}
	
}
