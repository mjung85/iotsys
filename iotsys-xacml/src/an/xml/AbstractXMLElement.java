package an.xml;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * FIXME
 * We are going to generalize all XACML file adapters and configuration elements, then we can use a single class to
 * represent all elements. So from now on, we will add some methods that conform this design, such as
 * getXMLElementsByType(String elementType). And first we will try refactor configuration elements, and then we will
 * start XACML file adapters.
 */
public abstract class AbstractXMLElement implements XMLElement {
    /**
     * The child XML elements should be assigned value in subclasses' constructor.
     */
    protected XMLElement[] childElements = new XMLElement[0];
    /**
     * The attributes should be assigned value in subclasses' constructor.
     */
    protected XMLAttribute[] attributes = new XMLAttribute[0];
    /**
     * The parent element's reference
     */
    protected XMLElement parent;
    /**
     * The namespace of this XML element, will be used if save this object to XML file.
     */
    protected String elementNamespaceURI;
    /**
     * Each element should has a name. Multiple elements with same type may occur in a same parent element,
     * but they should have different names.
     */
    protected String elementName;

    /**
     * Get the element name.
     */
    public String getElementName() {
        return elementName;
    }

    public String getNamespaceURI() {
        return elementNamespaceURI;
    }

    /**
     * Get all child elements of current XML element.
     */
    public synchronized XMLElement[] getChildElements() {
        return childElements;
    }

    /**
     * Get all attributes of current XML element.
     */
    public synchronized XMLAttribute[] getAttributes() {
        return attributes;
    }

    /**
     * Append a child element to current element, and set current element as parent of child element.
     */
    public synchronized void addChildElement(XMLElement child) {
        XMLElement[] newChildren = new XMLElement[childElements.length + 1];
        System.arraycopy(childElements, 0, newChildren, 0, childElements.length);
        child.setParentElement(this);
        newChildren[newChildren.length - 1] = child;
        childElements = newChildren;
    }

    /**
     * Append an attribute to current element, and set current element as owner of the attribute.
     */
    public synchronized void addAttribute(XMLAttribute attr) {
        XMLAttribute[] newAttrs = new XMLAttribute[attributes.length + 1];
        System.arraycopy(attributes, 0, newAttrs, 0, attributes.length);
        attr.setOwnerElement(this);
        newAttrs[newAttrs.length - 1] = attr;
        attributes = newAttrs;
    }

    /**
     * Add array of child elements to current element.
     */
    public synchronized void addChildElements(XMLElement[] children) {
        if (children != null && children.length > 0) {
            XMLElement[] newChildren = new XMLElement[childElements.length + children.length];
            System.arraycopy(childElements, 0, newChildren, 0, childElements.length);
            for (int i = 0; i < children.length; i ++) {
                children[i].setParentElement(this);
                newChildren[childElements.length + i] = children[i];
            }
            childElements = newChildren;
        }
    }

    /**
     * Add array of attributes to current element.
     */
    public synchronized void addAttributes(XMLAttribute[] attrs) {
        if (attrs != null && attrs.length > 0) {
            XMLAttribute[] newAttrs = new XMLAttribute[attributes.length + attrs.length];
            System.arraycopy(attributes, 0, newAttrs, 0, attributes.length);
            for (int i = 0; i < attrs.length; i ++) {
                attrs[i].setOwnerElement(this);
                newAttrs[attributes.length + i] = attrs[i];
            }
            attributes = newAttrs;
        }
    }

    public void setParentElement(XMLElement parent) {
        this.parent = parent;
    }

    public XMLElement getParentElement() {
        return parent;
    }

    /**
     * Get the root XML element.
     */
    public XMLElement getRootElement() {
        XMLElement p = getParentElement();
        XMLElement root = this;

        while (p != null) {
            root = p;
            p = p.getParentElement();
        }
        return root;
    }

    protected abstract Class<?> getElementClass(String elemType);

    /**
     * Add such a method to avoid the defered NullPointerException.
     * @param elemType
     * @return
     */
    protected Class<?> getElementClassWithCheckNull(String elemType) {
        Class<?> clazz = getElementClass(elemType);
        if (clazz == null) {
            throw new NullPointerException(
                    "The corresponding Java class is not registered for the XML element type \"" + elemType + "\".");
        }
        return clazz;
    }

