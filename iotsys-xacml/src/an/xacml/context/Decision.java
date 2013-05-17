package an.xacml.context;

import an.xacml.DefaultXACMLElement;
import an.xacml.Version;
import an.xacml.XACMLElement;
import an.xacml.policy.Effect;

public enum Decision implements XACMLElement {
    Permit,
    Deny,
    Indeterminate,
    NotApplicable;

    /**
     * Compares PERMIT/DENY with corresponding Effect.
     * @param effect The Effect type object
     * @return
     */
    public boolean equals(Effect effect) {
        if (effect != null &&
            ((effect == Effect.Permit && this == Permit) ||
             (effect == Effect.Deny && this == Deny))) {
            return true;
        }
        return false;
    }

    /**
     * This method is used to make use of "getVersion" method of
     * DefaultElement
     */
    private XACMLElement delg = new DefaultXACMLElement() {};

    public Version getElementVersion() {
        return delg.getElementVersion();
    }

    public XACMLElement getRootElement() {
        // Since we don't need parse Response, and this element doesn't make use of root element, we don't support the
        // operation at this element.
        throw new UnsupportedOperationException("We don't support get root element on Decision element.");
    }

    public XACMLElement getParentElement() {
        // Since we don't need parse Response, and this element doesn't make use of parent element, we don't support the
        // operation at this element.
        throw new UnsupportedOperationException("We don't support get parent element on Decision element.");
    }

    public void setParentElement(XACMLElement parent) {
        throw new UnsupportedOperationException("We don't support set parent element on Decision element.");
    }

    public String getElementName() {
        return delg.getElementName();
    }

    public void setElementName(String elemName) {
        delg.setElementName(elemName);
    }
}