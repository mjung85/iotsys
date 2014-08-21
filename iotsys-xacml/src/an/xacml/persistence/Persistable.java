package an.xacml.persistence;

/**
 * In PAP, each element must to implement the interface. It is used to save engine element
 * to a specific data store (by configuration).
 */
public interface Persistable {

    /**
     * The implementation should first create a corresponding dataobject by passing "this" object to 
     * DataObjectFactory, then calls "save" method of the new created data object.
     */
    public void persist();
}