package an.xacml.engine;

import static an.xacml.Constants.ATTR_DATE;
import static an.xacml.Constants.ATTR_DATETIME;
import static an.xacml.Constants.ATTR_TIME;
import static an.xacml.DefaultXACMLElement.compareObject;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import an.config.ConfigElement;
import an.log.LogFactory;
import an.log.Logger;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.Matchable;
import an.xacml.context.Attribute;
import an.xacml.context.Request;
import an.xacml.context.TargetElement;
import an.xacml.policy.AbstractPolicy;
import an.xacml.policy.AttributeDesignator;
import an.xacml.policy.AttributeValue;
import an.xacml.policy.ConjunctiveMatch;
import an.xacml.policy.DefaultMatch;
import an.xacml.policy.DisjunctiveMatch;
import an.xacml.policy.Target;
import an.xacml.policy.function.BuiltInFunction;
import an.xacml.policy.function.EquivalentFunction;

/**
 * This class is used to cache polices.
 */
public class PolicyCache extends Cache {
    public static final String ATTR_ENABLE_ATTRVALUEINDEX = "enableAttributeValueIndex";

    List<AbstractPolicy> emptyTargetCache = new ArrayList<AbstractPolicy>();
    private boolean enableAttrValueIndex;

    public PolicyCache(ConfigElement config) {
        super(config);
        loadConfigurations(config);
    }

    protected void loadConfigurations(ConfigElement config) {
        super.loadConfigurations(config);
        enableAttrValueIndex = (Boolean)config.getAttributeValueByName(ATTR_ENABLE_ATTRVALUEINDEX);
    }

    public void add(AbstractPolicy policy)
    throws BuiltInFunctionNotFoundException, CacheSizeExceedCapacityException {
        List<AttributeIndex> attrIndexes = extractAttributeIndexesFromPolicy(policy);
        if (attrIndexes.size() > 0) {
            for (AttributeIndex each : attrIndexes) {
                policy.addToCache(each, this);
            }
        }
        else {
            emptyTargetCache.add(policy);
        }
    }

