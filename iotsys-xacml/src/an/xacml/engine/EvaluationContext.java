package an.xacml.engine;

import static an.xacml.Constants.ATTR_DATE;
import static an.xacml.Constants.ATTR_DATETIME;
import static an.xacml.Constants.ATTR_TIME;
import static an.xacml.Constants.PREFIX_ACTION;
import static an.xacml.Constants.PREFIX_ENVIRONMENT;
import static an.xacml.Constants.PREFIX_RESOURCE;
import static an.xacml.Constants.PREFIX_SUBJECT;
import static an.xacml.Constants.SUPPORTED_XPATH_VERSIONS;
import static an.xacml.engine.AttributeRetriever.ACTION;
import static an.xacml.engine.AttributeRetriever.ANY;
import static an.xacml.engine.AttributeRetriever.ENVIRONMENT;
import static an.xacml.engine.AttributeRetriever.RESOURCE;
import static an.xacml.engine.AttributeRetriever.SUBJECT;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.management.remote.TargetedNotification;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import an.xacml.AdditionalNamespaceMappingEntry;
import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.XACMLElement;
import an.xacml.context.Attribute;
import an.xacml.context.Request;
import an.xacml.context.Subject;
import an.xacml.context.TargetElement;
import an.xacml.policy.AbstractPolicy;
import an.xacml.policy.AttributeValue;
import an.xacml.policy.NamespaceContextProvider;
import an.xml.XMLDataTypeMappingException;
import an.xml.XMLDataTypeRegistry;
import an.xml.XMLGeneralException;

public class EvaluationContext implements AdditionalNamespaceMappingEntry {
    private Request request;
    private AbstractPolicy policy;
    private XMLGregorianCalendar current;
    private AttributeValue time;
    private AttributeValue date;
    private AttributeValue dateTime;

    private static XPathFactory xpathFactory = XPathFactory.newInstance();
    private XPath xpath;
    // Since the content of attribute value may introduce additional namespaces. We need add these namespaces to the top
    // level element, then they can be retrieved by program that requires those namespaces, such as a attribute selector.
    private Map<String, String> additionalNSMappings;

    public EvaluationContext(Request request) throws EvaluationContextException {
        this.request = request;
        try {
            current = XMLDataTypeRegistry.getDatatypeFactory().newXMLGregorianCalendar();
            time = AttributeValue.getInstance(Constants.TYPE_TIME, current);
            date = AttributeValue.getInstance(Constants.TYPE_DATE, current);
            dateTime = AttributeValue.getInstance(Constants.TYPE_DATETIME, current);
            xpath = xpathFactory.newXPath();
        } catch (Exception e) {
            throw new EvaluationContextException("Cannot initialize evaluation context due to error :", e);
        }
    }

    public void setCurrentEvaluatingPolicy(AbstractPolicy policy) {
        this.policy = policy;

        XACMLElement parent = policy;
        while (parent != null) {
            populatePolicyNamespaceMappings((AbstractPolicy)parent);
            parent = parent.getParentElement();
        }
    }

    public AbstractPolicy getCurrentEvaluatingPolicy() {
        return policy;
    }

    public Request getRequest() {
        return request;
    }

    public AttributeValue[] getAttributeValues(URI attrId, URI dataType, String issuer, URI subjCategory)
    throws XMLGeneralException, IndeterminateException {
        // First trying to get attributes from request
        AttributeValue[] result = getAttributeValuesFromRequest(attrId, dataType, issuer, subjCategory);
        // If no attribute got, we will try to get from attribute retrievers.
        if (result == null || result.length == 0) {
            // Trying to get dateTime from context.
            result = tryingGetDateTimeAttribute(attrId);
            if (result != null && result.length > 0) {
                return result;
            }

            // Trying to get attribute from attribute retrievers.
            AttributeRetrieverRegistry reg = AttributeRetrieverRegistry.getInstance(policy.getOwnerPDP());

            int type = getPossibleTypeOfAttributeId(attrId.toString());
            AttributeRetriever[] attrRetrs = reg.getAttributeRetrieversByType(type);
            for (AttributeRetriever attrRetr : attrRetrs) {
                if (attrRetr.isAttributeSupported(attrId, dataType)) {
                    result = attrRetr.retrieveAttributeValues(this, attrId, dataType, issuer, subjCategory);
                    if (result != null && result.length > 0) {
                        return result;
                    }
                }
            }

            if (type != ANY) {
                attrRetrs = reg.getAllAttributeRetrievers();
                for (AttributeRetriever attrRetr : attrRetrs) {
                    if (attrRetr.isAttributeSupported(attrId, dataType)) {
                        result = attrRetr.retrieveAttributeValues(this, attrId, dataType, issuer, subjCategory);
                        if (result != null && result.length > 0) {
                            return result;
                        }
                    }
                }
            }
        }
        for (AttributeValue v: result) {
        	// System.out.println("EvaluationContext: " + v.getClass().getName());
        }
        
        return result != null ? result : new AttributeValue[0];
    }

