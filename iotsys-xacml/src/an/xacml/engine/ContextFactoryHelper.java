package an.xacml.engine;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

import an.config.ConfigElement;
import an.config.ConfigurationException;
import an.xml.XMLGeneralException;

/**
 * If "an.xacml.engine.ContextFactory" is configured for a custom ContextFactory 
 * in configuration file or in VM argument, the helper will create an instance 
 * of the configured one. The custom ContextFactory should implement ContextFactory
 * interface, and it should have a constructor with following signature,
 * 
 * CustomContextFactory(ContextFactoryConfigElement)
 */
public abstract class ContextFactoryHelper {
    public static final String ATTR_CONTEXTFACTORY_CLASSNAME = "an.xacml.engine.ContextFactory";

    private static Map<PDP, ContextFactory> factoryRegistry = new Hashtable<PDP, ContextFactory>();
    private static Map<ContextFactory, PDP> pdpRegistry = new Hashtable<ContextFactory, PDP>();

    /**
     * Get configuration from PDP and then initialize the configured implementation.
     * @param pdp
     * @return
     * @throws XMLGeneralException 
     * @throws ConfigurationException 
     */
    public static synchronized ContextFactory getContextFactory(PDP pdp)
    throws XMLGeneralException, ConfigurationException {
        ContextFactory factory = (ContextFactory)factoryRegistry.get(pdp);

        if (factory == null) {
            ConfigElement config = (ConfigElement)pdp.getContextFactoryConfig();
            String factoryClassName = (String)config.getAttributeValueByName(ATTR_CONTEXTFACTORY_CLASSNAME);

            try {
                Class<?> factoryClass = Class.forName(factoryClassName);
                Constructor<?> factoryCons = factoryClass.getDeclaredConstructor(new Class[]{config.getClass()});
                factory = (ContextFactory)factoryCons.newInstance(config);
                factoryRegistry.put(pdp, factory);
                pdpRegistry.put(factory, pdp);
            }
            catch (Exception e) {
                throw new ConfigurationException("Error occurs when initialize the ContextFactory.", e);
            }
        }
        return factory;
    }

    public static PDP getPDP(ContextFactory ctxFactory) {
        return pdpRegistry.get(ctxFactory);
    }

    public static synchronized void removeContextFactory(PDP pdp) {
        ContextFactory cf = factoryRegistry.remove(pdp);
        pdpRegistry.remove(cf);
    }
}