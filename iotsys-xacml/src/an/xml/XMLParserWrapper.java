package an.xml;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class XMLParserWrapper {
    /**
     * Since current version of Xerces (2.9.x) has several bugs, we have to rely on Sun's implementation. In order
     * to keep the effect inside only our project (Customer may use Xerces or other DOM implementations, we don't
     * want force them to use Sun's implementation), we will directly initialize Sun's DocumentBuilderFactoryImpl
     * and XMLSchemaFactory when the following option is provided.
     */
    public static final String OPT_FORCE_SUNDOM = "an.xml.DocumentBuilderFactory.forceSUN";
    private static final String SUN_DOCUMENTBUILDER_FACTORY_CLASSNAME = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
    private static final String SUN_SCHEMA_FACTORY_CLASSNAME_6 = "com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory";
    private static final String SUN_SCHEMA_FACTORY_CLASSNAME_5 = "com.sun.org.apache.xerces.internal.jaxp.validation.xs.SchemaFactoryImpl";

    /**
     * The attribute name of "schemaLocation", which is used to retrieve schema file from XML document.
     */
    public static final String SCHEMA_LOCATION = "schemaLocation";

    private static Transformer XFORMER;
    private static final String XML_TAG_START = "<?xml";
    private static final String XML_TAG_STOP = "?>";

    private static Map<String, DocumentBuilderFactory> factoryReg = new HashMap<String, DocumentBuilderFactory>();
    private static DocumentBuilderFactory defaultFactory;
    private static Map<String, Validator> validatorReg = new HashMap<String, Validator>();
    private static SchemaFactory schemaFactory;

    private static ErrorHandler errHandler = new DefaultErrorHandler();

    private synchronized static SchemaFactory getSchemaFactory() throws XMLGeneralException {
        if (schemaFactory == null) {
            // We use sun's implementation by default.
            String opt = System.getProperty(OPT_FORCE_SUNDOM, "true");
            // If no such option configured, we use default method to initialize the factory.
            if (opt == null) {
                schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            }
            // We forced use Sun's implementation to avoid Xerces or other implementation's effects.
            else {
                try {
                    Class<?> fClz = null;
                    String jVer = System.getProperty("java.version");
                    if (jVer.startsWith("1.5")) {
                        fClz = Class.forName(SUN_SCHEMA_FACTORY_CLASSNAME_5);
                    }
                    else {
                        fClz = Class.forName(SUN_SCHEMA_FACTORY_CLASSNAME_6);
                    }
                    schemaFactory = (SchemaFactory)fClz.newInstance();
                } catch (Exception e) {
                    throw new XMLGeneralException("Error occurs while initialize the Sun's DocumentBuilderFactory", e);
                }
            }
        }
        return schemaFactory;
    }

    private static DocumentBuilderFactory getDocumentBuilderFactory() throws XMLGeneralException {
        String opt = System.getProperty(OPT_FORCE_SUNDOM);
        // If no such option configured, we use default method to initialize the factory.
        if (opt == null) {
            return DocumentBuilderFactory.newInstance();
        }
        // We forced use Sun's implementation to avoid Xerces or other DOM implementation's effects.
        else {
            try {
                Class<?> fClz = Class.forName(SUN_DOCUMENTBUILDER_FACTORY_CLASSNAME);
                return (DocumentBuilderFactory)fClz.newInstance();
            } catch (Exception e) {
                throw new XMLGeneralException("Error occurs while initialize the Sun's DocumentBuilderFactory", e);
            }
        }
    }

    private synchronized static DocumentBuilder getDefaultDocumentBuilder(String ... schemaLocations)
    throws ParserConfigurationException, MalformedURLException, SAXException, XMLGeneralException { 	
    	if (schemaLocations == null || schemaLocations.length == 0) {
            return getDefaultDocumentBuilder();
        }
        else {
            // We concate all schema locations togather with "|", and use it as the factory's key in registry.
            String key = schemaLocations[0];
            for (int i = 1; i < schemaLocations.length; i ++) {
                key = key + "|" + schemaLocations[i];
            }

            DocumentBuilderFactory f = factoryReg.get(key);
            if (f == null) {
                f = newFactory(schemaLocations);
                factoryReg.put(key, f);
            }

            // We need create a new instance for DocumentBuilder, because if we cached it when we run with JDK5, it
            // will lead memory leak.
            DocumentBuilder builder = f.newDocumentBuilder();
            builder.setErrorHandler(errHandler);
            return builder;
        }
    }

    private synchronized static DocumentBuilder getDefaultDocumentBuilder()
    throws ParserConfigurationException, MalformedURLException, SAXException, XMLGeneralException {
        if (defaultFactory == null) {
            defaultFactory = newFactory();
        }

        // We need create a new instance for DocumentBuilder, because if we cached it when we run with JDK5, it
        // will lead memory leak.
        DocumentBuilder builder = defaultFactory.newDocumentBuilder();
        builder.setErrorHandler(new DefaultErrorHandler());
        return builder;
    }

    private static DocumentBuilderFactory newFactory(String ... schemaLocations)
    throws ParserConfigurationException, MalformedURLException, SAXException, XMLGeneralException {
        // We don't need explicit specify the factory's implementation, since in the xerces's jar file, there are
        // service provide configurations in META-INF/services.
        DocumentBuilderFactory factory = getDocumentBuilderFactory();
        factory.setNamespaceAware(true);

        if (schemaLocations != null && schemaLocations.length > 0) {
            // Initialize the Source array for schema factory.
            Source[] sources = new Source[schemaLocations.length];
            for (int x = 0; x < sources.length; x ++) {
                sources[x] = new StreamSource(schemaLocations[x]);
            }
            // The schema factory will combine all the given sources to a single Schema.
            Schema schema = getSchemaFactory().newSchema(sources);
            factory.setSchema(schema);
        }
        return factory;
    }

    private synchronized static Transformer getTransformer() throws Exception {
        if (XFORMER == null) {
            XFORMER = TransformerFactory.newInstance().newTransformer();
        }
        return XFORMER;
    }

    private static synchronized Validator getValidator(String ... schemaLocations)
    throws SAXException, MalformedURLException, XMLGeneralException {
        // We concate all schema locations togather with "|", and use it as the factory's key in registry.
        String key = schemaLocations[0];
        for (int i = 1; i < schemaLocations.length; i ++) {
            key = key + "|" + schemaLocations[i];
        }

        Validator validator = validatorReg.get(key);
        if (validator == null) {
            // Initialize the Source array for schema factory.
            Source[] sources = new Source[schemaLocations.length];
            for (int x = 0; x < sources.length; x ++) {
                sources[x] = new StreamSource(schemaLocations[x]);
            }
            // The schema factory will combine all the given sources to a single Schema.
            validator = getSchemaFactory().newSchema(sources).newValidator();
        }
        return validator;
    }

    public static Element parse(InputStream in, String ... schemaLocations)
    throws XMLGeneralException {
        try {
            // Get corresponding document builder and then parse the XML
            Document document = getDefaultDocumentBuilder(schemaLocations).parse(in);
            return document.getDocumentElement();
        }
        catch (Exception e) {
            throw new XMLGeneralException("Error occurs during parse XML file.", e);
        }
    }

    public static void validateElement(Element elem, String ... schemaLocations)
    throws SAXException, IOException, XMLGeneralException {
        Source source = new DOMSource(elem);
        getValidator(schemaLocations).validate(source);
    }

    public static String verifySchemaFile(String schema) throws XMLGeneralException {
        String lower = schema.toLowerCase();
        if (lower.startsWith("file:") || lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("ftp://")) {
            return schema;
        }

        File fSchema = new File(schema);
        if (fSchema.exists() && fSchema.isFile()) {
        	// System.out.println(XMLParserWrapper.class.getSimpleName() + ".verifySchemaFile(): " + schema + " exists as file");
            return schema;
        }
        
        URL path = Thread.currentThread().getContextClassLoader().getResource(schema);
        if (path != null) {
        	return path.toString();
        }
        
        // we got an absolute path or a relative path, so we don't try to load it from classpath.
        if (!schema.equalsIgnoreCase(fSchema.getName())) {
            throw new XMLGeneralException("Can not find the schema file '" + schema + "'");
        }

        URL urlSchema = XMLParserWrapper.class.getResource(schema);
        if (urlSchema != null) {
            return urlSchema.toString();
        }

        // Fix the issue that can't load the resource in jar which deployed on Tomcat
        urlSchema = XMLParserWrapper.class.getResource("/" + schema);
        if (urlSchema != null) {
            return urlSchema.toString();
        }

        urlSchema = ClassLoader.getSystemResource(schema);
        if (urlSchema != null) {
            return urlSchema.toString();
        }
        
        
        
        throw new XMLGeneralException("Can not load the schema file '" + schema + "'");
    }

    public static Document newDocument()
    throws ParserConfigurationException, MalformedURLException, SAXException, XMLGeneralException {
        return getDefaultDocumentBuilder().newDocument();
    }

    public static String getNodeXMLText(Node node) throws Exception {
        // Prepare the DOM document for writing
        Source source = new DOMSource(node);

        // Prepare the output file
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        Result result = new StreamResult(baOut);

        // Write the DOM document to the result
        getTransformer().transform(source, result);
        return baOut.toString().trim();
    }

    /**
     * This method is for large xml element which we have to dump it to an output stream
     * @param node
     * @return
     */
    public static void dumpNode(Node node, OutputStream out) throws Exception {
        // Prepare the DOM document for writing
        Source source = new DOMSource(node);
        Result result = new StreamResult(out);

        // Write the DOM document to the output stream
        getTransformer().transform(source, result);
    }

    public static String getNodeContentAsText(Node node) throws Exception {
        // We should use the getNodeName instead of getLocalName, because we need get the name with prefix, then we can
        // perform search in the whole XML text in the next steps.
        String nName = node.getNodeName();
        String nStart = "<" + nName, nStop = "</" + nName, tagStop = ">";

        String content = getNodeXMLText(node);
        // Remove the XML header
        if (content.startsWith(XML_TAG_START)) {
            content = content.substring(content.indexOf(XML_TAG_STOP) + XML_TAG_STOP.length()).trim();
        }

        // Remove the node tag
        int posStart = content.indexOf(nStart);
        int posStop = content.indexOf(nStop);
        if (posStart >= 0 && posStop >= 0 && posStop > posStart) {
            posStart = content.indexOf(tagStop, posStart) + 1;
            content = content.substring(posStart, posStop);
            return content;
        }
        return "";
    }

    public static Map<String, String> getNamespaceMappings(Element elem) {
        Map<String, String> nsMap = new Hashtable<String, String>();

        NamedNodeMap allAttrs = elem.getAttributes();
        for (int i = 0; i < allAttrs.getLength(); i ++) {
            Attr attr = (Attr)allAttrs.item(i);
            String localName = attr.getLocalName();
            String ns = attr.getNamespaceURI();
            // We don't need "xlmns"
            if (ns != null && ns.equals(XMLNS_ATTRIBUTE_NS_URI) && !localName.equals(XMLNS_ATTRIBUTE)) {
                nsMap.put(attr.getLocalName(), attr.getValue());
            }
        }
        return nsMap;
    }
}

class DefaultErrorHandler implements ErrorHandler, DOMErrorHandler {
    public void error(SAXParseException exception) throws SAXException {
        throw exception;
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }

    public void warning(SAXParseException exception) throws SAXException {
        throw exception;
    }

    public boolean handleError(DOMError error) {
        String message = error.getType() + " : " + error.getMessage();
        Object t = error.getRelatedException();

        if (t != null && t instanceof Throwable) {
            throw new RuntimeException(message, (Throwable)t);
        }
        else {
            throw new RuntimeException(message);
        }
    }
}