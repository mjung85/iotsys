package an.xacml.policy;

import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.XACMLElement;
import an.xacml.engine.EvaluationContext;

public class VariableReference extends DefaultXACMLElement implements Expression {
    private String variableId;

    public VariableReference(String vId) {
        this.variableId = vId;
    }

    public String getVariableId() {
        return variableId;
    }

    public VariableDefinition getVariableDefinition() throws IndeterminateException {
        // Trying to get a Policy element in parent elements.
        XACMLElement current = this;
        XACMLElement parent = current.getParentElement();
        while (parent != null && !(parent instanceof Policy)) {
            parent = current;
            current = parent.getParentElement();
        }

        if (parent != null) {
            return ((Policy)parent).lookupVariableDefinition(variableId);
        }
        else {
            throw new IndeterminateException("There is no Policy element in the parent tree of VariableReference " +
            		"element.", Constants.STATUS_SYNTAXERROR);
        }
    }

    public Object evaluate(EvaluationContext ctx) throws IndeterminateException {
        try {
            return getVariableDefinition().evaluate(ctx);
        }
        catch (IndeterminateException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw new IndeterminateException("There is error occurs while evaluating VariableReference.", t,
                    Constants.STATUS_SYNTAXERROR);
        }
    }
}