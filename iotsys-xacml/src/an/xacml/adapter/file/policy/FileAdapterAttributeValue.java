package an.xacml.adapter.file.policy;

import static an.xacml.adapter.file.XMLFileDataAdapterRegistry.getPolicyDataAdapterClassByXACMLElementType;

import java.lang.reflect.Constructor;
import java.net.URI;

import org.w3c.dom.Element;

import an.log.LogFactory;
import an.log.Logger;
import an.xacml.Expression;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.AttributeValue;
import an.xml.XMLElement;
import an.xml.XMLParserWrapper;

public class FileAdapterAttributeValue extends FileAdapterExpression {
    /**
    <xs:element name="AttributeValue" type="xacml:AttributeValueType" substitutionGroup="xacml:Expression"/>
    <xs:complexType name="AttributeValueType" mixed="true">
        <xs:complexContent mixed="true">
            <xs:extension base="xacml:ExpressionType">
                <xs:sequence>
                    <xs:any namespace="##any" processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
                </xs:sequence>
                <xs:attribute name="DataType" type="xs:anyURI" use="required"/>
                <xs:anyAttribute namespace="##any" processContents="lax"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "AttributeValue";
    public static final String ATTR_DATATYPE = "DataType";

    protected FileAdapterAttributeValue() {}

    public FileAdapterAttributeValue(Element elem) throws Exception {
        elementNamespaceURI = elem.getNamespaceURI();
        elementName = elem.getLocalName();

        URI dataType = new URI(elem.getAttribute(ATTR_DATATYPE));
        Expression childExp = retrieveChildExpression(elem);

        String attrValue = XMLParserWrapper.getNodeContentAsText(elem);
        engineElem = AttributeValue.getInstance(dataType, attrValue);
        engineElem.setElementName(elem.getLocalName());
        ((AttributeValue)engineElem).setChildExpression(childExp);
    }

    protected Expression retrieveChildExpression(Element elem) {
        // Trying to get child expression.
        try {
            addChildElements(extractChildXMLElements(elem));
            addAttributes(extractXMLAttributes(elem));

            XMLElement[] childElems = getChildElements();
            if (childElems.length == 1) {
                XACMLElement childEngineElem = ((DataAdapter)childElems[0]).getEngineElement();
                if (childEngineElem instanceof Expression) {
                    return (Expression)childEngineElem;
                }
            }
        }
        catch (Exception ex) {
            Logger logger = LogFactory.getLogger();
            logger.error("Error occurs while retrieving child expression from '" + elementName + "'", ex);
        }

        return null;
    }

    public FileAdapterAttributeValue(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        AttributeValue attrVal = (AttributeValue)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        xmlElement.setAttribute(ATTR_DATATYPE, attrVal.getDataType().toString());
        Expression childExp = attrVal.getChildExpression();
        if (childExp != null) {
            Class<?> dataAdapterClz = getPolicyDataAdapterClassByXACMLElementType(childExp.getClass());
            Constructor<?> daConstr = dataAdapterClz.getConstructor(XACMLElement.class);
            DataAdapter da = (DataAdapter)daConstr.newInstance(childExp);
            xmlElement.appendChild((Element)da.getDataStoreObject());
        }
        else {
            xmlElement.appendChild(getDefaultDocument().createTextNode(attrVal.getValue().toString()));
        }
    }
}