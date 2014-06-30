package an.xacml.engine;

import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import an.config.ConfigElement;
import an.xacml.policy.AbstractPolicy;

public class DefaultDataStorePolicyResolver implements PolicyResolver {
    private Map<URI, AbstractPolicy> policies = new Hashtable<URI, AbstractPolicy>();

    public DefaultDataStorePolicyResolver(ConfigElement config) {}

    public boolean isPolicySupported(URI policyId) {
        return policies.containsKey(policyId);
    }

    public AbstractPolicy[] resolveAllPolicies() {
        return policies.values().toArray(new AbstractPolicy[0]);
    }

    public AbstractPolicy resolvePolicy(URI policyId) {
        return policies.get(policyId);
    }

    protected void setPolicies(AbstractPolicy[] policies) {
        this.policies.clear();
        if (policies != null && policies.length > 0) {
            for (AbstractPolicy policy : policies) {
                this.policies.put(policy.getId(), policy);
            }
        }
    }

    public void update(AbstractPolicy[] toBeUpdated, URI[] toBeDeleted) {
        // TODO Should update together with PolicyCache
    }
}