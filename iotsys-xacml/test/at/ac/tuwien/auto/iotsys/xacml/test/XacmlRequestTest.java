package at.ac.tuwien.auto.iotsys.xacml.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import an.xacml.adapter.file.XACMLParser;
import an.xacml.context.Decision;
import an.xacml.context.Request;
import an.xacml.context.Response;
import an.xacml.context.Result;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AbstractPolicy;
import at.ac.tuwien.auto.iotsys.clients.pdp.DecisionType;
import at.ac.tuwien.auto.iotsys.clients.pdp.PDP;
import at.ac.tuwien.auto.iotsys.clients.pdp.PDPBeanService;
import at.ac.tuwien.auto.iotsys.util.FileHelper;
import at.ac.tuwien.auto.iotsys.xacml.util.AbstractNode;
import at.ac.tuwien.auto.iotsys.xacml.util.Attribute;
import at.ac.tuwien.auto.iotsys.xacml.util.XacmlRequest;

public class XacmlRequestTest {

	private static final Logger log = Logger.getLogger(XacmlRequestTest.class
			.getName());

	private XacmlRequest request;

	@Before
	public void prepare() {
		request = new XacmlRequest();
		
		// request.setIssuer("AnyOne");
		
		request.addSubjectAttribute(
				XacmlRequest.SUBJECT_ATTRIBUTE_ID,
				XacmlRequest.XML_SCHEMA_TYPE_STRING,
				"ODg5YTY1NGVkZDU1ZDg1NWI2OWZiM2E1NDJmZjhjMDM0ODEwMWMxODBjYTZjYTJkZGEwZWJiYzJkMzM0M2FiMQ==");
		// request.addSubjectAttribute(XacmlRequest.SUBJECT_ATTRIBUTE_IP_ADDRESS,
		// XacmlRequest.XML_SCHEMA_TYPE_STRING, "192.168.1.100");

		request.addResourceAttribute(XacmlRequest.RESOURCE_ATTRIBUTE_ID,
				XacmlRequest.XML_SCHEMA_TYPE_STRING,
				"45a8f48f70-9d9b-d233-6776-06333cd3c5");
		request.addResourceAttribute(XacmlRequest.RESOURCE_ATTRIBUTE_SERVICENAME,
				XacmlRequest.XML_SCHEMA_TYPE_STRING, "EnergiedatenService");
		// request.addResourceAttribute(XacmlRequest.RESOURCE_ATTRIBUTE_IP_ADDRESS,
		// XacmlRequest.XML_SCHEMA_TYPE_STRING, "192.168.1.101");
		// request.addResourceAttribute(XacmlRequest.RESOURCE_ATTRIBUTE_PROTOCOL,
		// XacmlRequest.XML_SCHEMA_TYPE_STRING, "http");
		// request.addResourceAttribute(XacmlRequest.RESOURCE_ATTRIBUTE_PATH,
		// XacmlRequest.XML_SCHEMA_TYPE_STRING, "/resourcePath");

		request.addActionAttribute(XacmlRequest.ACTION_ATTRIBUTE_ID,
				XacmlRequest.XML_SCHEMA_TYPE_STRING, "getMeterReadings");
	}

	@Test
	public void testAttribute() {
		Attribute av = new Attribute(
				"urn:tuwien:auto:smartwebgrid:subject:role",
				"http://www.w3.org/2001/XMLSchema#string", "TestEintrag");
		log.info(AbstractNode.transformDocument(av.getDocument(), true));
	}

	@Test
	public void testRequest() {
		log.info(request.getRequest());
	}

	@Test
	public void testSoapRequest() {
		log.info(request.getRequestAbstract());
	}

//	@Test
//	public void testPdpRequestNoSecurity() {
//		String content = request.getRequestAbstract();
//
//		// default pdp at http://localhost:8080/SwgPdp?wsdl
//		PDPBeanService pdpService = new PDPBeanService();
//		PDP pdp = pdpService.getPDPPort();
//		DecisionType dt = pdp.authorize(content);
//		log.info(dt.toString());
//		Assert.assertEquals("Decision objects are not the same", DecisionType.PERMIT, dt);
//	}

	@Test
	public void testEnterpriseXacml() {
		System.setProperty(XACMLParser.CONTEXT_KEY_DEFAULT_SCHEMA_FILE,
				"xacml-2.0-context.xsd");
		System.setProperty(XACMLParser.POLICY_KEY_DEFAULT_SCHEMA_FILE,
				"xacml-2.0-policy.xsd");

		try {
			// create request object
			Request xacmlRequest = XACMLParser
					.parseRequest(new ByteArrayInputStream(request.getRequest()
							.getBytes()));
		
			// load policy
			String policy = FileHelper.readFile("policies/test-policy.xml");

			AbstractPolicy abstractPolicy = XACMLParser
					.parsePolicy(new ByteArrayInputStream(policy.getBytes()));

			EvaluationContext evalCtx = new EvaluationContext(xacmlRequest);
			Result result = abstractPolicy.evaluate(evalCtx);

			Response actualResponse = new Response(new Result[] { result });

			ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
			XACMLParser.dumpResponse(actualResponse, tempOut);
			log.info("Dump actual response: " + tempOut.toString());
			Decision d = result.getDecision();
			log.info(d.toString());

			Assert.assertEquals("Decision objects are not the same", Decision.Permit, d);
			
		} catch (Exception e) {
		
		}
	}

}
