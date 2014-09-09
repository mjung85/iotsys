package an.xacml.context;

/**
 * Represent an Action element that in an XACML request.
 */
public class Action extends TargetElement {
    /**
     * Construct an Action element from a set of action attributes.
     * @param attrs
     */
    public Action(Attribute[] attrs) {
        populateAttributes(attrs);
        generateHashCode();
    }
}