package an.config;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

import an.xml.AbstractXMLElement;
import an.xml.XMLAttribute;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class ConfigElement extends AbstractXMLElement {
    protected Element elem;

    /**
     * A copy constructor that use to clone from another ConfigElement object.
     * 
     * In this constructor, we don't copy parent element. The parent will be set while it is copying as a child element.
     * If we clone a ConfigElement object from a leaf node (not from root), the cloned leaf element will become the root
     * element of the cloned tree.
     * @param config The ConfigElement object that need to be cloned.
     */
    public ConfigElement(ConfigElement config) {
        elem = config.elem;
        elementName = config.elementName;
        elementNamespaceURI = config.elementNamespaceURI;
        // copy attributes
        if (config.attributes != null && config.attributes.length > 0) {
            attributes = new XMLAttribute[config.attributes.length];
            for (int i = 0; i < config.attributes.length; i ++) {
                attributes[i] = new XMLAttribute(config.attributes[i]);
                // Set the owner element for the cloned attribute.
                attributes[i].setOwnerElement(this);
            }
        }
        // copy child elements
        if (config.childElements != null && config.childElements.length > 0) {
            childElements = new XMLElement[config.childElements.length];
            for (int i = 0; i < config.childElements.length; i ++) {
                childElements[i] = new ConfigElement((ConfigElement)config.childElements[i]);
                // Set the parent element for the child element.
                childElements[i].setParentElement(this);
            }
        }
    }

    public ConfigElement(Element elem) throws ConfigurationException {
        initialize(elem);
    }

    /**
     * Override the method from supper class, only use ConfigElement itself to initialize configuration, because
     * only attributes are useful in configuration. 
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
                        XMLElement xmlEl = new ConfigElement(el);
                        list.add(xmlEl);
                    }
                    node = node.getNextSibling();
                }
            }

            return list.toArray(new XMLElement[0]);
        }
        catch (Exception e) {
        	System.out.println(e.getClass().getSimpleName() + ":" + e.getMessage());
            throw new XMLGeneralException("Error occurs during extract child elements from XML file.", e);
        }
    }

    protected Class<?> getElementClass(String elemType) {
        throw new UnsupportedOperationException("ConfigElement doesn't support get element class.");
    }

    public String getTextValue() {
        throw new UnsupportedOperationException("ConfigElement doesn't support get text value from it.");
    }

    protected void initialize(Element elem) throws ConfigurationException {
        try {
            this.elem = elem;
            elementNamespaceURI = elem.getNamespaceURI();
            elementName = elem.getLocalName();
            addChildElements(extractChildXMLElements(elem));
            addAttributes(extractXMLAttributes(elem));
        }
        catch (Exception e) {
            throw new ConfigurationException("Error occurs during parse " + elem.getLocalName() + " configuration.", e);
        }
    }

    /**
     * FIXME This method should be moved to AbstractXMLElement class after the engine element are refactored.
     */
    protected boolean isDerivedFrom(String ns, String typeName) {
        if (typeName != null) {
            TypeInfo thisType = elem.getSchemaTypeInfo();
            return thisType.isDerivedFrom(
                    (ns == null ? thisType.getTypeNamespace() : ns), typeName, TypeInfo.DERIVATION_EXTENSION);
        }
        else {
            return false;
        }
    }

    /**
     * FIXME This method should be moved to AbstractXMLElement class after the engine element are refactored.
     */
    public synchronized XMLElement[] getXMLElementsByType(String type) {
        Vector<XMLElement> elements = new Vector<XMLElement>();
        for (int i = 0; i < childElements.length; i ++) {
            XMLElement child = childElements[i];
            if (child instanceof ConfigElement && ((ConfigElement)child).isDerivedFrom(null, type)) {
                elements.add(childElements[i]);
            }
        }
        return (XMLElement[])elements.toArray(new ConfigElement[0]);
    }

    /**
     * FIXME This method should be moved to AbstractXMLElement class after the engine element are refactored.
     */
    public synchronized XMLElement[] getXMLElementsByTypeNS(String ns, String type) {
        Vector<XMLElement> elements = new Vector<XMLElement>();
        for (int i = 0; i < childElements.length; i ++) {
            XMLElement child = childElements[i];
            if (child instanceof ConfigElement && ((ConfigElement)child).isDerivedFrom(ns, type)) {
                elements.add(childElements[i]);
            }
        }
        return (XMLElement[])elements.toArray(new ConfigElement[0]);
    }

    /**
     * FIXME This method should be moved to AbstractXMLElement class after the engine element are refactored.
     */
    public XMLElement getSingleXMLElementByType(String type) throws XMLGeneralException {
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

        // We explicitly return a ConfigElement[] typed result to avoid cast issues.
        return elements.toArray(new ConfigElement[0]);
    }
}