package an.xacml.policy.function;

import static an.xacml.policy.AttributeValue.FALSE;
import static an.xacml.policy.AttributeValue.TRUE;
import static an.xacml.policy.function.CommonFunctions.checkArgumentType;
import static an.xacml.policy.function.CommonFunctions.checkArguments;
import static an.xacml.policy.function.CommonFunctions.checkArrayArguments;

import java.net.URI;

import javax.naming.ldap.LdapName;

import an.datatype.rfc822Name;
import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AttributeValue;

@XACMLFunctionProvider
public abstract class MatchFunctions {
    private static boolean stringMatch(Object[] params, URI type1, URI type2) throws IndeterminateException {
        checkArguments(params, 2);
        params = checkArrayArguments(params);
        AttributeValue pattern = (AttributeValue)params[0];
        AttributeValue toBeMatched = (AttributeValue)params[1];
        checkArgumentType(pattern, type1);
        checkArgumentType(toBeMatched, type2);

        String strPattern = pattern.getValue().toString();
        String source = toBeMatched.getValue().toString();
        if (strPattern.charAt(0) != '^') {
            strPattern = ".*" + strPattern;
        }
        if (strPattern.charAt(strPattern.length() - 1) != '$') {
            strPattern = strPattern + ".*";
        }
        strPattern = strPattern.replaceAll("\\-\\[", "&&[^");

        return source.matches(strPattern);
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:string-regexp-match"
    })
    public static AttributeValue stringRegularExpressionMatch(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return stringMatch(params, Constants.TYPE_STRING, Constants.TYPE_STRING) ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:2.0:function:anyURI-regexp-match"
    })
    public static AttributeValue uriRegularExpressionMatch(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return stringMatch(params, Constants.TYPE_STRING, Constants.TYPE_ANYURI) ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:2.0:function:ipAddress-regexp-match"
    })
    public static AttributeValue ipAddressRegularExpressionMatch(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return stringMatch(params, Constants.TYPE_STRING, Constants.TYPE_IPADDRESS) ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:2.0:function:dnsName-regexp-match"
    })
    public static AttributeValue dnsNameRegularExpressionMatch(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return stringMatch(params, Constants.TYPE_STRING, Constants.TYPE_DNSNAME) ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:2.0:function:rfc822Name-regexp-match"
    })
    public static AttributeValue rfc822NameRegularExpressionMatch(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return stringMatch(params, Constants.TYPE_STRING, Constants.TYPE_RFC822NAME) ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:2.0:function:x500Name-regexp-match"
    })
    public static AttributeValue x500NameRegularExpressionMatch(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        return stringMatch(params, Constants.TYPE_STRING, Constants.TYPE_X500NAME) ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:x500Name-match"
    })
    public static AttributeValue x500NameMatch(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue pattern = (AttributeValue)params[0];
        AttributeValue toBeMatched = (AttributeValue)params[1];
        checkArgumentType(pattern, Constants.TYPE_X500NAME);
        checkArgumentType(toBeMatched, Constants.TYPE_X500NAME);

        LdapName x500Pattern = (LdapName)pattern.getValue();
        LdapName x500Source = (LdapName)toBeMatched.getValue();

        return x500Source.startsWith(x500Pattern) ? TRUE : FALSE;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match"
    })
    public static AttributeValue rfc822NameMatch(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue pattern = (AttributeValue)params[0];
        AttributeValue toBeMatched = (AttributeValue)params[1];
        checkArgumentType(pattern, Constants.TYPE_STRING);
        checkArgumentType(toBeMatched, Constants.TYPE_RFC822NAME);

        String strPattern = (String)pattern.getValue();
        rfc822Name rfc822Source = (rfc822Name)toBeMatched.getValue();

        int atIndex = strPattern.indexOf("@");
        if (atIndex > 0) {
            String local = strPattern.substring(0, atIndex);
            String domain = strPattern.substring(atIndex + 1);

            return rfc822Source.getLocalPart().equals(local) &&
                    rfc822Source.getDomain().equalsIgnoreCase(domain) ? TRUE : FALSE;
        }
        else if (strPattern.charAt(0) == '.') {
            return rfc822Source.getDomain().endsWith(strPattern.toLowerCase()) ? TRUE : FALSE;
        }
        else {
            return rfc822Source.getDomain().equalsIgnoreCase(strPattern) ? TRUE : FALSE;
        }
    }
}