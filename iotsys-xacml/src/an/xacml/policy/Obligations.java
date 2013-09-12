package an.xacml.policy;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import an.xacml.DefaultXACMLElement;
import an.xacml.IndeterminateException;
import an.xacml.context.Decision;
import an.xacml.engine.EvaluationContext;

public class Obligations extends DefaultXACMLElement {
    private Obligation[] obligations;
    private Map<Decision, Obligation[]> obliByDecision = new Hashtable<Decision, Obligation[]>();

    public Obligations(Obligation[] obls) {
        if (obls != null) {
            this.obligations = new Obligation[obls.length];
            System.arraycopy(obls, 0, obligations, 0, obls.length);
            initialize();
        }
    }

    /**
     * A copy constructor which is used to create a new Obligations instance from an existing one.
     * @param obls
     */
    public Obligations(Obligations obls) {
        if (obls.obligations == null) {
            this.obligations = null;
        }
        else {
            this.obligations = new Obligation[obls.obligations.length];
            for (int i = 0; i < obls.obligations.length; i ++) {
                this.obligations[i] = new Obligation(obls.obligations[i]);
            }
            initialize();
        }
    }

    protected void initialize() {
        ArrayList<Obligation> permit = new ArrayList<Obligation>();
        ArrayList<Obligation> deny = new ArrayList<Obligation>();
        for (int i = 0; i < obligations.length; i ++) {
            if (obligations[i].getFulfillOnEffect() == Effect.Permit) {
                permit.add(obligations[i]);
            }
            else if (obligations[i].getFulfillOnEffect() == Effect.Deny) {
                deny.add(obligations[i]);
            }
        }
        obliByDecision.put(Decision.Permit, permit.toArray(new Obligation[0]));
        obliByDecision.put(Decision.Deny, deny.toArray(new Obligation[0]));
    }

    public Obligation[] getAllObligations() {
        return obligations;
    }

    public Obligation[] getObligationsByDecision(Decision decision) {
        Obligation[] result = obliByDecision.get(decision);
        if (result == null && obligations != null) {
            Vector<Obligation> obliList = new Vector<Obligation>();
            for (int i = 0; i < obligations.length; i ++) {
                if (obligations[i].getFulfillOnEffect().equals(decision)) {
                    obliList.add(obligations[i]);
                }
            }
            result = (Obligation[])obliList.toArray(new Obligation[0]);
        }
        return result;
    }

    public void appendObligations(Obligation[] obls) {
        if (obls != null && obls.length > 0) {
            if (obligations == null) {
                obligations = obls;
            }
            else {
                Obligation[] appended = new Obligation[obligations.length + obls.length];
                System.arraycopy(obligations, 0, appended, 0, obligations.length);
                System.arraycopy(obls, 0, appended, obligations.length, obls.length);
                obligations = appended;
            }
            initialize();
        }
    }

    public void evaluateAllChildAttributeAssigments(EvaluationContext ctx) throws IndeterminateException {
        if (obligations != null) {
            for (Obligation obl : obligations) {
                AttributeAssignment[] attrAssigs = obl.getAttributeAssignments();
                if (attrAssigs != null) {
                    for (AttributeAssignment attrAssig : attrAssigs) {
                        attrAssig.evaluate(ctx);
                    }
                }
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            Obligations other = (Obligations)o;
            Obligation[] o1 = getAllObligations();
            Obligation[] o2 = other.getAllObligations();
            if (o1 != null && o2 != null && o1.length == o2.length) {
                for (int i = 0; i < o1.length; i ++) {
                    if (!o1[i].equals(o2[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}