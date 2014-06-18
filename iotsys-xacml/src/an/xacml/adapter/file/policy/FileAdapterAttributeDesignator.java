package an.xacml.adapter.file.policy;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.policy.AttributeDesignator;

public class FileAdapterAttributeDesignator extends FileAdapterExpression {
    /**
    <xs:complexType name="AttributeDesignatorType">
        <xs:complexContent>
            <xs:extension base="xacml:ExpressionType">
                <xs:attribute name="AttributeId" type="xs:anyURI" use="required"/>
                <xs:attribute name="DataType" type="xs:anyURI" use="required"/>
                <xs:attribute name="Issuer" type="xs:string" use="optional"/>
                <xs:attribute name="MustBePresent" type="xs:boolean" use="optional" default="false"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "AttributeDesignator";
    public static final String ATTR_ATTRIBUTEID = "AttributeId";
    public static final String ATTR_DATATYPE = "DataType";
    public static final String ATTR_ISSUER = "Issuer";
    public static final String ATTR_MUSTBEPRESENT = "MustBePresent";

    protected FileAdapterAttributeDesignator() {}

    public FileAdapterAttributeDesignator(Element elem) throws PolicySyntaxException {
        initialize(elem);

        URI attrId = (URI)getAttributeValueByName(ATTR_ATTRIBUTEID);
        URI dataType = (URI)getAttributeValueByName(ATTR_DATATYPE);
        String issuer = (String)getAttributeValueByName(ATTR_ISSUER);
        boolean mustBePresent = (Boolean)getAttributeValueByName(ATTR_MUSTBEPRESENT);
        engineElem = new AttributeDesignator(attrId, dataType, issuer, mustBePresent);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterAttributeDesignator(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        AttributeDesignator attrDes = (AttributeDesignator)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_ATTRIBUTEID, attrDes.getAttributeID().toString());
        xmlElement.setAttribute(ATTR_DATATYPE, attrDes.getDataType().toString());
        if (attrDes.getIssuer() != null) {
            xmlElement.setAttribute(ATTR_ISSUER, attrDes.getIssuer());
        }
        if (attrDes.isAttributeMustBePresent()) {
            xmlElement.setAttribute(ATTR_MUSTBEPRESENT, Boolean.TRUE.toString());
        }
    }
}