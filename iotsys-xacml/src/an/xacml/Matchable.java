package an.xacml;

import an.xacml.engine.EvaluationContext;

/**
 * All elements, included in Target element, PolicySet, Policy and Rule should implement the interface.
 */
public interface Matchable {
    /**
     * Return true if matched.
     * @param request The passed in request context, implementation may retrieve attribute from it.
     * @return
     * @throws IndeterminateException
     */
    public boolean match(EvaluationContext ctx) throws IndeterminateException;
}