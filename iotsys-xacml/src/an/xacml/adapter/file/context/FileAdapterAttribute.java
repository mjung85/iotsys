package an.xacml.adapter.file.context;

import static an.xacml.Constants.CONTEXT_NAMESPACE;

import java.net.URI;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import an.xacml.XACMLElement;
import an.xacml.context.Attribute;
import an.xml.XMLParserWrapper;

public class FileAdapterAttribute extends AbstractFileAdapterContextElement {
    /**
    <xs:element name="Attribute" type="xacml-context:AttributeType"/>
    <xs:complexType name="AttributeType">
        <xs:sequence>
            <xs:element ref="xacml-context:AttributeValue" maxOccurs="unbounded"/>
        </xs:sequence>
        <xs:attribute name="AttributeId" type="xs:anyURI" use="required"/>
        <xs:attribute name="DataType" type="xs:anyURI" use="required"/>
        <xs:attribute name="Issuer" type="xs:string" use="optional"/>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "Attribute";
    public static final String ATTR_ATTRIBUTEID = "AttributeId";
    public static final String ATTR_DATATYPE = "DataType";
    public static final String ATTR_ISSUER = "Issuer";
    public static final String ELEMENT_ATTRIBUTEVALUE = "AttributeValue";

    public FileAdapterAttribute(Element elem) throws Exception {
        this.elementName = elem.getLocalName();
        this.elementNamespaceURI = elem.getNamespaceURI();

        URI attrId = new URI(elem.getAttribute(ATTR_ATTRIBUTEID));
        URI dataType = new URI(elem.getAttribute(ATTR_DATATYPE));
        String issuer = elem.getAttribute(ATTR_ISSUER);

        NodeList attrValues = elem.getElementsByTagNameNS(this.elementNamespaceURI, ELEMENT_ATTRIBUTEVALUE);
        int len = attrValues.getLength();
        String[] values = new String[len];
        for (int i = 0; i < len; i ++) {
            values[i] = XMLParserWrapper.getNodeContentAsText(attrValues.item(i));
        }
        engineElem = new Attribute(attrId, dataType, issuer, values);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterAttribute(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Attribute attr = (Attribute)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        xmlElement.setAttribute(ATTR_ATTRIBUTEID, attr.getAttributeID().toString());
        xmlElement.setAttribute(ATTR_DATATYPE, attr.getDataType().toString());
        if (attr.getIssuer() != null && attr.getIssuer().length() > 0) {
            xmlElement.setAttribute(ATTR_ISSUER, attr.getIssuer());
        }
        Object[] attrVals = attr.getAttributeValues();
        if (attrVals != null && attrVals.length > 0) {
            for (int i = 0; i < attrVals.length; i ++) {
                Element elem = getDefaultDocument().createElementNS(CONTEXT_NAMESPACE, ELEMENT_ATTRIBUTEVALUE);
                elem.appendChild(getDefaultDocument().createTextNode(attrVals[i].toString()));
                xmlElement.appendChild(elem);
            }
        }
    }
}