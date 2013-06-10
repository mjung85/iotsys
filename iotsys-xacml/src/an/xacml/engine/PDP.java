package an.xacml.engine;

import static an.xacml.engine.PDPStatus.KEY_RUN_STATUS;
import static an.xacml.engine.PDPStatus.STATUS_RUN_INITIALIZED;
import static an.xacml.engine.PDPStatus.STATUS_RUN_NOTRUN;
import static an.xacml.engine.PDPStatus.STATUS_RUN_RELOADPOLICY;
import static an.xacml.engine.PDPStatus.STATUS_RUN_RUNING;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import an.config.ConfigElement;
import an.config.ConfigurationException;
import an.control.AbstractMonitorableAndControllable;
import an.control.OperationFailedException;
import an.log.LogFactory;
import an.log.Logger;
import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.PolicySyntaxException;
import an.xacml.Version;
import an.xacml.adapter.DataAdapter;
import an.xacml.adapter.DataAdapterException;
import an.xacml.context.Result;
import an.xacml.policy.AbstractPolicy;
import an.xacml.policy.function.BuiltInFunction;
import an.xml.XMLGeneralException;

/**
 * This class creates a PDP, which will load and cache all XACML policies. There may have more than one PDP in a JVM.
 * Each PDP should have a unique domain name, domain name is the PDP's identity.
 */
public class PDP extends AbstractMonitorableAndControllable {
    public static final String ELEM_PDP = "PDP";

    static final String ATTR_DOMAINNAME = "domainName";
    static final String ATTR_MULTIPOLICYCOMBALG = "multiPoliciesCombineAlg";
    static final String ATTR_MUSTBEPRESENT = "supportMustBePresent";
    static final String ATTR_INNEREXPRESSION = "supportInnerExpression";
    static final String ATTR_RESOLVEPOLICY = "resolveReferencedPoliciesOnLoad";
    static final String ELEMTYPE_DATASTORE = "DataStoreType";
    static final String ELEMTYPE_CONTEXT_FACTORY = "ContextFactoryType";
    static final String ELEM_CACHE_MANAGER = "CacheManager";
    static final String ELEM_POLICY_RESOLVER = "PolicyResolverRegistry";
    static final String ELEM_ATTRIBUTE_FACTORY = "AttributeRetrieverRegistry";

    private static Map<String, PDP> pdpRegistry = new Hashtable<String, PDP>();
    private String domain;
    private DataStore policyLoader;
    private ContextFactory contextFactory;
    private CacheManager cacheMgr;
    private FunctionRegistry funcReg;
    private URI multiPoliciesCombineAlg;
    // Cache the configurations
    private boolean supportMustBePresent;
    private boolean supportInnerExpression;
    private boolean resolveReferencedPoliciesOnLoad;
    private ConfigElement dsConfig;
    private ConfigElement ctxConfig;
    private ConfigElement cacheConfig;
    private ConfigElement policyResConfig;
    private ConfigElement attrRtrConfig;

    private Version version = new Version();
    private Logger logger;

    private PDP(ConfigElement config) throws PDPInitializeException {
        try {
            logger = LogFactory.getLogger();
            status = new PDPStatus();
            loadConfigurations(config);
            initialize();
        }
        catch (Exception e) {
            throw new PDPInitializeException("There is error occurs while initialize PDP.", e);
        }
    }

    protected void loadConfigurations(ConfigElement config) throws XMLGeneralException {
        domain = (String)config.getAttributeValueByName(ATTR_DOMAINNAME);
        multiPoliciesCombineAlg = (URI)config.getAttributeValueByName(ATTR_MULTIPOLICYCOMBALG);
        supportMustBePresent = (Boolean)config.getAttributeValueByName(ATTR_MUSTBEPRESENT);
        supportInnerExpression = (Boolean)config.getAttributeValueByName(ATTR_INNEREXPRESSION);
        resolveReferencedPoliciesOnLoad = (Boolean)config.getAttributeValueByName(ATTR_RESOLVEPOLICY);
        dsConfig = (ConfigElement)config.getSingleXMLElementByType(ELEMTYPE_DATASTORE);
        ctxConfig = (ConfigElement)config.getSingleXMLElementByType(ELEMTYPE_CONTEXT_FACTORY);
        cacheConfig = (ConfigElement)config.getSingleXMLElementByName(ELEM_CACHE_MANAGER);
        policyResConfig = (ConfigElement)config.getSingleXMLElementByName(ELEM_POLICY_RESOLVER);
        attrRtrConfig = (ConfigElement)config.getSingleXMLElementByName(ELEM_ATTRIBUTE_FACTORY);
    }

