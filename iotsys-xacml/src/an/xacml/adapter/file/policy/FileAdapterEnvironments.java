package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.policy.Environments;

public class FileAdapterEnvironments extends FileAdapterTargetElements {
    public static final String ELEMENT_NAME = "Environments";
    public FileAdapterEnvironments(Element elem) throws Exception {
        initializeTargetElement(elem, Environments.class);
    }

    public FileAdapterEnvironments(XACMLElement engineElem) throws Exception {
        if (engineElem.getElementName() == null) {
            engineElem.setElementName(ELEMENT_NAME);
        }
        initializeTargetElement(engineElem);
    }
}