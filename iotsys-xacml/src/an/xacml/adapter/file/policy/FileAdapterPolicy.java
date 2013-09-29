package an.xacml.adapter.file.policy;

import static an.xacml.Constants.POLICY_SCHEMA_LOCATION;
import static an.xacml.Constants.POLICY_NAMESPACE;
import static an.xml.XMLParserWrapper.SCHEMA_LOCATION;
import static an.xml.XMLParserWrapper.getNamespaceMappings;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.adapter.file.TextXMLElement;
import an.xacml.policy.CombinerParameters;
import an.xacml.policy.Defaults;
import an.xacml.policy.Obligations;
import an.xacml.policy.Policy;
import an.xacml.policy.Rule;
import an.xacml.policy.RuleCombinerParameters;
import an.xacml.policy.Target;
import an.xacml.policy.VariableDefinition;
import an.xacml.policy.Version;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterPolicy extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="Policy" type="xacml:PolicyType"/>
    <xs:complexType name="PolicyType">
        <xs:sequence>
            <xs:element ref="xacml:Description" minOccurs="0"/>
            <xs:element ref="xacml:PolicyDefaults" minOccurs="0"/>
            <xs:element ref="xacml:CombinerParameters" minOccurs="0"/>
            <xs:element ref="xacml:Target"/>
            <xs:choice maxOccurs="unbounded">
                <xs:element ref="xacml:CombinerParameters" minOccurs="0"/>
                <xs:element ref="xacml:RuleCombinerParameters" minOccurs="0"/>
                <xs:element ref="xacml:VariableDefinition"/>
                <xs:element ref="xacml:Rule"/>
            </xs:choice>
            <xs:element ref="xacml:Obligations" minOccurs="0"/>
        </xs:sequence>
        <xs:attribute name="PolicyId" type="xs:anyURI" use="required"/>
        <xs:attribute name="Version" type="xacml:VersionType" default="1.0"/>
        <xs:attribute name="RuleCombiningAlgId" type="xs:anyURI" use="required"/>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "Policy";
    public static final String ATTR_POLICYID = "PolicyId";
    public static final String ATTR_VERSION = "Version";
    public static final String ATTR_RULECOMBININGALGID = "RuleCombiningAlgId";
    public static final String ELEMENT_DESCRIPTION = "Description";
    public static final String ELEMENT_COMBINERPARAMETERS = "CombinerParameters";

    public FileAdapterPolicy(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        URI policyId = (URI)getAttributeValueByName(ATTR_POLICYID);
        Version ver = (Version)getAttributeValueByName(ATTR_VERSION);
        URI ruleComId = (URI)getAttributeValueByName(ATTR_RULECOMBININGALGID);

        XMLElement chDesc = getSingleXMLElementByType(TextXMLElement.class);
        String desc = chDesc == null ? null : chDesc.getTextValue();
        XMLElement chDef = getSingleXMLElementByType(FileAdapterDefaults.class);
        Defaults defaults = chDef == null ? null : (Defaults)((DataAdapter)chDef).getEngineElement();
        // Since RuleCombinerParameters inherit from CombinerParameters, the method will return 2 items. We have to get
        // it by element name. And since the CombinerParameters have been merged in initialize, we will get only one
        // element.
        XMLElement chCom = getSingleXMLElementByName(ELEMENT_COMBINERPARAMETERS);
        CombinerParameters eeCom = chCom == null ? null : (CombinerParameters)((DataAdapter)chCom).getEngineElement();
        XMLElement chTarget = getSingleXMLElementByType(FileAdapterTarget.class);
        Target target = (Target)((DataAdapter)chTarget).getEngineElement();
        // The policy XMLSchema allows multiple RuleCombinerParameters elements, but XACML standard doesn't allow that.
        XMLElement chRCom = getSingleXMLElementByType(FileAdapterRuleCombinerParameters.class);
        RuleCombinerParameters eeRCom = chRCom == null ? null :
            (RuleCombinerParameters)((DataAdapter)chRCom).getEngineElement();
        XMLElement[] chVD = getXMLElementsByType(FileAdapterVariableDefinition.class);
        VariableDefinition[] varDefs = new VariableDefinition[chVD.length];
        for (int i = 0; i < chVD.length; i ++) {
            varDefs[i] = (VariableDefinition)((DataAdapter)chVD[i]).getEngineElement();
        }

        XMLElement[] chRule = getXMLElementsByType(FileAdapterRule.class);
        Rule[] rules = new Rule[chRule.length];
        for (int i = 0; i < chRule.length; i ++) {
            rules[i] = (Rule)((DataAdapter)chRule[i]).getEngineElement();
        }

        // The policy XMLSchema allows multiple Obligations elements, but XACML standard doesn't allow that.
        XMLElement chObls = getSingleXMLElementByType(FileAdapterObligations.class);
        Obligations obls = chObls == null ? null : (Obligations)((DataAdapter)chObls).getEngineElement();

        engineElem = new Policy(policyId, ver, ruleComId, desc, defaults, eeCom, eeRCom, target, varDefs, rules, obls);
        engineElem.setElementName(elem.getLocalName());
        // Set name space mapping for attribute selector
        ((Policy)engineElem).setPolicyNamespaceMappings(getNamespaceMappings(elem));
    }

    public FileAdapterPolicy(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Policy policy = (Policy)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        // Set the schema location
        xmlElement.setAttributeNS(W3C_XML_SCHEMA_INSTANCE_NS_URI, SCHEMA_LOCATION,
                POLICY_NAMESPACE + " " + POLICY_SCHEMA_LOCATION);

        xmlElement.setAttribute(ATTR_POLICYID, policy.getId().toString());
        if (policy.getPolicyVersion() != null) {
            xmlElement.setAttribute(ATTR_VERSION, policy.getPolicyVersion().getVersionValue());
        }
        xmlElement.setAttribute(ATTR_RULECOMBININGALGID, policy.getRuleCombiningAlgId().toString());
        if (policy.getDescription() != null) {
            Element desc = getDefaultDocument().createElementNS(POLICY_NAMESPACE, ELEMENT_DESCRIPTION);
            desc.appendChild(getDefaultDocument().createTextNode(policy.getDescription()));
            xmlElement.appendChild(desc);
        }
        if (policy.getDefaults() != null) {
            xmlElement.appendChild((Element)new FileAdapterDefaults(policy.getDefaults()).getDataStoreObject());
        }
        if (policy.getCombinerParameters() != null) {
            xmlElement.appendChild((Element)new FileAdapterCombinerParameters(policy.getCombinerParameters()).getDataStoreObject());
        }
        xmlElement.appendChild((Element)new FileAdapterTarget(policy.getTarget()).getDataStoreObject());
        if (policy.getRuleCombinerParameters() != null) {
            xmlElement.appendChild((Element)new FileAdapterRuleCombinerParameters(policy.getRuleCombinerParameters()).getDataStoreObject());
        }
        VariableDefinition[] varDefs = policy.getVariableDefinitions();
        if (varDefs != null && varDefs.length > 0) {
            for (int i = 0; i < varDefs.length; i ++) {
                xmlElement.appendChild((Element)new FileAdapterVariableDefinition(varDefs[i]).getDataStoreObject());
            }
        }
        Rule[] rules = policy.getRules();
        if (rules != null && rules.length > 0) {
            for (int i = 0; i < rules.length; i ++) {
                xmlElement.appendChild((Element)new FileAdapterRule(rules[i]).getDataStoreObject());
            }
        }
        if (policy.getObligations() != null) {
            xmlElement.appendChild((Element)new FileAdapterObligations(policy.getObligations()).getDataStoreObject());
        }

        // populate namespaces
        populateNamespaceMappings2XMLElement(policy, xmlElement);
    }
}