package an.xacml.adapter.file.policy;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import org.w3c.dom.Element;

import an.xacml.Matchable;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.ConjunctiveMatch;
import an.xml.XMLElement;

public abstract class FileAdapterTargetElement extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="Subject" type="xacml:SubjectType"/>
    <xs:complexType name="SubjectType">
        <xs:sequence>
            <xs:element ref="xacml:SubjectMatch" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:element name="Resource" type="xacml:ResourceType"/>
    <xs:complexType name="ResourceType">
        <xs:sequence>
            <xs:element ref="xacml:ResourceMatch" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:element name="Action" type="xacml:ActionType"/>
    <xs:complexType name="ActionType">
        <xs:sequence>
            <xs:element ref="xacml:ActionMatch" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:element name="Environment" type="xacml:EnvironmentType"/>
    <xs:complexType name="EnvironmentType">
        <xs:sequence>
            <xs:element ref="xacml:EnvironmentMatch" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>
     */
    protected void initializeTargetElement(Element elem, Class<?> engineClass) throws Exception {
        initialize(elem);

        XMLElement[] childElems = getChildElements();

        // Should at least one child
        Class<?> childClass = ((DataAdapter)childElems[0]).getEngineElement().getClass();
        Object childXACMLArray = Array.newInstance(childClass, childElems.length);

        for (int i = 0; i < childElems.length; i ++) {
            Array.set(childXACMLArray, i, ((DataAdapter)childElems[i]).getEngineElement());
        }
        Constructor<?> cons = engineClass.getConstructor(childXACMLArray.getClass());
        engineElem = (XACMLElement)cons.newInstance(childXACMLArray);
        engineElem.setElementName(elem.getLocalName());
    }

    protected void initializeTargetElement(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        xmlElement = createPolicyElement();

        Matchable[] matches = ((ConjunctiveMatch)engineElem).getMatchables();
        for (int i = 0; i < matches.length; i ++) {
            // Retrieve the corresponding DataAdapter class, then create an instance
            Class<?> dataAdapterClz = getPolicyDataAdapterClassByXACMLElementType(matches[i].getClass());
            Constructor<?> daConstr = dataAdapterClz.getConstructor(XACMLElement.class);
            DataAdapter da = (DataAdapter)daConstr.newInstance(matches[i]);
            xmlElement.appendChild((Element)da.getDataStoreObject());
        }
    }
}