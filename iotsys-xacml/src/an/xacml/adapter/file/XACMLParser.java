package an.xacml.adapter.file;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getContextDataAdapterClassByXACMLElementType;
import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getContextDataAdapterClassByXMLType;
import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;
import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXMLType;
import static an.xml.XMLParserWrapper.dumpNode;
import static an.xml.XMLParserWrapper.parse;
import static an.xml.XMLParserWrapper.validateElement;
import static an.xml.XMLParserWrapper.verifySchemaFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import org.w3c.dom.Element;

import an.config.ConfigurationException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.context.Request;
import an.xacml.context.Response;
import an.xacml.policy.AbstractPolicy;
import an.xml.XMLElement;

public class XACMLParser {
    public static final String POLICY_KEY_DEFAULT_SCHEMA_FILE = "an.xacml.policy.DefaultSchema";
    public static final String CONTEXT_KEY_DEFAULT_SCHEMA_FILE = "an.xacml.context.DefaultSchema";

    private XACMLParser() {}

    public static String getPolicyDefaultSchema() throws ConfigurationException {
        // We provider default value for the schema file
        String schema = System.getProperty(POLICY_KEY_DEFAULT_SCHEMA_FILE, "xacml-2.0-policy.xsd");
        if (schema == null) {
            throw new ConfigurationException("The required policy schema file is not configured.");
        }
        return schema;
    }

    public static String getContextDefaultSchema() throws ConfigurationException {
        // We provider default value for the schema file
        String schema = System.getProperty(CONTEXT_KEY_DEFAULT_SCHEMA_FILE, "xacml-2.0-context.xsd");
        if (schema == null) {
            throw new ConfigurationException("The required context schema file is not configured.");
        }
        return schema;
    }

    public static AbstractPolicy parsePolicy(InputStream in) throws Exception {
        // Retrieve the schema file from given path or classpath, and verify if it exists.
        String defaultSchema = verifySchemaFile(getPolicyDefaultSchema());
        // parse policy file
        Element root = parse(in, defaultSchema);
        DataAdapter da = createPolicyDataAdapterFromXMLElement(root);

        return (AbstractPolicy)da.getEngineElement();
    }

    public static void dumpPolicy(AbstractPolicy policy, OutputStream out) throws Exception {
        // Retrieve the schema file from given path or classpath, and verify if it exists.
        String defaultSchema = verifySchemaFile(getPolicyDefaultSchema());
        // Prepare the data adapter for policy element
        DataAdapter da = createPolicyDataAdapterFromEngineElement(policy);
        // Validate the element in case there are policies were created dynamically and not validated.
        Element element = (Element)da.getDataStoreObject();
        validateElement(element, defaultSchema);
        // Dump the xml element to out.
        dumpNode((Element)da.getDataStoreObject(), out);
    }

    public static Request parseRequest(InputStream in) throws Exception {
        // Retrieve the schema file from given path or classpath, and verify if it exists.
        String defaultSchema = verifySchemaFile(getContextDefaultSchema());
        // parse request file
        // System.out.println(XACMLParser.class.getSimpleName() + ".parseRequest(): " + defaultSchema);
        
        Element root = parse(in, defaultSchema);
        DataAdapter da = createContextDataAdapterFromXMLElement(root);

        return (Request)da.getEngineElement();
    }

    public static void dumpRequest(Request request, OutputStream out) throws Exception {
        // Retrieve the schema file from given path or classpath, and verify if it exists.
        String defaultSchema = verifySchemaFile(getContextDefaultSchema());
        // Prepare the data adapter for request element
        DataAdapter da = createContextDataAdapterFromEngineElement(request);
        // Validate the element in case there are requests were created dynamically and not validated.
        Element element = (Element)da.getDataStoreObject();
        validateElement(element, defaultSchema);
        // Dump the xml element to out.
        dumpNode((Element)da.getDataStoreObject(), out);
    }

