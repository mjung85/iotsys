package an.xacml.adapter.file.context;

import static an.xacml.Constants.CONTEXT_NAMESPACE;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.adapter.file.TextXMLElement;
import an.xacml.adapter.file.policy.FileAdapterObligations;
import an.xacml.context.Decision;
import an.xacml.context.Result;
import an.xacml.context.Status;
import an.xacml.policy.Obligations;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterResult extends AbstractFileAdapterContextElement {
    /**
    <xs:element name="Result" type="xacml-context:ResultType"/>
    <xs:complexType name="ResultType">
        <xs:sequence>
            <xs:element ref="xacml-context:Decision"/>
            <xs:element ref="xacml-context:Status" minOccurs="0"/>
            <xs:element ref="xacml:Obligations" minOccurs="0"/>
        </xs:sequence>
        <xs:attribute name="ResourceId" type="xs:string" use="optional"/>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "Result";
    public static final String ATTR_RESOURCEID = "ResourceId";
    public static final String ELEMENT_DECISION = "Decision";

    public FileAdapterResult(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        String resId = (String)getAttributeValueByName(ATTR_RESOURCEID);
        XMLElement chDesicion = getSingleXMLElementByName(ELEMENT_DECISION);
        XMLElement chStatus = getSingleXMLElementByType(FileAdapterStatus.class);
        XMLElement chObls = getSingleXMLElementByType(FileAdapterObligations.class);

        Decision decision = Decision.valueOf(((TextXMLElement)chDesicion).getTextValue());
        Status status = chStatus == null ? null : (Status)((DataAdapter)chStatus).getEngineElement();
        Obligations obls = chObls == null ? null : (Obligations)((DataAdapter)chObls).getEngineElement();

        engineElem = new Result(decision, status, obls, resId);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterResult(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Result result = (Result)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        if (result.getResourceId() != null) {
            xmlElement.setAttribute(ATTR_RESOURCEID, result.getResourceId());
        }

        Element decision = getDefaultDocument().createElementNS(CONTEXT_NAMESPACE, ELEMENT_DECISION);
        decision.appendChild(getDefaultDocument().createTextNode(result.getDecision().toString()));
        xmlElement.appendChild(decision);

        if (result.getStatus() != null) {
            xmlElement.appendChild((Element)new FileAdapterStatus(result.getStatus()).getDataStoreObject());
        }
        if (result.getObligations() != null) {
            xmlElement.appendChild((Element)new FileAdapterObligations(result.getObligations()).getDataStoreObject());
        }
    }
}