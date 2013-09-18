package an.xacml.policy.function;

import static an.xacml.engine.EvaluationContext.isPolicyXPathVersionSupported;
import static an.xacml.policy.AttributeValue.FALSE;
import static an.xacml.policy.AttributeValue.TRUE;
import static an.xacml.policy.function.CommonFunctions.checkArgumentType;
import static an.xacml.policy.function.CommonFunctions.checkArguments;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AbstractPolicy;
import an.xacml.policy.AttributeValue;
import an.xacml.policy.NamespaceContextProvider;

@XACMLFunctionProvider
public abstract class XPathFunctions {
    private static XPath xpath = XPathFactory.newInstance().newXPath();

    private static NodeList evaluateXPath(EvaluationContext ctx, String exp) throws IndeterminateException {
        try {
            AbstractPolicy evaluatingPolicy = ctx.getCurrentEvaluatingPolicy();
            if (evaluatingPolicy != null) {
                isPolicyXPathVersionSupported(evaluatingPolicy);

                NamespaceContextProvider nsCtx = new NamespaceContextProvider();
                Map<String, String> nsMap = evaluatingPolicy.getPolicyNamespaceMappings();
                Iterator<String> keys = nsMap.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    nsCtx.addNSMapping(key, nsMap.get(key));
                }
                xpath.setNamespaceContext(nsCtx);
            }

            return (NodeList)xpath.evaluate(exp, ctx.getRequest().getRootNode(), XPathConstants.NODESET);
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluating XPath expression : " + exp);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:xpath-node-count"
    })
    public static AttributeValue nodeCount(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 1);
        AttributeValue exp = (AttributeValue)params[0];
        checkArgumentType(exp, Constants.TYPE_STRING);
        String xpathExp = (String)exp.getValue();

        try {
            NodeList nList = evaluateXPath(ctx, xpathExp);
            return AttributeValue.getInstance(Constants.TYPE_INTEGER, BigInteger.valueOf(nList.getLength()));
        }
        catch (IndeterminateException intEx) {
            throw intEx;
        }
        catch (Exception ex) {
            throw new IndeterminateException("Error occurs while evaluating function nodeCount : " + xpathExp);
        }
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:xpath-node-equal"
    })
    public static AttributeValue nodeEqual(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue exp1 = (AttributeValue)params[0];
        AttributeValue exp2 = (AttributeValue)params[1];
        checkArgumentType(exp1, Constants.TYPE_STRING);
        checkArgumentType(exp2, Constants.TYPE_STRING);
        String xpathExp1 = (String)exp1.getValue();
        String xpathExp2 = (String)exp2.getValue();

        try {
            NodeList nList1 = evaluateXPath(ctx, xpathExp1);
            NodeList nList2 = evaluateXPath(ctx, xpathExp2);
            for (int i = 0; i < nList1.getLength(); i ++) {
                Node node1 = nList1.item(i);
                for (int j = 0; j < nList2.getLength(); j ++) {
                    Node node2 = nList2.item(j);
                    if (node2.isEqualNode(node1)) {
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
            throw new IndeterminateException("Error occurs while evaluating function nodeEqual : " +
                    xpathExp1 + ", " + xpathExp2);
        }
    }

    private static boolean isNodeMatch(Node node1, Node node2) {
        if (node1.isEqualNode(node2)) {
            return true;
        }
        else {
            NodeList children = node1.getChildNodes();
            for (int i = 0; i < children.getLength(); i ++) {
                Node child = children.item(i);
                if (isNodeMatch(child, node2)) {
                    return true;
                }
            }
        }
        return false;
    }

    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:function:xpath-node-match"
    })
    public static AttributeValue nodeMatch(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 2);
        AttributeValue exp1 = (AttributeValue)params[0];
        AttributeValue exp2 = (AttributeValue)params[1];
        checkArgumentType(exp1, Constants.TYPE_STRING);
        checkArgumentType(exp2, Constants.TYPE_STRING);
        String xpathExp1 = (String)exp1.getValue();
        String xpathExp2 = (String)exp2.getValue();

        try {
            NodeList nList1 = evaluateXPath(ctx, xpathExp1);
            NodeList nList2 = evaluateXPath(ctx, xpathExp2);
            for (int i = 0; i < nList1.getLength(); i ++) {
                Node node1 = nList1.item(i);
                for (int j = 0; j < nList2.getLength(); j ++) {
                    Node node2 = nList2.item(j);
                    if (isNodeMatch(node1, node2)) {
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
            throw new IndeterminateException("Error occurs while evaluating function nodeMatch : " +
                    xpathExp1 + ", " + xpathExp2);
        }
    }
}