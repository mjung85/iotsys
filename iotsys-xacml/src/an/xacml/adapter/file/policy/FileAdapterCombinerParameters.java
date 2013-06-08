package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.CombinerParameter;
import an.xacml.policy.CombinerParameters;
import an.xml.XMLElement;

public class FileAdapterCombinerParameters extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="CombinerParameters" type="xacml:CombinerParametersType"/>
    <xs:complexType name="CombinerParametersType">
        <xs:sequence>
            <xs:element ref="xacml:CombinerParameter" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "CombinerParameters";
    public FileAdapterCombinerParameters(Element elem) throws PolicySyntaxException {
        initialize(elem);

        XMLElement[] children = getChildElements();
        CombinerParameter[] params = new CombinerParameter[children.length];
        for (int i = 0; i < children.length; i ++) {
            params[i] = (CombinerParameter)((DataAdapter)children[i]).getEngineElement();
        }
        engineElem = new CombinerParameters(params);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterCombinerParameters(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        CombinerParameters cps = (CombinerParameters)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        CombinerParameter[] params = cps.getCrudeParameters();
        for (int i = 0; i < params.length; i ++) {
            xmlElement.appendChild((Element)new FileAdapterCombinerParameter(params[i]).getDataStoreObject());
        }
    }
}