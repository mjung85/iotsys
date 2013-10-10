package an.xacml.engine;

import java.net.URI;
import java.util.Map;

import org.w3c.dom.Element;

import an.xacml.IndeterminateException;
import an.xacml.policy.AttributeValue;

/**
 * Since XACML standard requires Context handler should have ability to retrieve attributes from outside, we defined
 * such an interface and related mechanism. One who will retrieve attributes from outside should implement this
 * interface, and then add it to PDP's configuration file for static registering, or register it at runtime by calling
 * "register" method on AttributeRetrieverRegistry.  Attribute Retrievers that registed in configuration file will be
 * loaded while start PDP.
 * 
 * Once Attribute Retrievers are registered, if an attribute is not retrieved by AttributeDesignator or
 * AttributeSelector, it will be trying to retrieved from these attribute retrievers.
 * 
 * All attribute retriever implementations should have a constructor with following signature if they are going to be
 * registered in configuration file.
 * 
 *          AttributeRetrieverImpl(ConfigElement config);
 * 
 * Since attribute retrievers are registed in a hash set, all implementation should implement hashCode and equals
 * method.
 */
public interface AttributeRetriever {
    // These constants indicate the attribute retriever supported attribute's type
    public static final int ANY = 0;
    public static final int SUBJECT = 1;
    public static final int ACTION = 2;
    public static final int RESOURCE = 3;
    public static final int ENVIRONMENT = 4;

    /**
     * Return the type this attribute retriever supported.
     * @return
     */
    public int getType();

    /**
     * Test if the attribute with given attributeId and dataType is supported by the attribute retriever.  The
     * implementation should keep consistency between this method and retrieveAttributeValues.
     * @param attrId
     * @param dataType
     * @return
     */
    public boolean isAttributeSupported(URI attrId, URI dataType);

    /**
     * This method is used by attribute designator to retrieve external attribtues.
     * @param attrId
     * @param dataType
     * @param issuer
     * @param subjCategory
     * @return
     */
    public AttributeValue[] retrieveAttributeValues(EvaluationContext context,
            URI attrId, URI dataType, String issuer, URI subjCategory)
    throws IndeterminateException;

    /**
     * This method is used by attribute selector to retrieve external attribtues.
     * @param requestCtxPath
     * @param dataType
     * @param request
     * @param additionalNSMappings
     * @return
     */
    public AttributeValue[] retrieveAttributeValues(EvaluationContext context, 
            String requestCtxPath, URI dataType, Element request, Map<String, String> additionalNSMappings)
    throws IndeterminateException;
}