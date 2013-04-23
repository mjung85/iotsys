package an.xacml.policy;

import java.net.URI;

import an.xacml.Expression;

public class SubjectMatch extends DefaultMatch {
    public SubjectMatch(URI matchId, AttributeValue value, Expression designatorOrSelector) {
        this.matchId = matchId;
        this.attributeValue = value;
        this.designatorOrSelector = designatorOrSelector;
    }
}