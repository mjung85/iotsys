package an.xacml.context;

import static an.xml.XMLDataTypeRegistry.getJavaType;
import static an.xml.XMLDataTypeRegistry.getTypedValue;

import java.net.URI;

import an.xacml.DefaultXACMLElement;
import an.xml.XMLDataTypeMappingException;

public class Attribute extends DefaultXACMLElement {
    private int hashCode;
    private int[] hashCodeByValues;
    private URI attributeId;
    private URI dataType;
    /**
     * Issuer is optional.
     */
    private String issuer;
    private Class<?> javaType;
    /**
     * At least one AttributeValue should be present. So we always won't get null for this field.
     */
    private Object[] typedValues;

    /**
     * Since XACML 2.0 doesn't restrict the context AttributeValue, so it can be any value. So we just pass the values
     * as String[], it comes from the text between &ltAttributeValue&gt and &ltAttributeValue/&gt.
     * @param attrId
     * @param xmlType
     * @param issuer
     * @param values
     * @throws XMLDataTypeMappingException 
     */
    public Attribute(URI attrId, URI xmlType, String issuer, String[] values) throws XMLDataTypeMappingException {
        this.attributeId = attrId;
        this.dataType = xmlType;
        this.issuer = issuer;

        javaType = getJavaType(xmlType.toString());
        typedValues = new Object[values.length];
        for (int i = 0; i < values.length; i ++) {
            typedValues[i] = getTypedValue(javaType, values[i]);
        }
    }

    public Object[] getAttributeValues() {
        return typedValues;
    }

    public URI getAttributeID() {
        return attributeId;
    }

    public URI getDataType() {
        return dataType;
    }

    public String getIssuer() {
        return issuer;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            Attribute other = (Attribute)o;
            Object[] otherValues = other.getAttributeValues();
            if (attributeId.equals(other.getAttributeID()) &&
                (issuer != null && issuer.equals(other.getIssuer())) &&
                typedValues != null && otherValues != null && 
                typedValues.length == otherValues.length) {
                for (int i = 0; i < typedValues.length; i ++) {
                    if (!typedValues[i].equals(otherValues[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public /*synchronized*/ int hashCode() {
        return hashCode;
    }

    public /*synchronized*/ int[] hashCodeByValues() {
        return hashCodeByValues;
    }
}