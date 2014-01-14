package an.xacml.policy;

public class Actions extends DisjunctiveMatch {
    private Action[] actions;
    public Actions(Action[] actions) {
        if (actions == null || actions.length < 1) {
            throw new IllegalArgumentException("actions should not be null or" +
                    " its length should not less than 1.");
        }
        matches = actions;
        this.actions = actions;
    }

    public Action[] getActions() {
        return actions;
    }
}