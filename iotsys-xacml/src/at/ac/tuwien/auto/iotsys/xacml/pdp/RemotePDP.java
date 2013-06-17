package at.ac.tuwien.auto.iotsys.xacml.pdp;

import java.net.URL;
import java.util.Map;

import at.ac.tuwien.auto.iotsys.clients.pdp.DecisionType;
import at.ac.tuwien.auto.iotsys.clients.pdp.PDP;
import at.ac.tuwien.auto.iotsys.clients.pdp.PDPBeanService;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Parameter;
import at.ac.tuwien.auto.iotsys.xacml.util.XacmlRequest;

public class RemotePDP implements Pdp {

	private URL wsdl = null;
	
	public RemotePDP() {
		
	}
	
	public RemotePDP(URL wsdlLocation) {
		wsdl = wsdlLocation;
	}

	@Override
	public synchronized boolean evaluate(String resource, String subject, String action,
			Map<Parameter, String> params) {

		XacmlRequest xRequest = new XacmlRequest();
		xRequest.addSubjectAttribute(XacmlRequest.SUBJECT_ATTRIBUTE_ID,
				XacmlRequest.XML_SCHEMA_TYPE_STRING, subject);
		xRequest.addResourceAttribute(XacmlRequest.RESOURCE_ATTRIBUTE_ID,
				XacmlRequest.XML_SCHEMA_TYPE_STRING, resource);
		xRequest.addActionAttribute(XacmlRequest.ACTION_ATTRIBUTE_ID,
				XacmlRequest.XML_SCHEMA_TYPE_STRING, action);

		for (Parameter p : params.keySet()) {
			switch (p) {
				case SUBJECT_IP_ADDRESS:
					xRequest.addSubjectAttribute(
							XacmlRequest.SUBJECT_ATTRIBUTE_IP_ADDRESS,
							XacmlRequest.XML_SCHEMA_TYPE_STRING, params.get(p));
					break;
	
				case RESOURCE_HOSTNAME:
					xRequest.addResourceAttribute(
							XacmlRequest.RESOURCE_ATTRIBUTE_HOSTNAME,
							XacmlRequest.URN_XACML_CONTEXT, params.get(p));
					break;
				case RESOURCE_IP_ADDRESS:
					xRequest.addResourceAttribute(
							XacmlRequest.RESOURCE_ATTRIBUTE_IP_ADDRESS,
							XacmlRequest.URN_XACML_CONTEXT, params.get(p));
					break;
				case RESOURCE_PATH:
					xRequest.addResourceAttribute(
							XacmlRequest.RESOURCE_ATTRIBUTE_PATH,
							XacmlRequest.URN_XACML_CONTEXT, params.get(p));
					break;
				case RESOURCE_PROTOCOL:
					xRequest.addResourceAttribute(
							XacmlRequest.RESOURCE_ATTRIBUTE_PROTOCOL,
							XacmlRequest.URN_XACML_CONTEXT, params.get(p));
					break;
				case RESOURCE_SERVICENAME:
					xRequest.addResourceAttribute(
							XacmlRequest.RESOURCE_ATTRIBUTE_PROTOCOL,
							XacmlRequest.URN_XACML_CONTEXT, params.get(p));
					break;
			}
		}

		PDPBeanService pdpService;
		if (wsdl != null) {
			pdpService = new PDPBeanService(wsdl);
		} else {
			pdpService = new PDPBeanService();
		}
		PDP pdp = pdpService.getPDPPort();

		DecisionType dt = pdp.authorize(xRequest.getRequestAbstract());
		if (!dt.equals(DecisionType.PERMIT)) {
			return false;
		}
		return true;
	}
}
