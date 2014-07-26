package an.xacml.policy;

import java.net.URI;

import an.xacml.DefaultXACMLElement;
import an.xml.XMLDataTypeMappingException;

public class Obligation extends DefaultXACMLElement {
    private AttributeAssignment[] attributeAssignments;
    private final URI obligationId;
    private final Effect fulfillOn;

    public Obligation(URI oId, Effect fulfillOn, AttributeAssignment[] attrAssignments) {
        this.obligationId = oId;
        this.fulfillOn = fulfillOn;
        this.attributeAssignments = attrAssignments;
    }

    /**
     * A copy constructor which is used to create a new Obligation instance from an existing one.
     * @param obl
     */
    public Obligation(Obligation obl) {
        this.obligationId = obl.obligationId;
        this.fulfillOn = obl.fulfillOn;
        if (obl.attributeAssignments == null) {
            this.attributeAssignments = null;
        }
        else {
            this.attributeAssignments = new AttributeAssignment[obl.attributeAssignments.length];
            try {
                for (int i = 0; i < obl.attributeAssignments.length; i ++) {
                    this.attributeAssignments[i] = new AttributeAssignment(obl.attributeAssignments[i]);
                }
            }
            // This should not be happen
            catch (XMLDataTypeMappingException ex) {}
        }
    }

    public URI getObligationId() {
        return obligationId;
    }

    public AttributeAssignment[] getAttributeAssignments() {
        return attributeAssignments;
    }

    public Effect getFulfillOnEffect() {
        return fulfillOn;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            Obligation other = (Obligation)o;
            if (compareObject(this.obligationId, other.obligationId) &&
                compareObject(this.fulfillOn, other.fulfillOn) &&
                this.attributeAssignments != null && other.attributeAssignments != null &&
                this.attributeAssignments.length == other.attributeAssignments.length) {
                for (int i = 0; i < this.attributeAssignments.length; i ++) {
                    if (!(this.attributeAssignments[i].equals(other.attributeAssignments[i]))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}