package an.xacml.policy;

import static an.xacml.context.Decision.Deny;
import static an.xacml.context.Decision.Permit;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.context.Result;
import an.xacml.engine.EvaluationContext;
import an.xacml.engine.FunctionRegistry;
import an.xacml.policy.function.BuiltInFunction;

public class Policy extends AbstractPolicy {
    private URI ruleCombiningAlgId;
    // Only 1 CombinerParameters here, if there is multiple CombinerParameters in policy file, they should be merged to
    // a single one. So does to RuleCombinerParameters.
    private CombinerParameters combinerParameters;
    private RuleCombinerParameters ruleCombinerParameters;
    private Rule[] rules;
    private VariableDefinition[] crudeVariableDef;
    private Map<String, VariableDefinition> variableDefs = new Hashtable<String, VariableDefinition>();

    public Policy(URI policyId, Version version, URI ruleComId, String desc, Defaults defaults,
            CombinerParameters cp, RuleCombinerParameters rcp, Target target, VariableDefinition[] varDefs,
            Rule[] rules, Obligations obls) {
        this.id = policyId;
        this.version = version;
        this.ruleCombiningAlgId = ruleComId;
        this.description = desc;
        this.defaults = defaults;
        this.combinerParameters = cp;
        this.ruleCombinerParameters = rcp;
        this.target = target;
        this.rules = rules;
        this.obligations = obls;

        this.crudeVariableDef = varDefs;
        if (varDefs != null && varDefs.length > 0) {
            for (int i = 0; i < varDefs.length; i ++) {
                variableDefs.put(varDefs[i].getVariableId(), varDefs[i]);
            }
        }
        generateHashCode();
    }

    public URI getRuleCombiningAlgId() {
        return ruleCombiningAlgId;
    }

    public CombinerParameters getCombinerParameters() {
        return combinerParameters;
    }

    public RuleCombinerParameters getRuleCombinerParameters() {
        return ruleCombinerParameters;
    }

    public VariableDefinition[] getVariableDefinitions() {
        return crudeVariableDef;
    }

    public Rule[] getRules() {
        return rules;
    }

    public Result evaluate(EvaluationContext ctx) throws IndeterminateException {
        try {
            ctx.setCurrentEvaluatingPolicy(this);

            if (target.match(ctx) && rules != null && rules.length > 0) {
                // Get rule-combine-alg function from function registry, and then pass rules, combinerParams and 
                // RuleCombinerParams to it, get the EvaluationResult
                FunctionRegistry functionReg = FunctionRegistry.getInstance();
                BuiltInFunction ruleCombAlg = functionReg.lookup(ruleCombiningAlgId);

                Result ruleResult =(Result)ruleCombAlg.invoke(ctx,
                        new Object[] {rules, combinerParameters, ruleCombinerParameters});
                // Retrieve the corresponding Obligations by Effect in EvaluationResult, and set it to EvaluationResult.
                if ((ruleResult.getDecision() == Permit || ruleResult.getDecision() == Deny) && obligations != null) {
                    // Clone the result
                    ruleResult = new Result(ruleResult);
                    appendPolicyObligationsToResult(ruleResult, obligations, ctx, supportInnerExpression());
                }
                return ruleResult;
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
            throw new IndeterminateException("Error occurs while evaluating Policy.", t, Constants.STATUS_SYNTAXERROR);
        }
    }

    public VariableDefinition lookupVariableDefinition(String id) {
        return variableDefs.get(id);
    }
}