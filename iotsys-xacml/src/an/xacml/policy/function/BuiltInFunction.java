package an.xacml.policy.function;

import java.net.URI;

import an.xacml.engine.EvaluationContext;

/**
 * All XACML defined functions should implement this interface.
 */
public interface BuiltInFunction {
    /**
     * Call the function and return a result.
     * @param params Parameter list.
     * @return
     */
    public Object invoke(EvaluationContext ctx, Object[] params) throws Exception;
    /**
     * Return the functions XACML id
     * @return
     */
    public URI getFunctionId();
    /**
     * The function should have attributes, this method is to get attribute for function by given key.
     * @param key
     * @return
     */
    public Object getAttribute(Object key);
    /**
     * Return all attributes on this function
     * @return
     */
    public Object[] getAllAttributes();
}