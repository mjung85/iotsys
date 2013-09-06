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
import an.xacml.policy.AbstractPolicy;
import an.xacml.policy.CombinerParameters;
import an.xacml.policy.Defaults;
import an.xacml.policy.IdReference;
import an.xacml.policy.Obligations;
import an.xacml.policy.Policy;
import an.xacml.policy.PolicyCombinerParameters;
import an.xacml.policy.PolicySet;
import an.xacml.policy.PolicySetCombinerParameters;
import an.xacml.policy.Target;
import an.xacml.policy.Version;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterPolicySet extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="PolicySet" type="xacml:PolicySetType"/>
    <xs:complexType name="PolicySetType">
        <xs:sequence>
            <xs:element ref="xacml:Description" minOccurs="0"/>
            <xs:element ref="xacml:PolicySetDefaults" minOccurs="0"/>
            <xs:element ref="xacml:Target"/>
            <xs:choice minOccurs="0" maxOccurs="unbounded">
                <xs:element ref="xacml:PolicySet"/>
                <xs:element ref="xacml:Policy"/>
                <xs:element ref="xacml:PolicySetIdReference"/>
                <xs:element ref="xacml:PolicyIdReference"/>
                <xs:element ref="xacml:CombinerParameters"/>
                <xs:element ref="xacml:PolicyCombinerParameters"/>
                <xs:element ref="xacml:PolicySetCombinerParameters"/>
            </xs:choice>
            <xs:element ref="xacml:Obligations" minOccurs="0"/>
        </xs:sequence>
        <xs:attribute name="PolicySetId" type="xs:anyURI" use="required"/>
        <xs:attribute name="Version" type="xacml:VersionType" default="1.0"/>
        <xs:attribute name="PolicyCombiningAlgId" type="xs:anyURI" use="required"/>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "PolicySet";
    public static final String ATTR_POLICYSETID = "PolicySetId";
    public static final String ATTR_VERSION = "Version";
    public static final String ATTR_POLICYCOMBININGALGID = "PolicyCombiningAlgId";

    public static final String ELEMENT_DESCRIPTION = "Description";
    public static final String ELEMENT_POLICYSETIDREFERENCE = "PolicySetIdReference";
    public static final String ELEMENT_POLICYIDREFERENCE = "PolicyIdReference";
    public static final String ELEMENT_COMBINERPARAMETERS = "CombinerParameters";

    public FileAdapterPolicySet(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        URI id = (URI)getAttributeValueByName(ATTR_POLICYSETID);
        Version ver = (Version)getAttributeValueByName(ATTR_VERSION);
        URI policyComId = (URI)getAttributeValueByName(ATTR_POLICYCOMBININGALGID);

        XMLElement chDesc = getSingleXMLElementByType(TextXMLElement.class);
        String desc = chDesc == null ? null : chDesc.getTextValue();
        XMLElement chDef = getSingleXMLElementByType(FileAdapterDefaults.class);
        Defaults defaults = chDef == null ? null : (Defaults)((DataAdapter)chDef).getEngineElement();
        XMLElement chTarget = getSingleXMLElementByType(FileAdapterTarget.class);
        Target target = (Target)((DataAdapter)chTarget).getEngineElement();

        // Since PolicyCombinerParameters inherit from CombinerParameters, the method will return multiple items. We
        // have to get it by element name. And since the CombinerParameters have been merged in initialize, we will get
        // only one element.
        XMLElement chCom = getSingleXMLElementByName(ELEMENT_COMBINERPARAMETERS);
        CombinerParameters eeCom = chCom == null ? null : (CombinerParameters)((DataAdapter)chCom).getEngineElement();
        // The policy XMLSchema allows multiple PolicyCombinerParameters elements, but XACML standard doesn't allow that.
        XMLElement chPCom = getSingleXMLElementByType(FileAdapterPolicyCombinerParameters.class);
        PolicyCombinerParameters eePCom = chPCom == null ? null :
            (PolicyCombinerParameters)((DataAdapter)chPCom).getEngineElement();
        XMLElement chPSetCom = getSingleXMLElementByType(FileAdapterPolicySetCombinerParameters.class);
        PolicySetCombinerParameters eePSetCom = chPSetCom == null ? null :
            (PolicySetCombinerParameters)((DataAdapter)chPSetCom).getEngineElement();

        // The policy XMLSchema allows multiple Obligations elements, but XACML standard doesn't allow that.
        XMLElement chObls = getSingleXMLElementByType(FileAdapterObligations.class);
        Obligations obls = chObls == null ? null : (Obligations)((DataAdapter)chObls).getEngineElement();

        // need to keep child policies order in engine element.
        engineElem = new PolicySet(id, ver, policyComId, desc, defaults, target, eeCom, eePCom, eePSetCom, obls);

        XMLElement[] allChildren = getChildElements();
        for (XMLElement child : allChildren) {
            if (child instanceof FileAdapterPolicySet || child instanceof FileAdapterPolicy) {
                ((PolicySet)engineElem).addPolicy((AbstractPolicy)((DataAdapter)child).getEngineElement());
            }
            else if (child instanceof FileAdapterIdReference) {
                ((PolicySet)engineElem).addPolicyIdReference((IdReference)((DataAdapter)child).getEngineElement());
            }
        }

        engineElem.setElementName(elem.getLocalName());
        // Set name space mapping for attribute selector
        ((PolicySet)engineElem).setPolicyNamespaceMappings(getNamespaceMappings(elem));
    }

    public FileAdapterPolicySet(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        PolicySet policySet = (PolicySet)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        // Set the schema location
        xmlElement.setAttributeNS(W3C_XML_SCHEMA_INSTANCE_NS_URI, SCHEMA_LOCATION,
                POLICY_NAMESPACE + " " + POLICY_SCHEMA_LOCATION);

        xmlElement.setAttribute(ATTR_POLICYSETID, policySet.getId().toString());
        if (policySet.getPolicyVersion() != null) {
            xmlElement.setAttribute(ATTR_VERSION, policySet.getPolicyVersion().getVersionValue());
        }
        xmlElement.setAttribute(ATTR_POLICYCOMBININGALGID, policySet.getPolicyCombiningAlgId().toString());
        if (policySet.getDescription() != null) {
            Element desc = getDefaultDocument().createElementNS(POLICY_NAMESPACE, ELEMENT_DESCRIPTION);
            desc.appendChild(getDefaultDocument().createTextNode(policySet.getDescription()));
            xmlElement.appendChild(desc);
        }
        if (policySet.getDefaults() != null) {
            xmlElement.appendChild((Element)new FileAdapterDefaults(policySet.getDefaults()).getDataStoreObject());
        }
        xmlElement.appendChild((Element)new FileAdapterTarget(policySet.getTarget()).getDataStoreObject());

        // need to keep child policies order in XACML element.
        XACMLElement[] allCrudePolicies = policySet.getAllCrudeChildPolicies();
        for (XACMLElement policyElem : allCrudePolicies) {
            if (policyElem instanceof PolicySet) {
                xmlElement.appendChild((Element)new FileAdapterPolicySet(policyElem).getDataStoreObject());
            }
            else if (policyElem instanceof Policy) {
                xmlElement.appendChild((Element)new FileAdapterPolicy(policyElem).getDataStoreObject());
            }
            else {
                xmlElement.appendChild((Element)new FileAdapterIdReference(policyElem).getDataStoreObject());
            }
        }

        if (policySet.getCombinerParameters() != null) {
            xmlElement.appendChild((Element)new FileAdapterCombinerParameters(policySet.getCombinerParameters()).getDataStoreObject());
        }
        if (policySet.getPolicyCombinerParameters() != null) {
            xmlElement.appendChild((Element)new FileAdapterPolicyCombinerParameters(policySet.getPolicyCombinerParameters()).getDataStoreObject());
        }
        if (policySet.getPolicySetCombinerParameters() != null) {
            xmlElement.appendChild((Element)new FileAdapterPolicySetCombinerParameters(policySet.getPolicySetCombinerParameters()).getDataStoreObject());
        }
        if (policySet.getObligations() != null) {
            xmlElement.appendChild((Element)new FileAdapterObligations(policySet.getObligations()).getDataStoreObject());
        }

        // populate namespaces
        populateNamespaceMappings2XMLElement(policySet, xmlElement);
    }
}