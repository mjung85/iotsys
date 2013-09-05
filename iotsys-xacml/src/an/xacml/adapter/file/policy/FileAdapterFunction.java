package an.xacml.adapter.file.policy;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.policy.Function;

public class FileAdapterFunction extends FileAdapterExpression {
    /**
    <xs:element name="Function" type="xacml:FunctionType" substitutionGroup="xacml:Expression"/>
    <xs:complexType name="FunctionType">
        <xs:complexContent>
            <xs:extension base="xacml:ExpressionType">
                <xs:attribute name="FunctionId" type="xs:anyURI" use="required"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "Function";
    public static final String ATTR_FUNCTIONID = "FunctionId";

    public FileAdapterFunction(Element elem) throws PolicySyntaxException {
        initialize(elem);

        URI funcId = (URI)getAttributeValueByName(ATTR_FUNCTIONID);
        engineElem = new Function(funcId);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterFunction(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Function function = (Function)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_FUNCTIONID, function.getFunctionId().toString());
    }
}