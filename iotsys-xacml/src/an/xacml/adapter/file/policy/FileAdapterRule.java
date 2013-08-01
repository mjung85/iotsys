package an.xacml.adapter.file.policy;

import static an.xacml.Constants.POLICY_NAMESPACE;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.adapter.file.TextXMLElement;
import an.xacml.policy.Condition;
import an.xacml.policy.Effect;
import an.xacml.policy.Rule;
import an.xacml.policy.Target;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterRule extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="Rule" type="xacml:RuleType"/>
    <xs:complexType name="RuleType">
        <xs:sequence>
            <xs:element ref="xacml:Description" minOccurs="0"/>
            <xs:element ref="xacml:Target" minOccurs="0"/>
            <xs:element ref="xacml:Condition" minOccurs="0"/>
        </xs:sequence>
        <xs:attribute name="RuleId" type="xs:string" use="required"/>
        <xs:attribute name="Effect" type="xacml:EffectType" use="required"/>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "Rule";
    public static final String ATTR_RULEID = "RuleId";
    public static final String ATTR_EFFECT = "Effect";
    public static final String ELEMENT_DESCRIPTION = "Description";

    public FileAdapterRule(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        String ruleId = (String)getAttributeValueByName(ATTR_RULEID);
        Effect effect = (Effect)getAttributeValueByName(ATTR_EFFECT);

        XMLElement chDesc = getSingleXMLElementByType(TextXMLElement.class);
        XMLElement chTarget = getSingleXMLElementByType(FileAdapterTarget.class);
        XMLElement chCond = getSingleXMLElementByType(FileAdapterCondition.class);

        String desc = chDesc == null ? null : ((TextXMLElement)chDesc).getTextValue();
        Target target = chTarget == null ? null : (Target)((DataAdapter)chTarget).getEngineElement();
        Condition cond = chCond == null ? null : (Condition)((DataAdapter)chCond).getEngineElement();
        engineElem = new Rule(ruleId, effect, desc, target, cond);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterRule(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Rule rule = (Rule)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_RULEID, rule.getRuleId());
        xmlElement.setAttribute(ATTR_EFFECT, rule.getEffect().toString());

        if (rule.getDescription() != null) {
            Element desc = getDefaultDocument().createElementNS(POLICY_NAMESPACE, ELEMENT_DESCRIPTION);
            desc.appendChild(getDefaultDocument().createTextNode(rule.getDescription()));
            xmlElement.appendChild(desc);
        }
        if (rule.getTarget() != null) {
            xmlElement.appendChild((Element)new FileAdapterTarget(rule.getTarget()).getDataStoreObject());
        }
        if (rule.getCondition() != null) {
            xmlElement.appendChild((Element)new FileAdapterCondition(rule.getCondition()).getDataStoreObject());
        }
    }
}