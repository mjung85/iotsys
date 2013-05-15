package an.xacml.adapter;


import an.xacml.XACMLElement;

/**
 * The data adapter is used to perform conversion between engine XACML element and data store object.
 * DataAdapter imlementation should have following 2 constructor:
 * 
 *     public DataAdapterImpl(XACMLElement engineElem);    // To construct the DataAdapter from engine element.
 *     public DataAdapterImpl(Object dsObject);            // To construct the DataAdapter from data store object.
 */
public interface DataAdapter {
    /**
     * The method is to convert data store object to XACML engine element. This method may parse the XML file or
     * RDBMS record to generate a corresponding XACML engine element. This is used to load data from data store.
     * 
     * Note, this method may always return null if and only if the DataAdapter is constructed from XACML engine
     * element. Implementation doesn't guarantee its return value under this situation, because we don't need keep
     * the reference to the engine element that we constructed from. In some scenarios, to not keep the reference
     * will reduce the memory usage. It's must to return a non-null value if the DataAdapter is constructed from
     * a data store object.
     */
    public XACMLElement getEngineElement();
    /**
     * Return an underlying data store recognized object that represents the engine element. Which is used to save
     * the XACML engine element to underlying data store.
     * 
     * Note, this method may always return null if and only if the DataAdapter is constructed from data store object.
     * Implementation doesn't guarantee its return value under this situation, because we don't need keep the
     * reference to the data store object that we constructed from. In some scenarios, to not keep the reference
     * will reduce the memory usage. It's must to return a non-null value if the DataAdapter is constructed from
     * an XACML engine element.
     */
    public Object getDataStoreObject();
}