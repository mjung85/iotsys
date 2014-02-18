package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.policy.Subject;

public class FileAdapterSubject extends FileAdapterTargetElement {
    public static final String ELEMENT_NAME = "Subject";
    public FileAdapterSubject(Element elem) throws Exception {
        initializeTargetElement(elem, Subject.class);
    }

    public FileAdapterSubject(XACMLElement engineElem) throws Exception {
        if (engineElem.getElementName() == null) {
            engineElem.setElementName(ELEMENT_NAME);
        }
        initializeTargetElement(engineElem);
    }
}