package an.xacml.adapter.file.policy;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.policy.AttributeSelector;

public class FileAdapterAttributeSelector extends FileAdapterExpression {
    /**
    <xs:element name="AttributeSelector" type="xacml:AttributeSelectorType" substitutionGroup="xacml:Expression"/>
    <xs:complexType name="AttributeSelectorType">
        <xs:complexContent>
            <xs:extension base="xacml:ExpressionType">
                <xs:attribute name="RequestContextPath" type="xs:string" use="required"/>
                <xs:attribute name="DataType" type="xs:anyURI" use="required"/>
                <xs:attribute name="MustBePresent" type="xs:boolean" use="optional" default="false"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "AttributeSelector";
    public static final String ATTR_REQUESTCONTEXTPATH = "RequestContextPath";
    public static final String ATTR_DATATYPE = "DataType";
    public static final String ATTR_MUSTBEPRESENT = "MustBePresent";

    public FileAdapterAttributeSelector(Element elem) throws PolicySyntaxException {
        initialize(elem);

        String contextPath = (String)getAttributeValueByName(ATTR_REQUESTCONTEXTPATH);
        URI dataType = (URI)getAttributeValueByName(ATTR_DATATYPE);
        boolean mustBePresent = (Boolean)getAttributeValueByName(ATTR_MUSTBEPRESENT);
        engineElem = new AttributeSelector(contextPath, dataType, mustBePresent);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterAttributeSelector(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        AttributeSelector attrSel = (AttributeSelector)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_REQUESTCONTEXTPATH, attrSel.getRequestContentPath());
        xmlElement.setAttribute(ATTR_DATATYPE, attrSel.getDataType().toString());
        if (attrSel.isAttributeMustBePresent()) {
            xmlElement.setAttribute(ATTR_MUSTBEPRESENT, Boolean.TRUE.toString());
        }
    }
}