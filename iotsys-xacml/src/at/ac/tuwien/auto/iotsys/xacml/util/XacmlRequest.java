package at.ac.tuwien.auto.iotsys.xacml.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Creates a valid XACML decision request. This implementation is able to export
 * the Request entity as element, document and string as well as the SAML
 * RequestAbstract entity that encapsulates the Request entity.
 * 
 * @author Thomas Hofer
 * 
 */
public class XacmlRequest extends AbstractNode {

	public static final String SUBJECT_ATTRIBUTE_ID = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
	public static final String SUBJECT_ATTRIBUTE_IP_ADDRESS = "urn:oasis:names:tc:xacml:1.0:subject:ip-address";
	
	public static final String RESOURCE_ATTRIBUTE_ID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
	public static final String RESOURCE_ATTRIBUTE_PROTOCOL = "urn:oasis:names:tc:xacml:1.0:resource:protocol";
	public static final String RESOURCE_ATTRIBUTE_IP_ADDRESS = "urn:oasis:names:tc:xacml:1.0:resource:ip-address";
	public static final String RESOURCE_ATTRIBUTE_HOSTNAME = "urn:oasis:names:tc:xacml:1.0:resource:hostname";
	public static final String RESOURCE_ATTRIBUTE_PATH = "urn:oasis:names:tc:xacml:1.0:resource:path";
	public static final String RESOURCE_ATTRIBUTE_SERVICENAME = "urn:tuwien:auto:smartwebgrid:resource:servicename";
	
	public static final String ACTION_ATTRIBUTE_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";
	
	
	private RequestEntity subject = new RequestEntity("Subject");
	private RequestEntity resource = new RequestEntity("Resource");
	private RequestEntity action = new RequestEntity("Action");

	private Document xacmlRequest;

	public XacmlRequest() {
		xacmlRequest = getBuilder().newDocument();
	}

	/**
	 * Create a samlp:RequestAbstract element which encapsulates the Request
	 * entity of the XACML decision request.
	 * 
	 * @return string containing the decision request in a RequestAbstract
	 *         element
	 */
	public String getRequestAbstract() {
		Element request = xacmlRequest.createElementNS(
				"urn:oasis:names:tc:SAML:2.0:protocol", "RequestAbstract");
		xacmlRequest.createElementNS("urn:oasis:names:tc:SAML:2.0:protocol",
				"samlp");
		request.setPrefix("samlp");

		Attr xmlns = xacmlRequest.createAttribute("xmlns");
		xmlns.setValue("urn:oasis:names:tc:xacml:2.0:context:schema:os");
		request.setAttributeNode(xmlns);

		Attr inputCtxOnly = xacmlRequest.createAttributeNS(
				"urn:oasis:xacml:2.0:saml:protocol:schema:os",
				"xacml-samlp:InputContextOnly");
		inputCtxOnly.setValue("true");
		request.setAttributeNode(inputCtxOnly);

		Attr returnCtx = xacmlRequest.createAttributeNS(
				"urn:oasis:xacml:2.0:saml:protocol:schema:os",
				"xacml-samlp:ReturnContext");
		returnCtx.setValue("true");
		request.setAttributeNode(returnCtx);

		Attr xsi = xacmlRequest.createAttributeNS(
				"http://www.w3.org/2001/XMLSchema-instance", "xsi:type");
		xsi.setValue("xacml-samlp:XACMLAuthzDecisionQueryType");
		request.setAttributeNode(xsi);

		Attr xs = xacmlRequest.createAttribute("xmlns:xs");
		xs.setValue("http://www.w3.org/2001/XMLSchema");
		request.setAttributeNode(xs);

		Attr id = xacmlRequest.createAttribute("ID");
		id.setValue("ID_" + UUID.randomUUID().toString());
		request.setAttributeNode(id);

		Attr version = xacmlRequest.createAttribute("Version");
		version.setValue("2.0");
		request.setAttributeNode(version);

		Attr issueInstant = xacmlRequest.createAttribute("IssueInstant");
		issueInstant.setValue(new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
		request.setAttributeNode(issueInstant);

		// add issuer tag
		Element issuerElement = xacmlRequest.createElementNS(
				"urn:oasis:names:tc:SAML:2.0:assertion", "Issuer");
		issuerElement.setTextContent(getIssuer());
		issuerElement.setPrefix("saml");

		request.appendChild(issuerElement);

		// add Request element to RequestAbstract
		request.appendChild(this.getElement(xacmlRequest));

		xacmlRequest.appendChild(request);

		String result = AbstractNode.transformDocument(xacmlRequest, true);

		return result;
	}

	public String getRequest() {
		xacmlRequest.appendChild(this.getElement(xacmlRequest));
		return AbstractNode.transformDocument(xacmlRequest, true);
	}

	public void addActionAttribute(String urn, String type, String value) {
		action.add(new Attribute(urn, type, value));
	}

	public void addSubjectAttribute(String urn, String type, String value) {
		subject.add(new Attribute(urn, type, value));
	}

	public void addResourceAttribute(String urn, String type, String value) {
		resource.add(new Attribute(urn, type, value));
	}

	public Document getDocument() {
		return getDocument(xacmlRequest);
	}

	public Document getDocument(Document doc) {
		doc.appendChild(getElement(doc));
		return doc;
	}

	/**
	 * Creates the Request {@link org.w3c.dom.Element} of the XACML decision
	 * request. Therefore parses all Subject, Resource and Action attributes
	 * that have been added.
	 * 
	 * @return Element containing the Request entity of the decision request.
	 */
	public Element getElement(Document doc) {
		Element request = doc.createElementNS(URN_XACML_CONTEXT, "Request");

		Attr xmlns = doc.createAttribute("xmlns");
		xmlns.setValue(URN_XACML_CONTEXT);
		request.setAttributeNode(xmlns);

		Attr ns2 = doc.createAttribute("xmlns:ns2");
		ns2.setValue("urn:oasis:names:tc:xacml:2.0:policy:schema:os");
		request.setAttributeNode(ns2);

		request.appendChild(subject.getElement(doc));
		request.appendChild(resource.getElement(doc));
		request.appendChild(action.getElement(doc));

		Element env = doc.createElement("Environment");
		request.appendChild(env);

		return request;
	}
}
