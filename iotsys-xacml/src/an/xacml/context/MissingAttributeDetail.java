package an.xacml.context;

import java.net.URI;

import an.xacml.DefaultXACMLElement;
import an.xacml.policy.AttributeDesignator;
import an.xacml.policy.AttributeSelector;
import an.xacml.policy.AttributeValue;

public class MissingAttributeDetail extends DefaultXACMLElement {
    private URI attrId;
    private URI dataType;
    // Indicate the PDP's acceptable attribute values, for now, we don't support return such values.
    private AttributeValue[] acceptableValues;
    private String issuer;
    private String requestContentPath;

    private int hashCode;

    public MissingAttributeDetail(URI attrId, URI dataType, String issuer, String[] attrValues) {
        this.attrId = attrId;
        this.dataType = dataType;
        this.issuer = issuer;
        // For now, we don't support set attribute values.
        generateHashCode();
    }

    public MissingAttributeDetail(AttributeDesignator designator) {
        this.attrId = designator.getAttributeID();
        this.dataType = designator.getDataType();
        this.issuer = designator.getIssuer();
        generateHashCode();
    }

    public MissingAttributeDetail(AttributeSelector selector) {
        this.dataType = selector.getDataType();
        this.requestContentPath = selector.getRequestContentPath();
        generateHashCode();
    }

    public URI getAttributeId() {
        return attrId;
    }

    public URI getDataType() {
        return dataType;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getRequestContentPath() {
        return requestContentPath;
    }

    public AttributeValue[] getAcceptableAttributeValues() {
        return acceptableValues;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            MissingAttributeDetail other = (MissingAttributeDetail)o;
            if (attrId.equals(other.attrId) && dataType.equals(other.dataType) &&
                ((issuer == null && other.issuer == null) || issuer.equals(other.issuer)) &&
                ((requestContentPath == null && other.requestContentPath == null) ||
                  requestContentPath.equals(other.requestContentPath)) &&
                ((acceptableValues == null && other.acceptableValues == null) ||
                  acceptableValues.length == other.acceptableValues.length)) {
                // compare attribute values
                for (int i = 0; i < acceptableValues.length; i ++) {
                    if (!acceptableValues[i].equals(other.acceptableValues[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void generateHashCode() {
        hashCode = 11;
        hashCode = hashCode * 13 + (attrId == null ? 0 : attrId.hashCode());
        hashCode = hashCode * 13 + dataType.hashCode();
        hashCode = hashCode * 13 + (issuer == null ? 0 : issuer.hashCode());
        hashCode = hashCode * 13 + (requestContentPath == null ? 0 : requestContentPath.hashCode());
        if (acceptableValues != null) {
            for (int i = 0; i < acceptableValues.length; i ++) {
                hashCode = hashCode * 13 + acceptableValues[i].hashCode();
            }
        }
    }

    public int hashCode() {
        return hashCode;
    }
}