package an.xacml.policy;

import java.util.Vector;
import an.xacml.Matchable;

public class Target extends ConjunctiveMatch {
    private Subjects subjects;
    private Resources resources;
    private Actions actions;
    private Environments environments;
    /**
     * Construct with an Array of Matchables. The matchables are a set of all subjects, resources, actions and
     * environments.
     * @param matchables
     */
    public Target(Matchable[] matchables) {
        matches = matchables;
    }

    /**
     * Construct with subjects, resources, actions and environments.
     * @param subjects
     * @param resources
     * @param actions
     * @param envs
     */
    public Target(Subjects subjects, Resources resources, Actions actions, Environments envs) {
        Vector<Matchable> vMatch = new Vector<Matchable>();
        if (subjects != null) vMatch.add(subjects);
        if (resources != null) vMatch.add(resources);
        if (actions != null) vMatch.add(actions);
        if (envs != null) vMatch.add(envs);
        matches = vMatch.toArray(new Matchable[0]);
        this.subjects = subjects;
        this.resources = resources;
        this.actions = actions;
        this.environments = envs;
    }

    public Subjects getSubjects() {
        return subjects;
    }

    public Resources getResources() {
        return resources;
    }

    public Actions getActions() {
        return actions;
    }

    public Environments getEnvironments() {
         return environments;
    }
}