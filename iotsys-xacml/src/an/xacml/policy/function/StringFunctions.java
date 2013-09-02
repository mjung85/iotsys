package an.xacml.policy.function;

import static an.xacml.policy.AttributeValue.FALSE;
import static an.xacml.policy.AttributeValue.TRUE;
import static an.xacml.policy.function.CommonFunctions.checkArguments;
import static an.xacml.policy.function.CommonFunctions.checkArgumentType;
import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AttributeValue;

@XACMLFunctionProvider
public abstract class StringFunctions {
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-normalize-space"
    })
    public static AttributeValue trim(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue value = (AttributeValue)params[0];
        checkArgumentType(value, Constants.TYPE_STRING);

        try {
            String strValue = (String)value.getValue();
            return AttributeValue.getInstance(value.getDataType(), strValue.trim());
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function trim.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-normalize-to-lower-case"
    })
    public static AttributeValue toLowerCase(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue value = (AttributeValue)params[0];
        checkArgumentType(value, Constants.TYPE_STRING);

        try {
            String strValue = (String)value.getValue();
            return AttributeValue.getInstance(value.getDataType(), strValue.toLowerCase());
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function toLowerCase.", e);
        }
    }

    private static int compareString(Object[] params) throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue s1 = (AttributeValue)params[0];
        AttributeValue s2 = (AttributeValue)params[1];
        checkArgumentType(s1, Constants.TYPE_STRING);
        checkArgumentType(s2, Constants.TYPE_STRING);
        String a = (String)s1.getValue();
        String b = (String)s2.getValue();
        return a.compareTo(b);
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-greater-than"
    })
    public static AttributeValue stringGreaterThan(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareString(params) > 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal"
    })
    public static AttributeValue stringGreaterThanOrEqual(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareString(params) >= 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-less-than"
    })
    public static AttributeValue stringLessThan(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareString(params) < 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal"
    })
    public static AttributeValue stringLessThanOrEqual(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareString(params) <= 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:2.0:function:string-concatenate"
    })
    public static AttributeValue stringConcat(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, -1);
        if (params.length < 2) {
            throw new IndeterminateException("Expected at least 2 parameters, but only got " + params.length);
        }
        AttributeValue s1 = (AttributeValue)params[0];
        checkArgumentType(s1, Constants.TYPE_STRING);

        try {
            StringBuffer buf = new StringBuffer();
            buf.append((String)s1.getValue());
            for (int i = 1; i < params.length; i ++) {
                AttributeValue s = (AttributeValue)params[i];
                checkArgumentType(s, Constants.TYPE_STRING);
                buf.append((String)s.getValue());
            }
            return AttributeValue.getInstance(s1.getDataType(), buf.toString());
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function stringConcat.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:2.0:function:uri-string-concatenate"
    })
    public static AttributeValue uriConcat(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, -1);
        if (params.length < 2) {
            throw new IndeterminateException("Expected at least 2 parameters, but only got " + params.length);
        }
        AttributeValue s1 = (AttributeValue)params[0];
        checkArgumentType(s1, Constants.TYPE_ANYURI);

        try {
            StringBuffer buf = new StringBuffer();
            buf.append(s1.getValue().toString());
            for (int i = 1; i < params.length; i ++) {
                AttributeValue s = (AttributeValue)params[i];
                checkArgumentType(s, Constants.TYPE_STRING);
                buf.append((String)s.getValue());
            }
            return AttributeValue.getInstance(s1.getDataType(), buf.toString());
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function uriConcat.", e);
        }
    }
}