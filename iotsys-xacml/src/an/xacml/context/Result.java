package an.xacml.context;

import static an.xacml.context.Decision.Permit;
import static an.xacml.context.Decision.Deny;
import static an.xacml.context.Decision.NotApplicable;
import an.xacml.IndeterminateException;
import an.xacml.engine.DefaultCacheable;
import an.xacml.policy.Obligations;

/**
 * Represent the evaluation result returned from Evaluatable.
 */
public class Result extends DefaultCacheable {
    // A quick reference for NotApplicable result.
    public static final Result PERMIT = new Result(Permit, new Status(StatusCode.EVAL_STATUS_OK));
    public static final Result DENY = new Result(Deny, new Status(StatusCode.EVAL_STATUS_OK));
    public static final Result NOTAPPLICABLE = new Result(NotApplicable, new Status(StatusCode.EVAL_STATUS_OK));

    private final Decision decision;
    private final Status status;
    private Obligations obligations;
    private String resourceId;

    public Result(Decision decision, Status status, Obligations oblgs, String resId) {
        this.decision = decision;
        this.status = status;
        this.obligations = oblgs;
        this.resourceId = resId;
    }

    public Result(Decision decision, Status status) {
        this.decision = decision;
        this.status = status;
    }

    public Result(IndeterminateException e) {
        this.decision = Decision.Indeterminate;
        this.status = new Status(e);
    }

    /**
     * A copy constructor which used to clone a Result from an existing one.
     * @param result
     */
    public Result(Result result) {
        this.decision = result.decision;
        this.status = new Status(result.status);
        this.obligations = result.obligations == null ? null : new Obligations(result.obligations);
        this.resourceId = result.resourceId;
    }

    public Decision getDecision() {
        return decision;
    }

    public Status getStatus() {
        return status;
    }

    public void setResourceId(String resId) {
        this.resourceId = resId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void appendObligations(Obligations obls) {
        if (this.obligations == null) {
            this.obligations = obls;
        }
        else {
            this.obligations.appendObligations(obls.getAllObligations());
        }
    }

    public Obligations getObligations() {
        return obligations;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            Result other = (Result)o;
            return compareObject(this.decision, other.getDecision()) &&
                    compareObject(this.status, other.getStatus()) &&
                    compareObject(this.obligations, other.getObligations()) &&
                    compareObject(this.resourceId, other.getResourceId());
        }
        return false;
    }
}