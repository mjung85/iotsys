package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.policy.Resource;

public class FileAdapterResource extends FileAdapterTargetElement {
    public static final String ELEMENT_NAME = "Resource";
    public FileAdapterResource(Element elem) throws Exception {
        initializeTargetElement(elem, Resource.class);
    }

    public FileAdapterResource(XACMLElement engineElem) throws Exception {
        if (engineElem.getElementName() == null) {
            engineElem.setElementName(ELEMENT_NAME);
        }
        initializeTargetElement(engineElem);
    }
}