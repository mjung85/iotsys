package at.ac.tuwien.auto.iotsys.xacml.pdp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import an.config.*;
import an.xacml.engine.*;
import an.xacml.adapter.file.*;
import an.xacml.context.*;
import an.xacml.policy.*;
import an.xml.XMLGeneralException;
import an.log.*;

import at.ac.tuwien.auto.iotsys.commons.interceptor.Parameter;
import at.ac.tuwien.auto.iotsys.util.FileHelper;

public class EnterprisePDP {

	private Logger log;

	public enum RequestParam {
		SUBJECT,
		SUBJECT_IP_ADDRESS,
		RESOURCE,
		RESOURCE_PROTOCOL,
		RESOURCE_IP_ADDRESS,
		RESOURCE_HOSTNAME,
		RESOURCE_PATH,
		ACTION
	}
	
	private org.apache.log4j.Logger log4j = org.apache.log4j.Logger
			.getLogger(getClass());

	public EnterprisePDP() {
		// log4j.info(System.getProperty("java.class.path"));

		// Configuration config = new Configuration("config/pdp.xml");
		// ConfigElement rootConfig = config.getConfigurationElement();
		//
		// ConfigElement logConfig = (ConfigElement) rootConfig
		// .getSingleXMLElementByType(Logger.ELEMTYPE_LOG);
		// LogFactory.initialize(logConfig);
		log = LogFactory.getLogger();

		// ConfigElement pdpConfig = (ConfigElement) rootConfig
		// .getXMLElementsByName(PDP.ELEM_PDP)[0];

	}

	/**
	 * Evaluates a decision request against the underlying XACML policy. The
	 * three String attributes are mandatory. Additional parameters can be set
	 * with the params Map.
	 * 
	 * @param resource	XACML resource-id
	 * @param subject	XACML subject-id
	 * @param action	XACML action-id
	 * @param params	additional parameters 
	 * 
	 * @return in case the request is evaluated to Permit true, false otherwise.
	 */
	public boolean evaluate(String resource, String subject, String action,
			Map<Parameter, String> params) {
		log4j.info("Resource: " + resource);
		log4j.info("Subject: " + subject);
		log4j.info("Action: " + action);

		if (params == null) {
			params = new HashMap<Parameter, String>();
		}
		params.put(Parameter.RESOURCE, resource);
		params.put(Parameter.SUBJECT, subject);
		params.put(Parameter.ACTION, action);

		try {
			String xacmlRequest = FileHelper.readFile("request/request.xml");

			xacmlRequest = replaceParams(xacmlRequest, params);
			// log4j.info(xacmlRequest);

			Request req = XACMLParser.parseRequest(new ByteArrayInputStream(
					xacmlRequest.getBytes()));
			AbstractPolicy policy = XACMLParser
					.parsePolicy(new ByteArrayInputStream(FileHelper.readFile(
							"policies/xacml-policy.xml").getBytes()));

			Result result = policy.evaluate(new EvaluationContext(req));
			Response actualResponse = new Response(new Result[] { result });

			ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
			XACMLParser.dumpResponse(actualResponse, tempOut);
			// log4j.info("Dump actual response: " + tempOut.toString());
			Decision d = result.getDecision();

			log4j.info(d);
			if (!d.equals(Effect.Permit)) {
				return false;
			}
			return true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			log4j.error(e.getMessage());
			log4j.error(e.getClass().getSimpleName());
			e.printStackTrace();
		}

		return false;
	}

	private String replaceParams(String str, Map<Parameter, String> params) {
		for (Parameter r : params.keySet()) {
			str = str.replaceAll("\\{\\$" + r + "\\}", params.get(r).trim());
			// log4j.info("\\{\\$" + r + "\\}");
		}
		str = str.replaceAll("\\{\\$(.)*\\}", "");
		return str;
	}
}
