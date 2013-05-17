package an.xml;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.datatype.DatatypeConstants.DATE;
import static javax.xml.datatype.DatatypeConstants.DATETIME;
import static javax.xml.datatype.DatatypeConstants.TIME;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/**
 * Provide the mapping of XML Schema type to Java type.
 */
public final class XMLDataTypeRegistry {
    /**
     * The registry also provide a Java primitive-wrapper class mapping.
     */
    private static Map<Class<?>, Class<?>> primitiveMapper = new Hashtable<Class<?>, Class<?>>();
    private static Map<QName, Class<?>> javaByXml = new Hashtable<QName, Class<?>>();

    private static DatatypeFactory dataTypeFactory;

    /**
     * Define all XML types that used in XACML.
     */
    public static final String XML_STRING = "string";
    public static final String XML_BOOLEAN = "boolean";
    public static final String XML_INTEGER = "integer";
    public static final String XML_LONG = "long";
    public static final String XML_INT = "int";
    public static final String XML_DOUBLE = "double";
    public static final String XML_ANYURI = "anyURI";
    public static final String XPATH_DURATION_DAYTIME = "dayTimeDuration";
    public static final String XPATH_DURATION_YEARMONTH = "yearMonthDuration";

    public static final String XPATH_DRAFT_16AUG2002 = "http://www.w3.org/TR/2002/WD-xquery-operators-20020816";

    static {
        // Map primitive types to their wrapper types. We only map those types that are used by XACML.
        primitiveMapper.put(int.class, Integer.class);
        primitiveMapper.put(long.class, Long.class);
        primitiveMapper.put(boolean.class, Boolean.class);

        // Primitive types.
        javaByXml.put(new QName(W3C_XML_SCHEMA_NS_URI, XML_STRING), String.class);
        javaByXml.put(new QName(W3C_XML_SCHEMA_NS_URI, XML_BOOLEAN), Boolean.class);
        javaByXml.put(new QName(W3C_XML_SCHEMA_NS_URI, XML_INTEGER), BigInteger.class);
        javaByXml.put(new QName(W3C_XML_SCHEMA_NS_URI, XML_LONG), Long.class);
        javaByXml.put(new QName(W3C_XML_SCHEMA_NS_URI, XML_INT), Integer.class);
        javaByXml.put(new QName(W3C_XML_SCHEMA_NS_URI, XML_DOUBLE), BigDecimal.class);
        javaByXml.put(new QName(W3C_XML_SCHEMA_NS_URI, XML_ANYURI), URI.class);
        // Date and time types
        javaByXml.put(TIME, XMLGregorianCalendar.class);
        javaByXml.put(DATE, XMLGregorianCalendar.class);
        javaByXml.put(DATETIME, XMLGregorianCalendar.class);
        // Below data types are defined in the core standard of XACML 2.0, however, an errata published at 29 Jan, 2008
        // deleted them and then added them as XACML specific data types. But to keep compatibility with the core
        // standard (especially for conformance test), we will support both these types and the corrected data types.
        javaByXml.put(new QName(XPATH_DRAFT_16AUG2002, XPATH_DURATION_DAYTIME), Duration.class);
        javaByXml.put(new QName(XPATH_DRAFT_16AUG2002, XPATH_DURATION_YEARMONTH), Duration.class);
    }

    private XMLDataTypeRegistry() {}

    public static Class<?> getJavaType(QName xmlType) throws XMLDataTypeMappingException {
    	Class<?> result = javaByXml.get(xmlType);
    	if (result == null) {
    		throw new XMLDataTypeMappingException("Can't get the registered class for XML type: " + xmlType);
    	}
    	return result;
    }

    public static Class<?> getJavaType(String xmlType) throws XMLDataTypeMappingException {
    	Class<?> result = javaByXml.get(XMLTypeAsQName(xmlType));
    	if (result == null) {
    		throw new XMLDataTypeMappingException("Can't get the registered Java class for XML type: " + xmlType);
    	}
    	return result;
    }

    public static void register(QName typeQName, Class<?> elemClass) {
        javaByXml.put(typeQName, elemClass);
    }

    public static void unregister(QName typeQName) {
        javaByXml.remove(typeQName);
    }

    /**
     * Convert a XML type from a String to a QName
     * @param xmlType
     * @return
     */
    public static QName XMLTypeAsQName(String xmlType) {
        if (xmlType != null) {
            int pos = xmlType.indexOf("#");
            if (pos >= 0) {
                return new QName(xmlType.substring(0, pos), xmlType.substring(pos + 1));
            }
            else {
                return new QName(null, xmlType);
            }
        }
        return null;
    }

    /**
     * Convert a XML type from QName to a String representation.
     * @param qName
     * @return
     */
    public static String XMLTypeAsString(QName qName) {
        if (qName != null) {
            String ns = qName.getNamespaceURI();
            String lp = qName.getLocalPart();
            // Link the namespace and localpart as a XML Schema defined
            // type, for example: http://www.w3.org/2001/XMLSchema#string
            return ns == null ? lp : (ns + "#" + lp);
        }
        return null;
    }

    public static Class<?> getWrapperClassOfPrimitive(Class<?> primitive) throws XMLDataTypeMappingException {
        if (primitive.isPrimitive()) {
            return primitiveMapper.get(primitive);
        }
        else {
            throw new XMLDataTypeMappingException("The given class is not a primitive type : " + primitive.getName());
        }
    }

    /**
     * This method is used to convert XML attributes to a given typed value.
     * @param type
     * @param value
     * @return
     * @throws XMLDataTypeMappingException
     */
    public static Object getTypedValue(Class<?> type, String value) throws XMLDataTypeMappingException {
        Class<?> classType = type;
        if (type.isArray()) {
            throw new XMLDataTypeMappingException("Currently we don't support get Array type value.");
        }
        else {
            if (type.isPrimitive()) {
                classType = getWrapperClassOfPrimitive(type);
            }

            try {
                String methodName = "valueOf";
                Method valueOf = classType.getMethod(methodName, new Class[] {String.class});
                return valueOf.invoke(null, new Object[] {value});
            }
            catch (Exception e) {}

            try {
                Constructor<?> cons = classType.getConstructor(new Class[] {String.class});
                return cons.newInstance(new Object[] {value});
            }
            catch (Exception e) {}

            // convert time/dateTime and other data type to XMLGregorianCalendar or Duration class.
            try {
                String methodName = "new" + type.getSimpleName();
                Method newCalendarOrDuration = DatatypeFactory.class.getMethod(methodName, String.class);
                return newCalendarOrDuration.invoke(getDatatypeFactory(), value);
            }
            catch (Exception e) {}
        }

        throw new XMLDataTypeMappingException("The string value \"" + value + "\" could not be converted to class \"" + 
                type.getName() + "\"");
    }

    public static synchronized DatatypeFactory getDatatypeFactory() throws DatatypeConfigurationException {
        if (dataTypeFactory == null) {
            dataTypeFactory = DatatypeFactory.newInstance();
        }
        return dataTypeFactory;
    }
}