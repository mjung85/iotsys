package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.policy.Environment;

public class FileAdapterEnvironment extends FileAdapterTargetElement {
    public static final String ELEMENT_NAME = "Environment";
    public FileAdapterEnvironment(Element elem) throws Exception {
        initializeTargetElement(elem, Environment.class);
    }

    public FileAdapterEnvironment(XACMLElement engineElem) throws Exception {
        if (engineElem.getElementName() == null) {
            engineElem.setElementName(ELEMENT_NAME);
        }
        initializeTargetElement(engineElem);
    }
}