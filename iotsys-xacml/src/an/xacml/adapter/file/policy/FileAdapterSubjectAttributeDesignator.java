package an.xacml.adapter.file.policy;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.policy.SubjectAttributeDesignator;

public class FileAdapterSubjectAttributeDesignator extends FileAdapterAttributeDesignator {
    /**
    <xs:element name="SubjectAttributeDesignator" type="xacml:SubjectAttributeDesignatorType" substitutionGroup="xacml:Expression"/>
    <xs:complexType name="SubjectAttributeDesignatorType">
        <xs:complexContent>
            <xs:extension base="xacml:AttributeDesignatorType">
                <xs:attribute name="SubjectCategory" type="xs:anyURI" use="optional" default="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "SubjectAttributeDesignator";
    public static final String ATTR_SUBJECTCATEGORY = "SubjectCategory";

    public FileAdapterSubjectAttributeDesignator(Element elem) throws PolicySyntaxException {
        initialize(elem);

        URI attrId = (URI)getAttributeValueByName(ATTR_ATTRIBUTEID);
        URI dataType = (URI)getAttributeValueByName(ATTR_DATATYPE);
        String issuer = (String)getAttributeValueByName(ATTR_ISSUER);
        boolean mustBePresent = (Boolean)getAttributeValueByName(ATTR_MUSTBEPRESENT);
        URI subjCategory = (URI)getAttributeValueByName(ATTR_SUBJECTCATEGORY);
        engineElem = new SubjectAttributeDesignator(attrId, dataType, subjCategory, issuer, mustBePresent);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterSubjectAttributeDesignator(XACMLElement engineElem) throws Exception {
        super(engineElem);
        this.engineElem.setElementName(ELEMENT_NAME);
        SubjectAttributeDesignator subjDes = (SubjectAttributeDesignator)engineElem;
        if (subjDes.getSubjectCategory() != null) {
            xmlElement.setAttribute(ATTR_SUBJECTCATEGORY, subjDes.getSubjectCategory().toString());
        }
    }
}