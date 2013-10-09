package an.xacml.policy.function;

import static an.xacml.policy.AttributeValue.FALSE;
import static an.xacml.policy.AttributeValue.TRUE;
import static an.xacml.policy.function.CommonFunctions.checkArgumentType;
import static an.xacml.policy.function.CommonFunctions.checkArguments;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AttributeValue;

@XACMLFunctionProvider
public abstract class NumberFunctions {
    private static void checkArgumentNumberType(AttributeValue value) throws IndeterminateException {
        if (!(value.getValue() instanceof Number)) {
            throw new IndeterminateException("Expected a number type, but got a " +
                    value.getValue().getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    @EquivalentFunction
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:double-equal"
    })
    public static AttributeValue doubleEquals(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue o1 = (AttributeValue)params[0];
        AttributeValue o2 = (AttributeValue)params[1];
        checkArgumentType(o1, Constants.TYPE_DOUBLE);
        checkArgumentType(o2, Constants.TYPE_DOUBLE);
        if (o1 != null && o2 != null) {
            if (o1 == o2) {
                return TRUE;
            }
            Comparable double1 = (Comparable)o1.getValue();
            Object double2 = o2.getValue();
            return double1.compareTo(double2) == 0 ? TRUE : FALSE;
        }
        return FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-add",
        "urn:oasis:names:tc:xacml:1.0:function:double-add"
    })
    public static AttributeValue numberAdd(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, -1);
        if (params.length < 2) {
            throw new IndeterminateException("Expected at least 2 parameters, but only got " + params.length);
        }
        AttributeValue num = (AttributeValue)params[0];
        checkArgumentNumberType(num);

