package an.xacml.adapter.file.context;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.context.StatusCode;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterStatusCode extends AbstractFileAdapterContextElement {
    /**
    <xs:element name="StatusCode" type="xacml-context:StatusCodeType"/>
    <xs:complexType name="StatusCodeType">
        <xs:sequence>
            <xs:element ref="xacml-context:StatusCode" minOccurs="0"/>
        </xs:sequence>
        <xs:attribute name="Value" type="xs:anyURI" use="required"/>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "StatusCode";
    public static final String ATTR_VALUE = "Value";

    public FileAdapterStatusCode(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        URI value = (URI)getAttributeValueByName(ATTR_VALUE);
        XMLElement chChild = getSingleXMLElementByType(FileAdapterStatusCode.class);

        StatusCode child = chChild == null ? null : (StatusCode)((DataAdapter)chChild).getEngineElement();

        engineElem = new StatusCode(value, child);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterStatusCode(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        StatusCode code = (StatusCode)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        xmlElement.setAttribute(ATTR_VALUE, code.getValue().toString());
        if (code.getChild() != null) {
            xmlElement.appendChild((Element)new FileAdapterStatusCode(code.getChild()).getDataStoreObject());
        }
    }
}