package an.xacml.policy;

import java.net.URI;

import an.xacml.Expression;

public class ResourceMatch extends DefaultMatch {
    public ResourceMatch(URI matchId, AttributeValue value, Expression designatorOrSelector) {
        this.matchId = matchId;
        this.attributeValue = value;
        this.designatorOrSelector = designatorOrSelector;
    }
}