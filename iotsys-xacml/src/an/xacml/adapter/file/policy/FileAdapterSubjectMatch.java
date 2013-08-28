package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.policy.SubjectMatch;

public class FileAdapterSubjectMatch extends FileAdapterTargetElementMatch {
    public static final String ELEMENT_NAME = "SubjectMatch";
    public FileAdapterSubjectMatch(Element elem) throws Exception {
        initializeTargetElement(elem, SubjectMatch.class);
    }

    public FileAdapterSubjectMatch(XACMLElement engineElem) throws Exception {
        if (engineElem.getElementName() == null) {
            engineElem.setElementName(ELEMENT_NAME);
        }
        initializeTargetElement(engineElem);
    }
}