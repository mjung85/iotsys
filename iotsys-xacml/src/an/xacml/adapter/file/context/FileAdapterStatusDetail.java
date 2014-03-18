package an.xacml.adapter.file.context;

import static an.xacml.Constants.CONTEXT_NAMESPACE;

import java.lang.reflect.Array;

import org.w3c.dom.Element;

import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.context.MissingAttributeDetail;
import an.xacml.context.StatusDetail;
import an.xml.XMLElement;
import an.xml.XMLParserWrapper;

public class FileAdapterStatusDetail extends AbstractFileAdapterContextElement {
    /**
    <xs:element name="StatusDetail" type="xacml-context:StatusDetailType"/>
    <xs:complexType name="StatusDetailType">
        <xs:sequence>
            <xs:any namespace="##any" processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "StatusDetail";
    public FileAdapterStatusDetail(Element elem) throws Exception {
        initialize(elem);

        // First determine if this element is MissingAttributeDetail, otherwise, we'll convert it to plain text
        Object detail = null;
        XMLElement[] chMissings = getXMLElementsByType(FileAdapterMissingAttributeDetail.class);
        if (chMissings != null && chMissings.length > 0) {
            MissingAttributeDetail[] missings = new MissingAttributeDetail[chMissings.length];
            for (int i = 0; i < chMissings.length; i ++) {
                missings[i] = (MissingAttributeDetail)((DataAdapter)chMissings[i]).getEngineElement();
            }
            detail = missings;
        }
        else {
            detail = XMLParserWrapper.getNodeContentAsText(elem);
            if (detail == null || ((String)detail).length() == 0) {
                detail = null;
            }
        }

        engineElem = new StatusDetail(detail);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterStatusDetail(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        StatusDetail detail = (StatusDetail)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        Object any = detail.getDetail();
        if (any != null && any.toString().length() > 0) {
            if (any.getClass().isArray()) {
                int size = Array.getLength(any);
                for (int i = 0; i < size; i ++) {
                    Object component = Array.get(any, i);
                    // First determine if this is a MissingAttributeDetail, if no, we'll convert it to plain text
                    Element elem = null;
                    if (component instanceof MissingAttributeDetail) {
                        elem = (Element)new FileAdapterMissingAttributeDetail((MissingAttributeDetail)component).getDataStoreObject();
                    }
                    else {
                        elem = getDefaultDocument().createElementNS(
                                CONTEXT_NAMESPACE, component.getClass().getSimpleName());
                        elem.appendChild(getDefaultDocument().createTextNode(component.toString()));
                    }
                    xmlElement.appendChild(elem);
                }
            }
            else {
                // First determine if this is a MissingAttributeDetail, if no, we'll convert it to plain text
                Element elem = null;
                if (any instanceof MissingAttributeDetail) {
                    elem = (Element)new FileAdapterMissingAttributeDetail((MissingAttributeDetail)any).getDataStoreObject();
                }
                else {
                    elem = getDefaultDocument().createElementNS(
                            CONTEXT_NAMESPACE, any.getClass().getSimpleName());
                    elem.appendChild(getDefaultDocument().createTextNode(any.toString()));
                }
                xmlElement.appendChild(elem);
            }
        }
    }
}