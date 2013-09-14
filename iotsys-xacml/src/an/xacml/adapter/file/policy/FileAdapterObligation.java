package an.xacml.adapter.file.policy;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.AttributeAssignment;
import an.xacml.policy.Effect;
import an.xacml.policy.Obligation;
import an.xml.XMLElement;

public class FileAdapterObligation extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="Obligation" type="xacml:ObligationType"/>
    <xs:complexType name="ObligationType">
        <xs:sequence>
            <xs:element ref="xacml:AttributeAssignment" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
        <xs:attribute name="ObligationId" type="xs:anyURI" use="required"/>
        <xs:attribute name="FulfillOn" type="xacml:EffectType" use="required"/>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "Obligation";
    public static final String ATTR_OBLIGATIONID = "ObligationId";
    public static final String ATTR_FULFILLON = "FulfillOn";

    public FileAdapterObligation(Element elem) throws PolicySyntaxException {
        initialize(elem);

        URI oId = (URI)getAttributeValueByName(ATTR_OBLIGATIONID);
        Effect effect = (Effect)getAttributeValueByName(ATTR_FULFILLON);
        XMLElement[] children = getChildElements();
        AttributeAssignment[] attrAssignments = new AttributeAssignment[children.length];
        for (int i = 0; i < children.length; i ++) {
            attrAssignments[i] = (AttributeAssignment)((DataAdapter)children[i]).getEngineElement();
        }
        engineElem = new Obligation(oId, effect, attrAssignments);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterObligation(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Obligation obl = (Obligation)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_OBLIGATIONID, obl.getObligationId().toString());
        xmlElement.setAttribute(ATTR_FULFILLON, obl.getFulfillOnEffect().toString());
        AttributeAssignment[] attrAssgs = obl.getAttributeAssignments();
        for (int i = 0; i < attrAssgs.length; i ++) {
            xmlElement.appendChild((Element)new FileAdapterAttributeAssignment(attrAssgs[i]).getDataStoreObject());
        }
    }
}