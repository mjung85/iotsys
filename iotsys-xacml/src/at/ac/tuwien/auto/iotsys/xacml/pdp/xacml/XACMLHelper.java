package at.ac.tuwien.auto.iotsys.xacml.pdp.xacml;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamWriter;

import org.jboss.security.xacml.core.model.context.RequestType;
import org.picketlink.identity.federation.core.exceptions.ConfigurationException;
import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.saml.v2.common.IDGenerator;
import org.picketlink.identity.federation.core.saml.v2.util.XMLTimeUtil;
import org.picketlink.identity.federation.core.saml.v2.writers.SAMLRequestWriter;
import org.picketlink.identity.federation.core.util.StaxUtil;
import org.picketlink.identity.federation.saml.v2.assertion.NameIDType;
import org.picketlink.identity.federation.saml.v2.protocol.XACMLAuthzDecisionQueryType;

public class XACMLHelper {
	
	private static String convertRequestTypeToString(RequestType rt, String issuer) {

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

}
