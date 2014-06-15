package an.xacml.adapter.file.policy;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;

import java.lang.reflect.Constructor;

import org.w3c.dom.Element;

import an.xacml.Expression;
import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.Condition;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterCondition extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="Condition" type="xacml:ConditionType"/>
    <xs:complexType name="ConditionType">
        <xs:sequence>
            <xs:element ref="xacml:Expression"/>
        </xs:sequence>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "Condition";
    public FileAdapterCondition(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        XMLElement[] children = getChildElements();
        if (children.length == 1) {
            engineElem = new Condition((Expression)((DataAdapter)children[0]).getEngineElement());
            engineElem.setElementName(elem.getLocalName());
        }
        else {
            throw new PolicySyntaxException("Expected 1 expression element inside Condition, but we got " + 
                    children.length);
        }
    }

    public FileAdapterCondition(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Condition condition = (Condition)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();

        Expression exp = condition.getExpression();
        // Retrieve the corresponding DataAdapter class, then create an instance
        Class<?> dataAdapterClz = getPolicyDataAdapterClassByXACMLElementType(exp.getClass());
        Constructor<?> daConstr = dataAdapterClz.getConstructor(XACMLElement.class);
        DataAdapter da = (DataAdapter)daConstr.newInstance(exp);
        xmlElement.appendChild((Element)da.getDataStoreObject());
    }
}