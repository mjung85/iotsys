package an.xacml.adapter.file.policy;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.CombinerParameter;
import an.xacml.policy.PolicyCombinerParameters;
import an.xml.XMLElement;

public class FileAdapterPolicyCombinerParameters extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="PolicyCombinerParameters" type="xacml:PolicyCombinerParametersType"/>
    <xs:complexType name="PolicyCombinerParametersType">
        <xs:complexContent>
            <xs:extension base="xacml:CombinerParametersType">
                <xs:attribute name="PolicyIdRef" type="xs:anyURI" use="required"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "PolicyCombinerParameters";
    public static final String ATTR_POLICYIDREF = "PolicyIdRef";

    public FileAdapterPolicyCombinerParameters(Element elem) throws PolicySyntaxException {
        initialize(elem);

        URI policyId = (URI)getAttributeValueByName(ATTR_POLICYIDREF);

        XMLElement[] children = getChildElements();
        CombinerParameter[] params = new CombinerParameter[children.length];
        for (int i = 0; i < children.length; i ++) {
            params[i] = (CombinerParameter)((DataAdapter)children[i]).getEngineElement();
        }
        engineElem = new PolicyCombinerParameters(policyId, params);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterPolicyCombinerParameters(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        PolicyCombinerParameters pp = (PolicyCombinerParameters)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_POLICYIDREF, pp.getPolicyId().toString());
        CombinerParameter[] params = pp.getCrudeParameters();
        for (int i = 0; i < params.length; i ++) {
            xmlElement.appendChild((Element)new FileAdapterCombinerParameter(params[i]).getDataStoreObject());
        }
    }
}