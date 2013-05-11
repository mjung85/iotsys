package an.xacml.adapter.file.context;

import static an.xacml.Constants.CONTEXT_NAMESPACE;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.adapter.file.TextXMLElement;
import an.xacml.context.Status;
import an.xacml.context.StatusCode;
import an.xacml.context.StatusDetail;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterStatus extends AbstractFileAdapterContextElement {
    /**
    <xs:element name="Status" type="xacml-context:StatusType"/>
    <xs:complexType name="StatusType">
        <xs:sequence>
            <xs:element ref="xacml-context:StatusCode"/>
            <xs:element ref="xacml-context:StatusMessage" minOccurs="0"/>
            <xs:element ref="xacml-context:StatusDetail" minOccurs="0"/>
        </xs:sequence>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "Status";
    public static final String ELEMENT_STATUSMESSAGE = "StatusMessage";

    public FileAdapterStatus(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        XMLElement chStaCode = getSingleXMLElementByType(FileAdapterStatusCode.class);
        XMLElement chStaMsg = getSingleXMLElementByName(ELEMENT_STATUSMESSAGE);
        XMLElement chStaDetail = getSingleXMLElementByType(FileAdapterStatusDetail.class);

        StatusCode statusCode = chStaCode == null ? null : (StatusCode)((DataAdapter)chStaCode).getEngineElement();
        String msg = chStaMsg == null ? null : ((TextXMLElement)chStaMsg).getTextValue();
        StatusDetail detail = chStaDetail == null ? null : (StatusDetail)((DataAdapter)chStaDetail).getEngineElement();

        engineElem = new Status(statusCode);
        ((Status)engineElem).setStatusMessage(msg);
        ((Status)engineElem).setStatusDetail(detail);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterStatus(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Status status = (Status)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        xmlElement.appendChild((Element)new FileAdapterStatusCode(status.getStatusCode()).getDataStoreObject());
        if (status.getStatusMessage() != null) {
            Element elem = getDefaultDocument().createElementNS(CONTEXT_NAMESPACE, ELEMENT_STATUSMESSAGE);
            elem.appendChild(getDefaultDocument().createTextNode(status.getStatusMessage()));
            xmlElement.appendChild(elem);
        }
        if (status.getStatusDetail() != null) {
            xmlElement.appendChild((Element)new FileAdapterStatusDetail(status.getStatusDetail()).getDataStoreObject());
        }
    }
}