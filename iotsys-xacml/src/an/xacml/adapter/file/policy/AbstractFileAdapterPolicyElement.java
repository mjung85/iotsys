package an.xacml.adapter.file.policy;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXMLType;
import an.xacml.adapter.DataAdapter;
import an.xacml.adapter.file.AbstractFileAdapterElement;

public abstract class AbstractFileAdapterPolicyElement extends AbstractFileAdapterElement implements DataAdapter {
    @Override
    protected Class<?> getElementClass(String elemType) {
    	Class<?> elemClz = getPolicyDataAdapterClassByXMLType(elemType);
        // If type is null, it should be a primitive XML type, we get the corresponding Java type from DataTypeRegistry.
        if (elemClz == null) {
        	getElementClassFromSystem(elemType);
        }
        return elemClz;
    }
}