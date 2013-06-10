package an.xacml.adapter.file.policy;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;

import java.lang.reflect.Constructor;
import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.Expression;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.AttributeValue;
import an.xacml.policy.DefaultMatch;
import an.xml.XMLElement;

public abstract class FileAdapterTargetElementMatch extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="ActionMatch" type="xacml:ActionMatchType"/>
    <xs:complexType name="ActionMatchType">
        <xs:sequence>
            <xs:element ref="xacml:AttributeValue"/>
            <xs:choice>
                <xs:element ref="xacml:ActionAttributeDesignator"/>
                <xs:element ref="xacml:AttributeSelector"/>
            </xs:choice>
        </xs:sequence>
        <xs:attribute name="MatchId" type="xs:anyURI" use="required"/>
    </xs:complexType>

    <xs:element name="EnvironmentMatch" type="xacml:EnvironmentMatchType"/>
    <xs:complexType name="EnvironmentMatchType">
        <xs:sequence>
            <xs:element ref="xacml:AttributeValue"/>
            <xs:choice>
                <xs:element ref="xacml:EnvironmentAttributeDesignator"/>
                <xs:element ref="xacml:AttributeSelector"/>
            </xs:choice>
        </xs:sequence>
        <xs:attribute name="MatchId" type="xs:anyURI" use="required"/>
    </xs:complexType>

    <xs:element name="ResourceMatch" type="xacml:ResourceMatchType"/>
    <xs:complexType name="ResourceMatchType">
        <xs:sequence>
            <xs:element ref="xacml:AttributeValue"/>
            <xs:choice>
                <xs:element ref="xacml:ResourceAttributeDesignator"/>
                <xs:element ref="xacml:AttributeSelector"/>
            </xs:choice>
        </xs:sequence>
        <xs:attribute name="MatchId" type="xs:anyURI" use="required"/>
    </xs:complexType>

    <xs:element name="SubjectMatch" type="xacml:SubjectMatchType"/>
    <xs:complexType name="SubjectMatchType">
        <xs:sequence>
            <xs:element ref="xacml:AttributeValue"/>
            <xs:choice>
                <xs:element ref="xacml:SubjectAttributeDesignator"/>
                <xs:element ref="xacml:AttributeSelector"/>
            </xs:choice>
        </xs:sequence>
        <xs:attribute name="MatchId" type="xs:anyURI" use="required"/>
    </xs:complexType>
     */
    public static final String ATTR_MATCHID = "MatchId";

    protected void initializeTargetElement(Element elem, Class<?> engineClass) throws Exception {
        initialize(elem);

        URI matchId = (URI)getAttributeValueByName(ATTR_MATCHID);
        XMLElement attrValue = getSingleXMLElementByType(FileAdapterAttributeValue.class);
        XMLElement designatorOrSelector = getSingleXMLElementByType(FileAdapterAttributeDesignator.class);
        if (designatorOrSelector == null) {
            designatorOrSelector = getSingleXMLElementByType(FileAdapterAttributeSelector.class);
        }

        Constructor<?> matchCons = engineClass.getConstructor(
                new Class[] {URI.class, AttributeValue.class, Expression.class});
        engineElem = (XACMLElement)matchCons.newInstance(matchId, 
                ((DataAdapter)attrValue).getEngineElement(), ((DataAdapter)designatorOrSelector).getEngineElement());
        engineElem.setElementName(elem.getLocalName());
    }


    protected void initializeTargetElement(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        DefaultMatch match = (DefaultMatch)engineElem;

        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_MATCHID, match.getMatchID().toString());
        xmlElement.appendChild((Element)new FileAdapterAttributeValue(match.getAttributeValue()).getDataStoreObject());

        Expression exp = match.getAttributeDesignatorOrSelector();
        // Retrieve the corresponding DataAdapter class, then create an instance
        Class<?> dataAdapterClz = getPolicyDataAdapterClassByXACMLElementType(exp.getClass());
        Constructor<?> daConstr = dataAdapterClz.getConstructor(XACMLElement.class);
        DataAdapter da = (DataAdapter)daConstr.newInstance(exp);
        xmlElement.appendChild((Element)da.getDataStoreObject());
    }
}