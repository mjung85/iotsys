package an.xacml.adapter.file.policy;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;

import java.lang.reflect.Constructor;
import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.Expression;
import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.Apply;
import an.xml.XMLElement;

public class FileAdapterApply extends FileAdapterExpression {
    /**
    <xs:element name="Apply" type="xacml:ApplyType" substitutionGroup="xacml:Expression"/>
    <xs:complexType name="ApplyType">
        <xs:complexContent>
            <xs:extension base="xacml:ExpressionType">
                <xs:sequence>
                    <xs:element ref="xacml:Expression" minOccurs="0" maxOccurs="unbounded"/>
                </xs:sequence>
                <xs:attribute name="FunctionId" type="xs:anyURI" use="required"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "Apply";
    public static final String ATTR_FUNCTIONID = "FunctionId";

    public FileAdapterApply(Element elem) throws PolicySyntaxException {
        initialize(elem);

        URI funcId = (URI)getAttributeValueByName(ATTR_FUNCTIONID);

        XMLElement[] children = getChildElements();
        Expression[] exps = new Expression[children.length];
        for (int i = 0; i < exps.length; i ++) {
            exps[i] = (Expression)((DataAdapter)children[i]).getEngineElement();
        }
        engineElem = new Apply(funcId, exps);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterApply(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Apply apply = (Apply)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_FUNCTIONID, apply.getFunctionId().toString());

        Expression[] exps = apply.getExpressions();
        for (int i = 0; i < exps.length; i ++) {
            // Retrieve the corresponding DataAdapter class, then create an instance
            Class<?> dataAdapterClz = getPolicyDataAdapterClassByXACMLElementType(exps[i].getClass());
            Constructor<?> daConstr = dataAdapterClz.getConstructor(XACMLElement.class);
            DataAdapter da = (DataAdapter)daConstr.newInstance(exps[i]);
            xmlElement.appendChild((Element)da.getDataStoreObject());
        }
    }
}