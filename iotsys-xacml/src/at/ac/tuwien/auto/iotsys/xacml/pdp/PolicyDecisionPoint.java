package at.ac.tuwien.auto.iotsys.xacml.pdp;

import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.log4j.Logger;
import org.jboss.security.xacml.core.JBossRequestContext;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.core.model.context.ResultType;
import org.jboss.security.xacml.factories.RequestResponseContextFactory;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.sunxacml.BasicEvaluationCtx;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.PDP;
import org.jboss.security.xacml.sunxacml.PDPConfig;
import org.jboss.security.xacml.sunxacml.ParsingException;
import org.jboss.security.xacml.sunxacml.ctx.RequestCtx;
import org.jboss.security.xacml.sunxacml.ctx.ResponseCtx;
import org.jboss.security.xacml.sunxacml.ctx.Result;
import org.jboss.security.xacml.sunxacml.finder.AttributeFinder;
import org.jboss.security.xacml.sunxacml.finder.AttributeFinderModule;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinder;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinderModule;
import org.jboss.security.xacml.sunxacml.finder.ResourceFinder;
import org.jboss.security.xacml.sunxacml.finder.ResourceFinderModule;
import org.picketlink.identity.federation.core.saml.v2.util.DocumentUtil;
import org.picketlink.identity.federation.core.saml.v2.util.SOAPSAMLXACMLUtil;
import org.picketlink.identity.federation.saml.v2.protocol.XACMLAuthzDecisionQueryType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;



/**
 * Abstract PDP using the SunXACML implementation. The main functionality is
 * already implemented, only the policy loading is missing and is part of a
 * specific implementation.
 * 
 * @author Thomas Hofer
 */
public abstract class PolicyDecisionPoint {

	/**
	 * SunXACML PDP implementation
	 */
	private PDP policyDecisionPoint;

	private Lock lock = new ReentrantLock();

	private static final Logger logger = Logger
			.getLogger(PolicyDecisionPoint.class);

	/**
	 * Evaluates the incoming request and returns the policy evaluation result,
	 * wrapped in a ResponseContext.
	 * 
	 * @param request
	 *            the request object containing the XACML Decision Request
	 * @return ResponseContext
	 */
	public synchronized ResponseContext evaluate(RequestContext request) {

		if (policyDecisionPoint == null) {
			bootstrap();
		}

		RequestCtx requestCtx = (RequestCtx) request
				.get(XACMLConstants.REQUEST_CTX);

		if (requestCtx == null)
			throw new IllegalStateException(
					"Request Context does not contain a request");

		ResponseCtx responseCtx = null;

		lock.lock();
		try {
			EvaluationCtx evaluationCtx = new BasicEvaluationCtx(requestCtx);
			responseCtx = policyDecisionPoint.evaluate(evaluationCtx);

		} catch (ParsingException e) {
			Result result = new Result(Result.DECISION_INDETERMINATE);
			responseCtx = new ResponseCtx(result);
			logger.error(e.getMessage());
		} finally {
			lock.unlock();
		}
		ResponseContext responseContext = RequestResponseContextFactory
				.createResponseContext();
		responseContext.set(XACMLConstants.RESPONSE_CTX, responseCtx);
		return responseContext;
	}
	
	/**
	 * 
	 * @param xacmlStr
	 * @return
	 */
	public synchronized ResponseContext evaluate(String xacmlStr) {
		Source request = convertFromString(xacmlStr);

		RequestContext context = this.getRequestContext(request);
		
		return evaluate(context);
	}

	/**
	 * Bootstrapping the {@link PDPConfig} and initializing the {@link PDP}. If
	 * you overwrite this method be aware to set the {@link PDP}
	 */
	public void bootstrap() {
		AttributeFinder attributeFinder = new AttributeFinder();
		attributeFinder.setModules(this.createAttributeFinderModules());

		PolicyFinder policyFinder = new PolicyFinder();
		policyFinder.setModules(this.createPolicyFinderModules());

		ResourceFinder resourceFinder = new ResourceFinder();
		resourceFinder.setModules(this.createResourceFinderModules());

		PDPConfig pdpConfig = new PDPConfig(attributeFinder, policyFinder,
				resourceFinder);

		setPDP(new PDP(pdpConfig));
	}

	/**
	 * 
	 * @param responseContext
	 * @return
	 */
	public DecisionType getDecisionResult(ResponseContext responseContext) {

		ResultType result = responseContext.getResult();
		// log.info(result);

		String resultValue = result.getDecision().value();
		// log.info(resultValue);

		return DecisionType.fromValue(resultValue);
	}
	
	/**
	 * Sets the policy decision point.
	 * 
	 * @param pdp
	 *            initialized policy decision point
	 */
	public final void setPDP(PDP pdp) {
		this.policyDecisionPoint = pdp;
	}

	/**
	 * Extracts the XACML Request from a Source object and envelops it in a
	 * RequestContext.
	 * 
	 * @param Source
	 * 
	 * @return RequestContext
	 */
	private RequestContext getRequestContext(Source request) {
		Document doc = null;

//		if (log.isDebugEnabled()) {
//			// log.debug("Received Message::" + DocumentUtil.asString(doc));
//		}

		RequestContext context = new JBossRequestContext();
		try {
			doc = (Document) DocumentUtil.getNodeFromSource(request);

			XACMLAuthzDecisionQueryType xacmlQuery = SOAPSAMLXACMLUtil
					.getXACMLQueryType(doc);
			RequestType requestType = xacmlQuery.getRequest();

			context.setRequest(requestType);
		} catch (Exception e) {

		}

		return context;
	}
	
	/**
	 * Converts a string to a DOMSource object.
	 * 
	 * @param Stringstr
	 * 
	 * @return DOMSource
	 */
	private DOMSource convertFromString(String str) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		DocumentBuilder builder;
		DOMSource src = new DOMSource();

		try {
			builder = factory.newDocumentBuilder();

			// Use String reader
			Document document = builder.parse(new InputSource(new StringReader(
					str)));

			src = new DOMSource(document);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return src;

	}

	/**
	 * Creates list of
	 * {@link org.jboss.security.xacml.sunxacml.finder.AttributeFinderModule}.
	 * 
	 * @return list of AttributeFinderModules
	 */
	abstract protected List<AttributeFinderModule> createAttributeFinderModules();

	/**
	 * Creates a set of
	 * {@link org.jboss.security.xacml.sunxacml.finder.PolicyFinderModules}.
	 * 
	 * @return set of PolicyFinderModules
	 */
	abstract protected Set<PolicyFinderModule> createPolicyFinderModules();

	/**
	 * Creates a List of
	 * {@link org.jboss.security.xacml.sunxacml.finder.ResourceFinderModule}s.
	 * This is part of configuring the PDP.
	 * 
	 * @return list of ResourceFinderModules
	 */
	abstract protected List<ResourceFinderModule> createResourceFinderModules();

}
