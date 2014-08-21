package an.xacml;

/**
 * This is the root interface of all XACML elements.
 */
public interface XACMLElement {
    public static final String POLICY_TAG_EFFECT = "EffectType";
    public static final String POLICY_TAG_VERSION = "VersionType";
    public static final String POLICY_TAG_VERSIONMATCH = "VersionMatchType";
    public static final String XML_BASE64BINARY = "base64Binary";
    public static final String XML_HEXBINARY = "hexBinary";
    /**
     * Each engine element instance may have a name, but it is not required. One who want to use it should first set it.
     * @return
     */
    public String getElementName();
    public void setElementName(String elemName);
    /**
     * Return the corresponding element's element version
     * @return
     */
    public Version getElementVersion();

    /**
     * Return the root XACML element. The root element should be a Policy or PolicySet element.
     * @return
     */
    public XACMLElement getRootElement();

    /**
     * Return the direct parent element.
     * @return
     */
    public XACMLElement getParentElement();

    /**
     * Set the direct parent element.
     * @return
     */
    public void setParentElement(XACMLElement parent);
}