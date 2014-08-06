package an.xacml.adapter.file.context;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.context.Attribute;
import an.xacml.context.Subject;
import an.xacml.context.TargetElement;

public class FileAdapterSubject extends FileAdapterTargetElement {
    /**
    <xs:element name="Subject" type="xacml-context:SubjectType"/>
    <xs:complexType name="SubjectType">
        <xs:sequence>
            <xs:element ref="xacml-context:Attribute" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
        <xs:attribute name="SubjectCategory" type="xs:anyURI" default="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"/>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "Subject";
    public static final String ATTR_SUBJECTCATEGORY = "SubjectCategory";

    public FileAdapterSubject(Element elem) throws PolicySyntaxException {
        initialize(elem);

        URI subjCategory = (URI)getAttributeValueByName(ATTR_SUBJECTCATEGORY);
        Attribute[] attrs = extractAttributes();
        engineElem = new Subject(subjCategory, attrs);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterSubject(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Subject subj = (Subject)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        if (subj.getSubjectCategory() != null) {
            xmlElement.setAttribute(ATTR_SUBJECTCATEGORY, subj.getSubjectCategory().toString());
        }
        populateAttributes((TargetElement)engineElem);
    }
}