package an.xacml.engine;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import an.config.ConfigElement;
import an.log.LogFactory;
import an.log.Logger;

public class PolicyResolverRegistry {
    public static final String ELEMTYPE_POLICYRESOLVER = "PolicyResolverType";
    public static final String ATTR_POLICYRESOLVER_CLASSNAME = "an.xacml.engine.PolicyResolver";

    private static Map<PDP, PolicyResolverRegistry> policyResolverRegistry = new Hashtable<PDP, PolicyResolverRegistry>();
    private static Map<PolicyResolverRegistry, PDP> pdpRegistry = new Hashtable<PolicyResolverRegistry, PDP>();
    // An empty registry
    private static PolicyResolverRegistry defaultReg = new PolicyResolverRegistry(null);

    private Set<PolicyResolver> resolvers = new HashSet<PolicyResolver>();
    private Logger logger;

    public PolicyResolverRegistry(ConfigElement config) {
        logger = LogFactory.getLogger();
        // load all policy resolvers that configured in this data store.
        if (config != null) {
            ConfigElement[] items = (ConfigElement[])config.getXMLElementsByType(ELEMTYPE_POLICYRESOLVER);
            for (ConfigElement each : items) {
                String className = (String)each.getAttributeValueByName(ATTR_POLICYRESOLVER_CLASSNAME);
                try {
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> cons = clazz.getConstructor(each.getClass());
                    PolicyResolver policyResolver = (PolicyResolver)cons.newInstance(each);
                    register(policyResolver);
                }
                catch (Exception ex) {
                    logger.error("Error occurs while loading policy resolver : " + className +
                            ", will continue to load next.", ex);
                }
            }
        }
    }

    public static synchronized PolicyResolverRegistry getInstance() {
        return defaultReg;
    }

    public static synchronized PolicyResolverRegistry getInstance(PDP pdp) {
        if (pdp == null) {
            return defaultReg;
        }

        PolicyResolverRegistry reg = policyResolverRegistry.get(pdp);

        if (reg == null) {
            reg = new PolicyResolverRegistry((ConfigElement)pdp.getPolicyResolverRegistryConfig());
            policyResolverRegistry.put(pdp, reg);
            pdpRegistry.put(reg, pdp);
        }
        return reg;
    }

    public static PDP getPDP(PolicyResolverRegistry reg) {
        return pdpRegistry.get(reg);
    }

    public static synchronized void removeInstance(PDP pdp) {
        PolicyResolverRegistry pr = policyResolverRegistry.remove(pdp);
        pdpRegistry.remove(pr);
    }

    /**
     * Policy resolver's implementation also could register itself to a DataStore dynamically using register method
     * instead of using configuration file.
     * @param retriever
     */
    public synchronized void register(PolicyResolver resolver) {
        resolvers.add(resolver);
    }

    public synchronized void unregister(PolicyResolver resolver) {
        resolvers.remove(resolver);
    }

    public PolicyResolver[] getAllPolicyResolvers() {
        return resolvers.toArray(new PolicyResolver[0]);
    }
}