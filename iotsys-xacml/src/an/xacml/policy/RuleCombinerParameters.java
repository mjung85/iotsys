package an.xacml.policy;

public class RuleCombinerParameters extends CombinerParameters {
    private String ruleId;

    public RuleCombinerParameters(String ruleId, CombinerParameter[] params) {
        super(params);
        this.ruleId = ruleId;
    }

    public String getRuleId() {
        return ruleId;
    }
}