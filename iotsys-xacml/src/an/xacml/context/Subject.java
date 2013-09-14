package an.xacml.context;

import java.net.URI;

public class Subject extends TargetElement {
    /**
     * The category has a default value if it is not present in request element. So it won't be null.
     */
    private URI subjectCategory;

    public Subject(Attribute[] attrs) {
        this(null, attrs);
    }

    public Subject(URI subjCategory, Attribute[] attrs) {
        subjectCategory = subjCategory;
        populateAttributes(attrs);
        generateHashCode();
    }

    public URI getSubjectCategory() {
        return subjectCategory;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            Subject other = (Subject)o;
            if (subjectCategory.equals(other.getSubjectCategory()) &&
                attributes.size() == other.attributes.size()) {
                for (Attribute current : attributes) {
                    if (other.attributes.indexOf(current) < 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    protected void generateHashCode() {
        hashCode = 17;
        hashCode = hashCode * type_constant + (subjectCategory == null ? 0 : subjectCategory.hashCode());
        for (int i = 0; i < attributes.size(); i ++) {
            Attribute attr = attributes.get(i);
            hashCode = hashCode * type_constant + attr.hashCode();
        }
    }
}