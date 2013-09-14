package an.xacml.adapter.file.policy;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;
import static an.xml.XMLParserWrapper.getNodeContentAsText;

import java.lang.reflect.Constructor;
import java.net.URI;

import org.w3c.dom.Element;

import an.log.LogFactory;
import an.log.Logger;
import an.xacml.Expression;
import an.xacml.IndeterminateException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.AttributeAssignment;

public class FileAdapterAttributeAssignment extends FileAdapterAttributeValue {
    /**
    <xs:element name="AttributeAssignment" type="xacml:AttributeAssignmentType"/>
    <xs:complexType name="AttributeAssignmentType" mixed="true">
        <xs:complexContent mixed="true">
            <xs:extension base="xacml:AttributeValueType">
                <xs:attribute name="AttributeId" type="xs:anyURI" use="required"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "AttributeAssignment";
    public static final String ATTR_ATTRIBUTEID = "AttributeId";
    public static final String ATTR_DATATYPE = "DataType";

    public FileAdapterAttributeAssignment(Element elem) throws Exception {
        this.elementName = elem.getLocalName();
        this.elementNamespaceURI = elem.getNamespaceURI();

        URI attrId = new URI(elem.getAttribute(ATTR_ATTRIBUTEID));
        URI dataType = new URI(elem.getAttribute(ATTR_DATATYPE));
        Expression childExp = retrieveChildExpression(elem);

        String attrValue = getNodeContentAsText(elem);
        engineElem = new AttributeAssignment(attrId, dataType, attrValue);
        engineElem.setElementName(elem.getLocalName());
        ((AttributeAssignment)engineElem).setChildExpression(childExp);
    }

    public FileAdapterAttributeAssignment(XACMLElement engineElem) throws Exception {
        Logger logger = LogFactory.getLogger();
        this.engineElem = engineElem;
        AttributeAssignment attrAssg = (AttributeAssignment)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_ATTRIBUTEID, attrAssg.getAttributeId().toString());
        xmlElement.setAttribute(ATTR_DATATYPE, attrAssg.getDataType().toString());
        try {
            // We don't append child expression, we are trying to append the evaluated result to AttributeAssignment.
            xmlElement.appendChild(getDefaultDocument().createTextNode(attrAssg.getValue().toString()));
        }
        // If there is error, we are going to append expression, not the evaluated result.
        catch (IndeterminateException intEx) {
            logger.warn("Error occurs while get value from AttributeAssignment.", intEx);
            Expression childExp = attrAssg.getChildExpression();
            if (childExp != null) {
                Class<?> dataAdapterClz = getPolicyDataAdapterClassByXACMLElementType(childExp.getClass());
                Constructor<?> daConstr = dataAdapterClz.getConstructor(XACMLElement.class);
                DataAdapter da = (DataAdapter)daConstr.newInstance(childExp);
                xmlElement.appendChild((Element)da.getDataStoreObject());
            }
        }
    }
}