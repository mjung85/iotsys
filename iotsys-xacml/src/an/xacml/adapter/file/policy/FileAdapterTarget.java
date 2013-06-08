package an.xacml.adapter.file.policy;

import org.w3c.dom.Element;

import an.xacml.PolicySyntaxException;
import an.xacml.XACMLElement;
import an.xacml.adapter.DataAdapter;
import an.xacml.policy.Actions;
import an.xacml.policy.Environments;
import an.xacml.policy.Resources;
import an.xacml.policy.Subjects;
import an.xacml.policy.Target;
import an.xml.XMLElement;
import an.xml.XMLGeneralException;

public class FileAdapterTarget extends AbstractFileAdapterPolicyElement {
    /**
    <xs:element name="Target" type="xacml:TargetType"/>
    <xs:complexType name="TargetType">
        <xs:sequence>
            <xs:element ref="xacml:Subjects" minOccurs="0"/>
            <xs:element ref="xacml:Resources" minOccurs="0"/>
            <xs:element ref="xacml:Actions" minOccurs="0"/>
            <xs:element ref="xacml:Environments" minOccurs="0"/>
        </xs:sequence>
    </xs:complexType>
     */
    public static final String ELEMENT_NAME = "Target";
    public FileAdapterTarget(Element elem) throws PolicySyntaxException, XMLGeneralException {
        initialize(elem);

        XMLElement chSubjs = getSingleXMLElementByType(FileAdapterSubjects.class);
        XMLElement chReses = getSingleXMLElementByType(FileAdapterResources.class);
        XMLElement chActs = getSingleXMLElementByType(FileAdapterActions.class);
        XMLElement chEnvs = getSingleXMLElementByType(FileAdapterEnvironments.class);

        Subjects subjs = chSubjs == null ? null : (Subjects)((DataAdapter)chSubjs).getEngineElement();
        Resources reses = chReses == null ? null : (Resources)((DataAdapter)chReses).getEngineElement();
        Actions acts = chActs == null ? null : (Actions)((DataAdapter)chActs).getEngineElement();
        Environments envs = chEnvs == null ? null : (Environments)((DataAdapter)chEnvs).getEngineElement();
        engineElem = new Target(subjs, reses, acts, envs);
        engineElem.setElementName(elem.getLocalName());
    }

    public FileAdapterTarget(XACMLElement engineElem) throws Exception {
        this.engineElem = engineElem;
        Target target = (Target)engineElem;

        if (this.engineElem.getElementName() == null) {
            this.engineElem.setElementName(ELEMENT_NAME);
        }
        xmlElement = createPolicyElement();
        Subjects subjs = target.getSubjects();
        if (subjs != null) {
            xmlElement.appendChild((Element)new FileAdapterSubjects(subjs).getDataStoreObject());
        }

        Resources reses = target.getResources();
        if (reses != null) {
            xmlElement.appendChild((Element)new FileAdapterResources(reses).getDataStoreObject());
        }

        Actions acts = target.getActions();
        if (acts != null) {
            xmlElement.appendChild((Element)new FileAdapterActions(acts).getDataStoreObject());
        }

        Environments envs = target.getEnvironments();
        if (envs != null) {
            xmlElement.appendChild((Element)new FileAdapterEnvironments(envs).getDataStoreObject());
        }
    }
}