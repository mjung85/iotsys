package an.xacml.adapter.file.context;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.context.Environment;
import an.xacml.context.TargetElement;

public class FileAdapterEnvironment extends FileAdapterTargetElement {
    /**
    <xs:element name="Environment" type="xacml-context:EnvironmentType"/>
    <xs:complexType name="EnvironmentType">
        <xs:sequence>
            <xs:element ref="xacml-context:Attribute" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>
     */
	public static final String ELEMENT_NAME = "Environment";
    public FileAdapterEnvironment(Element elem) throws PolicySyntaxException {
        initialize(elem);
        engineElem = new Environment(extractAttributes());
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterEnvironment(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createContextElement();
        populateAttributes((TargetElement)engineElem);
    }
}