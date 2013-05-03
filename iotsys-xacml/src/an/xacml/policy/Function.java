package an.xacml.policy;

import java.net.URI;

import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.Expression;
import an.xacml.engine.EvaluationContext;

public class Function extends DefaultXACMLElement implements Expression {
    private URI functionId;
    private AttributeValue evalFuncId;

    public Function(URI funcId) {
        this.functionId = funcId;
        try {
            this.evalFuncId = AttributeValue.getInstance(Constants.TYPE_ANYURI, functionId);
        }
        catch (Exception ex) {}
    }

    /**
     * We only need return a function id to parent element, parent element will evaluate the rest elements and use the
     * result as parameters of the built-in function, and then evaluate the built-in function.
     */
    public Object evaluate(EvaluationContext ctx) {
        return evalFuncId;
    }

    public URI getFunctionId() {
        return functionId;
    }
}