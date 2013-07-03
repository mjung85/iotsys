package an.xacml.engine;

import java.util.ArrayList;
import java.util.Iterator;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.AbstractPolicy;
import an.xacml.policy.IdReference;
import an.xacml.policy.PolicySet;

/**
 * This class is used to preprocess data adapters that loaded from underlying data store. Currently it in charge of
 * populate policies to IdReference element.  It may be added other processes in the future.
 */
public class DataAdapterPreprocessor {
    private DataAdapter[] das;
    private AbstractPolicy[] policies;

    private ArrayList<IdReference> idReferences = new ArrayList<IdReference>();

    public DataAdapterPreprocessor(DataAdapter[] das) throws PolicySyntaxException {
        this.das = das;
    }

    private void convertDataAdaptersToPolicies() {
        if (das != null) {
            policies = new AbstractPolicy[das.length];
            for (int i = 0; i < das.length; i ++) {
                policies[i] = (AbstractPolicy)das[i].getEngineElement();
            }
        }
    }

    public void resolveAllPolicies() throws PolicySyntaxException {
        if (policies != null && policies.length > 0) {
            for (int i = 0; i < policies.length; i ++) {
                if (policies[i] instanceof PolicySet) {
                    searchAndRegisterIdReferenceElement((PolicySet)policies[i]);
                }
            }

            Iterator<IdReference> it = idReferences.iterator();
            while (it.hasNext()) {
                it.next().resolvePolicy();
            }
        }
    }

    private void searchAndRegisterIdReferenceElement(PolicySet policy) {
        PolicySet policySet = (PolicySet)policy;
        ArrayList<PolicySet> childPolicySets = new ArrayList<PolicySet>();

        XACMLElement[] allPolicies = policySet.getAllCrudeChildPolicies();
        for (XACMLElement elem : allPolicies) {
            if (elem instanceof IdReference) {
                idReferences.add((IdReference)elem);
            }
            if (elem instanceof PolicySet) {
                childPolicySets.add((PolicySet)elem);
            }
        }

        for (PolicySet item : childPolicySets) {
            searchAndRegisterIdReferenceElement(item);
        }
    }

    public AbstractPolicy[] getPolicies() {
        if (policies == null) {
            convertDataAdaptersToPolicies();
        }
        return policies;
    }
}