package an.xacml.policy;

public class Action extends ConjunctiveMatch {
    private ActionMatch[] actionMatches;

    public Action(ActionMatch[] actionMatches) {
        if (actionMatches == null || actionMatches.length < 1) {
            throw new IllegalArgumentException("actionMatches should not be null or" +
                    " its length should not less than 1.");
        }
        matches = actionMatches;
        this.actionMatches = actionMatches;
    }

    public ActionMatch[] getActionMatches() {
        return actionMatches;
    }
}