        try {
            Object realNum = num.getValue();
            Class<?> clazz = realNum.getClass();
            Method add = clazz.getMethod("add", clazz);
            for (int i = 1; i < params.length; i ++) {
                AttributeValue number = (AttributeValue)params[i];
                checkArgumentNumberType(number);
                realNum = add.invoke(realNum, number.getValue());
            }

            return AttributeValue.getInstance(num.getDataType(), realNum);
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function numberAdd.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-subtract",
        "urn:oasis:names:tc:xacml:1.0:function:double-subtract"
    })
    public static AttributeValue numberSubtract(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue num = (AttributeValue)params[0];
        AttributeValue subtrahend = (AttributeValue)params[1];
        checkArgumentNumberType(num);
        checkArgumentNumberType(subtrahend);

        try {
            Object realNum = num.getValue();
            Class<?> clazz = realNum.getClass();
            Method subtract = clazz.getMethod("subtract", clazz);
            return AttributeValue.getInstance(num.getDataType(), subtract.invoke(realNum, subtrahend.getValue()));
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function numberSubtract.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-multiply",
        "urn:oasis:names:tc:xacml:1.0:function:double-multiply"
    })
    public static AttributeValue numberMultiply(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue num = (AttributeValue)params[0];
        AttributeValue multiplicand = (AttributeValue)params[1];
        checkArgumentNumberType(num);
        checkArgumentNumberType(multiplicand);

        try {
            Object realNum = num.getValue();
            Class<?> clazz = realNum.getClass();
            Method multiply = clazz.getMethod("multiply", clazz);
            return AttributeValue.getInstance(num.getDataType(), multiply.invoke(realNum, multiplicand.getValue()));
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function numberMultiply.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-divide",
        "urn:oasis:names:tc:xacml:1.0:function:double-divide"
    })
    public static AttributeValue numberDivide(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue num = (AttributeValue)params[0];
        AttributeValue divisor = (AttributeValue)params[1];
        checkArgumentNumberType(num);
        checkArgumentNumberType(divisor);

        if (((Number)divisor.getValue()).intValue() == 0) {
            throw new IndeterminateException("Divided by zero.");
        }

        try {
            Object realNum = num.getValue();
            Class<?> clazz = realNum.getClass();
            Method divide = clazz.getMethod("divide", clazz);
            return AttributeValue.getInstance(num.getDataType(), divide.invoke(realNum, divisor.getValue()));
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function numberDivide.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-abs",
        "urn:oasis:names:tc:xacml:1.0:function:double-abs"
    })
    public static AttributeValue numberAbs(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue num = (AttributeValue)params[0];
        checkArgumentNumberType(num);

        try {
            Object realNum = num.getValue();
            Class<?> clazz = realNum.getClass();
            Method abs = clazz.getMethod("abs");
            return AttributeValue.getInstance(num.getDataType(), abs.invoke(realNum));
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function numberAbs.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static int compareNumber(Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue num1 = (AttributeValue)params[0];
        AttributeValue num2 = (AttributeValue)params[1];
        checkArgumentNumberType(num1);
        checkArgumentNumberType(num2);

        Object real1 = num1.getValue();
        Object real2 = num2.getValue();
        if (real1 instanceof Comparable) {
            return ((Comparable)real1).compareTo(real2);
        }
        throw new IndeterminateException("Expected a Comparable java typed value, but got a " +
                real1.getClass().getName());
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-greater-than",
        "urn:oasis:names:tc:xacml:1.0:function:double-greater-than"
    })
    public static AttributeValue numberGreaterThan(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareNumber(params) > 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-greater-than-or-equal",
        "urn:oasis:names:tc:xacml:1.0:function:double-greater-than-or-equal"
    })
    public static AttributeValue numberGreaterThanOrEqual(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareNumber(params) >= 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-less-than",
        "urn:oasis:names:tc:xacml:1.0:function:double-less-than"
    })
    public static AttributeValue numberLessThan(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareNumber(params) < 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-less-than-or-equal",
        "urn:oasis:names:tc:xacml:1.0:function:double-less-than-or-equal"
    })
    public static AttributeValue numberLessThanOrEqual(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareNumber(params) <= 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:double-to-integer"
    })
    public static AttributeValue double2Integer(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue d = (AttributeValue)params[0];
        checkArgumentType(d, Constants.TYPE_DOUBLE);

        try {
            BigDecimal num = (BigDecimal)d.getValue();
            return AttributeValue.getInstance(Constants.TYPE_INTEGER, num.toBigInteger());
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function double2Integer.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-to-double"
    })
    public static AttributeValue integer2Double(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue i = (AttributeValue)params[0];
        checkArgumentType(i, Constants.TYPE_INTEGER);

        try {
            BigInteger num = (BigInteger)i.getValue();
            return AttributeValue.getInstance(Constants.TYPE_DOUBLE, num.toString());
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function integer2Double.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:integer-mod"
    })
    public static AttributeValue integerMod(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue num = (AttributeValue)params[0];
        AttributeValue divisor = (AttributeValue)params[1];
        checkArgumentType(num, Constants.TYPE_INTEGER);
        checkArgumentType(divisor, Constants.TYPE_INTEGER);

        if (((Number)divisor.getValue()).intValue() == 0) {
            throw new IndeterminateException("Divided by zero.");
        }

        try {
            BigInteger i = (BigInteger)num.getValue();
            BigInteger d = (BigInteger)divisor.getValue();
            return AttributeValue.getInstance(num.getDataType(), i.mod(d));
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function integerMod.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:round"
    })
    public static AttributeValue round(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue num = (AttributeValue)params[0];
        checkArgumentType(num, Constants.TYPE_DOUBLE);

        try {
            BigDecimal d = (BigDecimal)num.getValue();
            return AttributeValue.getInstance(num.getDataType(), d.round(new MathContext(0, RoundingMode.HALF_UP)));
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function round.", e);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:floor"
    })
    public static AttributeValue floor(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue num = (AttributeValue)params[0];
        checkArgumentType(num, Constants.TYPE_DOUBLE);

        try {
            BigDecimal d = (BigDecimal)num.getValue();
            return AttributeValue.getInstance(num.getDataType(), d.round(new MathContext(0, RoundingMode.FLOOR)));
        }
        catch (Exception e) {
            throw new IndeterminateException("Error occurs while evaluating function floor.", e);
        }
    }
}