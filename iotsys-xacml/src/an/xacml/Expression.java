package an.xacml;

import an.xacml.engine.EvaluationContext;

/**
 * Expression is to evaluate something, and then get the result. The evaluate method is intend to do this.
 */
public interface Expression {
    /**
     * Evaluation something and return a result.
     * @param request The passed in request context, implementation may retrieve attribute from it.
     * @return The evaluate result.
     */
    public Object evaluate(EvaluationContext ctx) throws IndeterminateException;
}