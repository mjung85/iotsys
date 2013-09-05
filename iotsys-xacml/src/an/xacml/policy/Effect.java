package an.xacml.policy;

import an.xacml.DefaultXACMLElement;
import an.xacml.Version;
import an.xacml.XACMLElement;
import an.xacml.context.Decision;

/**
 * Represent the Effect element of XACML. It provides an equals method that can be compared to
 * EvaluationResult.
 */
public enum Effect implements XACMLElement {
    Permit,
    Deny;

    /**
     * This method is used to make use of "getVersion" method of
     * DefaultElement
     */
    private XACMLElement delg = new DefaultXACMLElement() {};

    public Version getElementVersion() {
        return delg.getElementVersion();
    }

    public Decision getCorrespondingDecision() {
        if (this == Permit) {
            return Decision.Permit;
        }
        else {
            return Decision.Deny;
        }
    }
    /**
     * Compares to corresponding EvaluationResult.
     * @param decision
     * @return
     */
    public boolean equals(Decision decision) {
        if (decision != null &&
            ((decision == Decision.Permit && this == Permit) ||
             (decision == Decision.Deny && this == Deny))) {
            return true;
        }
        return false;
    }

    public XACMLElement getRootElement() {
        throw new UnsupportedOperationException("We don't support get root element on Effect element because it " +
        		"doesn't make use of root element.");
    }

    public XACMLElement getParentElement() {
        throw new UnsupportedOperationException("We don't support get parent element on Effect element because it " +
        		"doesn't make use of root element.");
    }

    public void setParentElement(XACMLElement parent) {
        throw new UnsupportedOperationException("We don't support set parent element on Effect element because it " +
        		"doesn't make use of parent element.");
    }

    public String getElementName() {
        return delg.getElementName();
    }

    public void setElementName(String elemName) {
        delg.setElementName(elemName);
    }
}