package an.xacml.context;

import java.net.URI;
import java.util.ArrayList;
import java.util.Vector;

import an.xacml.DefaultXACMLElement;

public abstract class TargetElement extends DefaultXACMLElement {
    protected int type_constant;
    protected int hashCode;
    /**
     * The attributes could be empty.
     */
    protected ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    protected Attribute[] allAttributes;

    public Attribute[] getAttributeById(URI attrId) {
        Vector<Attribute> result = new Vector<Attribute>();
        for (Attribute attr : attributes) {
            if (attr.getAttributeID().equals(attrId)) {
                result.add(attr);
            }
        }
        return result.toArray(new Attribute[0]);
    }

    public Attribute[] getAllAttributes() {
        return allAttributes;
    }
    /**
     * Re-write the equals method to compare all the attributes of Action element.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            TargetElement other = (TargetElement)o;
            if (attributes.size() == other.attributes.size()) {
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

    public int hashCode() {
        return hashCode;
    }

    /**
     * Generate the hash code from each of attribute.
     */
    protected void generateHashCode() {
        hashCode = 31;
        for (int i = 0; i < attributes.size(); i ++) {
            Attribute attr = attributes.get(i);
            hashCode = hashCode * type_constant + attr.hashCode();
        }
    }

    protected void populateAttributes(Attribute[] attrs) {
        if (attrs != null) {
            allAttributes = attrs;
            // keep the original order
            for (int i = 0; i < attrs.length; i ++) {
                attributes.add(attrs[i]);
            }
        }
    }
}