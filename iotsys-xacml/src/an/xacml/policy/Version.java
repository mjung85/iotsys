package an.xacml.policy;

import an.xacml.DefaultXACMLElement;

public class Version extends DefaultXACMLElement implements Comparable<Version> {
    protected String version;
    public static final String VERSION_PATTERN = "(\\d+\\.)*\\d+";

    public Version(String version) {
        if (version.matches(VERSION_PATTERN)) {
            this.version = version;
        }
        else {
            throw new IllegalArgumentException("The given value \"" + version + "\" doesn't match the version format");
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o != null && getClass() == o.getClass()) {
            return version.equals(((Version)o).getVersionValue());
        }
        return false;
    }

    public String getVersionValue() {
        return version;
    }

    public int compareTo(Version another) {
        String sep = "\\.";
        String[] thisVal = version.split(sep);
        String[] otherVal = another.getVersionValue().split(sep);
        int min = Math.min(thisVal.length, otherVal.length);
        for (int i = 0; i < min; i ++) {
            if (!thisVal[i].equals(otherVal[i])) {
                float result = 0;
                if (i == 0) {
                    result = Float.parseFloat(thisVal[i]) - Float.parseFloat(otherVal[i]);
                }
                else {
                    result = Float.parseFloat("." + thisVal[i]) - Float.parseFloat("." + otherVal[i]);
                }
                return result < 0 ? -1 : 1;
            }
        }
        return thisVal.length - otherVal.length;
    }

    public static Version valueOf(String version) {
        return new Version(version);
    }

    public String toString() {
        return version;
    }
}