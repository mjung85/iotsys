package an.xacml.adapter.file.policy;

import static an.xacml.Constants.POLICY_NAMESPACE;

import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.file.TextXMLElement;
import an.xacml.policy.Defaults;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterDefaults extends AbstractFileAdapterPolicyElement {
    /**
    <xs:complexType name="DefaultsType">
        <xs:sequence>
            <xs:choice>
                <xs:element ref="xacml:XPathVersion"/>
            </xs:choice>
        </xs:sequence>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "Defaults";
    public static final String ELEMENT_XPATHVERSION = "XPathVersion";

    public FileAdapterDefaults(Element elem) throws PolicySyntaxException, XMLGeneralException, URISyntaxException {
        initialize(elem);

        // The XPathVersion is a URI type, we have to treat it as a text node.
        XMLElement xpathVersion = getSingleXMLElementByType(TextXMLElement.class);
        engineElem = new Defaults(new URI(((TextXMLElement)xpathVersion).getTextValue()));
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterDefaults(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Defaults defaults = (Defaults)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        Element xpathVersion = getDefaultDocument().createElementNS(POLICY_NAMESPACE, ELEMENT_XPATHVERSION);
        xpathVersion.appendChild(getDefaultDocument().createTextNode(defaults.getXPathVersion().toString()));
        xmlElement.appendChild(xpathVersion);
    }
}