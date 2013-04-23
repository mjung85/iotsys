package an.xacml.policy;

import static an.xml.XMLDataTypeRegistry.getJavaType;
import static an.xml.XMLDataTypeRegistry.getTypedValue;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import an.xacml.AdditionalNamespaceMappingEntry;
import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.XACMLElement;
import an.xacml.engine.EvaluationContext;
import an.xacml.engine.PDP;
import an.xml.XMLDataTypeMappingException;

public class AttributeValue extends DefaultXACMLElement implements Expression, AdditionalNamespaceMappingEntry {
    public static AttributeValue TRUE;
    public static AttributeValue FALSE;
    static {
        try {
            TRUE = new AttributeValue(Constants.TYPE_BOOLEAN, Boolean.TRUE);
            FALSE = new AttributeValue(Constants.TYPE_BOOLEAN, Boolean.FALSE);
        } catch (XMLDataTypeMappingException e) {}
    }

    protected URI dataType;
    protected Class<?> javaType;
    protected Object typedValue;
    // the attribute value can include child expression, we should evaluate it and return the result.
    protected Expression childExp;

    // Since the content of attribute value may introduce additional namespaces. We need add these namespaces to the top
    // level element, then they can be retrieved by program that requires those namespaces, such as a attribute selector.
    private Map<String, String> additionalNSMappings;

    /**
     * Construct from a typed value. The value should be come from context's Attribute. The context's Attribute element
     * is in charge of parse the value and convert it to a typed value.
     * @param value
     * @throws XMLDataTypeMappingException 
     */
    protected AttributeValue(URI xmlType, Object value) throws XMLDataTypeMappingException {
    	Class<?> expectedType = getJavaType(xmlType.toString());
    	Class<?> actualType = value.getClass();
        if (!expectedType.isAssignableFrom(actualType)) {
            throw new XMLDataTypeMappingException(
            		"The attribute value's type '" + actualType.getName() +
            		"' doesn't match the XMLSchema data type '" + expectedType.getName() + "'.");
        }
        this.dataType = xmlType;
        this.typedValue = value;
    }

    /**
     * Construct from a String value. The value should be come from underlying data store. The data store adapter is 
     * in charge of parse the value in data store. This class is in charge of convert it to a typed value.
     * @param value
     * @throws XMLDataTypeMappingException 
     */
    protected AttributeValue(URI xmlType, String value) throws XMLDataTypeMappingException {
        this.dataType = xmlType;
        this.javaType = getJavaType(xmlType.toString());
        this.typedValue = getTypedValue(javaType, value);
    }

    private static void checkNull(URI xmlType, Object value) throws XMLDataTypeMappingException {
    	if (xmlType == null) {
    		throw new XMLDataTypeMappingException("The given XML type is NULL.");
    	}
    	if (value == null) {
    		throw new XMLDataTypeMappingException("The given value is NULL.");
    	}
    }

    public static AttributeValue getInstance(URI xmlType, String value) throws XMLDataTypeMappingException {
    	checkNull(xmlType, value);
        if (xmlType.equals(Constants.TYPE_BOOLEAN) && value != null) {
            if (value.equalsIgnoreCase("true")) {
                return TRUE;
            }
            else if (value.equalsIgnoreCase("false")) {
                return FALSE;
            }
        }
        return new AttributeValue(xmlType, value);
    }

    public static AttributeValue getInstance(URI xmlType, Object value) throws XMLDataTypeMappingException {
    	checkNull(xmlType, value);
        if (xmlType.equals(Constants.TYPE_BOOLEAN) && value != null && value instanceof Boolean) {
            if (value.equals(Boolean.TRUE)) {
                return TRUE;
            }
            else if (value.equals(Boolean.FALSE)) {
                return FALSE;
            }
        }
        return new AttributeValue(xmlType, value);
    }

    public void setChildExpression(Expression exp) {
        this.childExp = exp;
    }

    public Expression getChildExpression() {
        return this.childExp;
    }

    /**
     * Just return self if there is no child expression, or return the child expression's evaluation result.
     * @throws IndeterminateException 
     */
    public Object evaluate(EvaluationContext ctx) throws IndeterminateException {
        // First evaluate child expression.
        if (supportInnerExpression() && this.childExp != null) {
            Object result = childExp.evaluate(ctx);
            AttributeValue finalResult = null;
            if (result.getClass().isArray()) {
                // We expected a single AttributeValue instance.
                if (Array.getLength(result) == 1) {
                    finalResult = ((AttributeValue[])result)[0];
                }
                // If multiple AttributeValue instances are returned, we will throw an IndeterminateException.
                else {
                    throw new IndeterminateException("The child expression returned more than one AttributeValue.",
                            Constants.STATUS_SYNTAXERROR);
                }
            }
            else {
                finalResult = (AttributeValue)result;
            }
            // If the child expression's data type does not match the parents' one, we will throw an exception.
            if (!finalResult.getDataType().equals(this.dataType)) {
                throw new IndeterminateException("The child expression's data type '" + finalResult.getDataType() + 
                        "' doesn't match the parents' data type '" + dataType + "'.", Constants.STATUS_SYNTAXERROR);
            }
            return finalResult;
        }
        // If no child expression, we will return this.
        else {
            return this;
        }
    }

    public URI getDataType() {
        return dataType;
    }

    public Object getValue() throws IndeterminateException {
        populateAdditionalNSMappings();
        if (supportInnerExpression() && this.childExp != null) {
            // We don't need return the value of AttributeValue because the caller will get the most inner expression's
            // AttributeValue which has no child expression, caller can directly get value from it.
            throw new IndeterminateException("The child expression must be evaluated before get the value.",
                    Constants.STATUS_PROCESSINGERROR);
        }
        else {
            return typedValue;
        }
    }

    public void setAdditionalNSMappings(Map<String, String> mappings) {
        this.additionalNSMappings = mappings;
    }

    public Map<String, String> getAdditionalNSMappings() {
        return additionalNSMappings;
    }

    /**
     * If this element has additional namespace mappings that required by attribtue selector, we need add them to the
     * top level Policy or PolicySet.
     */
    private void populateAdditionalNSMappings() {
        if (additionalNSMappings != null && additionalNSMappings.size() > 0) {
            XACMLElement root = getRootElement();
            if (root != null && root instanceof AbstractPolicy) {
                Map<String, String> nsMap = ((AbstractPolicy)root).getPolicyNamespaceMappings();
                Iterator<String> keys = additionalNSMappings.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    nsMap.put(key, additionalNSMappings.get(key));
                }
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == this.getClass()) {
            return dataType.equals(((AttributeValue)o).dataType) &&
                   compareObject(childExp, ((AttributeValue)o).childExp) &&
                   typedValue.equals(((AttributeValue)o).typedValue);
        }
        return false;
    }

    public int hashCode() {
        int hashCode = 17;
        hashCode = hashCode * 13 + typedValue.hashCode();
        hashCode = hashCode * 13 + (childExp == null ? 0 : childExp.hashCode());
        return hashCode;
    }

    protected boolean supportInnerExpression() {
        XACMLElement root = getRootElement();
        if (root instanceof AbstractPolicy) {
            PDP pdp = ((AbstractPolicy)root).getOwnerPDP();
            if (pdp != null) {
                return pdp.supportInnerExpression();
            }
        }
        // If we are in response, or we are not in PDP, then PEP should in charge of evaluate it.
        return false;
    }
}