    public AttributeValue[] getAttributeValues(String requestCtxPath, URI dataType) throws IndeterminateException {
        try {
            // First trying to get attributes from request
            AttributeValue[] result = getAttributeValuesFromRequest(requestCtxPath, dataType);
            // If no attribute got, we will try to get from attribute retrievers.
            if (result == null || result.length == 0) {
                AttributeRetrieverRegistry reg = AttributeRetrieverRegistry.getInstance(policy.getOwnerPDP());
                Map<String, String> mappings = new HashMap<String, String>();
                // Additional Namespace mappings.
                mappings.putAll(policy.getPolicyNamespaceMappings());
                if (additionalNSMappings != null) {
                    mappings.putAll(additionalNSMappings);
                }

                AttributeRetriever[] attrRetrs = reg.getAllAttributeRetrievers();
                for (AttributeRetriever attrRetr : attrRetrs) {
                    result = attrRetr.retrieveAttributeValues(
                            this, requestCtxPath, dataType, getRequest().getRootNode(), mappings);
                    if (result != null && result.length > 0) {
                        return result;
                    }
                }
            }
            return result != null ? result : new AttributeValue[0];
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception e) {
            throw new IndeterminateException("There is error occurs during retrieve attributes from request.", e,
                    Constants.STATUS_SYNTAXERROR);
        }
    }

    private AttributeValue[] tryingGetDateTimeAttribute(URI attrId) {
        if (ATTR_TIME.equals(attrId)) {
            return new AttributeValue[] {time};
        }
        else if (ATTR_DATE.equals(attrId)) {
            return new AttributeValue[] {date};
        }
        else if (ATTR_DATETIME.equals(attrId)) {
            return new AttributeValue[] {dateTime};
        }
        return null;
    }

    public void setAdditionalNSMappings(Map<String, String> mappings) {
        this.additionalNSMappings = mappings;
        // We should populate the namespace mappings that provided by current element to XPath's context.
        // We don't remove previous mappings.
        populateAdditionalNamespaces(additionalNSMappings);
    }

    public Map<String, String> getAdditionalNSMappings() {
        return additionalNSMappings;
    }

    private AttributeValue[] getAttributeValuesFromRequest(URI attrId, URI dataType, String issuer, URI subjCategory)
    throws IndeterminateException {
        try {
            Vector<AttributeValue> result = new Vector<AttributeValue>();

            TargetElement[] allElements;
            if (subjCategory == null) {
                // if going to get a subject attribute.
                allElements = getRequest().getAllTargetElements();
            }
            else {
                // get non-subject attribute.
                allElements = getRequest().getSubjects();
            }
            
            for (int i = 0; i < allElements.length; i ++) {
                // If this is for subject attribute designator, the search scope is restriced to same category.
                if (subjCategory == null || subjCategory.equals(((Subject)allElements[i]).getSubjectCategory())) {
                    Attribute[] attrs = allElements[i].getAttributeById(attrId);
                    for (Attribute attr : attrs) {
                        // The designate attribute should not be null
                        if (attr != null && 
                            // If the designated data type is null, we don't need to match it
                            (attr.getDataType() == null || dataType.equals(attr.getDataType())) &&
                            // If the issuer is required for attribute designator, we need do matching with designated 
                            // attribute.
                            (issuer == null || issuer.equals(attr.getIssuer()))) {
                        	
                        	Object[] values = attr.getAttributeValues();
                            for (int x = 0; x < values.length; x ++) {
                                result.add(AttributeValue.getInstance(attr.getDataType(), values[x]));
                            }
                        }
                    }
                }
            }
            return result.toArray(new AttributeValue[0]);
        }
        // Code should not run to here, because the datatype and value are get from an existing Attribute object, it 
        // should has already passed type check. However, we still want place code here in case there are errors.
        catch (Throwable t) {
            throw new IndeterminateException("There is error occurs during retrieve attributes from request.", t,
                    Constants.STATUS_SYNTAXERROR);
        }
    }