    public AbstractPolicy[] get(Request request) {
        try {
            readLock();

            List<AbstractPolicy> result = new ArrayList<AbstractPolicy>();
            result.addAll(emptyTargetCache);

            TargetElement[] allElems = request.getAllTargetElements();
            if (allElems != null && allElems.length > 0) {
                for (TargetElement each : allElems) {
                    Attribute[] attrs = each.getAllAttributes();
                    if (attrs != null && attrs.length > 0) {
                        for (Attribute attr : attrs) {
                            // Retrieve policies from cache using attribute index
                            List<Cacheable> policies = null;
                            URI attrId = attr.getAttributeID();
                            URI dataType = attr.getDataType();
                            // Get default attribute indexed policies
                            policies = get(new AttributeIndex(attrId, dataType));
                            // If current cache support value index, we add value indexed policies.
                            if (enableAttrValueIndex) {
                                if (policies == null) {
                                    policies = new ArrayList<Cacheable>();
                                }
                                Object[] values = attr.getAttributeValues();
                                for (Object value : values) {
                                    List<Cacheable> temp = get(new AttributeIndex(attrId, dataType, value));
                                    if (temp != null) {
                                        policies.addAll(temp);
                                    }
                                }
                            }

                            if (policies != null && policies.size() > 0) {
                                for (Cacheable policy : policies) {
                                    // Remove duplicated policies from result.
                                    if (!result.contains(policy)) {
                                        result.add((AbstractPolicy)policy);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return result.toArray(new AbstractPolicy[0]);
        }
        finally {
            readUnlock();
        }
    }

    public void update(AbstractPolicy[] toBeUpdated, URI[] toBeDeleted) {
        // TODO
    }

    protected void dump(OutputStream out) {
        PrintWriter writer = new PrintWriter(out);
        writer.print("Current indexed cache is \"" + getClass().getName() + "\", size = " + size +
                ", indexed cacheables distinct size = " + super.getAll().size());
        writer.flush();
        super.dump(out);
    }

    protected List<Cacheable> getAll() {
        try {
            readLock();
            List<Cacheable> result = super.getAll();
            result.addAll(emptyTargetCache);
            return result;
        }
        finally {
            readUnlock();
        }
    }

    public int distinctSize() {
        try {
            readLock();
            return getAll().size();
        }
        finally {
            readUnlock();
        }
    }

    protected void removeAll() {
        try {
            writeLock();
            cache.clear();
            emptyTargetCache.clear();
            size = 0;
        }
        finally {
            writeUnlock();
        }
    }

    private List<AttributeIndex> extractAttributeIndexesFromPolicy(AbstractPolicy policy)
    throws BuiltInFunctionNotFoundException {
        List<AttributeIndex> result = new ArrayList<AttributeIndex>();

        // Get context information
        PDP pdp = policy.getOwnerPDP();
        AttributeRetriever[] allAttrRetrs = AttributeRetrieverRegistry.getInstance(pdp).getAllAttributeRetrievers();
        Target target = policy.getTarget();

        List<DisjunctiveMatch> targetElems = getElementsFromTarget(target);
        // loop in Subjects, Actions, Resources and Environments to get child Subject[], Action[], Resource[] and
        // Environment[].
        for (DisjunctiveMatch dMatch : targetElems) {
            Matchable[] cMatches = dMatch.getMatchables();
            if (cMatches != null && cMatches.length > 0) {
                // loop in each Subject, Action, Resource and Environment to get child SubjectMatch, ActionMatch,
                // ResourceMatch and EnvironmentMatch.
                for (int i = 0; i < cMatches.length; i ++) {
                    ConjunctiveMatch cMatch = (ConjunctiveMatch)cMatches[i];
                    Matchable[] defaultMatches = cMatch.getMatchables();
                    if (defaultMatches != null && defaultMatches.length > 0) {
                        // loop in each SubjectMatch, ActionMatch, ResourceMatch and EnvironmentMatch to get child
                        // AttributeDesignators.
                        for (int j = 0; j < defaultMatches.length; j ++) {
                            DefaultMatch defaultMatch = (DefaultMatch)defaultMatches[j];
                            // get the attribute designator
                            Expression expression = defaultMatch.getAttributeDesignatorOrSelector();
                            if (expression instanceof AttributeDesignator) {
                                AttributeDesignator designator = (AttributeDesignator)expression;
                                // If current PDP support mustBePresent and the attribute designator also required
                                // the attribute must be present, we don't add the attribute to index.
                                if (!pdp.supportMustBePresent() || !designator.isAttributeMustBePresent()) {
                                    URI attrId = designator.getAttributeID();
                                    URI dataType = designator.getDataType();
                                    /*
                                     * Considering a policy which requires "attr1" and "attr2" attributes. If there is
                                     * an AttributeRetriever in system supports all of these 2 attributes, regardless
                                     * the coming request ships those attributes or not, this policy should be evaluated
                                     * against the request anyway, because it can get those attributes from
                                     * AttributeRetriever. Also, this policy should be evaluated against any request
                                     * since it doesn't require attributes from request, it can get them from
                                     * AttributeRetriever.  If we add this policy to the cache use attribute keys, it
                                     * will never be retrieved by request that doesn't ship the corresponding
                                     * attributes.
                                     *
                                     * That is to say, if an attribute is supported by AttributeRetriever, we should not
                                     * require the coming request to have this attribute. So we will treat above policy
                                     * as if it has an empty target, and it will be added to a the empty target area (
                                     * line 61).
                                     */
                                    // check if there are attribute retrievers support current attribute, if there is,
                                    // we won't add the attribute as an index
                                    if (!isAttributeSupportedByRetrievers(attrId, dataType, allAttrRetrs)) {
                                        AttributeIndex index = null;
                                        if (enableAttrValueIndex && isMatchFunctionEquivalentType(defaultMatch)) {
                                            // add attribute value into account
                                            index = new AttributeIndex(designator, defaultMatch.getAttributeValue());
                                        }
                                        else {
                                            index = new AttributeIndex(attrId, dataType);
                                        }
                                        if (!result.contains(index)) {
                                            result.add(index);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<DisjunctiveMatch> getElementsFromTarget(Target target) {
        List<DisjunctiveMatch> targetElems = new ArrayList<DisjunctiveMatch>();
        DisjunctiveMatch subjs = target.getSubjects();
        if (subjs != null) {
            targetElems.add(subjs);
        }

        DisjunctiveMatch res = target.getResources();
        if (res != null) {
            targetElems.add(res);
        }
        
        DisjunctiveMatch acts = target.getActions();
        if (acts != null) {
            targetElems.add(acts);
        }

        DisjunctiveMatch envs = target.getEnvironments();
        if (envs != null) {
            targetElems.add(envs);
        }
        return targetElems;
    }

    private boolean isAttributeSupportedByRetrievers(URI attrId, URI dataType, AttributeRetriever[] attrRetrs) {
        // If the attribute is in evaluation context, we directly return true.
        if (ATTR_TIME.equals(attrId) || ATTR_DATE.equals(attrId) || ATTR_DATETIME.equals(attrId)) {
            return true;
        }
        // Otherwise, we see if it can be retrieved from attribute retrievers.
        if (attrRetrs != null) {
            for (AttributeRetriever each : attrRetrs) {
                if (each.isAttributeSupported(attrId, dataType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMatchFunctionEquivalentType(DefaultMatch match) throws BuiltInFunctionNotFoundException {
        try {
            BuiltInFunction func = FunctionRegistry.getInstance().lookup(match.getMatchID());
            if (func.getAttribute(EquivalentFunction.class) != null) {
                return true;
            }
            return false;
        }
        catch (Exception e) {
            if (e instanceof BuiltInFunctionNotFoundException) {
                throw (BuiltInFunctionNotFoundException)e;
            }
            // code won't run to here because we have already get the FunctionRegistry instance.
            else {
                logger.error("Error occurs while get FunctionRegistry instance.", e);
                return false;
            }
        }
    }
}

class AttributeIndex {
    private int hashCode;
    private String str;
    private URI attrId;
    private URI dataType;
    private Object value;

    public AttributeIndex(URI attrId, URI dataType) {
        this.attrId = attrId;
        this.dataType = dataType;
        generateHashCode();
    }

    public AttributeIndex(URI attrId, URI dataType, Object value) {
        this.attrId = attrId;
        this.dataType = dataType;
        this.value = value;
        generateHashCode();
    }

    public AttributeIndex(AttributeDesignator designator, AttributeValue attrValue) {
        Logger logger = LogFactory.getLogger();
        this.attrId = designator.getAttributeID();
        this.dataType = designator.getDataType();
        try {
            this.value = attrValue.getValue();
        } catch (IndeterminateException e) {
            logger.warn("Error occurs while get value from AttributeValue.", e);
            this.value = null;
        }
        generateHashCode();
    }

    private void generateHashCode() {
        hashCode = this.attrId.hashCode() + hashCode * 13;
        hashCode = this.dataType.hashCode() + hashCode * 13;
        hashCode = (value == null ? 0 : value.hashCode()) + hashCode * 13;
        str = attrId.toString() + "(" + dataType.toString() + "," + (value == null ? "null" : value) + ")";
    }

    public URI getAttributeId() {
        return attrId;
    }

    public URI getDataType() {
        return dataType;
    }

    public Object getValue() {
        return value;
    }

    public int hashCode() {
        return hashCode;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            AttributeIndex other = (AttributeIndex)o;
            if (this.attrId.equals(other.attrId) && this.dataType.equals(other.dataType) &&
               compareObject(value, other.value)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return str;
    }
}