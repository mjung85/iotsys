package an.xacml.policy.function;

import an.log.LogFactory;
import an.log.Logger;
import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.context.Decision;
import an.xacml.context.Result;
import an.xacml.engine.EvaluationContext;
import an.xacml.policy.AbstractPolicy;
import an.xacml.policy.Effect;
import an.xacml.policy.Rule;

@XACMLFunctionProvider
public abstract class CombiningAlgorithms {
    private static void checkArguments(Object[] params, int expectedNumber) throws IndeterminateException {
        // check parameters number
        if (expectedNumber > 0 && params.length != expectedNumber) {
            throw new IndeterminateException("Expected " + expectedNumber +
                    " parameters, but got " + params.length + ".");
        }
    }

    @XACMLFunction("an:multiple-policies-deny-overrides")
    public static Result multiplePoliciesDenyOverrides(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 1);
        AbstractPolicy[] policies = (AbstractPolicy[])params[0];
        return policyDenyOverrides(ctx, new Object[] {policies, null, null, null});
    }

    @XACMLFunction("an:multiple-policies-permit-overrides")
    public static Result multiplePoliciesPermitOverrides(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 1);
        AbstractPolicy[] policies = (AbstractPolicy[])params[0];
        return policyPermitOverrides(ctx, new Object[] {policies, null, null, null});
    }

    @XACMLFunction("an:multiple-policies-first-applicable")
    public static Result multiplePoliciesFirstApplicable(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 1);
        AbstractPolicy[] policies = (AbstractPolicy[])params[0];
        return policyFirstApplicable(ctx, new Object[] {policies, null, null, null});
    }

    @XACMLFunction("an:multiple-policies-only-one-applicable")
    public static Result multiplePoliciesOnlyOneApplicable(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 1);
        AbstractPolicy[] policies = (AbstractPolicy[])params[0];
        return policyOnlyOneApplicable(ctx, new Object[] {policies, null, null, null});
    }

    /**
     * Decision denyOverridesRuleCombiningAlgorithm(Rule rule[]) {
     *     Boolean atLeastOneError  = false;
     *     Boolean potentialDeny    = false;
     *     Boolean atLeastOnePermit = false;
     *     for (i = 0; i < lengthOf(rules); i ++) {
     *         Decision decision = evaluate(rule[i]);
     *         if (decision == Deny) {
     *             return Deny;
     *         }
     *         if (decision == Permit) {
     *             atLeastOnePermit = true;
     *             continue;
     *         }
     *         if (decision == NotApplicable) {
     *             continue;
     *         }
     *         if (decision == Indeterminate) {
     *             atLeastOneError = true;
     *  
     *             if (effect(rule[i]) == Deny) {
     *                 potentialDeny = true;
     *             }
     *             continue;
     *         }
     *     }
     *     if (potentialDeny) {
     *         return Indeterminate;
     *     }
     *     if (atLeastOnePermit) {
     *         return Permit;
     *     }
     *     if (atLeastOneError) {
     *         return Indeterminate;
     *     }
     *     return NotApplicable;
     * }
     * 
     * @param request
     * @param rules
     * @param cmbParams
     * @param ruleCombParams
     * @return
     * @throws IndeterminateException
     */
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides",
        "urn:oasis:names:tc:xacml:1.1:rule-combining-algorithm:ordered-deny-overrides"
    })
    public static Result ruleDenyOverrides(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        boolean atLeastOneError = false, potentialDeny = false, atLeastOnePermit = false;
        IndeterminateException indeterminateEx = null;
        Logger logger = LogFactory.getLogger();

        checkArguments(params, 3);
        Rule[] rules = (Rule[])params[0];

        Result permitResult = Result.PERMIT;

        for (Rule rule : rules) {
            try {
                Result result = rule.evaluate(ctx);
                Decision decision = result.getDecision();

                if (decision == Decision.Deny) {
                    return result;
                }
                if (decision == Decision.Permit) {
                    atLeastOnePermit = true;
                    permitResult = result;
                    continue;
                }
                if (decision == Decision.NotApplicable) {
                    continue;
                }
            }
            catch (IndeterminateException intEx) {
                logger.warn("Error occur while evaluating in ruleDenyOverrides algorithm.", intEx);
                // Save the IndeterminateException
                indeterminateEx = intEx;

                atLeastOneError = true;
                if (rule.getEffect() == Effect.Deny) {
                    potentialDeny = true;
                }
                continue;
            }
        }

        if (potentialDeny) {
            throw indeterminateEx;
        }
        if (atLeastOnePermit) {
            return permitResult;
        }
        if (atLeastOneError) {
            throw indeterminateEx;
        }
        return Result.NOTAPPLICABLE;
    }

    /**
     * Decision permitOverridesRuleCombiningAlgorithm(Rule rule[]) {
     *     Boolean atLeastOneError  = false;
     *     Boolean potentialPermit  = false;
     *     Boolean atLeastOneDeny   = false;
     *     for (i=0 ; i < lengthOf(rule) ; i ++) {
     *         Decision decision = evaluate(rule[i]);
     *         if (decision == Deny) {
     *             atLeastOneDeny = true;
     *             continue;
     *         }
     *         if (decision == Permit) {
     *             return Permit;
     *         }
     *         if (decision == NotApplicable) {
     *             continue;
     *         }
     *         if (decision == Indeterminate) {
     *             atLeastOneError = true;
     * 
     *             if (effect(rule[i]) == Permit) {
     *                 potentialPermit = true;
     *             }
     *             continue;
     *         }
     *     }
     *     if (potentialPermit) {
     *         return Indeterminate;
     *     }
     *     if (atLeastOneDeny) {
     *         return Deny;
     *     }
     *     if (atLeastOneError) {
     *         return Indeterminate;
     *     }
     *     return NotApplicable;
     * }
     * 
     * @param request
     * @param rules
     * @param cmbParams
     * @param ruleCombParams
     * @return
     * @throws IndeterminateException
     */
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides",
        "urn:oasis:names:tc:xacml:1.1:rule-combining-algorithm:ordered-permit-overrides"
    })
    public static Result rulePermitOverrides(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        boolean atLeastOneError = false, potentialPermit = false, atLeastOneDeny = false;
        IndeterminateException indeterminateEx = null;
        Logger logger = LogFactory.getLogger();

        checkArguments(params, 3);
        Rule[] rules = (Rule[])params[0];

        Result denyResult = Result.DENY;

        for (Rule rule : rules) {
            try {
                Result result = rule.evaluate(ctx);
                Decision decision = result.getDecision();

                if (decision == Decision.Deny) {
                    atLeastOneDeny = true;
                    denyResult = result;
                    continue;
                }
                if (decision == Decision.Permit) {
                    return result;
                }
                if (decision == Decision.NotApplicable) {
                    continue;
                }
            }
            catch (IndeterminateException intEx) {
                logger.warn("Error occur while evaluating in rulePermitOverrides algorithm.", intEx);
                // Save the IndeterminateException
                indeterminateEx = intEx;

                atLeastOneError = true;
                if (rule.getEffect() == Effect.Permit) {
                    potentialPermit = true;
                }
                continue;
            }
        }

        if (potentialPermit) {
            throw indeterminateEx;
        }
        if (atLeastOneDeny) {
            return denyResult;
        }
        if (atLeastOneError) {
            throw indeterminateEx;
        }
        return Result.NOTAPPLICABLE;
    }

    /**
     * Decision firstApplicableEffectRuleCombiningAlgorithm(Rule rule[]) {
     *     for (i = 0 ; i < lengthOf(rule); i ++) {
     *         Decision decision = evaluate(rule[i]);
     *         if (decision == Deny) {
     *             return Deny;
     *         }
     *         if (decision == Permit) {
     *             return Permit;
     *         }
     *         if (decision == NotApplicable) {
     *             continue;
     *         }
     *         if (decision == Indeterminate) {
     *             return Indeterminate;
     *         }
     *     }
     *     return NotApplicable;
     * }
     * 
     * @param request
     * @param rules
     * @param cmbParams
     * @param ruleCombParams
     * @return
     * @throws IndeterminateException
     */
    @XACMLFunction("urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable")
    public static Result ruleFirstApplicable(EvaluationContext ctx, Object[] params)
    throws IndeterminateException {
        checkArguments(params, 3);
        Rule[] rules = (Rule[])params[0];
        for (Rule rule : rules) {
            Result result = rule.evaluate(ctx);
            Decision decision = result.getDecision();

            if (decision == Decision.NotApplicable) {
                continue;
            }
            return result;
        }
        return Result.NOTAPPLICABLE;
    }

    /**
     * Decision denyOverridesPolicyCombiningAlgorithm(Policy policy[]) {
     *     Boolean atLeastOnePermit = false;
     *     for(i=0 ; i < lengthOf(policy); i ++) {
     *         Decision decision = evaluate(policy[i]);
     *         if (decision == Deny) {
     *             return Deny;
     *         }
     *         if (decision == Permit) {
     *             atLeastOnePermit = true;
     *             continue;
     *         }
     *         if (decision == NotApplicable) {
     *             continue;
     *         }
     *         if (decision == Indeterminate) {
     *             return Deny;
     *         }
     *     }
     *     if (atLeastOnePermit) {
     *         return Permit;
     *     }
     *     return NotApplicable;
     * }
     * 
     * @param request
     * @param policies
     * @param cmbParams
     * @param policyCombParams
     * @param policySetCombParams
     * @return
     */
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:deny-overrides",
        "urn:oasis:names:tc:xacml:1.1:policy-combining-algorithm:ordered-deny-overrides"
    })
    public static Result policyDenyOverrides(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        boolean atLeastOnePermit = false;
        checkArguments(params, 4);
        AbstractPolicy[] policies = (AbstractPolicy[])params[0];
        Logger logger = LogFactory.getLogger();

        // We need keep the original result that evaluated by policies.
        Result permitResult = Result.PERMIT;

        for (AbstractPolicy policy : policies) {
            try {
                Result result = policy.evaluate(ctx);
                Decision decision = result.getDecision();

                if (decision == Decision.Deny) {
                    return result;
                }
                if (decision == Decision.Permit) {
                    atLeastOnePermit = true;
                    permitResult = result;
                    continue;
                }
                if (decision == Decision.NotApplicable) {
                    continue;
                }
            }
            catch (IndeterminateException intEx) {
                logger.warn("Error occur while evaluating in policyDenyOverrides algorithm.", intEx);
                return Result.DENY;
            }
        }
        if (atLeastOnePermit) {
            return permitResult;
        }
        return Result.NOTAPPLICABLE;
    }

    /**
     * Decision permitOverridesPolicyCombiningAlgorithm(Policy policy[]) {
     *     Boolean atLeastOneError = false;
     *     Boolean atLeastOneDeny  = false;
     *     for (i = 0; i < lengthOf(policy); i ++) {
     *         Decision decision = evaluate(policy[i]);
     *         if (decision == Deny) {
     *             atLeastOneDeny = true;
     *             continue;
     *         }
     *         if (decision == Permit) {
     *             return Permit;
     *         }
     *         if (decision == NotApplicable) {
     *             continue;
     *         }
     *         if (decision == Indeterminate) {
     *             atLeastOneError = true;
     *             continue;
     *         }
     *     }
     *     if (atLeastOneDeny) {
     *         return Deny;
     *     }
     *     if (atLeastOneError) {
     *         return Indeterminate;
     *     }
     *     return NotApplicable;
     * }
     * 
     * @param request
     * @param policies
     * @param cmbParams
     * @param policyCombParams
     * @param policySetCombParams
     * @return
     * @throws IndeterminateException
     */
    @XACMLFunction({
        "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:permit-overrides",
        "urn:oasis:names:tc:xacml:1.1:policy-combining-algorithm:ordered-permit-overrides"
    })
    public static Result policyPermitOverrides(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        boolean atLeastOneError = false, atLeastOneDeny = false;
        IndeterminateException indeterminateEx = null;
        Logger logger = LogFactory.getLogger();

        checkArguments(params, 4);
        AbstractPolicy[] policies = (AbstractPolicy[])params[0];

        Result denyResult = Result.DENY;

        for (AbstractPolicy policy : policies) {
            try {
                Result result = policy.evaluate(ctx);
                Decision decision = result.getDecision();

                if (decision == Decision.Deny) {
                    atLeastOneDeny = true;
                    denyResult = result;
                    continue;
                }
                if (decision == Decision.Permit) {
                    return result;
                }
                if (decision == Decision.NotApplicable) {
                    continue;
                }
            }
            catch (IndeterminateException intEx) {
                logger.warn("Error occur while evaluating in policyPermitOverrides algorithm.", intEx);
                // Save the IndeterminateException
                indeterminateEx = intEx;

                atLeastOneError = true;
                continue;
            }
        }
        if (atLeastOneDeny) {
            return denyResult;
        }
        if (atLeastOneError) {
            throw indeterminateEx;
        }
        return Result.NOTAPPLICABLE;
    }

    /**
     * Decision firstApplicableEffectPolicyCombiningAlgorithm(Policy policy[]) {
     *     for (i = 0; i < lengthOf(policy); i ++) {
     *         Decision decision = evaluate(policy[i]);
     *         if(decision == Deny) {
     *             return Deny;
     *         }
     *         if(decision == Permit) {
     *             return Permit;
     *         }
     *         if (decision == NotApplicable) {
     *             continue;
     *         }
     *         if (decision == Indeterminate) {
     *             return Indeterminate;
     *         }
     *     }
     *     return NotApplicable;
     * }
     * 
     * @param request
     * @param policies
     * @param cmbParams
     * @param policyCombParams
     * @param policySetCombParams
     * @return
     * @throws IndeterminateException
     */
    @XACMLFunction("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable")
    public static Result policyFirstApplicable(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        checkArguments(params, 4);
        AbstractPolicy[] policies = (AbstractPolicy[])params[0];

        for (AbstractPolicy policy : policies) {
            Result result = policy.evaluate(ctx);
            Decision decision = result.getDecision();

            if (decision == Decision.NotApplicable) {
                continue;
            }
            return result;
        }
        return Result.NOTAPPLICABLE;
    }

    /**
     * Decision onlyOneApplicablePolicyPolicyCombiningAlogrithm(Policy policy[]) {
     *   Boolean          atLeastOne     = false;
     *   Policy           selectedPolicy = null;
     *   ApplicableResult appResult;
     * 
     *   for (i = 0; i < lengthOf(policy); i ++) {
     *      appResult = isApplicable(policy[I]);
     * 
     *      if (appResult == Indeterminate) {
     *          return Indeterminate;
     *      }
     *      if(appResult == Applicable) {
     *          if (atLeastOne) {
     *              return Indeterminate;
     *          }
     *          else {
     *              atLeastOne     = true;
     *              selectedPolicy = policy[i];
     *          }
     *      }
     *      if (appResult == NotApplicable) {
     *          continue;
     *      }
     *   }
     *   if (atLeastOne) {
     *       return evaluate(selectedPolicy);
     *   }
     *   else {
     *       return NotApplicable;
     *   }
     * }
     * 
     * @param request
     * @param policies
     * @param cmbParams
     * @param policyCombParams
     * @param policySetCombParams
     * @return
     * @throws IndeterminateException
     */
    @XACMLFunction("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:only-one-applicable")
    public static Result policyOnlyOneApplicable(EvaluationContext ctx, Object[] params) throws IndeterminateException {
        boolean atLeastOne = false;
        AbstractPolicy selectedPolicy = null;

        checkArguments(params, 4);
        AbstractPolicy[] policies = (AbstractPolicy[])params[0];

        for (AbstractPolicy policy : policies) {
           if (policy.match(ctx)) {
               if (atLeastOne) {
                   throw new IndeterminateException(
                           "Expected only 1 policy is applicable, but we got another applicable : " + policy.getId(),
                           Constants.STATUS_PROCESSINGERROR);
               }
               atLeastOne = true;
               selectedPolicy = policy;
           }
        }

        if (atLeastOne) {
            return selectedPolicy.evaluate(ctx);
        }
        else {
            return Result.NOTAPPLICABLE;
        }
    }
}