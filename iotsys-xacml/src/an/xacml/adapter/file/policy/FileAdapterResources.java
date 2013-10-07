package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.policy.Resources;

public class FileAdapterResources extends FileAdapterTargetElements {
    public static final String ELEMENT_NAME = "Resources";
    public FileAdapterResources(Element elem) throws Exception {
        initializeTargetElement(elem, Resources.class);
    }

    public FileAdapterResources(XACMLElement engineElem) throws Exception {
        if (engineElem.getElementName() == null) {
            engineElem.setElementName(ELEMENT_NAME);
        }
        initializeTargetElement(engineElem);
    }
}