package an.xacml.context;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getContextDataAdapterClassByXACMLElementType;

import java.lang.reflect.Constructor;

import org.w3c.dom.Element;

import an.xacml.DefaultXACMLElement;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;

public class Request extends DefaultXACMLElement {
    private int hashCode;
    /**
     * At least one Subject should be in the array.
     * If there are same "subject-category" in different Subject elements, then all these Subject elements
     * should be merged to a single Subject element. The merging should be happened before the RequestElement
     * is created. So following subjects are the results after done such merging.
     */
    private Subject[] subjects;
    /**
     * At least one Resource should be in the array.
     */
    private Resource[] resources;
    /**
     * Action should not be null.
     */
    private Action action;
    /**
     * Environment should not be null.
     */
    private Environment environment;

    private TargetElement[] allElements;
    /**
     * The request root element of request element, which is provided for AttributeSelector. If request isn't
     * constructed from XML element, we will build up one from all target elements. 
     */
    private Element requestElement;

    public Request(Subject[] subjs, Resource[] reses, Action act, Environment env) {
        this(null, subjs, reses, act, env);
    }

    public Request(Element elem, Subject[] subjs, Resource[] reses, Action act, Environment env) {
        this.requestElement = elem;
        this.subjects = subjs;
        this.resources = reses;
        this.action = act;
        this.environment = env;

        mergeAllElements();
    }

    public Subject[] getSubjects() {
        return subjects;
    }
 
    public Resource[] getResources() {
        return resources;
    }

    public Action getAction() {
        return action;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public TargetElement[] getAllTargetElements() {
        return allElements;
    }

    public Element getRootNode() throws Exception {
        if (requestElement == null) {
            // If the request isn't constructed from XML element, we will generate an XML element for it. So that it
            // doesn't depend on the file based request to evaluate AttributeSelector.
            Class<?> adapterClass = getContextDataAdapterClassByXACMLElementType(getClass());
            Constructor<?> constructor = adapterClass.getConstructor(XACMLElement.class);
            DataAdapter da = (DataAdapter)constructor.newInstance(this);
            requestElement = (Element)da.getDataStoreObject();
        }
        return requestElement;
    }

    /**
     * Generate the hashCode from all fields. If a RequestElement equals to the other, then their hashCode 
     * should also be equal. If 2 RequestElement are equal, their Index should also be equal.
     */
    public int hashCode() {
        return hashCode;
    }

    /**
     * Compare each attribute of RequestElement.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            Request other = (Request)o;
            if (subjects.length == other.subjects.length && resources.length == other.resources.length &&
                action.equals(other.action) && environment.equals(other.environment)) {
                for (int i = 0; i < subjects.length; i ++) {
                    if (!subjects[i].equals(other.subjects[i])) {
                        return false;
                    }
                }
                for (int i = 0; i < resources.length; i ++) {
                    if (!resources[i].equals(other.resources[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    protected void mergeAllElements() {
        allElements = new TargetElement[subjects.length + resources.length + 2];
        System.arraycopy(subjects, 0, allElements, 0, subjects.length);
        System.arraycopy(resources, 0, allElements, subjects.length, resources.length);
        allElements[allElements.length - 2] = action;
        allElements[allElements.length - 1] = environment;
    }
}