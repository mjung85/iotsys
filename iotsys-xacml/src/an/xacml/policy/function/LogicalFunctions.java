package an.xacml.policy.function;

import static an.xacml.policy.AttributeValue.FALSE;
import static an.xacml.policy.AttributeValue.TRUE;
import static an.xacml.policy.function.CommonFunctions.checkNull;

import java.math.BigInteger;

import an.xacml.Constants;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AttributeValue;

@XACMLFunctionProvider
public abstract class LogicalFunctions {
    public static void checkArgumentType(Object param) throws IndeterminateException {
        if (!(param instanceof Expression)) {
            throw new IndeterminateException("Expected 'Expression', but got '" + param.getClass() + "'");
        }
    }

    @LogicalFunction
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:or"
    })
    public static AttributeValue or(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        if (params != null) {
            checkNull(params);
            for (Object param : params) {
                checkArgumentType(param);
                // evaluate the expression when needed.
                Object evalResult = ((Expression)param).evaluate(ctx);
                if (evalResult == TRUE) {
                    return TRUE;
                }
            }
        }
        return FALSE;
    }

    @LogicalFunction
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:and"
    })
    public static AttributeValue and(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        if (params != null) {
            checkNull(params);
            for (Object param : params) {
                checkArgumentType(param);
                // evaluate the expression when needed.
                Object evalResult = ((Expression)param).evaluate(ctx);
                if (evalResult == FALSE) {
                    return FALSE;
                }
            }
        }
        return TRUE;
    }

    @LogicalFunction
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:n-of"
    })
    public static AttributeValue nOf(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        if (params != null && params.length > 0) {
            checkNull(params);
            checkArgumentType(params[0]);

            // evaluate the first argument first
            AttributeValue arg1 = (AttributeValue)((Expression)params[0]).evaluate(ctx);
            CommonFunctions.checkArgumentType(arg1, Constants.TYPE_INTEGER);
            Object value = arg1.getValue();

            // If the first argument is 0. We directly return true.
            int intArg1 = ((BigInteger)value).intValue();
            if (intArg1 == 0) {
                return TRUE;
            }

            // If followed arguments does not match the first argument, we throw an exception.
            if (intArg1 > params.length - 1) {
                throw new IndeterminateException("Expected at least " + intArg1 + " elements, but we got only " +
                        (params.length - 1) + ".");
            }

            int count = 0;
            for (int i = 1; i < params.length; i ++) {
                checkArgumentType(params[i]);
                // evaluate the expression when needed.
                Object evalResult = ((Expression)params[i]).evaluate(ctx);
                if (evalResult == TRUE && intArg1 == ++ count) {
                    return TRUE;
                }
            }
            return FALSE;
        }
        else {
            throw new IndeterminateException("Expected at least 1 argument, but we got null or 0.");
        }
    }

    @LogicalFunction
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:not"
    })
    public static AttributeValue not(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        if (params != null && params.length == 1) {
            checkNull(params);
            checkArgumentType(params[0]);
            AttributeValue arg = (AttributeValue)((Expression)params[0]).evaluate(ctx);
            CommonFunctions.checkArgumentType(arg, Constants.TYPE_BOOLEAN);
            return arg == TRUE ? FALSE : TRUE;
        }
        else {
            throw new IndeterminateException("Expected 1 parameter, but we got null or more than 1 parameters.");
        }
    }
}