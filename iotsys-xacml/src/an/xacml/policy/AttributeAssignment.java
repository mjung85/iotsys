package an.xacml.policy;

import java.net.URI;

import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xml.XMLDataTypeMappingException;

public class AttributeAssignment extends AttributeValue {
    private final URI attributeId;

    public AttributeAssignment(URI attrId, URI xmlType, Object value) throws XMLDataTypeMappingException {
        super(xmlType, value);
        this.attributeId = attrId;
    }

    public AttributeAssignment(URI attrId, URI xmlType, String value) throws XMLDataTypeMappingException {
        super(xmlType, value);
        this.attributeId = attrId;
    }

    /**
     * A copy constructor which is used to create a new AttributeAssignment instance from an existing one.
     * @param attrAssig
     * @throws XMLDataTypeMappingException
     */
    public AttributeAssignment(AttributeAssignment attrAssig) throws XMLDataTypeMappingException {
        // Since all feilds of AttributeValue are constants, we don't need copy them, just use them in new instance.
        super(attrAssig.dataType, attrAssig.typedValue);
        this.attributeId = attrAssig.attributeId;
        this.childExp = attrAssig.childExp;
    }

    public URI getAttributeId() {
        return attributeId;
    }

    /**
     * This is an unnormal method, it will modify the internal fields' value. Once the AttributeAssignment instance
     * is evaluated, its value is fixed, and its child expression is set to null.
     */
    public Object evaluate(EvaluationContext ctx) throws IndeterminateException {
        if (supportInnerExpression() && this.childExp != null) {
            AttributeValue result = (AttributeValue)super.evaluate(ctx);
            this.childExp = null;
            this.typedValue = result.getValue();
        }
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == this.getClass()) {
            return getDataType().equals(((AttributeAssignment)o).getDataType()) &&
                   compareObject(childExp, ((AttributeAssignment)o).childExp) &&
                   typedValue.equals(((AttributeAssignment)o).typedValue) &&
                   attributeId.equals(((AttributeAssignment)o).attributeId);
        }
        return false;
    }
}