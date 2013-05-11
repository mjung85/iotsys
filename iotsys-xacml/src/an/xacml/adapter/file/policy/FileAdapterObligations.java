package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.Obligation;
import an.xacml.policy.Obligations;
import an.xml.XMLElement;

public class FileAdapterObligations extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="Obligations" type="xacml:ObligationsType"/>
    <xs:complexType name="ObligationsType">
        <xs:sequence>
            <xs:element ref="xacml:Obligation" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "Obligations";
    public FileAdapterObligations(Element elem) throws PolicySyntaxException {
        initialize(elem);

        XMLElement[] children = getChildElements();
        Obligation[] obligations = new Obligation[children.length];
        for (int i = 0; i < children.length; i ++) {
            obligations[i] = (Obligation)((DataAdapter)children[i]).getEngineElement();
        }
        engineElem = new Obligations(obligations);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterObligations(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Obligations obls = (Obligations)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        Obligation[] allObls = obls.getAllObligations();
        for (int i = 0; i < allObls.length; i ++) {
            xmlElement.appendChild((Element)new FileAdapterObligation(allObls[i]).getDataStoreObject());
        }
    }
}