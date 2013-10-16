package an.xacml.policy;

import static an.xacml.context.Decision.Deny;
import static an.xacml.context.Decision.Permit;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;

import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.context.Result;
import an.xacml.engine.EvaluationContext;
import an.xacml.engine.FunctionRegistry;
import an.xacml.policy.function.BuiltInFunction;

public class PolicySet extends AbstractPolicy {
    private URI policyCombiningAlgId;
    private CombinerParameters combinerParameters;
    private PolicyCombinerParameters policyCombinerParameters;
    private PolicySetCombinerParameters policySetCombinerParameters;
    private ArrayList<XACMLElement> all = new ArrayList<XACMLElement>(); 
    private AbstractPolicy[] allPolicies;

    // FIXME these ugly constructors should be replaced by setters and getters in next release.
    public PolicySet(URI policySetId, Version version, URI policyComId, String desc, Defaults defaults, Target target,
            CombinerParameters cp, PolicyCombinerParameters pcp, PolicySetCombinerParameters pscp, Obligations obls) {
        this.id = policySetId;
        this.version = version;
        this.policyCombiningAlgId = policyComId;
        this.description = desc;
        this.defaults = defaults;
        this.target = target;
        this.combinerParameters = cp;
        this.policyCombinerParameters = pcp;
        this.policySetCombinerParameters = pscp;
        this.obligations = obls;
        generateHashCode();
    }

    public URI getPolicyCombiningAlgId() {
        return policyCombiningAlgId;
    }

    public void addPolicy(AbstractPolicy policy) {
        all.add(policy);
    }

    public void addPolicyIdReference(IdReference idRef) {
        all.add(idRef);
    }

    public XACMLElement[] getAllCrudeChildPolicies() {
        return all.toArray(new XACMLElement[0]);
    }

    public AbstractPolicy[] getAllChildPolicies() throws PolicySyntaxException {
        if (allPolicies == null) {
            mergePolicies();
        }
        return allPolicies;
    }

    public CombinerParameters getCombinerParameters() {
        return combinerParameters;
    }

    public PolicyCombinerParameters getPolicyCombinerParameters() {
        return policyCombinerParameters;
    }

    public PolicySetCombinerParameters getPolicySetCombinerParameters() {
        return policySetCombinerParameters;
    }

    /**
     * Merge all policies to an array, and this array will be used for evaluation.
     * @throws PolicySyntaxException 
     */
    private synchronized void mergePolicies() throws PolicySyntaxException {
        if (allPolicies == null && all != null && all.size() > 0) {
            allPolicies = new AbstractPolicy[all.size()];
            int index = 0;
            for (XACMLElement elem : all) {
                if (elem instanceof AbstractPolicy) {
                    allPolicies[index ++] = (AbstractPolicy)elem;
                }
                else {
                    allPolicies[index ++] = ((IdReference)elem).getPolicy();
                }
            }
        }
    }

    public Result evaluate(EvaluationContext ctx) throws IndeterminateException {
        try {
            ctx.setCurrentEvaluatingPolicy(this);

            if (target.match(ctx)) {
                // We don't call the method from PolicySet's constructor, because referenced policies may not be loaded while
                // constructing the policySet.
                mergePolicies();
                if (allPolicies == null || allPolicies.length == 0) {
                    return Result.NOTAPPLICABLE;
                }

                // Get rule-combine-alg function from function registry, and then pass rules, combinerParams and 
                // RuleCombinerParams to it, get the EvaluationResult
                FunctionRegistry functionReg = FunctionRegistry.getInstance();
                BuiltInFunction policyCombAlg = functionReg.lookup(policyCombiningAlgId);

                Result policyResult =(Result)policyCombAlg.invoke(ctx, new Object[] {
                        allPolicies, combinerParameters, policyCombinerParameters, policySetCombinerParameters});
                // Retrieve the corresponding Obligations by Effect in EvaluationResult, and set it to EvaluationResult.
                if ((policyResult.getDecision() == Permit || policyResult.getDecision() == Deny) && obligations != null) {
                    if (policyResult == Result.PERMIT || policyResult == Result.DENY) {
                        policyResult = new Result(policyResult);
                    }
                    appendPolicyObligationsToResult(policyResult, obligations, ctx, supportInnerExpression());
                }
                return policyResult;
            }
            // NotApplicable
            return Result.NOTAPPLICABLE;
        }
        catch (IndeterminateException ex) {
            throw ex;
        }
        catch (Throwable t) {
            if (t instanceof InvocationTargetException) {
                Throwable targetT = ((InvocationTargetException)t).getTargetException();
                if (targetT instanceof IndeterminateException) {
                    throw (IndeterminateException)targetT;
                }
            }
            throw new IndeterminateException("Error occurs while evaluating PolicySet.", t,
                    Constants.STATUS_SYNTAXERROR);
        }
    }
}