package an.xacml.adapter.file.policy;

import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.policy.IdReference;
import an.xacml.policy.VersionMatch;
import an.xml.XMLGeneralException;

public class FileAdapterIdReference extends AbstractFileAdapterPolicyElement {
    /**
    <xs:complexType name="IdReferenceType">
        <xs:simpleContent>
            <xs:extension base="xs:anyURI">
                <xs:attribute name="Version" type="xacml:VersionMatchType" use="optional"/>
                <xs:attribute name="EarliestVersion" type="xacml:VersionMatchType" use="optional"/>
                <xs:attribute name="LatestVersion" type="xacml:VersionMatchType" use="optional"/>
            </xs:extension>
        </xs:simpleContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "IdReference";
    public static final String ATTR_VERSION = "Version";
    public static final String ATTR_EARLIESTVERSION = "EarliestVersion";
    public static final String ATTR_LATESTVERSION = "LatestVersion";

    public FileAdapterIdReference(Element elem) throws PolicySyntaxException, XMLGeneralException, DOMException, URISyntaxException {
        elementNamespaceURI = elem.getNamespaceURI();
        elementName = elem.getLocalName();
        // Even this element is a simple type, but it has some attributes.
        addAttributes(extractXMLAttributes(elem));

        URI id = new URI(elem.getTextContent());
        VersionMatch ver = (VersionMatch)getAttributeValueByName(ATTR_VERSION);
        VersionMatch ear = (VersionMatch)getAttributeValueByName(ATTR_EARLIESTVERSION);
        VersionMatch lat = (VersionMatch)getAttributeValueByName(ATTR_LATESTVERSION);
        engineElem = new IdReference(id, ver, ear, lat);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterIdReference(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        IdReference idRef = (IdReference)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        if (idRef.getVersion() != null) {
            xmlElement.setAttribute(ATTR_VERSION, idRef.getVersion().getPattern());
        }
        if (idRef.getEarliestVersion() != null) {
            xmlElement.setAttribute(ATTR_EARLIESTVERSION, idRef.getEarliestVersion().getPattern());
        }
        if (idRef.getLatestVersion() != null) {
            xmlElement.setAttribute(ATTR_LATESTVERSION, idRef.getLatestVersion().getPattern());
        }
        xmlElement.appendChild(getDefaultDocument().createTextNode(idRef.getId().toString()));
    }
}