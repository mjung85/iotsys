package an.xacml.policy;

import java.net.URI;

import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.context.MissingAttributeDetail;
import an.xacml.engine.EvaluationContext;
import an.xacml.engine.PDP;

public class AttributeDesignator extends DefaultXACMLElement implements Expression {
    protected URI attributeId;
    /**
     * The designated attribute's data type
     */
    protected URI dataType;
    // Indicate if the designate attribute must be present, if it is true and the attribute is missing, an 
    // IndeterminateException should be thrown.
    protected boolean mustBePresent;
    protected String issuer;

    public AttributeDesignator(URI attrId, URI dataType, String issuer, boolean mustBePresent) {
        this.attributeId = attrId;
        this.dataType = dataType;
        this.issuer = issuer;
        this.mustBePresent = mustBePresent;
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

    public boolean isAttributeMustBePresent() {
        return mustBePresent;
    }

    /**
     * Return the selected attribute values from request. The returned type should be AttributeValue[].
     */
    public Object evaluate(EvaluationContext ctx) throws IndeterminateException {
        return getAttributesFromRequest(ctx);
    }

    protected AttributeValue[] getAttributesFromRequest(EvaluationContext ctx)
    throws IndeterminateException {
        try {
            AttributeValue[] result = ctx.getAttributeValues(attributeId, dataType, issuer, null);

            // Must be present?
            if ((result == null || result.length == 0) && supportMustBePresent() && mustBePresent) {
                // throw an IndeterminateException
                IndeterminateException ex = new IndeterminateException(
                        "The required attribute is missing : " + attributeId, Constants.STATUS_MISSINGATTRIBUTE);
                // Return an IndeterminateException with an array of MissingAttributeDetail. This array object will be
                // finally put into a Status object that includes in a Response.
                ex.setAttachedObject(new MissingAttributeDetail[] {new MissingAttributeDetail(this)});
                throw ex;
            }

            return result;
        }
        catch (IndeterminateException ex) {
            throw ex;
        }
        // Code should not run to here, because the datatype and value are get from an existing Attribute object, it 
        // should has already passed type check. However, we still want place code here in case there are errors.
        catch (Throwable t) {
            throw new IndeterminateException("There is error occurs during retrieve attributes from request.", t,
                    Constants.STATUS_SYNTAXERROR);
        }
    }

    /**
     * Determine if current PDP supports "mustBePresent" feature. If we don't support such feature, there is no 
     * exception throws if the expected attribute doesn't exist in request and "mustBePresent" is set to "true".
     * We configured such an option is to increase the matching performance.
     * @return
     */
    protected boolean supportMustBePresent() {
        PDP pdp = ((AbstractPolicy)getRootElement()).getOwnerPDP();
        if (pdp == null) {
            // We are not in a PDP context.
            return true;
        }
        else {
            return (Boolean)pdp.supportMustBePresent();
        }
    }
}