    /**
     * Extract child elements from XML element, and convert them to XMLElement. 
     * @param element
     * @return
     * @throws XMLGeneralException
     */
    protected XMLElement[] extractChildXMLElements(Element element) throws XMLGeneralException {
        Vector<XMLElement> list = new Vector<XMLElement>();

        try {
            if (element != null) {
                Node node = element.getFirstChild();
                while (node != null) {
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element)node;
    
                        String nodeType = el.getSchemaTypeInfo().getTypeName();
                        Class<?> nodeClass = getElementClassWithCheckNull(nodeType);
                        Class<?>[] paramTypes = {Element.class};
                        Constructor<?> constructor = nodeClass.getConstructor(paramTypes);
                        Object[] params = {(Element)node};
                        XMLElement xmlEl = (XMLElement)constructor.newInstance(params);
                        list.add(xmlEl);
                    }
                    node = node.getNextSibling();
                }
            }

            return list.toArray(new XMLElement[0]);
        }
        catch (Exception e) {
            throw new XMLGeneralException("Error occurs during extract child elements from XML file.", e);
        }
    }

    /**
     * Extract attributes from W3C Element, and convert them to XMLAttribute.
     * @param element
     * @return
     * @throws XMLGeneralException
     */
    protected XMLAttribute[] extractXMLAttributes(Element element) throws XMLGeneralException {
        Vector<XMLAttribute> list = new Vector<XMLAttribute>();

        if (element != null) {
            NamedNodeMap attrs = element.getAttributes();
            for (int i = 0; i < attrs.getLength(); i ++) {
                Node node = attrs.item(i);
                String attrNS = node.getNamespaceURI();
                if (node.getNodeType() == Node.ATTRIBUTE_NODE && node instanceof Attr &&
                    (attrNS == null ? true :
                    !attrNS.equals(XMLNS_ATTRIBUTE_NS_URI) && !attrNS.equals(W3C_XML_SCHEMA_INSTANCE_NS_URI))) {
                    list.add(new XMLAttribute((Attr)node));
                }
            }
        }

        return list.toArray(new XMLAttribute[0]);
    }

    public synchronized Object getAttributeValueByName(String name) {
        for (int i = 0; i < attributes.length; i ++) {
            if (attributes[i].getName().equalsIgnoreCase(name)) {
                return attributes[i].getValue();
            }
        }
        return null;
    }

    public synchronized XMLElement[] getXMLElementsByType(Class<?> type) {
        Vector<XMLElement> elements = new Vector<XMLElement>();
        for (int i = 0; i < childElements.length; i ++) {
            if (type.isAssignableFrom(childElements[i].getClass())) {
                elements.add(childElements[i]);
            }
        }
        // Must use the passed-in class type as toArray's parameter. If use "new XMLElement[0]" directly, the result
        // will be convert to a XMLElement[] type and with XMLElement subclass' object in it. It cannot be down
        // cast to an array such as PDPConfigElement[], since the Array type could not be down cast.
        return (XMLElement[])elements.toArray((Object[])Array.newInstance(type, 0));
    }

    public XMLElement getSingleXMLElementByType(Class<?> type) throws XMLGeneralException {
        XMLElement[] result = (XMLElement[])getXMLElementsByType(type);
        if (result.length > 1) {
            throw new XMLGeneralException("Expected 1 item but got multiple.");
        }
        return result.length > 0 ? result[0] : null;
    }

    public synchronized XMLElement[] getXMLElementsByName(String name) {
        Vector<XMLElement> elements = new Vector<XMLElement>();
        for (int i = 0; i < childElements.length; i ++) {
            if (name.equalsIgnoreCase(childElements[i].getElementName())) {
                elements.add(childElements[i]);
            }
        }

        // Since the elements' type of the array may different, so we return a generic XMLElement[] type.
        // Caller should take care of doing down cast.
        return elements.toArray(new XMLElement[0]);
    }

    public XMLElement getSingleXMLElementByName(String name) throws XMLGeneralException {
        XMLElement[] result = getXMLElementsByName(name);
        if (result.length > 1) {
            throw new XMLGeneralException("Expected 1 item but got multiple.");
        }
        return result.length > 0 ? result[0] : null;
    }

    public String toString() {
        String header = elementNamespaceURI == null ? elementName : elementNamespaceURI + ":" + elementName;
        StringBuffer attrBuf = new StringBuffer();
        if (attributes.length > 0) {
            for (XMLAttribute each : attributes) {
                attrBuf.append(" ");
                attrBuf.append(each.toString());
            }
        }
        StringBuffer elemBuf = new StringBuffer();
        if (childElements.length > 0) {
            for (XMLElement each : childElements) {
                elemBuf.append("\n");
                elemBuf.append(each.toString());
            }
        }
        boolean hasChildren = (elemBuf.toString().length() > 0);
        return "<" + header + attrBuf.toString() + (hasChildren ? ">" : "/>") + elemBuf.toString() +
               (hasChildren ? "\n</" + header + ">" : "");
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof AbstractXMLElement) {
            return toString().equals(o.toString());
        }
        return false;
    }
}