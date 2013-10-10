package an.xacml.engine;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


import an.config.ConfigElement;
import an.xacml.IndeterminateException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.adapter.DataAdapterException;
import an.xacml.context.Request;
import an.xacml.context.Result;
import an.xacml.policy.AbstractPolicy;
import an.xml.XMLGeneralException;

/**
 * The CacheManager manage cache mechanism that used in PDP system.
 * 
 * There are 2 types of caches that used in PDP. One is policy cache, which is used to cache all policies.
 * Policies are loaded from a underlying data store, such as policy files under a directory of local file 
 * system.  Policies are added to a hash map cache using Target as index, so that when a request is coming,
 * we can use request's Target as key to get the corresponding matched polices, then to evaluate policies.
 * 
 * The other is decision cache, which is used to cache the decisions. Decisions are got from policy's 
 * evaluation result, and will be added to decision cache using request as index. So that when a request
 * is coming, we first retrieve corresponding decision from decision cache using the request, if there is
 * same request has evaluated before, then we can get a decision from cache, and we don't need retrieve
 * policies and evaluate them.  Except using request as index, decision cache also has another index, which
 * is policy. It is used to remove corresponding decision from decision cache when a policy is updated.
 * 
 */
public class CacheManager {
    public static final String ELEM_POLICY_CACHE = "PolicyCache";
    public static final String ELEM_EVALRESULT_CACHE = "EvaluationResultCache";
    public static final String ATTR_EVALRESULTCACHE = "enableEvaluationResultCache";

    private static Map<PDP, CacheManager> managerRegistry = new Hashtable<PDP, CacheManager>();
    private static Map<CacheManager, PDP> pdpRegistry = new Hashtable<CacheManager, PDP>();

    private boolean enableResultCache;
    private ConfigElement policyCacheConfig;
    private ConfigElement evaCacheConfig;

    private EvaluationResultCache resultByRequest;
    PolicyCache policyCache;

    private CacheManager(ConfigElement config) throws XMLGeneralException {
        loadConfigurations(config);
        initialize();
    }

    protected void loadConfigurations(ConfigElement config) throws XMLGeneralException {
        enableResultCache = (Boolean)config.getAttributeValueByName(ATTR_EVALRESULTCACHE);
        policyCacheConfig = (ConfigElement)config.getSingleXMLElementByName(ELEM_POLICY_CACHE);
        evaCacheConfig = (ConfigElement)config.getSingleXMLElementByName(ELEM_EVALRESULT_CACHE);
    }

    /**
     * Each PDP has its own CacheManager, pass a PDP object will get corresponding CacheManager instance.
     * @throws XMLGeneralException 
     */
    public static synchronized CacheManager getInstance(PDP pdp) throws XMLGeneralException {
        CacheManager mgr = (CacheManager)managerRegistry.get(pdp);
        if (mgr == null) {
            mgr = new CacheManager((ConfigElement)pdp.getCacheManagerConfig());
            managerRegistry.put(pdp, mgr);
            pdpRegistry.put(mgr, pdp);
        }
        return mgr;
    }

    public static PDP getPDP(CacheManager cacheMgr) {
        return pdpRegistry.get(cacheMgr);
    }

    public static synchronized void removeInstance(PDP pdp) {
        CacheManager cm = managerRegistry.remove(pdp);
        pdpRegistry.remove(cm);
    }

    /**
     * Initialize the CacheManager, initialize all Caches.
     * @throws XMLGeneralException 
     */
    protected void initialize() throws XMLGeneralException {
        policyCache = new PolicyCache(policyCacheConfig);
        if (enableResultCache) {
            resultByRequest = new EvaluationResultCache(evaCacheConfig);
        }
    }

    /**
     * Add a EvaluationResult object to evaluation result caches.
     * @param request
     * @param policyId
     * @param result
     * @throws CacheSizeExceedCapacityException 
     */
    protected void addEvalResult(Request request, Result result)
    throws CacheSizeExceedCapacityException {
        if (enableResultCache) {
            ((DefaultCacheable)result).addToCache(request, resultByRequest);
        }
    }

    public Result getEvalResultByRequest(Request request) {
        if (enableResultCache) {
            List<Cacheable> results = resultByRequest.get(request);
            if (results == null || results.size() == 0) {
                return null;
            }

            return (Result)results.get(0);
        }
        return null;
    }

