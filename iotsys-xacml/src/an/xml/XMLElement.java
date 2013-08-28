package an.xml;

/**
 * All of XML element should implements it. It maps a DOM element to a Java object. It represent an 
 * XML element, whose type is represented as a class.
 */
public interface XMLElement {
    public String getElementName();
    public String getNamespaceURI();

    /**
     * Get all child elements of current XML element.
     * @return
     */
    public XMLElement[] getChildElements();

    /**
     * Get all attributes of current XML element.
     * @return
     */
    public XMLAttribute[] getAttributes();

    /**
     * Add a child element to current XML element.
     * @param child
     */
    public void addChildElement(XMLElement child);

    /**
     * Add an attribute to current XML element.
     * @param attr
     */
    public void addAttribute(XMLAttribute attr);
    public void addChildElements(XMLElement[] children);
    public void addAttributes(XMLAttribute[] attrs);
    public void setParentElement(XMLElement parent);
    public XMLElement getParentElement();
    public XMLElement getRootElement();

    public String getTextValue();
}