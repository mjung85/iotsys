package an.xacml.policy;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xacml.engine.FunctionRegistry;
import an.xacml.policy.function.BuiltInFunction;
import an.xacml.policy.function.LogicalFunction;

public class Apply extends DefaultXACMLElement implements Expression {
    private Expression[] expressions;
    private URI functionId;

    public Apply(URI funcId, Expression[] exps) {
        this.functionId = funcId;
        this.expressions = exps;
    }

    public Object evaluate(EvaluationContext ctx) throws IndeterminateException {
        try {
            if (expressions == null) {
                expressions = new Expression[0];
            }            

            FunctionRegistry functionReg = FunctionRegistry.getInstance();
            BuiltInFunction function = functionReg.lookup(functionId);
            
            Object[] params = new Object[expressions.length];
            // If function is logical function, we won't evaluate the expression, we let function itself to evaluate
            // it as need. This is a standard specified behavior.
            if (isLogicalFunction(function)) {
                params = expressions;
            }
            else {
                for (int i = 0; i < expressions.length; i ++) {
                    params[i] = expressions[i].evaluate(ctx);
                }
            }
            
            try {
                return function.invoke(ctx, params);
            }
            catch (InvocationTargetException functionInvEx) {
                // All indeterminate exception throws from function should be processing error, ...
                Throwable t = functionInvEx.getTargetException();
                if (t instanceof IndeterminateException) {
                    // ... except it has already had an definitely status code.
                    if (((IndeterminateException)t).getStatusCode().equals(Constants.STATUS_UNKNOWNERROR)) {
                        ((IndeterminateException)t).setStatusCode(Constants.STATUS_PROCESSINGERROR);
                    }
                    throw t;
                }
                throw functionInvEx;
            }
        }
        catch (IndeterminateException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw new IndeterminateException("Error occurs while evaluating Apply element.", t, 
                    Constants.STATUS_PROCESSINGERROR);
        }
    }

    public URI getFunctionId() {
        return functionId;
    }

    public Expression[] getExpressions() {
        return expressions;
    }

    private static boolean isLogicalFunction(BuiltInFunction func) {
        if (func.getAttribute(LogicalFunction.class) != null) {
            return true;
        }
        return false;
    }
}