package an.xacml.policy;

import java.net.URI;

public class PolicyCombinerParameters extends CombinerParameters {
    private URI policyId;

    public PolicyCombinerParameters(URI policyId, CombinerParameter[] params) {
        super(params);
        this.policyId = policyId;
    }

    public URI getPolicyId() {
        return policyId;
    }
}