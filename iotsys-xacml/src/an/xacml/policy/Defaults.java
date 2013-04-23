package an.xacml.policy;

import an.xacml.DefaultXACMLElement;
import java.net.URI;

public class Defaults extends DefaultXACMLElement {
    // the xpathVersion may be null.
    private URI xpathVersion;

    public Defaults(URI xpathVersion) {
        this.xpathVersion = xpathVersion;
    }

    public URI getXPathVersion() {
        return xpathVersion;
    }
}