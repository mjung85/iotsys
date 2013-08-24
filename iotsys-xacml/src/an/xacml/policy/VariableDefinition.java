package an.xacml.policy;

import an.xacml.DefaultXACMLElement;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;

public class VariableDefinition extends DefaultXACMLElement {
    private String variableId;
    private Expression expression;

    public VariableDefinition(String vId, Expression exp) {
        this.variableId = vId;
        this.expression = exp;
    }

    public Object evaluate(EvaluationContext ctx) throws IndeterminateException {
        return expression.evaluate(ctx);
    }

    public String getVariableId() {
        return variableId;
    }

    public Expression getExpression() {
        return expression;
    }
}