    protected void initialize() throws ConfigurationException, XMLGeneralException {
        policyLoader = DataStoreHelper.getDataStore(this);
        contextFactory = ContextFactoryHelper.getContextFactory(this);
        cacheMgr = CacheManager.getInstance(this);
        AttributeRetrieverRegistry.getInstance(this);
        status.updateProperty(KEY_RUN_STATUS, STATUS_RUN_INITIALIZED);
    }

    protected void loadPolicies() throws DataAdapterException, CacheSizeExceedCapacityException,
    PolicySyntaxException, BuiltInFunctionNotFoundException {
        // Load policies from data store
        DataAdapter[] dataAdapters = policyLoader.load();
        // Preprocess
        DataAdapterPreprocessor processor = new DataAdapterPreprocessor(dataAdapters);
        // Get all policies
        AbstractPolicy[] policies = processor.getPolicies();
        // Init default policy resolver.
        PolicyResolver[] resolvers = PolicyResolverRegistry.getInstance(this).getAllPolicyResolvers();
        for (PolicyResolver resolver : resolvers) {
            if (resolver instanceof DefaultDataStorePolicyResolver) {
                ((DefaultDataStorePolicyResolver)resolver).setPolicies(policies);
            }
        }
        // Resolve all policies if required
        if (resolveReferencedPoliciesOnLoad) {
            processor.resolveAllPolicies();
        }
        // add policies to cache
        cacheMgr.addPolicies(policies);
    }

    /**
     * Initialize the PDP, load policies and cache them, and other things.
     * @throws CacheSizeExceedCapacityException 
     * @throws DataAdapterException 
     * @throws PolicySyntaxException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws BuiltInFunctionExistsException 
     * @throws BuiltInFunctionNotFoundException 
     */
    protected void startPDP() throws CacheSizeExceedCapacityException, DataAdapterException, PolicySyntaxException,
    IOException, ClassNotFoundException, BuiltInFunctionExistsException, BuiltInFunctionNotFoundException {
        // load all functions
        funcReg = FunctionRegistry.getInstance();
        // load all policies
        loadPolicies();
        if (logger.isDebugEnabled()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            cacheMgr.policyCache.dump(out);
            logger.debug("=================Dump all Cacheables that current in Policy Cache=================");
            logger.debug("\nCurrent non-indexed empty target cache size = " +
                    cacheMgr.policyCache.emptyTargetCache.size() + ".\n" + 
                    cacheMgr.policyCache.emptyTargetCache + "\n" + out.toString());
            logger.debug("=====================================Done=========================================");
        }
        // update status
        status.updateProperty(KEY_RUN_STATUS, STATUS_RUN_RUNING);
    }

    public synchronized static PDP getInstance(ConfigElement config)
    throws PDPInitializeException {
        String domainName = (String)config.getAttributeValueByName(ATTR_DOMAINNAME);
        PDP pdp = pdpRegistry.get(domainName);
        if (pdp == null) {
            pdp = new PDP(config);
            pdpRegistry.put(domainName, pdp);
        }

        return pdp;
    }

    public String getDomain() {
        return domain;
    }

    /**
     * This method read a configuration entry to determine which decision will be used when multiple 
     * PolicySets match the same request. It will be called by ContextHandler during evaluating policies.
     */
    public Result getMultiPoliciesDecision(EvaluationContext ctx, AbstractPolicy[] policies)
    throws IndeterminateException {
        if (policies == null || policies.length == 0) {
            // NotApplicable
            return Result.NOTAPPLICABLE;
        }
        else if (policies.length == 1) {
            // evaluate the policy
            return policies[0].evaluate(ctx);
        }
        else {
            // retrieve the function, and then evaluate the policies in the function.
            try {
                BuiltInFunction func = funcReg.lookup(multiPoliciesCombineAlg);
                return (Result)func.invoke(ctx, new Object[] {policies});
            } catch (Exception e) {
                IndeterminateException intEx;
                // The IndeterminateException throws from evaluation of policies.
                if (e instanceof IndeterminateException) {
                    intEx = (IndeterminateException)e;
                }
                // Other exceptions, such as BuiltInFunctionNotFoundException, we treat it as processing error.
                else {
                     intEx = new IndeterminateException("Server encounter errors while handling the request", 
                            e, Constants.STATUS_PROCESSINGERROR);
                }
                throw intEx;
            }
        }
    }

