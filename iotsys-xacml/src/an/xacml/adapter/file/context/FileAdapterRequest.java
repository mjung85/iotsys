package an.xacml.adapter.file.context;

import static an.xacml.Constants.CONTEXT_SCHEMA_LOCATION;
import static an.xacml.Constants.CONTEXT_NAMESPACE;
import static an.xml.XMLParserWrapper.SCHEMA_LOCATION;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.context.Action;
import an.xacml.context.Environment;
import an.xacml.context.Request;
import an.xacml.context.Resource;
import an.xacml.context.Subject;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterRequest extends AbstractFileAdapterContextElement {
    /**
    <xs:element name="Request" type="xacml-context:RequestType"/>
    <xs:complexType name="RequestType">
        <xs:sequence>
            <xs:element ref="xacml-context:Subject" maxOccurs="unbounded"/>
            <xs:element ref="xacml-context:Resource" maxOccurs="unbounded"/>
            <xs:element ref="xacml-context:Action"/>
            <xs:element ref="xacml-context:Environment"/>
        </xs:sequence>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "Request";
    public FileAdapterRequest(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        XMLElement[] chSubjs = getXMLElementsByType(FileAdapterSubject.class);
        Subject[] subjs = new Subject[chSubjs.length];
        for (int i = 0; i < chSubjs.length; i ++) {
            subjs[i] = (Subject)((DataAdapter)chSubjs[i]).getEngineElement();
        }

        XMLElement[] chReses = getXMLElementsByType(FileAdapterResource.class);
        Resource[] reses = new Resource[chReses.length];
        for (int i = 0; i < chReses.length; i ++) {
            reses[i] = (Resource)((DataAdapter)chReses[i]).getEngineElement();
        }

        XMLElement chAct = getSingleXMLElementByType(FileAdapterAction.class);
        XMLElement chEnv = getSingleXMLElementByType(FileAdapterEnvironment.class);
        engineElem = new Request(elem, subjs, reses,
                (Action)((DataAdapter)chAct).getEngineElement(),
                (Environment)((DataAdapter)chEnv).getEngineElement());
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterRequest(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Request req = (Request)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        // Set the schema location
        xmlElement.setAttributeNS(W3C_XML_SCHEMA_INSTANCE_NS_URI, SCHEMA_LOCATION,
                CONTEXT_NAMESPACE + " " + CONTEXT_SCHEMA_LOCATION);

        Subject[] subjs = req.getSubjects();
        // There should must have at least 1 subject
        for (int i = 0; i < subjs.length; i ++) {
            xmlElement.appendChild((Element)new FileAdapterSubject(subjs[i]).getDataStoreObject());
        }
        Resource[] reses = req.getResources();
        // There should must have at least 1 resource
        for (int i = 0; i < reses.length; i ++) {
            xmlElement.appendChild((Element)new FileAdapterResource(reses[i]).getDataStoreObject());
        }
        xmlElement.appendChild((Element)new FileAdapterAction(req.getAction()).getDataStoreObject());
        xmlElement.appendChild((Element)new FileAdapterEnvironment(req.getEnvironment()).getDataStoreObject());
    }
}