    /**
     * This method is only used to load policies from underlying data store, it should not used to update policies from
     * PAP.  To update policies, use updatePolicies instead.
     * @param policy
     * @throws CacheSizeExceedCapacityException
     * @throws BuiltInFunctionNotFoundException 
     */
    protected void addPolicies(AbstractPolicy[] policies)
    throws CacheSizeExceedCapacityException, BuiltInFunctionNotFoundException {
        for (AbstractPolicy policy : policies) {
            policyCache.add(policy);
        }
    }

    public AbstractPolicy[] getPoliciesByRequest(Request request) {
        return policyCache.get(request);
    }

    public AbstractPolicy[] getAllPolicies() {
        return policyCache.getAll().toArray(new AbstractPolicy[0]);
    }

    public int getCachedPolicyNumber() {
        return policyCache.distinctSize();
    }

    /**
     * When PAP is sending updated policies, this method will be called to update those policies in cache.
     * The method will update policies in policy cache by specific IDs, and invalidate all evaluation results.
     * @param policies
     * @throws CacheSizeExceedCapacityException 
     * @throws DataAdapterException 
     */
    public void updatePolicies(AbstractPolicy[] toBeUpdated, URI[] toBeDeleted)
    throws CacheSizeExceedCapacityException, DataAdapterException {
        if ((toBeUpdated != null && toBeUpdated.length > 0) || (toBeDeleted != null && toBeDeleted.length > 0)) {
            if (enableResultCache) {
                resultByRequest.invalidateAll();
            }
            policyCache.update(toBeUpdated, toBeDeleted);
            // Updated policies to underlying data store.
            synchronized (this) {
                try {
                    DataAdapter[] daUpdated = null, daDeleted = null;

                    if (toBeUpdated != null && toBeUpdated.length > 0) {
                        daUpdated = new DataAdapter[toBeUpdated.length];
                        for (int i = 0; i < toBeUpdated.length; i ++) {
                            daUpdated[i] = createDataAdapterFromEngineElement(toBeUpdated[i]);
                        }
                    }

                    if (toBeDeleted != null && toBeDeleted.length > 0) {
                        daDeleted = new DataAdapter[toBeDeleted.length];
                        for (int i = 0; i < toBeDeleted.length; i ++) {
                            final URI deleteID = toBeDeleted[i];
                            // Construct a dumy policy that only include ID, since to remove an entry from cache only
                            // requires the id.
                            daDeleted[i] = new DataAdapter() {
                                public XACMLElement getEngineElement() {
                                    return new OnlyIDPolicy(deleteID);
                                }
                                // We don't need this method
                                public Object getDataStoreObject() {
                                    throw new UnsupportedOperationException(
                                            "We don't support getXMLElement operation on this class.");
                                }
                            };
                        }
                    }

                    DataStoreHelper.getDataStore(getPDP(this)).update(daUpdated, daDeleted);
                }
                catch (Exception ex) {
                    throw new DataAdapterException(
                            "Policies were not synchronized to underly data store due to errors.", ex);
                }
            }
        }
    }

    protected void removeAll() {
        policyCache.removeAll();
        if (enableResultCache) {
            resultByRequest.removeAll();
        }
    }

    private DataAdapter createDataAdapterFromEngineElement(XACMLElement engineElem) throws Exception {
        Class<?> adapterClass = getPolicyDataAdapterClassByXACMLElementType(engineElem.getClass());
        // All file adapters should have a constructor with a parameter that type is "XACMLElement"
        Constructor<?> constructor = adapterClass.getConstructor(XACMLElement.class);
        // initialize the data adapter from engine element.
        return (DataAdapter)constructor.newInstance(engineElem);
    }

    private class OnlyIDPolicy extends AbstractPolicy {
        private OnlyIDPolicy(URI id) {
            this.id = id;
            generateHashCode();
        }

        public Result evaluate(EvaluationContext ctx) throws IndeterminateException {
            throw new UnsupportedOperationException("We don't support evaluate operation on this class.");
        }

        protected void mergeTargets() {
            throw new UnsupportedOperationException("We don't support mergeTargets operation on this class.");
        }
    }
}