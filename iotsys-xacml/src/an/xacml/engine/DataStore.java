package an.xacml.engine;

import an.xacml.adapter.DataAdapter;
import an.xacml.adapter.DataAdapterException;

/**
 * A factory class that used to load policies from specific data store.
 */
public interface DataStore {
    /**
     * Is used to load all policies, return an Iterator that hold a list of DataAdapter.
     */
    public DataAdapter[] load() throws DataAdapterException;

    /**
     * Save all engine elements to target data store.
     */
    public void save() throws DataAdapterException;

    /**
     * Update some of engine elements.
     */
    public void update(DataAdapter[] toBeUpdated, DataAdapter[] toBeDeleted) throws DataAdapterException;

    /**
     * Shutdown the data store.
     */
    public void shutdown();
}