package an.xacml.policy;

import an.xacml.DefaultXACMLElement;
import an.xacml.IndeterminateException;
import an.xacml.Matchable;
import an.xacml.engine.EvaluationContext;

/**
 * ConjunctiveMatch provide a default implementation for a set of Matchables,
 * if all Matchables are matched, the match method will return true, otherwise,
 * will return false.
 */
public abstract class ConjunctiveMatch extends DefaultXACMLElement implements Matchable {
    /**
     * The list of Matchable will be set by subclass that extend ConjunctiveMatch
     */
    protected Matchable[] matches;

    public boolean match(EvaluationContext ctx) throws IndeterminateException {
        if (matches != null) {
            for (int i = 0; i < matches.length; i ++) {
                if (!matches[i].match(ctx)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Matchable[] getMatchables() {
        return matches;
    }
}