    /**
     * This method creates a ContextHandler, and then pass a request context to it for process.
     * @throws PDPNotReadyException 
     */
    public Object handleRequest(Object reqCtx) throws PDPNotReadyException {
        if (status.getProperty(KEY_RUN_STATUS).equals(STATUS_RUN_RUNING)) {
            ContextHandler handler = contextFactory.getContextHandler();
            return handler.handle(reqCtx);
        }
        else {
            throw new PDPNotReadyException("PDP is not ready.");
        }
    }

    public boolean supportMustBePresent() {
        return supportMustBePresent;
    }

    public boolean supportInnerExpression() {
        return supportInnerExpression;
    }

    public boolean resolveReferencedPoliciesOnLoad() {
        return resolveReferencedPoliciesOnLoad;
    }

    public ConfigElement getDataStoreConfig() {
        return dsConfig;
    }

    public ConfigElement getContextFactoryConfig() {
        return ctxConfig;
    }

    public ConfigElement getCacheManagerConfig() {
        return cacheConfig;
    }

    public ConfigElement getPolicyResolverRegistryConfig() {
        return policyResConfig;
    }

    public ConfigElement getAttributeRetrieverRegistryConfig() {
        return attrRtrConfig;
    }

    public synchronized void shutdown() {
        shutdownPDP();
    }

    public synchronized void shutdownForce() {
        shutdownPDP();
    }

    public void reloadPolicies() throws DataAdapterException, CacheSizeExceedCapacityException,
    PolicySyntaxException, BuiltInFunctionNotFoundException {
        System.out.println("Reloading policies ...");
        try {
            // update status
            status.updateProperty(KEY_RUN_STATUS, STATUS_RUN_RELOADPOLICY);

            cacheMgr.policyCache.writeLock();
            // clear both result and policy cache
            cacheMgr.removeAll();
            // reload all policies
            loadPolicies();
            if (logger.isDebugEnabled()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                cacheMgr.policyCache.dump(out);
                logger.debug("=================Dump all Cacheables that current in Policy Cache=================");
                logger.debug("\nCurrent non-indexed empty target cache size = " +
                        cacheMgr.policyCache.emptyTargetCache.size() + ".\n" + 
                        cacheMgr.policyCache.emptyTargetCache + "\n" + out.toString());
                logger.debug("=====================================Done=========================================");
            }
        }
        finally {
            cacheMgr.policyCache.writeUnlock();
            status.updateProperty(KEY_RUN_STATUS, STATUS_RUN_RUNING);
        }
    }

    private void shutdownPDP() {
        status.updateProperty(KEY_RUN_STATUS, STATUS_RUN_NOTRUN);
        cacheMgr.removeAll();
        policyLoader.shutdown();
        AttributeRetrieverRegistry.removeInstance(this);
        CacheManager.removeInstance(this);
        ContextFactoryHelper.removeContextFactory(this);
        DataStoreHelper.removeDataStore(this);
        pdpRegistry.remove(domain);
    }

    public synchronized void start() throws OperationFailedException {
        System.out.println("Starting PDP '" + getDomain() + "' ... ");

        Object current = status.getProperty(KEY_RUN_STATUS);
        if (current.equals(STATUS_RUN_INITIALIZED)) {
            try {
                startPDP();
                System.out.println("PDP '" + getDomain() + "' has been started. Version <" + version.getVersion() + ">");
            } catch (Exception e) {
                throw new OperationFailedException("Start PDP failed.", e);
            }
        }
        else {
            System.out.println("PDP '" + getDomain() + "' is " + current + ", you can't start it unless it is "
                    + STATUS_RUN_INITIALIZED + ".");
        }
    }
}