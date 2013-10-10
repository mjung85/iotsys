package at.ac.tuwien.auto.iotsys.xacml.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <Attribute xmlns="urn:oasis:names:tc:xacml:2.0:context:schema:os"
 * AttributeId="urn:tuwien:auto:smartwebgrid:subject:role"
 * DataType="http://www.w3.org/2001/XMLSchema#string" Issuer="redhatPdpEntity">
 * <AttributeValue
 * xmlns="urn:oasis:names:tc:xacml:2.0:context:schema:os">SERVICE_CONSUMER
 * </AttributeValue> </Attribute>
 * 
 * @author tom
 * 
 */
public class Attribute extends AbstractNode {

	private String urn;
	private String type;
	private String value;

	public Attribute(String urn, String type, String value) {
		this.urn = urn;
		this.type = type;
		this.value = value;
	}

	public Attribute(Document doc, String urn, String type, String value) {
		this(urn, type, value);
	}

	public Document getDocument() {
		return getDocument(getBuilder().newDocument());
	}

	/**
	 * Appends to the given {@link org.w3c.dom.Document} an XACML/SAML conform
	 * Attribute {@link org.w3c.dom.Element} which encapsulates an
	 * AttributeValue.
	 * 
	 * @param {@link org.w3c.dom.Document} reference which is used to create an
	 *        {@link org.w3c.dom.Element}
	 * @return document with appended Attribute Element.
	 */
	public Document getDocument(Document doc) {
		Element attribute = getElement(doc);

		doc.appendChild(attribute);
		return doc;
	}

	public Element getElement(Document doc) {
		Element attribute = doc.createElementNS(URN_XACML_CONTEXT, "Attribute");

		attribute.setAttribute("AttributeId", urn);
		attribute.setAttribute("DataType", type);
		String issuer = getIssuer();
		if (!issuer.isEmpty()) {
			attribute.setAttribute("Issuer", issuer);
		}
		attribute.setAttribute("xmlns", URN_XACML_CONTEXT);
		

		Element attributeValue = doc.createElementNS(URN_XACML_CONTEXT,
				"AttributeValue");
		attributeValue.setTextContent(value);
		attributeValue.setAttribute("xmlns", URN_XACML_CONTEXT);

		attribute.appendChild(attributeValue);

		return attribute;
	}

}
