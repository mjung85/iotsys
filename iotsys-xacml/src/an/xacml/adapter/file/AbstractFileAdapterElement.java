package an.xacml.adapter.file;

import static an.xacml.Constants.CONTEXT_NAMESPACE;
import static an.xacml.Constants.POLICY_NAMESPACE;
import static an.xml.XMLDataTypeRegistry.getJavaType;
import static an.xml.XMLParserWrapper.newDocument;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.AbstractPolicy;
import an.xacml.policy.PolicyCombinerParameters;
import an.xacml.policy.PolicySetCombinerParameters;
import an.xacml.policy.RuleCombinerParameters;
import an.xml.AbstractXMLElement;
import an.xml.XMLDataTypeMappingException;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;
import an.xml.XMLParserWrapper;

public abstract class AbstractFileAdapterElement extends AbstractXMLElement {
    protected XACMLElement engineElem;
    protected Element xmlElement;
    protected static Document defaultDocument;

    public static final String COMBINER_PARAMETERS = "CombinerParameters";
    public static final String RULE_COMBINER_PARAMETERS = "RuleCombinerParameters";
    public static final String POLICY_COMBINER_PARAMETERS = "PolicyCombinerParameters";
    public static final String POLICY_SET_COMBINER_PARAMETERS = "PolicySetCombinerParameters";
    public static final String[] CAN_BE_MERGED = 
        {COMBINER_PARAMETERS, RULE_COMBINER_PARAMETERS, POLICY_COMBINER_PARAMETERS, POLICY_SET_COMBINER_PARAMETERS};

    protected static final String CONTEXT_ELEMENT = "CONTEXT_ELEMENT";
    protected static final String POLICY_ELEMENT = "POLICY_ELEMENT";

    protected void initialize(Element elem) throws PolicySyntaxException {
        try {
            elementNamespaceURI = elem.getNamespaceURI();
            elementName = elem.getLocalName();
            addChildElements(extractChildXMLElements(elem));
            addAttributes(extractXMLAttributes(elem));
        }
        catch (Exception e) {
            throw new PolicySyntaxException("Error occurs during parse '" + elementName + "' element.", e);
        }
    }

