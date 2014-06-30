package an.xacml.policy.function;

import static an.xacml.policy.AttributeValue.FALSE;
import static an.xacml.policy.AttributeValue.TRUE;
import static an.xacml.policy.function.CommonFunctions.checkArgumentType;
import static an.xacml.policy.function.CommonFunctions.checkArguments;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AttributeValue;

@XACMLFunctionProvider
public abstract class DateTimeFunctions {
    private static void checkArgumentDateType(AttributeValue attrVal) throws IndeterminateException {
        Object actualAttr = attrVal.getValue();
        if (!(actualAttr instanceof XMLGregorianCalendar)) {
            throw new IndeterminateException("Expected a XMLGregorianCalendar type, but we got a " +
                    actualAttr.getClass().getName());
        }
    }

    private static void checkArgumentDurationType(AttributeValue attrVal) throws IndeterminateException {
        Object actualAttr = attrVal.getValue();
        if (!(actualAttr instanceof Duration)) {
            throw new IndeterminateException("Expected a Duration type, but we got a " +
                    actualAttr.getClass().getName());
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-add-dayTimeDuration",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-add-yearMonthDuration",
        "urn:oasis:names:tc:xacml:1.0:function:date-add-yearMonthDuration"
    })
    public static AttributeValue dateAdd(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue date = (AttributeValue)params[0];
        AttributeValue duration = (AttributeValue)params[1];
        checkArgumentDateType(date);
        checkArgumentDurationType(duration);

        try {
            XMLGregorianCalendar cloned = (XMLGregorianCalendar)((XMLGregorianCalendar)date.getValue()).clone();
            Duration actualDuration = (Duration)duration.getValue();
            cloned.add(actualDuration);

            return AttributeValue.getInstance(date.getDataType(), cloned);
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluating function dateAdd", ex);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-subtract-dayTimeDuration",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-subtract-yearMonthDuration",
        "urn:oasis:names:tc:xacml:1.0:function:date-subtract-yearMonthDuration"
    })
    public static AttributeValue dateSubtract(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue date = (AttributeValue)params[0];
        AttributeValue duration = (AttributeValue)params[1];
        checkArgumentDateType(date);
        checkArgumentDurationType(duration);

        try {
            XMLGregorianCalendar cloned = (XMLGregorianCalendar)((XMLGregorianCalendar)date.getValue()).clone();
            Duration actualDuration = (Duration)duration.getValue();
            cloned.add(actualDuration.negate());

            return AttributeValue.getInstance(date.getDataType(), cloned);
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluating function dateSubtract", ex);
        }
    }

    private static int compareDate(Object[] params) throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue date1 = (AttributeValue)params[0];
        AttributeValue date2 = (AttributeValue)params[1];
        checkArgumentDateType(date1);
        checkArgumentDateType(date2);

        XMLGregorianCalendar actualDate1 = (XMLGregorianCalendar)date1.getValue();
        XMLGregorianCalendar actualDate2 = (XMLGregorianCalendar)date2.getValue();
        return actualDate1.compare(actualDate2);
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:date-greater-than",
        "urn:oasis:names:tc:xacml:1.0:function:time-greater-than",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than"
    })
    public static AttributeValue dateGreaterThan(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareDate(params) > 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal",
        "urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal"
    })
    public static AttributeValue dateGreaterThanOrEqual(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareDate(params) >= 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:date-less-than",
        "urn:oasis:names:tc:xacml:1.0:function:time-less-than",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than"
    })
    public static AttributeValue dateLessThan(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareDate(params) < 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal",
        "urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal",
        "urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal"
    })
    public static AttributeValue dateLessThanOrEqual(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return compareDate(params) <= 0 ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:time-in-range"
    })
    public static AttributeValue timeInRange(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 3);
        AttributeValue t1 = (AttributeValue)params[0];
        AttributeValue t2 = (AttributeValue)params[1];
        AttributeValue t3 = (AttributeValue)params[2];
        checkArgumentType(t1, Constants.TYPE_TIME);
        checkArgumentType(t2, Constants.TYPE_TIME);
        checkArgumentType(t3, Constants.TYPE_TIME);

        XMLGregorianCalendar time1 = (XMLGregorianCalendar)t1.getValue();
        XMLGregorianCalendar time2 = (XMLGregorianCalendar)t2.getValue();
        XMLGregorianCalendar time3 = (XMLGregorianCalendar)t3.getValue();

        if (time2.compare(time3) > 0) {
            throw new IndeterminateException("Invalid time range : " + time2 + " - " + time3);
        }

        return time1.compare(time2) >= 0 && time1.compare(time3) <= 0 ? TRUE : FALSE;
    }
}