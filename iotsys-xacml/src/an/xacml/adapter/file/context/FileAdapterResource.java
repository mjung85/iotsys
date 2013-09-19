package an.xacml.adapter.file.context;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.context.Attribute;
import an.xacml.context.Resource;
import an.xacml.context.ResourceContent;
import an.xacml.context.TargetElement;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterResource extends FileAdapterTargetElement {
    /**
    <xs:element name="Resource" type="xacml-context:ResourceType"/>
    <xs:complexType name="ResourceType">
        <xs:sequence>
            <xs:element ref="xacml-context:ResourceContent" minOccurs="0"/>
            <xs:element ref="xacml-context:Attribute" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "Resource";
    public FileAdapterResource(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        XMLElement chRC = getSingleXMLElementByType(FileAdapterResourceContent.class);
        XMLElement[] chAttrs = getXMLElementsByType(FileAdapterAttribute.class);
        Attribute[] attrs = new Attribute[chAttrs.length];
        for (int i = 0; i < chAttrs.length; i ++) {
            attrs[i] = (Attribute)((DataAdapter)chAttrs[i]).getEngineElement();
        }
        engineElem = new Resource(chRC == null ? null : (ResourceContent)((DataAdapter)chRC).getEngineElement(), attrs);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterResource(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Resource res = (Resource)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        if (res.getResourceContent() != null) {
            xmlElement.appendChild((Element)new FileAdapterResourceContent(res.getResourceContent()).getDataStoreObject());
        }
        populateAttributes((TargetElement)engineElem);
    }
}