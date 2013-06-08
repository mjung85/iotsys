package an.xacml.policy;

public class Resources extends DisjunctiveMatch {
    private Resource[] resources;
    public Resources(Resource[] resources) {
        if (resources == null || resources.length < 1) {
            throw new IllegalArgumentException("resources should not be null or" +
                    " its length should not less than 1.");
        }
        matches = resources;
        this.resources = resources;
    }

    public Resource[] getResources() {
        return resources;
    }
}