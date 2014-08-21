package an.xacml.policy;

import java.net.URI;

import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.context.MissingAttributeDetail;
import an.xacml.engine.EvaluationContext;
import an.xacml.engine.PDP;

public class AttributeSelector extends DefaultXACMLElement implements Expression {
    private String requestContextPath;
    private boolean mustBePresent;
    protected URI dataType;

    public AttributeSelector(String contextPath, URI dataType, boolean mustBePresent) {
        this.requestContextPath = contextPath;
        this.dataType = dataType;
        this.mustBePresent = mustBePresent;
    }

    /**
     * Return the selected attribute values from request. The returned type should be AttributeValue[].
     */
    public Object evaluate(EvaluationContext ctx) throws IndeterminateException {
        AttributeValue[] result = ctx.getAttributeValues(requestContextPath, dataType);

        // Must be present?
        if ((result == null || result.length == 0) && supportMustBePresent() && mustBePresent) {
            // throw an IndeterminateException
            IndeterminateException ex = new IndeterminateException(
                    "The required attribute is missing : " + requestContextPath, Constants.STATUS_MISSINGATTRIBUTE);
            // Return an IndeterminateException with an array of MissingAttributeDetail. This array object will be
            // finally put into a Status object that includes in a Response.
            ex.setAttachedObject(new MissingAttributeDetail[] {new MissingAttributeDetail(this)});
            throw ex;
        }

        return result;
    }

    public URI getDataType() {
        return dataType;
    }

    public String getRequestContentPath() {
        return requestContextPath;
    }

    public boolean isAttributeMustBePresent() {
        return mustBePresent;
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
            return pdp.supportMustBePresent();
        }
    }
}