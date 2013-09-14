package an.xacml.adapter.file.policy;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;

import java.lang.reflect.Constructor;

import org.w3c.dom.Element;

import an.xacml.Expression;
import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.VariableDefinition;
import an.xml.XMLElement;

public class FileAdapterVariableDefinition extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="VariableDefinition" type="xacml:VariableDefinitionType"/>
    <xs:complexType name="VariableDefinitionType">
        <xs:sequence>
            <xs:element ref="xacml:Expression"/>
        </xs:sequence>
        <xs:attribute name="VariableId" type="xs:string" use="required"/>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "VariableDefinition";
    public static final String ATTR_VARIABLEID = "VariableId";

    public FileAdapterVariableDefinition(Element elem) throws PolicySyntaxException {
        initialize(elem);

        String varId = (String)getAttributeValueByName(ATTR_VARIABLEID);
        XMLElement[] children = getChildElements();
        if (children.length == 1) {
            engineElem = new VariableDefinition(varId, (Expression)((DataAdapter)children[0]).getEngineElement());
            engineElem.setElementName(elem.getLocalName());
        }
        else {
            throw new PolicySyntaxException("Expected 1 expression element inside VariableDefinition, but we got " + 
                    children.length);
        }
    }

    public FileAdapterVariableDefinition(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        VariableDefinition varDef = (VariableDefinition)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_VARIABLEID, varDef.getVariableId());

        Expression exp = varDef.getExpression();
        // Retrieve the corresponding DataAdapter class, then create an instance
        Class<?> dataAdapterClz = getPolicyDataAdapterClassByXACMLElementType(exp.getClass());
        Constructor<?> daConstr = dataAdapterClz.getConstructor(XACMLElement.class);
        DataAdapter da = (DataAdapter)daConstr.newInstance(exp);
        xmlElement.appendChild((Element)da.getDataStoreObject());
    }
}