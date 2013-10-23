package an.xacml.adapter.file.context;

import java.net.URI;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.adapter.file.policy.FileAdapterAttributeValue;
import an.xacml.context.MissingAttributeDetail;
import an.xacml.policy.AttributeValue;

public class FileAdapterMissingAttributeDetail extends AbstractFileAdapterContextElement {
    /**
    <xs:element name="MissingAttributeDetail" type="xacml-context:MissingAttributeDetailType"/>
    <xs:complexType name="MissingAttributeDetailType">
        <xs:sequence>
            <xs:element ref="xacml-context:AttributeValue" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
        <xs:attribute name="AttributeId" type="xs:anyURI" use="required"/>
        <xs:attribute name="DataType" type="xs:anyURI" use="required"/>
        <xs:attribute name="Issuer" type="xs:string" use="optional"/>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "MissingAttributeDetail";
    public static final String ATTR_ATTRIBUTEID = "AttributeId";
    public static final String ATTR_DATATYPE = "DataType";
    public static final String ATTR_ISSUER = "Issuer";
    public static final String ELEMENT_MISSINGATTRIBUTEDETAIL = "MissingAttributeDetail";

    public FileAdapterMissingAttributeDetail(Element elem) throws Exception {
        this.elementName = elem.getLocalName();
        this.elementNamespaceURI = elem.getNamespaceURI();

        URI attrId = new URI(elem.getAttribute(ATTR_ATTRIBUTEID));
        URI dataType = new URI(elem.getAttribute(ATTR_DATATYPE));
        String issuer = elem.getAttribute(ATTR_ISSUER);

        // We don't support attribute value for now.
        /*
        NodeList attrValues = elem.getElementsByTagName(ELEMENT_MISSINGATTRIBUTEDETAIL);
        int len = attrValues.getLength();
        String[] values = new String[len];
        for (int i = 0; i < len; i ++) {
            values[i] = XMLParserWrapper.getNodeContentAsText(attrValues.item(i));
        }
        */

        engineElem = new MissingAttributeDetail(attrId, dataType, issuer, null);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterMissingAttributeDetail(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        MissingAttributeDetail msAttrDetail = (MissingAttributeDetail)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();

        String strAttrId;
        URI attrId = msAttrDetail.getAttributeId();
        if (attrId == null) {
            strAttrId = msAttrDetail.getRequestContentPath();
        }
        else {
            strAttrId = attrId.toString();
        }
        xmlElement.setAttribute(ATTR_ATTRIBUTEID, strAttrId);
        xmlElement.setAttribute(ATTR_DATATYPE, msAttrDetail.getDataType().toString());
        if (msAttrDetail.getIssuer() != null) {
            xmlElement.setAttribute(ATTR_ISSUER, msAttrDetail.getIssuer());
        }
        AttributeValue[] attrVals = msAttrDetail.getAcceptableAttributeValues();
        if (attrVals != null && attrVals.length > 0) {
            for (int i = 0; i < attrVals.length; i ++) {
                xmlElement.appendChild((Element)new FileAdapterAttributeValue(attrVals[i]).getDataStoreObject());
            }
        }
    }
}