    protected XMLElement[] extractChildXMLElements(Element element) throws XMLGeneralException {
        Vector<XMLElement> list = new Vector<XMLElement>();

        try {
            if (element != null) {
                Node node = element.getFirstChild();
                while (node != null) {
                    // We will convert each child element to XMLElement
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element)node;
                        XMLElement xmlEl;

                        String nodeType = el.getSchemaTypeInfo().getTypeName();
                        Class<?> nodeClass = getElementClass(nodeType);
                        // If this child is a registered element(may be simple or complex type), we lookup a registered
                        // XMLElement, and then new an instance.
                        if (nodeClass != null) {
                            Constructor<?> constructor = nodeClass.getConstructor(Element.class);
                            xmlEl = (XMLElement)constructor.newInstance(node);
                        }
                        // If this child isn't registered. We considered this is a simple type or derived from a simple
                        // type, we will wrap it to a TextXMLElement.
                        else {
                            NodeList grandChildren = el.getChildNodes();
                            // If this element has only one child node, and it is a text node, we consider this is a 
                            // simple type or derived from a simple type, we direct use TextXMLElement.
                            if (grandChildren.getLength() == 1 &&
                                grandChildren.item(0).getNodeType() == Node.TEXT_NODE) {
                                Node grandChild = grandChildren.item(0);
                                String textValue = grandChild.getNodeValue().trim();
                                xmlEl = new TextXMLElement(el.getLocalName(), el.getNamespaceURI(), textValue);
                                // The text node may has attributes, we extract and add them here.
                                ((TextXMLElement)xmlEl).addAttributes(((TextXMLElement)xmlEl).extractXMLAttributes(el));
                            }
                            // We don't know why it has more than one children or it has no child, so we just construct
                            // a TextXMLElement from the whole content of element.
                            else {
                                xmlEl = new TextXMLElement(el.getLocalName(), el.getNamespaceURI(),
                                        XMLParserWrapper.getNodeContentAsText(el));
                            }
                        }
                        list.add(xmlEl);
                    }
                    // Iterate for next child
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
     * Append a child element to current element, and set current element as parent of child element.
     */
    public synchronized void addChildElement(XMLElement child) {
        // Check if this child element is needed to be merged with existing same element.
        if (isMergeableElement(child)) {
            for (int i = 0; i < childElements.length; i ++) {
                XMLElement item = childElements[i];
                // If there is existing element, we need to merge the child with existing element.
                if (canBeMerged(child, item)) {
                    item.addChildElements(child.getChildElements());
                    // We don't need iterate to next element, because the existing child elements should not include
                    // same elements that need to be merged.
                    return;
                }
            }
        }
        // If don't need to be merged, we add child element as normally
        super.addChildElement(child);
    }

    /**
     * Add array of child elements to current element.
     */
    public synchronized void addChildElements(XMLElement[] children) {
        // Since we need perform check on each child element, we have to call addChildElement circularly
        for (int i = 0; i < children.length; i ++) {
            addChildElement(children[i]);
        }
    }

    private static boolean isMergeableElement(XMLElement elem) {
        String name = elem.getElementName();
        String ns = elem.getNamespaceURI();
        for (int i = 0; i < CAN_BE_MERGED.length; i ++) {
            if (name.equals(CAN_BE_MERGED[i]) && ns.equals(POLICY_NAMESPACE)) {
                return true;
            }
        }
        return false;
    }

    private static boolean canBeMerged(XMLElement a, XMLElement b) {
        if (a.getElementName().equals(b.getElementName()) && a.getNamespaceURI().equals(b.getNamespaceURI())) {
            String elemName = a.getElementName();
            if (elemName.equals(COMBINER_PARAMETERS)) {
                return true;
            }
            else if (elemName.equals(RULE_COMBINER_PARAMETERS)) {
                return ((RuleCombinerParameters)((DataAdapter)a).getEngineElement()).getRuleId().equals(
                       ((RuleCombinerParameters)((DataAdapter)b).getEngineElement()).getRuleId());
            }
            else if (elemName.equals(POLICY_COMBINER_PARAMETERS)) {
                return ((PolicyCombinerParameters)((DataAdapter)a).getEngineElement()).getPolicyId().equals(
                       ((PolicyCombinerParameters)((DataAdapter)b).getEngineElement()).getPolicyId());
            }
            else if (elemName.equals(POLICY_SET_COMBINER_PARAMETERS)) {
                return ((PolicySetCombinerParameters)((DataAdapter)a).getEngineElement()).getPolicySetId().equals(
                       ((PolicySetCombinerParameters)((DataAdapter)b).getEngineElement()).getPolicySetId());
            }
        }
        return false;
    }

    protected static void populateNamespaceMappings2XMLElement(AbstractPolicy policy, Element element) {
        Map<String, String> nsMap = policy.getPolicyNamespaceMappings();
        if (nsMap != null && nsMap.size() > 0) {
            Iterator<String> keys = nsMap.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                element.setAttribute(XMLNS_ATTRIBUTE + ":" + key, nsMap.get(key));
            }
        }
    }

    public String getTextValue() {
        throw new UnsupportedOperationException("FileAdapterElement doesn't support get text value from it.");
    }

    public XACMLElement getEngineElement() {
        return engineElem;
    }

    public Object getDataStoreObject() {
        return xmlElement;
    }

    /*
     * We have to use the same document to create both policy and context element, because if we use different
     * documents create elements under the same root element, there will be validate errors.  This should not be an
     * issue, because the elements should be validated using the external schema after they have created.
     */
    protected static synchronized Document getDefaultDocument() throws Exception {
        if (defaultDocument == null) {
            defaultDocument = newDocument();
        }
        return defaultDocument;
    }

    protected Element createPolicyElement() throws Exception {
        return getDefaultDocument().createElementNS(POLICY_NAMESPACE, engineElem.getElementName());
    }

    protected Element createContextElement() throws Exception {
        return getDefaultDocument().createElementNS(CONTEXT_NAMESPACE, engineElem.getElementName());
    }

    protected Class<?> getElementClassFromSystem(String elemType) {
    	Class<?> elemClz = null;
        try {
        	elemClz = getJavaType(new QName(W3C_XML_SCHEMA_NS_URI, elemType));
		} catch (XMLDataTypeMappingException e) {
			// If it still not an XML primitive type, we consider it is a TextXMLElement type.
			elemClz = TextXMLElement.class;
		}
		return elemClz;
    }
}