package an.xacml.adapter.file.context;

import static an.xacml.Constants.CONTEXT_SCHEMA_LOCATION;
import static an.xacml.Constants.CONTEXT_NAMESPACE;
import static an.xml.XMLParserWrapper.SCHEMA_LOCATION;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.context.Response;
import an.xacml.context.Result;
import an.xml.XMLElement;

public class FileAdapterResponse extends AbstractFileAdapterContextElement {
    /**
    <xs:element name="Response" type="xacml-context:ResponseType"/>
    <xs:complexType name="ResponseType">
        <xs:sequence>
            <xs:element ref="xacml-context:Result" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "Response";
    public FileAdapterResponse(Element elem) throws PolicySyntaxException {
        initialize(elem);

        XMLElement[] chResults = getXMLElementsByType(FileAdapterResult.class);
        Result[] results = new Result[chResults.length];
        for (int i = 0 ; i < chResults.length; i ++) {
            results[i] = (Result)((DataAdapter)chResults[i]).getEngineElement();
        }

        engineElem = new Response(results);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterResponse(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Response res = (Response)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        // Set the schema location
        xmlElement.setAttributeNS(W3C_XML_SCHEMA_INSTANCE_NS_URI, SCHEMA_LOCATION,
                CONTEXT_NAMESPACE + " " + CONTEXT_SCHEMA_LOCATION);

        Result[] results = res.getResults();
        for (int i = 0; i < results.length; i ++) {
            xmlElement.appendChild((Element)new FileAdapterResult(results[i]).getDataStoreObject());
        }
    }
}