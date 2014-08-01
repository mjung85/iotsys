package an.xacml.policy;

import java.net.URI;
import java.util.Vector;

import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.context.MissingAttributeDetail;
import an.xacml.engine.EvaluationContext;

public class SubjectAttributeDesignator extends AttributeDesignator {
    private URI subjectCategory;

    public SubjectAttributeDesignator(URI attrId, URI dataType, URI subjCategory, String issuer, boolean mustBePresent) {
        super(attrId, dataType, issuer, mustBePresent);
        this.subjectCategory = subjCategory;
    }

    public URI getSubjectCategory() {
        return subjectCategory;
    }

    protected AttributeValue[] getAttributesFromRequest(EvaluationContext ctx)
    throws IndeterminateException {
        try {      	
            AttributeValue[] result = ctx.getAttributeValues(attributeId, dataType, issuer, subjectCategory);

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

            for (AttributeValue v: result) {
            //	System.out.println("Designator: " + v.getClass().getName());
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
}