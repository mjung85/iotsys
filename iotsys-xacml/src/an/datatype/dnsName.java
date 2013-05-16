package an.datatype;

import static an.datatype.hostName.PORT_RANGE_PATTERN;

/**
 * The "urn:oasis:names:tc:xacml:2.0:data-type:dnsName" primitive type represents a Domain Name Service (DNS) host name,
 * with optional port or port range.  The syntax SHALL be:
 *   
 *   dnsName = hostname [ ":" portrange ]
 * 
 * The hostname is formatted in accordance with IETF RFC 2396 "Uniform Resource Identifiers (URI): Generic Syntax",
 * section 3.2, except that a wildcard "*" may be used in the left-most component of the hostname to indicate
 * "any subdomain" under the domain specified to its right.
 */
public class dnsName {
    public static final String DNS_PATTERN = "[^:]+(:(" + PORT_RANGE_PATTERN + "))?";

    private hostName host;
    private String portRange;
    private String strValue;
    private int hashCode;

    public dnsName(String value) throws InvalidHostnameException, InvalidDNSNameException {
        if (value != null && value.matches(DNS_PATTERN)) {
            int colonPos = value.indexOf(":");
            if (colonPos >= 0) {
                host = new hostName(value.substring(0, colonPos));
                portRange = value.substring(colonPos + 1);
            }
            else {
                host = new hostName(value);
            }
            strValue = host.toString() + (portRange == null ? "" : (":" + portRange));
            hashCode = strValue.hashCode();
        }
        else {
            throw new InvalidDNSNameException("The value '" + value + "' is not an valid DNS name.");
        }
    }

    public static dnsName valueOf(String value) throws InvalidHostnameException, InvalidDNSNameException {
        return new dnsName(value);
    }

    public hostName getHostName() {
        return host;
    }

    public String getPortRange() {
        return portRange;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == this.getClass()) {
            return toString().equalsIgnoreCase(((dnsName)o).toString());
        }
        return false;
    }

    public String toString() {
        return strValue;
    }

    public int hashCode() {
        return hashCode;
    }
}