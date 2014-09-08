package an.xacml.policy;

import java.util.Hashtable;
import java.util.Map;

import an.xacml.DefaultXACMLElement;

public class CombinerParameters extends DefaultXACMLElement {
    private Map<String, AttributeValue> parameters;
    private CombinerParameter[] crudeParams;

    public CombinerParameters(CombinerParameter[] params) {
        crudeParams = params;
        parameters = new Hashtable<String, AttributeValue>();
        if (params != null) {
            for (int i = 0; i < params.length; i ++) {
                parameters.put(params[i].getParameterName(), params[i].getValue());
            }
        }
    }

    public Map<String, AttributeValue> getNamedParameters() {
        return parameters;
    }

    public AttributeValue getParameterValue(String name) {
        return parameters.get(name);
    }

    public CombinerParameter[] getCrudeParameters() {
        return crudeParams;
    }
}