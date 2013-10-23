package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.CombinerParameter;
import an.xacml.policy.RuleCombinerParameters;
import an.xml.XMLElement;

public class FileAdapterRuleCombinerParameters extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="RuleCombinerParameters" type="xacml:RuleCombinerParametersType"/>
    <xs:complexType name="RuleCombinerParametersType">
        <xs:complexContent>
            <xs:extension base="xacml:CombinerParametersType">
                <xs:attribute name="RuleIdRef" type="xs:string" use="required"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "RuleCombinerParameters";
    public static final String ATTR_RULEIDREF = "RuleIdRef";

    public FileAdapterRuleCombinerParameters(Element elem) throws PolicySyntaxException {
        initialize(elem);

        String ruleId = (String)getAttributeValueByName(ATTR_RULEIDREF);

        XMLElement[] children = getChildElements();
        CombinerParameter[] params = new CombinerParameter[children.length];
        for (int i = 0; i < children.length; i ++) {
            params[i] = (CombinerParameter)((DataAdapter)children[i]).getEngineElement();
        }
        engineElem = new RuleCombinerParameters(ruleId, params);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterRuleCombinerParameters(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        RuleCombinerParameters rcp = (RuleCombinerParameters)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_RULEIDREF, rcp.getRuleId());
        CombinerParameter[] params = rcp.getCrudeParameters();
        for (int i = 0; i < params.length; i ++) {
            xmlElement.appendChild((Element)new FileAdapterCombinerParameter(params[i]).getDataStoreObject());
        }
    }
}