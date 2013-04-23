package an.xacml.policy;

import static an.xacml.engine.FunctionRegistry.getInstance;
import static an.xacml.policy.AttributeValue.TRUE;

import java.net.URI;

import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.Matchable;
import an.xacml.engine.EvaluationContext;
import an.xacml.engine.FunctionRegistry;
import an.xacml.policy.function.BuiltInFunction;

public abstract class DefaultMatch extends DefaultXACMLElement implements Matchable {
    /**
     * It's a functionId, which referenced function must return a boolean result.
     */
    protected URI matchId;
    protected AttributeValue attributeValue;
    protected Expression designatorOrSelector;

    /**
     * The method get a built-in function and invoke it by passing attribute value and result of an expression (a 
     * designator or a attribute selector) as its parameters, and then return a boolean result indicate if the match
     * is successful or failed. If any error occurs, an IndeterminateException will be thrown (XACML 2.0 requires).
     */
    public boolean match(EvaluationContext ctx) throws IndeterminateException {
        try {
            Object param1 = attributeValue.evaluate(ctx);
            // The expression evaluate result should be an AttributeValue[]
            AttributeValue[] param2 = (AttributeValue[])designatorOrSelector.evaluate(ctx);

            FunctionRegistry funcReg = getInstance();
            BuiltInFunction func = funcReg.lookup(matchId);
            if (param2 != null) {
                // The <Match> element requires to match each of attributes returned from designator or selector,
                // if at least one matched, it will return true.
                for (AttributeValue attrVal : param2) {
                    if ((AttributeValue)func.invoke(ctx, new Object[] {param1, attrVal}) == TRUE) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (IndeterminateException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw new IndeterminateException("The function is failed to evaluating: ", e,
                    Constants.STATUS_PROCESSINGERROR);
        }
        catch (Throwable t) {
            throw new IndeterminateException("The match operation failed due to error: ", t,
                    Constants.STATUS_SYNTAXERROR);
        }
    }

    public URI getMatchID() {
        return matchId;
    }

    public AttributeValue getAttributeValue() {
        return attributeValue;
    }

    public Expression getAttributeDesignatorOrSelector() {
        return designatorOrSelector;
    }
}