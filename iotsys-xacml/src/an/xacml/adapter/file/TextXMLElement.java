package an.xacml.adapter.file;


import an.xacml.XACMLElement;
import an.xml.XMLElement;

/**
 * This class represents an text element such as the text node of AttributeValue. Note, even this class is derived
 * from AbstractFileAdapterElement, but it doesn't implement DataAdapter interface. Users should not use it as a
 * DataAdapter.
 */
public class TextXMLElement extends AbstractFileAdapterElement {
    private String textValue;

    public TextXMLElement(String elemName, String elemNS, String value) {
        this.elementName = elemName;
        this.elementNamespaceURI = elemNS;
        this.textValue = value;
    }

    public void addChildElement(XMLElement child) {
        throw new UnsupportedOperationException("TextXMLElement doesn't support add child to it.");
    }

    public void addChildElements(XMLElement[] children) {
        throw new UnsupportedOperationException("TextXMLElement doesn't support add children to it.");
    }

    public XMLElement[] getChildElements() {
        throw new UnsupportedOperationException("TextXMLElement doesn't support get children from it.");
    }

    public String getTextValue() {
        return textValue;
    }

    public XACMLElement getEngineElement() {
        throw new UnsupportedOperationException("TextXMLElement doesn't support get engine element from it.");
    }

    public Object getDataStoreObject() {
        throw new UnsupportedOperationException("TextXMLElement doesn't support get XML element from it.");
    }

    @Override
    protected Class<?> getElementClass(String elemType) {
        throw new UnsupportedOperationException("TextXMLElement doesn't support get element class from it.");
    }

    public String toString() {
        String header = elementNamespaceURI == null ? elementName : elementNamespaceURI + ":" + elementName;
        boolean hasValue = (textValue != null && textValue.length() > 0);
        return "<" + header + (hasValue ? ">" : "/>") + (textValue == null ? "" : textValue) +
               (hasValue ? "</" + header + ">" : "");
    }
}