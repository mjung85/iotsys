package an.xacml;

import static an.xacml.Constants.POLICY_NAMESPACE;
import static an.xml.XMLDataTypeRegistry.register;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import javax.naming.ldap.LdapName;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;

import an.datatype.base64Binary;
import an.datatype.dnsName;
import an.datatype.hexBinary;
import an.datatype.ipAddress;
import an.datatype.rfc822Name;
import an.xacml.policy.Effect;
import an.xacml.policy.Version;
import an.xacml.policy.VersionMatch;

/**
 * Provide a default getVersion implementation.
 */
public abstract class DefaultXACMLElement implements XACMLElement {
    // Default version is XACML 2.0
    private an.xacml.Version defaultVersion = new an.xacml.Version();
    private XACMLElement root;
    private XACMLElement parent;

    protected String elementName;

    // register required datatypes
    static {
        register(new QName(POLICY_NAMESPACE, POLICY_TAG_EFFECT), Effect.class);
        register(new QName(POLICY_NAMESPACE, POLICY_TAG_VERSION), Version.class);
        register(new QName(POLICY_NAMESPACE, POLICY_TAG_VERSIONMATCH), VersionMatch.class);
        register(new QName(W3C_XML_SCHEMA_NS_URI, XML_BASE64BINARY), base64Binary.class);
        register(new QName(W3C_XML_SCHEMA_NS_URI, XML_HEXBINARY), hexBinary.class);
        register(new QName(Constants.TYPE_DNSNAME.toString()), dnsName.class);
        register(new QName(Constants.TYPE_IPADDRESS.toString()), ipAddress.class);
        register(new QName(Constants.TYPE_RFC822NAME.toString()), rfc822Name.class);
        register(new QName(Constants.TYPE_X500NAME.toString()), LdapName.class);
        // Below data types are added in errata published at 29 Jan, 2008. To keep compatibility with the core standard
        // (especially for conformance test), we will support both these types and original data types.
        register(new QName(Constants.TYPE_DAYTIMEDURATION.toString()), Duration.class);
        register(new QName(Constants.TYPE_YEARMONTHDURATION.toString()), Duration.class);
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elemName) {
        this.elementName = elemName;
    }

    public an.xacml.Version getElementVersion() {
        return defaultVersion;
    }

    /**
     * Return the root XACML element. The root element should be RequestElement(for context elements) or 
     * Policy/PolicySet element(for policy elements).
     * 
     * The root element should be get by iterate on parent element.
     */
    public XACMLElement getRootElement() {
        if (root == null) {
            XACMLElement current = this;
            while (current != null) {
                root = current;
                current = root.getParentElement();
            }
        }
        return root;
    }

    /**
     * Return the direct parent XACML element.
     * The parent element should be passed from element's constructor.
     */
    public XACMLElement getParentElement() {
        return parent;
    }

    public void setParentElement(XACMLElement parent) {
        this.parent = parent;
    }
/*
    public synchronized void setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        attributes.put(key, value);
    }

    public synchronized Object getAttribute(String key) {
        if (attributes != null) {
            return attributes.get(key);
        }
        return null;
    }
*/
    public static boolean compareObject(Object a, Object b) {
        if (a == b) {
            return true;
        }
        else if (a != null) {
            return a.equals(b);
        }
        else if (b != null) {
            return b.equals(a);
        }
        return false;
    }
}