package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.AttributeValue;
import an.xacml.policy.CombinerParameter;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterCombinerParameter extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="CombinerParameter" type="xacml:CombinerParameterType"/>
    <xs:complexType name="CombinerParameterType">
        <xs:sequence>
            <xs:element ref="xacml:AttributeValue"/>
        </xs:sequence>
        <xs:attribute name="ParameterName" type="xs:string" use="required"/>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "CombinerParameter";
    public static final String ATTR_PARAMETERNAME = "ParameterName";

    public FileAdapterCombinerParameter(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        String paramName = (String)getAttributeValueByName(ATTR_PARAMETERNAME);
        XMLElement attrValue = getSingleXMLElementByType(FileAdapterAttributeValue.class);
        engineElem = new CombinerParameter(paramName, (AttributeValue)((DataAdapter)attrValue).getEngineElement());
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterCombinerParameter(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        CombinerParameter cp = (CombinerParameter)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_PARAMETERNAME, cp.getParameterName());
        xmlElement.appendChild((Element)new FileAdapterAttributeValue(cp.getValue()).getDataStoreObject());
    }
}