package at.ac.tuwien.auto.iotsys.xacml.pdp.xacml;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.jboss.security.xacml.core.model.context.ActionType;
import org.jboss.security.xacml.core.model.context.AttributeType;
import org.jboss.security.xacml.core.model.context.EnvironmentType;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.core.model.context.ResourceType;
import org.jboss.security.xacml.core.model.context.SubjectType;
import org.jboss.security.xacml.factories.RequestAttributeFactory;
import org.picketlink.identity.federation.core.exceptions.ConfigurationException;
import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.saml.v2.common.IDGenerator;
import org.picketlink.identity.federation.core.saml.v2.util.XMLTimeUtil;
import org.picketlink.identity.federation.core.saml.v2.writers.SAMLRequestWriter;
import org.picketlink.identity.federation.core.util.StaxUtil;
import org.picketlink.identity.federation.saml.v2.assertion.NameIDType;
import org.picketlink.identity.federation.saml.v2.protocol.XACMLAuthzDecisionQueryType;

/**
 * Wraps the data into a RequestType to be prepared for the PDP decision request.
 * 
 * @author Thomas Hofer
 *
 */
public class XacmlRequest {

	private String issuer = null;
	private String serviceName = null;
	
	public XacmlRequest() {
		this.issuer = "AUTO";
	}
	
	public XacmlRequest(String issuer, String serviceName) {
		this.issuer = issuer;
		this.serviceName = serviceName;
	}

	public XacmlRequest(String issuer) {
		this.issuer = issuer;
	}

	
	/**
	 * 
	 * @param subjectId
	 * @param resourceId
	 * @param actionId
	 * @return
	 */
	public RequestType getXACMLRequest(String subjectId, String resourceId, String actionId) {

		RequestType requestType = new RequestType();

		if (subjectId != null && subjectId != "") {
			requestType.getSubject().add(createSubject(subjectId.trim()));			
		}
		requestType.getSubject().add(createSubject(subjectId.trim()));			
		requestType.getResource().add(createResource(resourceId.trim()));
		requestType.setAction(createAction(actionId.trim()));
		// requestType.setEnvironment(createEnvironment());
		
		return requestType;
	}

	/**
	 * 
	 * @param rt
	 * @return
	 */
	public String convertRequestTypeToString(RequestType rt) {

		String request = "";

		String id = IDGenerator.create("ID_");

		XACMLAuthzDecisionQueryType queryType;
		try {
			queryType = new XACMLAuthzDecisionQueryType(id,
					XMLTimeUtil.getIssueInstant());
			queryType.setRequest(rt);

			// Create Issuer
			NameIDType nameIDType = new NameIDType();
			nameIDType.setValue(issuer);
			queryType.setIssuer(nameIDType);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLStreamWriter xmlStreamWriter = StaxUtil.getXMLStreamWriter(baos);

			SAMLRequestWriter samlRequestWriter = new SAMLRequestWriter(
					xmlStreamWriter);
			samlRequestWriter.write(queryType);

			request = new String(baos.toByteArray());

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return request;
	}
	
	private SubjectType createSubject(String subjectId) {
		// Create a subject type
		SubjectType subject = new SubjectType();
		subject.setSubjectCategory("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");

		subject.getAttribute().addAll(getSubjectAttributes(subjectId));

		return subject;
	}

	
	private List<AttributeType> getSubjectAttributes(String subjectId) {
		List<AttributeType> attrList = new ArrayList<AttributeType>();

		// create the subject attributes

		// SubjectID
		AttributeType attSubjectID = RequestAttributeFactory
				.createStringAttributeType(
						"urn:oasis:names:tc:xacml:1.0:subject:subject-id",
						issuer, subjectId);

		// Role
		/*AttributeType attRole = RequestAttributeFactory
				.createStringAttributeType(
						"urn:tuwien:auto:smartwebgrid:subject:role", issuer,
						"SERVICE_CONSUMER");
		attrList.add(attRole);
		*/
		attrList.add(attSubjectID);

		return attrList;
	}

	private ResourceType createResource(String resourceId) {
		ResourceType resourceType = new ResourceType();

		AttributeType attResourceID = RequestAttributeFactory
		.createStringAttributeType(
				"urn:oasis:names:tc:xacml:1.0:resource:resource-id",
				issuer,
				resourceId);


		AttributeType attResourceServiceName = RequestAttributeFactory
				.createStringAttributeType(
						"urn:tuwien:auto:smartwebgrid:resource:servicename",
						issuer, this.serviceName);

		// Add the attributes into the resource
		resourceType.getAttribute().add(attResourceID);
		// resourceType.getAttribute().add(attResourceServiceName);

		return resourceType;
	}


	private ActionType createAction(String actionId) {
		ActionType actionType = new ActionType();
		AttributeType attActionID = RequestAttributeFactory
				.createStringAttributeType(
						"urn:oasis:names:tc:xacml:1.0:action:action-id",
						issuer, actionId);
		actionType.getAttribute().add(attActionID);
		return actionType;
	}


	private EnvironmentType createEnvironment() {
		EnvironmentType env = new EnvironmentType();

		AttributeType attFacility = RequestAttributeFactory
				.createStringAttributeType(
						"urn:va:xacml:2.0:interop:rsa8:environment:locality",
						issuer, "Facility A");

		env.getAttribute().add(attFacility);
		return env;
	}
}
