package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.policy.Subjects;

public class FileAdapterSubjects extends FileAdapterTargetElements {
    public static final String ELEMENT_NAME = "Subjects";
    public FileAdapterSubjects(Element elem) throws Exception {
        initializeTargetElement(elem, Subjects.class);
    }

    public FileAdapterSubjects(XACMLElement engineElem) throws Exception {
        if (engineElem.getElementName() == null) {
            engineElem.setElementName(ELEMENT_NAME);
        }
        initializeTargetElement(engineElem);
    }
}