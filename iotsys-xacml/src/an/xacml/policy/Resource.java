package an.xacml.policy;

public class Resource extends ConjunctiveMatch {
    private ResourceMatch[] resourceMatches;

    public Resource(ResourceMatch[] resourceMatches) {
        if (resourceMatches == null || resourceMatches.length < 1) {
            throw new IllegalArgumentException("resourceMatches should not be null or" +
                    " its length should not less than 1.");
        }
        matches = resourceMatches;
        this.resourceMatches = resourceMatches;
    }

    public ResourceMatch[] getResourceMatches() {
        return resourceMatches;
    }
}