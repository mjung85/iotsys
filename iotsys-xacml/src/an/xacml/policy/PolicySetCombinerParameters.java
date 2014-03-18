package an.xacml.policy;

import java.net.URI;

public class PolicySetCombinerParameters extends CombinerParameters {
    private URI policySetId;

    public PolicySetCombinerParameters(URI policySetId, CombinerParameter[] params) {
        super(params);
        this.policySetId = policySetId;
    }

    public URI getPolicySetId() {
        return policySetId;
    }
}