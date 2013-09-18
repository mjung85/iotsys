package an.xacml.adapter.file.context;

import java.lang.reflect.Array;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import an.xacml.XACMLElement;
import an.xacml.context.ResourceContent;

public class FileAdapterResourceContent extends AbstractFileAdapterContextElement {
    /**
    <xs:element name="ResourceContent" type="xacml-context:ResourceContentType"/>
    <xs:complexType name="ResourceContentType" mixed="true">
        <xs:sequence>
            <xs:any namespace="##any" processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
        <xs:anyAttribute namespace="##any" processContents="lax"/>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "ResourceContent";
    public FileAdapterResourceContent(Element elem) throws Exception {
        this.elementName = elem.getLocalName();
        this.elementNamespaceURI = elem.getNamespaceURI();

        NodeList nodes = elem.getChildNodes();
        if (nodes.getLength() > 0) {
            if (nodes.getLength() == 1) {
                engineElem = new ResourceContent(nodes.item(0));
            }
            else {
                Node[] contents = new Node[nodes.getLength()];
                for (int i = 0; i < nodes.getLength(); i ++) {
                    contents[i] = nodes.item(i);
                }
                engineElem = new ResourceContent(contents);
            }
        }
        else {
            engineElem = new ResourceContent(null);
        }

        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterResourceContent(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        ResourceContent resCont = (ResourceContent)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        Object content = resCont.getContent();
        if (content != null) {
            Class<?> claz = content.getClass();
            if (claz.isArray()) {
                int size = Array.getLength(content);
                for (int i = 0; i < size; i ++) {
                    Node node = getDefaultDocument().importNode((Node)Array.get(content, i), true);
                    xmlElement.appendChild(node);
                }
            }
            else {
                Node node = getDefaultDocument().importNode((Node)content, true);
                xmlElement.appendChild(node);
            }
        }
    }
}