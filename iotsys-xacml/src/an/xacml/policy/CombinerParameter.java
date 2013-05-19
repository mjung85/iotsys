package an.xacml.policy;

import an.xacml.DefaultXACMLElement;

public class CombinerParameter extends DefaultXACMLElement {
    private String name;
    private AttributeValue value;

    public CombinerParameter(String name, AttributeValue attrValue) {
        this.name = name;
        this.value = attrValue;
    }

    public String getParameterName() {
        return name;
    }

    public AttributeValue getValue() {
        return value;
    }
}