    private AttributeValue[] getAttributeValuesFromRequest(String requestCtxPath, URI dataType)
    throws IndeterminateException {
        Vector<AttributeValue> result = new Vector<AttributeValue>();
        NodeList nList = null;

        try {
            AbstractPolicy root = getCurrentEvaluatingPolicy();
            if (root != null) {
                isPolicyXPathVersionSupported(root);
                nList = (NodeList)xpath.evaluate(
                        requestCtxPath, getRequest().getRootNode(), XPathConstants.NODESET);

                for (int i = 0; i < nList.getLength(); i ++) {
                    Node node = nList.item(i);
                    short nodeType = node.getNodeType();
                    if (nodeType == Node.TEXT_NODE || nodeType == Node.ATTRIBUTE_NODE || 
                        nodeType == Node.PROCESSING_INSTRUCTION_NODE || nodeType == Node.COMMENT_NODE) {
                        result.add(AttributeValue.getInstance(dataType, node.getNodeValue()));
                    }
                    else {
                        // throw an IndeterminateException of syntax-error
                        throw new IndeterminateException("The node selected by specfied XPath expression is not one of "
                        + "following - a text node, an attribute node, a processing instruction node or a comment node.", 
                        Constants.STATUS_SYNTAXERROR);
                    }
                }

                return result.toArray(new AttributeValue[0]);
            }
            else {
                throw new IndeterminateException("The root element is NULL, or is NOT a Policy or PolicySet object.",
                        Constants.STATUS_SYNTAXERROR);
            }
        }
        catch (IndeterminateException ie) {
            throw ie;
        }
        catch (XPathExpressionException xe) {
            // throw an IndeterminateException of syntax-error
            throw new IndeterminateException("Error occurs when evaluating xpath expression against request", xe, 
                    Constants.STATUS_PROCESSINGERROR);
        }
        catch (XMLDataTypeMappingException dmEx) {
            // throw an IndeterminateException of syntax-error
            throw new IndeterminateException("Error occurs when initialize an AttributeValue object", dmEx, 
                    Constants.STATUS_SYNTAXERROR);
        }
        catch (Throwable t) {
            throw new IndeterminateException("Error occurs while evaluating AttributeSelector.", t, 
                    Constants.STATUS_SYNTAXERROR);
        }
    }

    /**
     * We need populate top level Policy or PolicySet's namespace mappings to current XPath. This is needed by
     * Attribute Selector
     */
    private void populatePolicyNamespaceMappings(AbstractPolicy policy) {
        // Use root policy's namespace
        if (policy != null) {
            NamespaceContextProvider nsCtx = new NamespaceContextProvider();
            Map<String, String> nsMap = policy.getPolicyNamespaceMappings();
            Iterator<String> keys = nsMap.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                nsCtx.addNSMapping(key, nsMap.get(key));
            }
            xpath.setNamespaceContext(nsCtx);
        }
    }

    /**
     * If current element has additional namespace mappings, we need populate them to XPath, this is needed by Attribute
     * Selector.
     * @param nsMappings
     */
    private void populateAdditionalNamespaces(Map<String, String> nsMappings) {
        NamespaceContext obj = xpath.getNamespaceContext();
        if (obj != null && obj instanceof NamespaceContextProvider) {
            NamespaceContextProvider nsCtx = (NamespaceContextProvider)obj;
            Iterator<String> keys = nsMappings.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                nsCtx.addNSMapping(key, nsMappings.get(key));
            }
            xpath.setNamespaceContext(nsCtx);
        }
    }

    public static boolean isPolicyXPathVersionSupported(AbstractPolicy policy) throws IndeterminateException {
        URI xpathVer = policy.getXPathVersion();
        if (xpathVer != null) {
            String strVal = xpathVer.toString();
            for (int i = 0; i < SUPPORTED_XPATH_VERSIONS.length; i ++) {
                if (strVal.equals(SUPPORTED_XPATH_VERSIONS[i])) {
                    return true;
                }
            }
        }
        else {
            throw new IndeterminateException("The required XPathVersion is not configured in parent Policy or " +
                    "PolicySet element.", Constants.STATUS_SYNTAXERROR);
        }

        return false;
    }

    private static int getPossibleTypeOfAttributeId(String attrId) {
        if (attrId.startsWith(PREFIX_SUBJECT)) {
            return SUBJECT;
        }
        if (attrId.startsWith(PREFIX_ACTION)) {
            return ACTION;
        }
        if (attrId.startsWith(PREFIX_RESOURCE)) {
            return RESOURCE;
        }
        if (attrId.startsWith(PREFIX_ENVIRONMENT)) {
            return ENVIRONMENT;
        }
        return ANY;
    }
}