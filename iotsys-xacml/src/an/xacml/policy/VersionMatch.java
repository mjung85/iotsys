package an.xacml.policy;

import an.xacml.DefaultXACMLElement;

public class VersionMatch extends DefaultXACMLElement {
    private String pattern;
    public static final String VERSIONMATCH_PATTERN = "((\\d+|\\*)\\.)*(\\d+|\\*|\\+)";

    public VersionMatch(String pattern) {
        if (pattern.matches(VERSIONMATCH_PATTERN)) {
            this.pattern = convertVersionMatchToJavaRE(pattern);
        }
        else {
            throw new IllegalArgumentException("The given pattern \"" + pattern + 
                    "\" doesn't match the version match format");
        }
    }

    public boolean match(Version version) {
        return version.getVersionValue().matches(pattern);
    }

    private String convertVersionMatchToJavaRE(String versionMatch) {
        String plus = "\\.\\+", plusRep = "(.\\\\d+)*";
        String dot = "\\.", dotRep = "\\\\.";
        String ast = "\\*", astRep = "\\\\d";

        // replace all "*" with "\d"
        String phase1 = versionMatch.replaceAll(ast, astRep);
        // replace all ".+" with "(.\d+)*"
        String phase2 = phase1.replaceAll(plus, plusRep);
        // replace all "." with "\\.", include the "." in "(.\d+)*"
        return phase2.replaceAll(dot, dotRep);
    }

    public String getPattern() {
        return pattern;
    }

    public static VersionMatch valueOf(String pattern) {
        return new VersionMatch(pattern);
    }
}