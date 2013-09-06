package an.xacml.policy;

import java.net.URI;

import an.log.LogFactory;
import an.log.Logger;
import an.xacml.DefaultXACMLElement;
import an.xacml.PolicySyntaxException;
import an.xacml.engine.PDP;
import an.xacml.engine.PolicyResolver;
import an.xacml.engine.PolicyResolverRegistry;

public class IdReference extends DefaultXACMLElement {
    private URI id;
    private AbstractPolicy policy;
    private VersionMatch version;
    private VersionMatch earliestVersion;
    private VersionMatch latestVersion;
    private Logger logger;

    public IdReference(URI id, VersionMatch version, VersionMatch earliest, VersionMatch latest) {
        logger = LogFactory.getLogger();
        this.id = id;
        this.earliestVersion = earliest;
        this.latestVersion = latest;
    }

    /**
     * Resolve the referenced policy using policy resolvers.
     * @throws PolicySyntaxException 
     */
    public void resolvePolicy() throws PolicySyntaxException {
        PDP pdp = ((AbstractPolicy)getRootElement()).getOwnerPDP();
        if (pdp != null) {
            PolicyResolverRegistry reg = PolicyResolverRegistry.getInstance(pdp);
            PolicyResolver[] resolvers = reg.getAllPolicyResolvers();
            for (PolicyResolver resolver : resolvers) {
                if (resolver.isPolicySupported(id)) {
                    AbstractPolicy resolved = resolver.resolvePolicy(id);
                    if (resolved != null && validateResolvedPolicy(resolved)) {
                        resolved.setParentElement(getParentElement());
                        policy = resolved;
                        break;
                    }
                    else {
                        logger.warn("The policy resolved by " + resolver.getClass().getSimpleName() +
                                (resolved == null ?
                                 " is null." : " doesn't match the IdReference. Will try next resolver."));
                    }
                }
            }
        }
        if (policy == null) {
            throw new PolicySyntaxException("Could not resolve policy using the given id: " + id);
        }
    }

    private boolean validateResolvedPolicy(AbstractPolicy policy) {
        Version ver = policy.getPolicyVersion();

        if ((version != null && !version.match(ver)) ||
            (version != null && !earliestVersion.match(ver)) ||
            (version != null && !latestVersion.match(ver))) {
            return false;
        }
        return true;
    }

    /**
     * XACML 2.0 doesn't define the situation that policy's version doesn't match the IdReference's one, I just throw
     * an PolicySyntaxException for this situation.
     * @return
     * @throws PolicySyntaxException 
     */
    public void setPolicy(AbstractPolicy policy) throws PolicySyntaxException {
        if (!id.equals(policy.getId())) {
            throw new PolicySyntaxException("The referenced policy's ID does not match the expected.");
        }

        if (!validateResolvedPolicy(policy)) {
            throw new PolicySyntaxException("The referenced policy's version does not match the expected.");
        }
        policy.setParentElement(getParentElement());
        this.policy = policy;
    }

    public AbstractPolicy getPolicy() throws PolicySyntaxException {
        if (policy == null) {
            resolvePolicy();
        }
        return policy;
    }

    public URI getId() {
        return id;
    }

    public VersionMatch getVersion() {
        return version;
    }

    public VersionMatch getEarliestVersion() {
        return earliestVersion;
    }

    public VersionMatch getLatestVersion() {
        return latestVersion;
    }
}