package an.xacml.policy;

import java.net.URI;

import an.xacml.Expression;

public class EnvironmentMatch extends DefaultMatch {
    public EnvironmentMatch(URI matchId, AttributeValue value, Expression designatorOrSelector) {
        this.matchId = matchId;
        this.attributeValue = value;
        this.designatorOrSelector = designatorOrSelector;
    }
}