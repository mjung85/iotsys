package an.xacml.policy;

import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;

public class Condition extends DefaultXACMLElement {
    private Expression expression;

    public Condition(Expression exp) {
        this.expression = exp;
    }

    public boolean evaluate(EvaluationContext ctx) throws IndeterminateException {
        Object result = null;
        Object condVal = expression.evaluate(ctx);
        if (condVal != null && condVal instanceof AttributeValue) {
            result = ((AttributeValue)condVal).getValue();
            if (result != null && result instanceof Boolean) {
                return (Boolean)result;
            }
        }
        throw new IndeterminateException("Expect a Boolean value, but got a " +
                (result == null ? " null value." : result.getClass().getSimpleName() + " value : " + result),
                Constants.STATUS_PROCESSINGERROR);
    }

    public Expression getExpression() {
        return expression;
    }
}