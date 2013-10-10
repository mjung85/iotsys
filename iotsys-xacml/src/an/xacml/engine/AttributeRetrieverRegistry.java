package an.xacml.engine;

import static an.xacml.engine.AttributeRetriever.ANY;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import an.config.ConfigElement;
import an.log.LogFactory;
import an.log.Logger;

public class AttributeRetrieverRegistry {
    public static final String ELEMTYPE_RETRIEVER = "AttributeRetrieverType";
    public static final String ATTR_RETRIEVER_CLASSNAME = "an.xacml.engine.AttributeRetriever";

    private static Map<PDP, AttributeRetrieverRegistry> attrRetrieverRegistry = new Hashtable<PDP, AttributeRetrieverRegistry>();
    private static Map<AttributeRetrieverRegistry, PDP> pdpRegistry = new Hashtable<AttributeRetrieverRegistry, PDP>();
    // An empty registry
    private static AttributeRetrieverRegistry defaultReg = new AttributeRetrieverRegistry(null);

    @SuppressWarnings("unchecked")
    private Set<AttributeRetriever>[] attrRetrieversReg = new HashSet[] {
        // ANY                             SUBJECT,                           ACTION
        new HashSet<AttributeRetriever>(), new HashSet<AttributeRetriever>(), new HashSet<AttributeRetriever>(),
        // RESOURCE                        ENVIRONMENT
        new HashSet<AttributeRetriever>(), new HashSet<AttributeRetriever>()};
    private Logger logger;

    private AttributeRetrieverRegistry(ConfigElement config) {
        logger = LogFactory.getLogger();
        // load all attribute retrievers that configured in this PDP.
        if (config != null) {
            ConfigElement[] items = (ConfigElement[])config.getXMLElementsByType(ELEMTYPE_RETRIEVER);
            for (ConfigElement each : items) {
                String className = (String)each.getAttributeValueByName(ATTR_RETRIEVER_CLASSNAME);
                try {
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> cons = clazz.getConstructor(each.getClass());
                    AttributeRetriever attrRetr = (AttributeRetriever)cons.newInstance(each);
                    register(attrRetr);
                }
                catch (Exception ex) {
                    logger.error("Error occurs while loading attribute retriever : " + className +
                            ", will continue to load next.", ex);
                }
            }
        }
    }

    public static synchronized AttributeRetrieverRegistry getInstance() {
        return defaultReg;
    }

    public static synchronized AttributeRetrieverRegistry getInstance(PDP pdp) {
        if (pdp == null) {
            return defaultReg;
        }

        AttributeRetrieverRegistry reg = attrRetrieverRegistry.get(pdp);

        if (reg == null) {
            reg = new AttributeRetrieverRegistry((ConfigElement)pdp.getAttributeRetrieverRegistryConfig());
            attrRetrieverRegistry.put(pdp, reg);
            pdpRegistry.put(reg, pdp);
        }
        return reg;
    }

    public static PDP getPDP(AttributeRetrieverRegistry reg) {
        return pdpRegistry.get(reg);
    }

    public static synchronized void removeInstance(PDP pdp) {
        AttributeRetrieverRegistry ar = attrRetrieverRegistry.remove(pdp);
        pdpRegistry.remove(ar);
    }

    /**
     * Attribute retriever's implementation also could register itself to a PDP dynamically using register method
     * instead of using configuration file.
     * 
     * @param retriever
     */
    public synchronized void register(AttributeRetriever retriever) {
        int type = retriever.getType();
        attrRetrieversReg[type].add(retriever);
        if (type != ANY) {
            attrRetrieversReg[ANY].add(retriever);
        }
    }

    public synchronized void unregister(AttributeRetriever retriever) {
        int type = retriever.getType();
        attrRetrieversReg[type].remove(retriever);
        if (type != ANY) {
            attrRetrieversReg[ANY].remove(retriever);
        }
    }

    public AttributeRetriever[] getAllAttributeRetrievers() {
        return attrRetrieversReg[ANY].toArray(new AttributeRetriever[0]);
    }

    public AttributeRetriever[] getAttributeRetrieversByType(int type) {
        return attrRetrieversReg[type].toArray(new AttributeRetriever[0]);
    }
}