package an.xacml.context;

import an.xacml.DefaultXACMLElement;

public class ResourceContent extends DefaultXACMLElement {
    /**
     * Represent a resource content, which may be any of type. Typically in XACML, it should be an org.w3c.Element of
     * an XML document or an array of Element.
     */
    private Object content;

    public ResourceContent(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            return content.equals(((ResourceContent)o).content);
        }
        return false;
    }

    public int hashCode() {
        return content.hashCode();
    }
}