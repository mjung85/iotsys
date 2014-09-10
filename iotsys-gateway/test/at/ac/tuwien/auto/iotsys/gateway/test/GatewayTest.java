package at.ac.tuwien.auto.iotsys.gateway.test;
import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.xml.XmlPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import obix.Bool;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Ref;
import obix.Uri;

import org.junit.Test;

import at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBrokerImpl;
import at.ac.tuwien.auto.iotsys.gateway.util.ExiUtil;

public class GatewayTest extends AbstractGatewayTest {
	
	private static final Logger log = Logger
			.getLogger(GatewayTest.class.getName());
	
	@Test
	public void testLobbyHasAbout() {
		expect().body(hasXPath("/obj/ref[@href='about']")).
		when().get("/obix/");
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
		when().get("/testDevices/brightnessSensor1/");
	}
	
	@Test
	public void testWriteSensor() {
		given().body("<real val='42.0' />").
		expect().body(hasXPath("//real[@name='roomIllumination']")).
		expect().body(hasXPath("//real[@href='roomIllumination']")).
		expect().body(hasXPath("//real[@val!='42.0']")).
		when().put("/testDevices/brightnessSensor1/");
	}
	
	@Test
	public void testModifyValue() {
		given().body("<bool val='false' />").
		expect().body(hasXPath("/bool[@name='value' and @val='false']")).
		when().put("/testDevices/switch1/value");
		
		given().body("<bool val='true' />").
		expect().body(hasXPath("/bool[@name='value' and @val='true']")).
		when().put("/testDevices/switch1/value");
	}
	
	@Test
	public void testModifyObject() {
		given().body(  "<obj is='iot:LightSwitchActuator'>"
					 + "	<bool name='value' href='value' val='true'/>"
					 + "</obj>").
		expect().body(hasXPath("/obj[@href='/testDevices/switch2/']")).
		expect().body(hasXPath("/obj/bool[@name='value' and @val='true']")).
		when().put("/testDevices/switch2/");
		
		
		given().body(  "<obj is='iot:LightSwitchActuator'>"
				 + "	<bool name='value' href='value' val='false'/>"
				 + "</obj>").
		expect().body(hasXPath("/obj[@href='/testDevices/switch2/']")).
		expect().body(hasXPath("/obj/bool[@name='value' and @val='false']")).
		when().put("/testDevices/switch2/");
	}
	
	
	@Test
	public void testAcceptJson() {
		given().header("Accept", "application/json").
		expect().
		body("tag", equalTo("obj")).
		body("is", equalTo("iot:LightSwitchActuator")).
		body("href", equalTo("/testDevices/switch1/")).
		body("nodes[0].tag", equalTo("bool")).
		body("nodes[0].name", equalTo("value")).
		body("nodes[0].href", equalTo("value")).
		body("nodes[1].tag", equalTo("ref")).
		get("/testDevices/switch1/");
	}
	
	@Test
	public void testAcceptEXI() throws Exception {
		
		byte[] response = given().header("accept", "application/exi").
				get("/testDevices/switch1/").asByteArray();
		
		log.info("resposne : " + response.length);
		
		ExiUtil exiUtil = ExiUtil.getInstance();
		String xml = exiUtil.decodeEXI(response);
		
		assertEquals("/testDevices/switch1/", from(xml).get("obj.@href"));
		assertEquals("iot:LightSwitchActuator", from(xml).get("obj.@is"));
		assertEquals("value", from(xml).get("obj.bool.@name"));
		assertEquals("value", from(xml).get("obj.bool.@href"));
	}
	
