package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.policy.VariableReference;

public class FileAdapterVariableReference extends FileAdapterExpression {
    /**
    <xs:element name="VariableReference" type="xacml:VariableReferenceType" substitutionGroup="xacml:Expression"/>
    <xs:complexType name="VariableReferenceType">
        <xs:complexContent>
            <xs:extension base="xacml:ExpressionType">
                <xs:attribute name="VariableId" type="xs:string" use="required"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "VariableReference";
    public static final String ATTR_VARIABLEID = "VariableId";

    public FileAdapterVariableReference(Element elem) throws PolicySyntaxException {
        initialize(elem);

        String varId = (String)getAttributeValueByName(ATTR_VARIABLEID);
        engineElem = new VariableReference(varId);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterVariableReference(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        VariableReference varRef = (VariableReference)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_VARIABLEID, varRef.getVariableId());
    }
}