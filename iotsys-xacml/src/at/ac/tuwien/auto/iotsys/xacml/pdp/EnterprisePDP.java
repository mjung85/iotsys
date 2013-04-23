package at.ac.tuwien.auto.iotsys.xacml.pdp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import an.xacml.adapter.file.XACMLParser;
import an.xacml.context.Decision;
import an.xacml.context.Request;
import an.xacml.context.Response;
import an.xacml.context.Result;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AbstractPolicy;
import an.xacml.policy.Effect;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Parameter;
import at.ac.tuwien.auto.iotsys.util.FileHelper;

public class EnterprisePDP {
	
	private Logger log = Logger.getLogger(EnterprisePDP.class.getName());
	
	public EnterprisePDP() {

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
		log.info("Resource: " + resource);
		log.info("Subject: " + subject);
		log.info("Action: " + action);

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

			log.info(d.toString());
			if (!d.equals(Effect.Permit)) {
				return false;
			}
			return true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			log.severe(e.getMessage());
			log.severe(e.getClass().getSimpleName());
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
