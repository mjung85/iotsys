package an.xacml.policy;

import static an.xacml.policy.Effect.Permit;
import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.Evaluatable;
import an.xacml.IndeterminateException;
import an.xacml.Matchable;
import an.xacml.context.Result;
import an.xacml.engine.EvaluationContext;

public class Rule extends DefaultXACMLElement implements Evaluatable, Matchable {
    private String ruleId;
    private Effect effect;
    private String description;
    private Target target;
    private Condition condition;

    public Rule(String ruleId, Effect effect, String desc, Target target, Condition cond) {
        this.ruleId = ruleId;
        this.effect = effect;
        this.description = desc;
        this.target = target;
        this.condition = cond;
    }

    public Result evaluate(EvaluationContext ctx) throws IndeterminateException {
        try {
            if (target == null || target.match(ctx)) {
                if (condition == null || condition.evaluate(ctx)) {
                    // return the rule's Effect to EvaluationResult
                    if (effect == Permit) {
                        return Result.PERMIT;
                    }
                    else {
                        return Result.DENY;
                    }
                }
                // Condition evaluate to "false", return a NotApplicable.
            }
            // return NotApplicable with a non-parent status-code. Parent policy or policySet should wrap the statusCode
            // as a child to their own EvaluationResult.
            return Result.NOTAPPLICABLE;
        }
        catch (IndeterminateException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw new IndeterminateException("Error occurs while evaluating Rule.", t, Constants.STATUS_SYNTAXERROR);
        }
    }

    public boolean match(EvaluationContext ctx) throws IndeterminateException {
        return target.match(ctx);
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getDescription() {
        return description;
    }

    public Effect getEffect() {
        return effect;
    }

    public Target getTarget() {
        return target;
    }

    public Condition getCondition() {
        return condition;
    }
}