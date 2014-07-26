package an.xacml.context;

public class Resource extends TargetElement {
    /**
     * The content could be null;
     */
    private ResourceContent content;

    public Resource(ResourceContent content, Attribute[] attrs) {
        this.content = content;
        populateAttributes(attrs);
        generateHashCode();
    }

    public ResourceContent getResourceContent() {
        return content;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            Resource other = (Resource)o;
            if (compareObject(content, other.content) && attributes.size() == other.attributes.size()) {
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
        hashCode = 37;
        hashCode = hashCode * type_constant + (content == null ? 0 : content.hashCode());
        for (int i = 0; i < attributes.size(); i ++) {
            Attribute attr = attributes.get(i);
            hashCode = hashCode * type_constant + attr.hashCode();
        }
    }
}