	@Test
	public void testRelativeHrefs() {
		expect().
		body(hasXPath("/obj/ref[@href='about']")).
		get("/obix/");
		
		expect().
		body(hasXPath("/obj/ref[@href='obix/about']")).
		get("/obix");
		
		expect().
		body(hasXPath("/obj[@href='/testDevices/fanSpeed1/']")).
		body(hasXPath("/obj/int[@href='fanSpeedSetpoint']")).
		body(hasXPath("/obj/bool[@href='enabled']")).
		body(hasXPath("/obj/ref[@href='fanSpeedSetpoint/history']")).
		body(hasXPath("/obj/ref[@href='enabled/history']")).
		get("/testDevices/fanSpeed1/");
		
		expect().
		body(hasXPath("/obj[@href='fanSpeed1/']")).
		body(hasXPath("/obj/int[@href='fanSpeed1/fanSpeedSetpoint']")).
		body(hasXPath("/obj/bool[@href='fanSpeed1/enabled']")).
		body(hasXPath("/obj/ref[@href='fanSpeed1/fanSpeedSetpoint/history']")).
		body(hasXPath("/obj/ref[@href='fanSpeed1/enabled/history']")).
		get("/testDevices/fanSpeed1");
	}
	
	@Test
	public void testHrefComplexObj() {
		Obj complexObj= new Obj();
		complexObj.setHref(new Uri("examples/complexObj"));
		
		Bool b1 = new Bool();
		b1.setHref(new Uri("b1"));
		
		Int i1 = new Int();
		i1.setHref(new Uri("i1"));
		
		Int i2 = new Int();
		i2.setHref(new Uri("i2"));
		
		Obj childObj = new Obj();
		childObj.setHref(new Uri("childObj"));
		
		Real r1 = new Real();
		r1.setHref(new Uri("r"));
		
		childObj.add(r1);
		
		complexObj.add(b1);
		complexObj.add(i1);
		complexObj.add(i2);
		complexObj.add(childObj);
		
		ObjectBrokerImpl.getInstance().addObj(complexObj);
		
		expect().
		body(hasXPath("/obj[@href='/examples/complexObj/']")).
		body(hasXPath("/obj/bool[@href='b1']")).
		body(hasXPath("/obj/int[@href='i1']")).
		body(hasXPath("/obj/int[@href='i2']")).
		body(hasXPath("/obj/obj[@href='childObj']")).
		body(hasXPath("/obj/obj/real[@href='childObj/r']")).
		get("/examples/complexObj/");
		
		expect().
		body(hasXPath("/obj[@href='complexObj/']")).
		body(hasXPath("/obj/bool[@href='complexObj/b1']")).
		body(hasXPath("/obj/int[@href='complexObj/i1']")).
		body(hasXPath("/obj/int[@href='complexObj/i2']")).
		body(hasXPath("/obj/obj[@href='complexObj/childObj']")).
		body(hasXPath("/obj/obj/real[@href='complexObj/childObj/r']")).
		get("/examples/complexObj");
	}
	
	@Test
	public void testAbsoluteHrefs() {
		Obj referringObj= new Obj();
		referringObj.setHref(new Uri("examples/referringObj"));
		
		Ref ref = new Ref();
		ref.setHref(new Uri("/testDevices/fanSpeed1"));
		referringObj.add(ref);
		
		ObjectBrokerImpl.getInstance().addObj(referringObj);
		
		expect().
		body(hasXPath("/obj/ref[@href='/testDevices/fanSpeed1']")).
		get("/examples/referringObj/");
		
		expect().
		body(hasXPath("/obj/ref[@href='/testDevices/fanSpeed1']")).
		get("/examples/referringObj");
	}
	
	
	@Test
	public void testGroupComm() {
		Obj referringObj= new Obj();
		referringObj.setHref(new Uri("examples/referringObj"));
		
		Ref ref = new Ref();
		ref.setHref(new Uri("/testDevices/fanSpeed1"));
		referringObj.add(ref);
		
		ObjectBrokerImpl.getInstance().addObj(referringObj);
		
		expect().
		body(hasXPath("/obj/ref[@href='/testDevices/fanSpeed1']")).
		get("/examples/referringObj/");
		
		expect().
		body(hasXPath("/obj/ref[@href='/testDevices/fanSpeed1']")).
		get("/examples/referringObj");
	}
}
