package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.policy.ResourceMatch;

public class FileAdapterResourceMatch extends FileAdapterTargetElementMatch {
    public static final String ELEMENT_NAME = "ResourceMatch";
    public FileAdapterResourceMatch(Element elem) throws Exception {
        initializeTargetElement(elem, ResourceMatch.class);
    }

    public FileAdapterResourceMatch(XACMLElement engineElem) throws Exception {
        if (engineElem.getElementName() == null) {
            engineElem.setElementName(ELEMENT_NAME);
        }
        initializeTargetElement(engineElem);
    }
}