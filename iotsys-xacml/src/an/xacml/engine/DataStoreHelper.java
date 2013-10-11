package an.xacml.engine;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

import an.config.ConfigElement;
import an.config.ConfigurationException;
import an.xml.XMLGeneralException;

/**
 * If "an.xacml.engine.DataStore" is configured for a custom DataStore 
 * in configuration file or in VM argument, the helper will create an instance 
 * of the configured one. The custom PolicyLoader should implements PolicyLoader
 * interface, and it should have a constructor with following signature,
 * 
 * CustomDataStore(CustomConfigElement config)
 */
public abstract class DataStoreHelper {
    private static Map<PDP, DataStore> dsRegistry = new Hashtable<PDP, DataStore>();
    private static Map<DataStore, PDP> pdpRegistry = new Hashtable<DataStore, PDP>();
    /**
     * Each data store should has a policy loader, which is used to load policies from data store.
     * We support custom policy loaders. The custom policy loader should implement interface PolicyLoader, and its
     * implementation class should be defined by this attribute.
     */
    public static final String ATTR_DATASTORE_CLASSNAME = "an.xacml.engine.DataStore";

    /**
     * Each PDP has a unique policy loader.
     * @param pdp
     * @return
     * @throws ConfigurationException
     * @throws XMLGeneralException 
     */
    public static synchronized DataStore getDataStore(PDP pdp) throws ConfigurationException, XMLGeneralException {
        DataStore ds = (DataStore)dsRegistry.get(pdp);

        if (ds == null) {
            ConfigElement config = pdp.getDataStoreConfig();
            String dsClassName = (String)config.getAttributeValueByName(ATTR_DATASTORE_CLASSNAME);

            try {
                Class<?> dsClass = Class.forName(dsClassName);
                Constructor<?> dsCons = dsClass.getDeclaredConstructor(new Class[]{config.getClass()});
                ds = (DataStore)dsCons.newInstance(config);
                dsRegistry.put(pdp, ds);
                pdpRegistry.put(ds, pdp);
            }
            catch (Exception e) {
                throw new ConfigurationException("Error occurs when initialize the DataStore.", e);
            }
        }
        return ds;
    }

    public static PDP getPDP(DataStore ds) {
        return pdpRegistry.get(ds);
    }

    public static synchronized void removeDataStore(PDP pdp) {
        DataStore ds = dsRegistry.remove(pdp);
        pdpRegistry.remove(ds);
    }
}