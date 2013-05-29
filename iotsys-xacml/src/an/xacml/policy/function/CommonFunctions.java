package an.xacml.policy.function;

import static an.xacml.policy.AttributeValue.FALSE;
import static an.xacml.policy.AttributeValue.TRUE;

import java.math.BigInteger;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AttributeValue;

@XACMLFunctionProvider
public abstract class CommonFunctions {
    public static void checkArguments(Object[] params, int expectedNumber) throws IndeterminateException {
        // check null
        checkNull(params);
        // check parameters number
        if (expectedNumber > 0 && params.length != expectedNumber) {
            throw new IndeterminateException("Expected " + expectedNumber +
                    " parameters, but got " + params.length + ".");
        }
        // check parameters type
        for (Object param : params) {
            if (param != null && !(param instanceof AttributeValue) && !(param instanceof AttributeValue[])) {
                throw new IndeterminateException("Expected 'AttributeValue' type, but got '" +
                        param.getClass().getSimpleName() + "' type.");
            }
        }
    }

    public static void checkNull(Object[] params) throws IndeterminateException {
        for (Object param : params) {
            checkNull(param);
        }
    }

    public static void checkNull(Object arg) throws IndeterminateException {
        if (arg == null) {
            throw new IndeterminateException("The argument is null.");
        }
    }

    public static void checkArgumentType(AttributeValue attrVal, URI expectedType)
    throws IndeterminateException {
        URI actualType = attrVal.getDataType();
        if (!actualType.equals(expectedType)) {
            throw new IndeterminateException("Expected '" + expectedType.toString() +
                    "', but got '" + actualType.toString() + "'");
        }
    }

