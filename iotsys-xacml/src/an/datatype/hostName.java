package an.datatype;

public class hostName {
    /**
     * hostname      = *( domainlabel "." ) toplabel [ "." ]
     * domainlabel   = alphanum | alphanum *( alphanum | "-" ) alphanum
     * toplabel      = alpha | alpha *( alphanum | "-" ) alphanum
     * 
     * Hostnames take the form described in Section 3 of [RFC1034] and Section 2.1 of [RFC1123]: a sequence of domain
     * labels separated by ".", each domain label starting and ending with an alphanumeric character and possibly also
     * containing "-" characters.  The rightmost domain label of a fully qualified domain name will never start with a
     * digit, thus syntactically distinguishing domain names from IPv4 addresses, and may be followed by a single "."
     * if it is necessary to distinguish between the complete domain name and any local domain.  To actually be
     * "Uniform" as a resource locator, a URL hostname should be a fully qualified domain name.  In practice, however,
     * the host component may be a local domain literal.
     */
    public static final String HOSTNAME_PATTERN =
        "(([a-zA-Z0-9]|([a-zA-Z0-9]([a-zA-Z0-9\\-])*[a-zA-Z0-9]))\\.)*([a-zA-Z]|[a-zA-Z]([a-zA-Z0-9\\-])*[a-zA-Z0-9])\\.?";
    public static final String PORT_RANGE_PATTERN = "(\\d+)|(-\\d+)|((\\d+)-(\\d+)?)";
    private String host;
    private String[] parts;
    private int hashCode;

    public hostName(String value) throws InvalidHostnameException {
        if (value != null && value.matches(HOSTNAME_PATTERN)) {
            host = value;
            if (value.endsWith(".")) {
                value = value.substring(0, value.length() - 1);
            }
            parts = value.split("\\.");
            hashCode = host.hashCode();
        }
        else {
            throw new InvalidHostnameException("The value '" + value + "' is not an valid hostname.");
        }
    }

    public String getTopDomain() {
        return parts[parts.length - 1];
    }

    public String getMostSubDomain() {
        return parts[0];
    }

    public String toString() {
        return host;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == this.getClass()) {
            return toString().equalsIgnoreCase(((hostName)o).toString());
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }
}