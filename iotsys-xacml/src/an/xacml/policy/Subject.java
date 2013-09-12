package an.xacml.policy;

public class Subject extends ConjunctiveMatch {
    private SubjectMatch[] subjectMatches;

    public Subject(SubjectMatch[] subjectMatches) {
        if (subjectMatches == null || subjectMatches.length < 1) {
            throw new IllegalArgumentException("subjectMatches should not be null or" +
                    " its length should not less than 1.");
        }
        matches = subjectMatches;
        this.subjectMatches = subjectMatches;
    }

    public SubjectMatch[] getSubjectMatches() {
        return subjectMatches;
    }
}