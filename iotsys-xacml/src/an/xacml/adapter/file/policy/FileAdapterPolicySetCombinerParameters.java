package an.xacml.adapter.file.policy;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.CombinerParameter;
import an.xacml.policy.PolicySetCombinerParameters;
import an.xml.XMLElement;

public class FileAdapterPolicySetCombinerParameters extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="PolicySetCombinerParameters" type="xacml:PolicySetCombinerParametersType"/>
    <xs:complexType name="PolicySetCombinerParametersType">
        <xs:complexContent>
            <xs:extension base="xacml:CombinerParametersType">
                <xs:attribute name="PolicySetIdRef" type="xs:anyURI" use="required"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "PolicySetCombinerParameters";
    public static final String ATTR_POLICYSETIDREF = "PolicySetIdRef";

    public FileAdapterPolicySetCombinerParameters(Element elem) throws PolicySyntaxException {
        initialize(elem);

        URI policySetId = (URI)getAttributeValueByName(ATTR_POLICYSETIDREF);

        XMLElement[] children = getChildElements();
        CombinerParameter[] params = new CombinerParameter[children.length];
        for (int i = 0; i < children.length; i ++) {
            params[i] = (CombinerParameter)((DataAdapter)children[i]).getEngineElement();
        }
        engineElem = new PolicySetCombinerParameters(policySetId, params);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterPolicySetCombinerParameters(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        PolicySetCombinerParameters psp = (PolicySetCombinerParameters)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_POLICYSETIDREF, psp.getPolicySetId().toString());
        CombinerParameter[] params = psp.getCrudeParameters();
        for (int i = 0; i < params.length; i ++) {
            xmlElement.appendChild((Element)new FileAdapterCombinerParameter(params[i]).getDataStoreObject());
        }
    }
}