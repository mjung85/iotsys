package an.xacml.policy.function;

import static an.xacml.policy.AttributeValue.FALSE;
import static an.xacml.policy.AttributeValue.TRUE;
import static an.xacml.policy.function.CommonFunctions.checkArguments;
import static an.xacml.policy.function.CommonFunctions.checkArgumentType;

import java.net.URI;

import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xacml.engine.FunctionRegistry;
import an.xacml.policy.AttributeValue;

@XACMLFunctionProvider
public abstract class HighOrderBagFunctions {
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:any-of"
    })
    public static AttributeValue anyOf(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 3);
        AttributeValue funcId = (AttributeValue)params[0];
        AttributeValue o = (AttributeValue)params[1];
        AttributeValue[] bag = (AttributeValue[])params[2];
        checkArgumentType(funcId, Constants.TYPE_ANYURI);

        try {
            BuiltInFunction func = FunctionRegistry.getInstance().lookup((URI)funcId.getValue());
            for (AttributeValue each : bag) {
                AttributeValue evalResult = (AttributeValue)func.invoke(ctx, new Object[] {o, each});
                if (evalResult == TRUE) {
                    return TRUE;
                }
            }
            return FALSE;
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluate anyOf : " + funcId, ex);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:all-of"
    })
    public static AttributeValue allOf(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 3);
        AttributeValue funcId = (AttributeValue)params[0];
        AttributeValue o = (AttributeValue)params[1];
        AttributeValue[] bag = (AttributeValue[])params[2];
        checkArgumentType(funcId, Constants.TYPE_ANYURI);

        try {
            BuiltInFunction func = FunctionRegistry.getInstance().lookup((URI)funcId.getValue());
            for (AttributeValue each : bag) {
                AttributeValue evalResult = (AttributeValue)func.invoke(ctx, new Object[] {o, each});
                if (evalResult == FALSE) {
                    return FALSE;
                }
            }
            return TRUE;
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluate allOf : " + funcId, ex);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:any-of-any"
    })
    public static AttributeValue anyOfAny(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 3);
        AttributeValue funcId = (AttributeValue)params[0];
        AttributeValue[] bag1 = (AttributeValue[])params[1];
        AttributeValue[] bag2 = (AttributeValue[])params[2];
        checkArgumentType(funcId, Constants.TYPE_ANYURI);

        try {
            BuiltInFunction func = FunctionRegistry.getInstance().lookup((URI)funcId.getValue());
            for (AttributeValue each1 : bag1) {
                for (AttributeValue each2 : bag2) {
                    AttributeValue evalResult = (AttributeValue)func.invoke(ctx, new Object[] {each1, each2});
                    if (evalResult == TRUE) {
                        return TRUE;
                    }
                }
            }
            return FALSE;
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluate anyOfAny : " + funcId, ex);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:all-of-any"
    })
    public static AttributeValue allOfAny(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 3);
        AttributeValue funcId = (AttributeValue)params[0];
        AttributeValue[] bag1 = (AttributeValue[])params[1];
        AttributeValue[] bag2 = (AttributeValue[])params[2];
        checkArgumentType(funcId, Constants.TYPE_ANYURI);

        try {
            BuiltInFunction func = FunctionRegistry.getInstance().lookup((URI)funcId.getValue());
            for (AttributeValue each1 : bag1) {
                boolean flag = false;
                for (AttributeValue each2 : bag2) {
                    AttributeValue evalResult = (AttributeValue)func.invoke(ctx, new Object[] {each1, each2});
                    if (evalResult == TRUE) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return FALSE;
                }
            }
            return TRUE;
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluate allOfAny : " + funcId, ex);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:any-of-all"
    })
    public static AttributeValue anyOfAll(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 3);
        AttributeValue funcId = (AttributeValue)params[0];
        AttributeValue[] bag1 = (AttributeValue[])params[1];
        AttributeValue[] bag2 = (AttributeValue[])params[2];
        checkArgumentType(funcId, Constants.TYPE_ANYURI);

        try {
            BuiltInFunction func = FunctionRegistry.getInstance().lookup((URI)funcId.getValue());
            for (AttributeValue each1 : bag1) {
                boolean flag = true;
                for (AttributeValue each2 : bag2) {
                    AttributeValue evalResult = (AttributeValue)func.invoke(ctx, new Object[] {each1, each2});
                    if (evalResult == FALSE) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    return TRUE;
                }
            }
            return FALSE;
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluate anyOfAll : " + funcId, ex);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:all-of-all"
    })
    public static AttributeValue allOfAll(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 3);
        AttributeValue funcId = (AttributeValue)params[0];
        AttributeValue[] bag1 = (AttributeValue[])params[1];
        AttributeValue[] bag2 = (AttributeValue[])params[2];
        checkArgumentType(funcId, Constants.TYPE_ANYURI);

        try {
            BuiltInFunction func = FunctionRegistry.getInstance().lookup((URI)funcId.getValue());
            for (AttributeValue each1 : bag1) {
                for (AttributeValue each2 : bag2) {
                    AttributeValue evalResult = (AttributeValue)func.invoke(ctx, new Object[] {each1, each2});
                    if (evalResult == FALSE) {
                        return FALSE;
                    }
                }
            }
            return TRUE;
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluate allOfAll : " + funcId, ex);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:map"
    })
    public static AttributeValue[] map(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue funcId = (AttributeValue)params[0];
        AttributeValue[] bag = (AttributeValue[])params[1];
        checkArgumentType(funcId, Constants.TYPE_ANYURI);

        AttributeValue[] result = new AttributeValue[bag.length];
        try {
            BuiltInFunction func = FunctionRegistry.getInstance().lookup((URI)funcId.getValue());
            for (int i = 0; i < bag.length; i ++) {
                result[i] = (AttributeValue)func.invoke(ctx, new Object[] {bag[i]});
            }
            return result;
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluate map : " + funcId, ex);
        }
    }
}