package an.xacml.adapter.file.policy;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import org.w3c.dom.Element;

import an.xacml.Matchable;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.DisjunctiveMatch;
import an.xml.XMLElement;

public abstract class FileAdapterTargetElements extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="Subjects" type="xacml:SubjectsType"/>
    <xs:complexType name="SubjectsType">
        <xs:sequence>
            <xs:element ref="xacml:Subject" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:element name="Resources" type="xacml:ResourcesType"/>
    <xs:complexType name="ResourcesType">
        <xs:sequence>
            <xs:element ref="xacml:Resource" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:element name="Actions" type="xacml:ActionsType"/>
    <xs:complexType name="ActionsType">
        <xs:sequence>
            <xs:element ref="xacml:Action" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:element name="Environments" type="xacml:EnvironmentsType"/>
    <xs:complexType name="EnvironmentsType">
        <xs:sequence>
            <xs:element ref="xacml:Environment" maxOccurs="unbounded"/>
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

        Matchable[] matches = ((DisjunctiveMatch)engineElem).getMatchables();
        for (int i = 0; i < matches.length; i ++) {
            // Retrieve the corresponding DataAdapter class, then create an instance
            Class<?> dataAdapterClz = getPolicyDataAdapterClassByXACMLElementType(matches[i].getClass());
            Constructor<?> daConstr = dataAdapterClz.getConstructor(XACMLElement.class);
            DataAdapter da = (DataAdapter)daConstr.newInstance(matches[i]);
            xmlElement.appendChild((Element)da.getDataStoreObject());
        }
    }
}