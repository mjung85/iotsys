package an.xacml.policy;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextProvider implements NamespaceContext {
    private Map<String, String> listByPrefix = new Hashtable<String, String>();

    public NamespaceContextProvider() {
        listByPrefix.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        listByPrefix.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }

    public void addNSMapping(String prefix, String nsURI) {
        listByPrefix.put(prefix, nsURI);
    }

    public String getNamespaceURI (String prefix) {
        String result = listByPrefix.get(prefix);
        if (result == null) {
            return XMLConstants.NULL_NS_URI;
        }
        else {
            return result;
        }
    }
    
    public String getPrefix (String namespaceURI) {
        // It's not used for the context
        return null;
    }
    
    public Iterator<String> getPrefixes (String namespaceURI) {
        // It's not used for the context
        return null;
    }
}