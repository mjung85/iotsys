package an.xacml.policy;

import static an.xacml.Constants.POLICY_DEFAULT_VERSION;

import java.net.URI;
import java.util.Map;

import an.log.LogFactory;
import an.log.Logger;
import an.xacml.Evaluatable;
import an.xacml.IndeterminateException;
import an.xacml.Matchable;
import an.xacml.context.Result;
import an.xacml.engine.DefaultCacheable;
import an.xacml.engine.EvaluationContext;
import an.xacml.engine.PDP;

public abstract class AbstractPolicy extends DefaultCacheable implements Evaluatable, Matchable {
    /**
     * The property intends to provider namespace mapping for XPath evaluation.  The value actually is a map that 
     * includes all attributes in Policy or PolicySet element and with "xmlns:" in attribute name.  For example, if a 
     * Policy element has following attributes, some of them have "xmlns:" in name, and some of them don't have,
     *
     * <Policy>
     *    xmlns="urn:oasis:names:tc:xacml:2.0:policy:schema:cd:04"
     *    xmlns:xacml-context="urn:oasis:names:tc:xacml:2.0:context:schema:cd:04"
     *    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *    xsi:schemaLocation="urn:oasis:names:tc:xacml:2.0:policy:schema:cd:04 http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-cd-04.xsd"
     *    xmlns:md="http://www.med.example.com/schemas/record.xsd"
     *    PolicyId="urn:oasis:names:tc:xacml:2.0:example:policyid:1"
     *    RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides">
     *    ...
     * </Policy>
     * 
     * Then the namespace mapping should include following attributes name and value mappings,
     * 
     *      xacml-context="urn:oasis:names:tc:xacml:2.0:context:schema:cd:04"
     *      xsi="http://www.w3.org/2001/XMLSchema-instance"
     *      md="http://www.med.example.com/schemas/record.xsd"
     */
    protected Map<String, String> namespaceMapping;
    protected URI id;
    protected String description;
    protected Version version = POLICY_DEFAULT_VERSION;
    protected Target target;
    protected Target mergedTarget;
    protected Obligations obligations;
    protected Defaults defaults;

    private int hashCode;
    private PDP ownerPDP;

    public boolean match(EvaluationContext ctx) throws IndeterminateException {
        ctx.setCurrentEvaluatingPolicy(this);
        return target.match(ctx);
    }

    public int hashCode() {
        return hashCode;
    }

    public URI getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Defaults getDefaults() {
        return defaults;
    }

    public URI getXPathVersion() {
        if (defaults == null) {
            return null;
        }
        else {
            return defaults.getXPathVersion();
        }
    }

    public Version getPolicyVersion() {
        return version;
    }

    public Target getTarget() {
        return target;
    }

    public Map<String, String> getPolicyNamespaceMappings() {
        return namespaceMapping;
    }

    public void setPolicyNamespaceMappings(Map<String, String> nsMap) {
        namespaceMapping = nsMap;
    }

    public Obligations getObligations() {
        return obligations;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof AbstractPolicy) {
            AbstractPolicy policy = (AbstractPolicy)o;
            return this.getId().equals(policy.getId());
        }
        return false;
    }

    public void setOwnerPDP(PDP pdp) {
        this.ownerPDP = pdp;
    }

    public PDP getOwnerPDP() {
        if (ownerPDP == null) {
            if (getRootElement() != this) {
                ownerPDP = ((AbstractPolicy)getRootElement()).getOwnerPDP();
            }
        }
        return ownerPDP;
    }
    /**
     * Should be conform with equals method
     */
    protected void generateHashCode() {
        hashCode = id.hashCode();
    }

    public String toString() {
        return id.toString();
    }

    protected boolean supportInnerExpression() {
        PDP pdp = getOwnerPDP();
        if (pdp != null) {
            return pdp.supportInnerExpression();
        }
        // We are not in a PDP context.
        else {
            return false;
        }
    }

    protected static void appendPolicyObligationsToResult(
            Result result, Obligations obls, EvaluationContext ctx, boolean supportInnerExp) {
        Obligation[] obligations = obls.getObligationsByDecision(result.getDecision());
        // Clone each Obligation
        for (int i = 0; i < obligations.length; i ++) {
            obligations[i] = new Obligation(obligations[i]);
        }
        Obligations appender = new Obligations(obligations);

        if (supportInnerExp) {
            // Trying to evaluate all AttributeAssignments on the cloned Obligations.
            try {
                appender.evaluateAllChildAttributeAssigments(ctx);
            }
            // If there is error, we don't process it, just append the AttributeAssignments, and the error should be
            // processed by PEP.
            catch (IndeterminateException innerIntEx) {
                Logger logger = LogFactory.getLogger();
                logger.warn("Error occurs while evaluate all child AttributeAssignments of Obligations.", innerIntEx);
            }
        }
        // Append the obligations
        result.appendObligations(appender);
    }
}