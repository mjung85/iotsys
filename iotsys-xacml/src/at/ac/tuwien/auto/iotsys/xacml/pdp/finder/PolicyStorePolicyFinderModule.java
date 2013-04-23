package at.ac.tuwien.auto.iotsys.xacml.pdp.finder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.sunxacml.AbstractPolicy;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.MatchResult;
import org.jboss.security.xacml.sunxacml.Policy;
import org.jboss.security.xacml.sunxacml.PolicySet;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinder;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinderModule;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinderResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import at.ac.tuwien.auto.iotsys.xacml.pdp.finder.exceptions.PolicyNotFoundException;
import at.ac.tuwien.auto.iotsys.xacml.pdp.policystore.interfaces.PolicyLoader;

/**
 * Abstract Implementation of the
 * <code>org.jboss.security.xacml.sunxacml.finder.PolicyFinderModule</code> for
 * use with different types of policy loader mechanisms.
 * 
 * @author Thomas Hofer
 */
public class PolicyStorePolicyFinderModule extends PolicyFinderModule
		implements ErrorHandler {

	public static final String POLICY_SCHEMA_PROPERTY = "com.sun.xacml.PolicySchema";

	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	public static final String W3C_XML_SCHEMA_STRING = "http://www.w3.org/2001/XMLSchema#string";

	public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	public static final String XACML_POLICY_TAG = "Policy";

	public static final String XACML_POLICYSET_TAG = "PolicySet";

	private File schemaFile;

	private PolicyFinder policyFinder = null;

	private PolicyLoader policyLoader = null;

	private static final Logger log = Logger
			.getLogger(PolicyStorePolicyFinderModule.class);

	public PolicyStorePolicyFinderModule() {
		String schemaName = System.getProperty(POLICY_SCHEMA_PROPERTY);
		try {
			schemaFile = new File(schemaName);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

	public PolicyStorePolicyFinderModule(PolicyLoader loader) {
		super();
		this.policyLoader = loader;		
	}

	/**
	 * Required method to get the module working.
	 */
	@Override
	public boolean isRequestSupported() {
		return true;
	}
	
	@Override
	public void init(PolicyFinder finder) {
		setPolicyFinder(finder);
	}

	/**
	 * Uses the EvaluationCtx to retrieve a policy and creates a
	 * PolicyFinderResult that matches the given context.
	 * 
	 * @param context
	 *            Evaluation context
	 * 
	 * @return a policy matching PolicyFinderResult
	 */
	@Override
	public PolicyFinderResult findPolicy(EvaluationCtx context) {

		AbstractPolicy policy;
		try {
			log.info("Load policy!!!");
			policy = loadPolicy(context);
			// logger.info(policy);

			MatchResult match = policy.match(context);
			int result = match.getResult();

			// if there was an error, we stop right away
			if (result == MatchResult.INDETERMINATE)
				return new PolicyFinderResult(match.getStatus());

			if (result == MatchResult.MATCH) {
				return new PolicyFinderResult(policy);
			} else {
				/**
				 * Fallback return value. The specification says that an empty
				 * PolicyFinderResult is returned.
				 */
				return new PolicyFinderResult();
			}
		} catch (PolicyNotFoundException e) {
			return new PolicyFinderResult();
		}
	}

	/**
	 * Loads a policy by the given context. This method is used by
	 * <code>findPolicy(EvaluationCtx)</code> to load an XACML policy. It is
	 * recommended to use <code>convertXmlToPolicy(String)</code> to prepare the
	 * retrieved XACML XML string to an <code>AbstractPolicy</code> object.
	 * 
	 * @param context
	 *            EvaluationCtx of the current request
	 * 
	 * @return policy an implementation of <code>AbstractPolicy</code> that
	 *         matches the <code>EvaluationCtx</code>
	 * @throws PolicyNotFoundException
	 */
	protected AbstractPolicy loadPolicy(EvaluationCtx context)
			throws PolicyNotFoundException {
		String resourceId = getResourceId(context);
		String policyString = "";

		log.debug("Trying to find policy in related policystore " + policyLoader.getClass());
		
		if (policyLoader != null) {
			// try to load the policy file
			policyString = this.policyLoader.findPolicy(resourceId);
		} else {
			throw new PolicyNotFoundException(
					"PolicyLoader is null, please initiate PolicyFinderModule correctly");
		}

		return convertXmlToPolicy(policyString);
	}

	/**
	 * Converts an XML string into an <code>AbstractPolicy</code> object. It is
	 * assumed, that the incoming XML fragment is an XACML policy.
	 * 
	 * @param policyString
	 * 
	 * @return policy Policy or PolicySet, depending on given XML string
	 * @throws PolicyNotFoundException
	 */
	protected AbstractPolicy convertXmlToPolicy(String policyString)
			throws PolicyNotFoundException {
		try {
			// create the factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setIgnoringComments(true);

			DocumentBuilder db = null;

			// as of 1.2, we always are namespace aware
			factory.setNamespaceAware(true);

			// set the factory to work the way the system requires
			if (schemaFile == null) {
				// we're not doing any validation
				factory.setValidating(false);

				db = factory.newDocumentBuilder();
			} else {
				// we're using a validating parser
				factory.setValidating(true);

				factory.setAttribute(
						PolicyStorePolicyFinderModule.JAXP_SCHEMA_LANGUAGE,
						PolicyStorePolicyFinderModule.W3C_XML_SCHEMA);
				factory.setAttribute(
						PolicyStorePolicyFinderModule.JAXP_SCHEMA_SOURCE,
						schemaFile);

				db = factory.newDocumentBuilder();
				db.setErrorHandler(this);
			}

			// log.info(policyString);

			Document doc = db.parse(new ByteArrayInputStream(policyString
					.getBytes("UTF-8")));

			// handle the policy, if it's a known type
			Element root = doc.getDocumentElement();
			String name = root.getTagName();

			if (name.equals(PolicyStorePolicyFinderModule.XACML_POLICY_TAG)) {
				return Policy.getInstance(root);
			} else if (name
					.equals(PolicyStorePolicyFinderModule.XACML_POLICYSET_TAG)) {
				return PolicySet.getInstance(root, policyFinder);
				
			} else {
				// this isn't a root type that we know how to handle
				throw new Exception("Unknown root document type: " + name);
			}
		} catch (Exception e) {
			// UnsupportedEncodingException, SAXException, IOException,
			// ParsingException, ParserConfigurationException
			throw new PolicyNotFoundException(e);
		}
	}

	/**
	 * Extracts the Attribute of the XACML Decision Request with the AttributeId
	 * urn:oasis:names:tc:xacml:1.0:resource:resource-id.
	 * 
	 * @param context
	 *            EvaluationCtx of the current decision request
	 * @return a string with the resource id
	 */
	protected String getResourceId(EvaluationCtx context) {
		// log.info("Trying to get resourceId!");
		
		String resourceId = null;
		try {
			EvaluationResult evalResult = context.getResourceAttribute(new URI(
					PolicyStorePolicyFinderModule.W3C_XML_SCHEMA_STRING), new URI(
					XACMLConstants.ATTRIBUTEID_RESOURCE_ID), null);

			BagAttribute bagAttr = (BagAttribute) evalResult
					.getAttributeValue();

			Iterator it = bagAttr.iterator();
			int i = 0;
			while (it.hasNext()) {
				if (i > 0) {
					break;
				}
				AttributeValue tmp = (AttributeValue) it.next();
				resourceId = tmp.encode();
				log.info("RESOURCE-ID: " + resourceId);
				i++;
			}

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resourceId;
	}

	/**
	 * 
	 * @return
	 */
	public PolicyFinder getPolicyFinder() {
		return policyFinder;
	}

	/**
	 * 
	 * @param policyFinder
	 */
	public void setPolicyFinder(PolicyFinder policyFinder) {
		this.policyFinder = policyFinder;
	}

	
	
	public PolicyLoader getPolicyLoader() {
		return policyLoader;
	}

	public void setPolicyLoader(PolicyLoader policyLoader) {
		this.policyLoader = policyLoader;
	}

	/**
	 * Standard handler routine for the XML parsing.
	 * 
	 * @param exception
	 *            information on what caused the problem
	 */
	public void warning(SAXParseException exception) throws SAXException {
		log.warn("Warning on line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	/**
	 * Standard handler routine for the XML parsing.
	 * 
	 * @param exception
	 *            information on what caused the problem
	 * 
	 * @throws SAXException
	 *             always to halt parsing on errors
	 */
	public void error(SAXParseException exception) throws SAXException {
		log.warn("Error on line " + exception.getLineNumber() + ": "
				+ exception.getMessage() + " ... "
				+ "Policy will not be available");

		throw new SAXException("error parsing policy");
	}

	/**
	 * Standard handler routine for the XML parsing.
	 * 
	 * @param exception
	 *            information on what caused the problem
	 * 
	 * @throws SAXException
	 *             always to halt parsing on errors
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		log.warn("Fatal error on line " + exception.getLineNumber() + ": "
				+ exception.getMessage() + " ... "
				+ "Policy will not be available");

		throw new SAXException("fatal error parsing policy");
	}
}