    @EquivalentFunction
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-equal",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-equal",
        "urn:oasis:names:tc:xacml:1.0:function:integer-equal",
        "urn:oasis:names:tc:xacml:1.0:function:date-equal",
        "urn:oasis:names:tc:xacml:1.0:function:time-equal",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-equal",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-equal",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-equal",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-equal",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-equal",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-equal",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-equal",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-equal"
    })
    public static AttributeValue equals(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 2);
        params = checkArrayArguments(params);
        
        AttributeValue o1 = (AttributeValue)params[0];
        AttributeValue o2 = (AttributeValue)params[1];
        // System.out.println( o1.getValue() + " = " + o2.getValue());
        
        if (o1 != null && o2 != null) {
            if (o1 == o2) {
                return TRUE;
            }
            return o1.equals(o2) ? TRUE : FALSE;
        }
        return FALSE;
    }
    
    public static Object[] checkArrayArguments(Object[] params) throws IndeterminateException {
    	for (int i=0; i < params.length; i++) {
    		if (params[i] instanceof Object[]) {
    			Object[] temp = (Object[]) params[i];
    			if (temp.length == 1) {
    				params[i] = temp[0];
    			} else {
    				throw new IndeterminateException("Array is to long. Please check your policy.");
    			}
    		}
    	}
    	return params;
    }
    

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:integer-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:double-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:time-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:date-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-one-and-only",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-one-and-only"
    })
    public static AttributeValue bagOneAndOnly(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue[] bag = (AttributeValue[])params[0];
        if (bag != null && bag.length == 1) {
            return bag[0];
        }
        throw new IndeterminateException("Expected 1 and only 1 element in bag, but we got " +
                (bag == null ? "'null'" : bag.length));
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:integer-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:double-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:time-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:date-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-bag-size",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-bag-size"
    })
    public static AttributeValue bagSize(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue[] bag = (AttributeValue[])params[0];
        try {
            return AttributeValue.getInstance(Constants.TYPE_INTEGER, BigInteger.valueOf(bag.length));
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluating function bagSize.", ex);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:integer-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:double-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:time-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:date-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-is-in",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-is-in"
    })
    public static AttributeValue bagIsIn(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        checkNull(params);
        AttributeValue o = (AttributeValue)params[0];
        AttributeValue[] bag = (AttributeValue[])params[1];

        for (AttributeValue each : bag) {
            if (equals(ctx, new AttributeValue[] {o, each}) == TRUE) {
                return TRUE;
            }
        }
        return FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-bag",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-bag",
        "urn:oasis:names:tc:xacml:1.0:function:integer-bag",
        "urn:oasis:names:tc:xacml:1.0:function:double-bag",
        "urn:oasis:names:tc:xacml:1.0:function:time-bag",
        "urn:oasis:names:tc:xacml:1.0:function:date-bag",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-bag",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-bag",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-bag",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-bag",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-bag",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-bag",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-bag",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-bag"
    })
    public static AttributeValue[] bagBag(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        for (Object param : params) {
            if (param != null && !(param instanceof AttributeValue)) {
                throw new IndeterminateException("Expected 'AttributeValue' type, but got '" +
                        param.getClass().getSimpleName() + "' type.");
            }
        }
        AttributeValue[] result = new AttributeValue[params.length];
        System.arraycopy(params, 0, result, 0, params.length);
        return result;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:integer-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:double-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:time-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:date-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-intersection",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-intersection"
    })
    public static AttributeValue[] setIntersection(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue[] bag1 = (AttributeValue[])params[0];
        AttributeValue[] bag2 = (AttributeValue[])params[0];

        Set<AttributeValue> result = new HashSet<AttributeValue>();
        for (AttributeValue each1 : bag1) {
            for (AttributeValue each2 : bag2) {
                if (equals(ctx, new AttributeValue[] {each1, each2}) == TRUE) {
                    result.add(each1);
                }
            }
        }
        return result.toArray(bag1);
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:integer-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:double-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:time-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:date-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-at-least-one-member-of",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-at-least-one-member-of"
    })
    public static AttributeValue setAtLeastOneMemberOf(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue[] bag1 = (AttributeValue[])params[0];
        AttributeValue[] bag2 = (AttributeValue[])params[1];

        for (AttributeValue each : bag1) {
            if (bagIsIn(ctx, new Object[] {each, bag2}) == TRUE) {
                return TRUE;
            }
        }
        return FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-union",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-union",
        "urn:oasis:names:tc:xacml:1.0:function:integer-union",
        "urn:oasis:names:tc:xacml:1.0:function:double-union",
        "urn:oasis:names:tc:xacml:1.0:function:time-union",
        "urn:oasis:names:tc:xacml:1.0:function:date-union",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-union",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-union",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-union",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-union",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-union",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-union",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-union",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-union"
    })
    public static AttributeValue[] setUnion(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue[] bag1 = (AttributeValue[])params[0];
        AttributeValue[] bag2 = (AttributeValue[])params[0];

        Set<AttributeValue> result = new HashSet<AttributeValue>();
        for (AttributeValue each : bag1) {
            result.add(each);
        }
        for (AttributeValue each : bag2) {
            result.add(each);
        }
        return result.toArray(bag1);
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-subset",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-subset",
        "urn:oasis:names:tc:xacml:1.0:function:integer-subset",
        "urn:oasis:names:tc:xacml:1.0:function:double-subset",
        "urn:oasis:names:tc:xacml:1.0:function:time-subset",
        "urn:oasis:names:tc:xacml:1.0:function:date-subset",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-subset",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-subset",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-subset",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-subset",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-subset",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-subset",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-subset",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-subset"
    })
    public static AttributeValue setSubset(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue[] bag1 = (AttributeValue[])params[0];
        AttributeValue[] bag2 = (AttributeValue[])params[0];

        Set<AttributeValue> set1 = new HashSet<AttributeValue>();
        for (AttributeValue each : bag1) {
            set1.add(each);
        }
        Set<AttributeValue> set2 = new HashSet<AttributeValue>();
        for (AttributeValue each : bag2) {
            set2.add(each);
        }
        return set2.containsAll(set1) ? TRUE : FALSE;
    }

    @EquivalentFunction
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:boolean-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:integer-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:double-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:time-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:date-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:anyURI-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:hexBinary-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:base64Binary-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-set-equals",
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-set-equals"
    })
    public static AttributeValue setEquals(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue[] bag1 = (AttributeValue[])params[0];
        AttributeValue[] bag2 = (AttributeValue[])params[0];

        // FIXME - I didn't follow the specification here.
        Set<AttributeValue> set1 = new HashSet<AttributeValue>();
        for (AttributeValue each : bag1) {
            set1.add(each);
        }
        Set<AttributeValue> set2 = new HashSet<AttributeValue>();
        for (AttributeValue each : bag2) {
            set2.add(each);
        }
        return (set1.size() == set2.size() && set1.containsAll(set2)) ? TRUE : FALSE;
    }
}