    public static Response parseResponse(InputStream in) throws Exception {
        // Retrieve the schema file from given path or classpath, and verify if it exists.
        String defaultSchema = verifySchemaFile(getContextDefaultSchema());
        // parse response file
        Element root = parse(in, defaultSchema);
        DataAdapter da = createContextDataAdapterFromXMLElement(root);

        return (Response)da.getEngineElement();
    }

    public static void dumpResponse(Response response, OutputStream out) throws Exception {
        // Retrieve the schema file from given path or classpath, and verify if it exists.
        String defaultSchema = verifySchemaFile(getContextDefaultSchema());
        // Prepare the data adapter for response element
        DataAdapter da = createContextDataAdapterFromEngineElement(response);
        // Validate the element in case there are responses were created dynamically and not validated.
        Element element = (Element)da.getDataStoreObject();
        validateElement(element, defaultSchema);
        // Dump the xml element to out.
        dumpNode((Element)da.getDataStoreObject(), out);
    }

    static DataAdapter createPolicyDataAdapterFromXMLElement(Element elem) throws Exception {
        // Get the element's type
        String elemType = elem.getSchemaTypeInfo().getTypeName();
        Class<?> adapterClass = getPolicyDataAdapterClassByXMLType(elemType);
        // All file adapters should have a constructor with a parameter that type is "Element"
        Constructor<?> constructor = adapterClass.getConstructor(Element.class);
        // initialize the root XML element from DOM element. The root element will iterate each child element and
        // construct them.
        DataAdapter da = (DataAdapter)constructor.newInstance(elem);
        // Iterate each engine XACML element and set parent element. We have to populate parent element here because
        // engine element was not ready while constructing XMLElement. 
        // FIXME should this operation be moved to Policy or PolicySet's constructor?
        populateEngineParentElement((XMLElement)da);
        return da;
    }

    static DataAdapter createPolicyDataAdapterFromEngineElement(XACMLElement engineElem) throws Exception {
        Class<?> adapterClass = getPolicyDataAdapterClassByXACMLElementType(engineElem.getClass());
        // All file adapters should have a constructor with a parameter that type is "XACMLElement"
        Constructor<?> constructor = adapterClass.getConstructor(XACMLElement.class);
        return (DataAdapter)constructor.newInstance(engineElem);
    }

    static DataAdapter createContextDataAdapterFromXMLElement(Element elem) throws Exception {
        // Get the element's type
        String elemType = elem.getSchemaTypeInfo().getTypeName();
        Class<?> adapterClass = getContextDataAdapterClassByXMLType(elemType);
        // All file adapters should have a constructor with a parameter that type is "Element"
        Constructor<?> constructor = adapterClass.getConstructor(Element.class);
        // initialize the root XML element from DOM element. The root element will iterate each child element and
        // construct them.
        DataAdapter da = (DataAdapter)constructor.newInstance(elem);
        // Iterate each engine XACML element and set parent element. We have to populate parent element here because
        // engine element was not ready while constructing XMLElement.
        // FIXME should this operation be moved to Policy or PolicySet's constructor?
        populateEngineParentElement((XMLElement)da);
        return da;
    }

    static DataAdapter createContextDataAdapterFromEngineElement(XACMLElement engineElem) throws Exception {
        Class<?> adapterClass = getContextDataAdapterClassByXACMLElementType(engineElem.getClass());
        // All file adapters should have a constructor with a parameter that type is "XACMLElement"
        Constructor<?> constructor = adapterClass.getConstructor(XACMLElement.class);
        return (DataAdapter)constructor.newInstance(engineElem);
    }

    private static void populateEngineParentElement(XMLElement root) {
        XMLElement[] children = root.getChildElements();
        if (children != null && children.length > 0 && root instanceof DataAdapter) {
            XACMLElement xacmlRoot = ((DataAdapter)root).getEngineElement();
            for (int i = 0; i < children.length; i ++) {
                if (children[i] instanceof DataAdapter) {
                    ((DataAdapter)children[i]).getEngineElement().setParentElement(xacmlRoot);
                    populateEngineParentElement(children[i]);
                }
            }
        }
    }
}