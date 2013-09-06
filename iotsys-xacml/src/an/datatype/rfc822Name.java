package an.datatype;

/**
 * The "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name" primitive type represents an electronic mail address.
 * The valid syntax for such a name is described in IETF RFC 2821, Section 4.1.2, Command Argument Syntax, under the
 * term "Mailbox".
 */
public class rfc822Name {
    private String localPart;
    private String domain;
    private String address;
    private int hashCode;

    public rfc822Name(String value) throws InvalidRFC822NameException {
        if (value != null && value.length() > 0) {
            String[] parts = value.split("@");
            if (parts.length == 2) {
                localPart = parts[0];
                domain = parts[1].toLowerCase();
                address = localPart + "@" + domain;
                hashCode = address.hashCode();
                return;
            }
        }
        throw new InvalidRFC822NameException("The value '" + value + "' is not an valid rfc822 name.");
    }

    public String getLocalPart() {
        return localPart;
    }

    public String getDomain() {
        return domain;
    }

    public String toString() {
        return address;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == this.getClass()) {
            return localPart.equals(((rfc822Name)o).getLocalPart()) &&
                    domain.equalsIgnoreCase(((rfc822Name)o).getDomain());
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }

    public static rfc822Name valueOf(String value) throws InvalidRFC822NameException {
        return new rfc822Name(value);
    }
}