package an.xacml.policy;

public class Environment extends ConjunctiveMatch {
    private EnvironmentMatch[] environmentMatches;

    public Environment(EnvironmentMatch[] environmentMatches) {
        if (environmentMatches == null || environmentMatches.length < 1) {
            throw new IllegalArgumentException("subjectMatches should not be null or" +
                    " its length should not less than 1.");
        }
        matches = environmentMatches;
        this.environmentMatches = environmentMatches;
    }

    public EnvironmentMatch[] getEnvironmentMatches() {
        return environmentMatches;
    }
}