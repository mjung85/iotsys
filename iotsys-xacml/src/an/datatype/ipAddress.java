package an.datatype;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static an.datatype.hostName.PORT_RANGE_PATTERN;
import sun.net.util.IPAddressUtil;

/**
 * IP address
 * The "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress" primitive type represents an IPv4 or IPv6 network address,
 * with optional mask and optional port or port range.  The syntax SHALL be:
 * 
 * ipAddress = address [ "/" mask ] [ ":" [ portrange ] ]
 * 
 * For an IPv4 address, the address and mask are formatted in accordance with the syntax for a "host" in IETF RFC 2396
 * "Uniform Resource Identifiers (URI): Generic Syntax", section 3.2.
 * For an IPv6 address, the address and mask are formatted in accordance with the syntax for an "ipv6reference" in IETF
 * RFC 2732 "Format for Literal IPv6 Addresses in URL's".  (Note that an IPv6 address or mask, in this syntax, is
 * enclosed in literal "[" "]" brackets.)
 */
public class ipAddress {
    public static final String IPADDRESS_PATTERN = "[^/:]+(/[^/:]+)?(:(" + PORT_RANGE_PATTERN + "))?";

    private InetAddress address;
    private InetAddress mask;
    private String portRange;
    private String strValue;
    private int hashCode;

    public ipAddress(String value) throws InvalidIPAddressException {
        parse(value);
        hashCode = toString().hashCode();
    }

    public InetAddress getAddress() {
        return address;
    }

    public InetAddress getMask() {
        return mask;
    }

    public String getPortRange() {
        return portRange;
    }

    public static ipAddress valueOf(String value) throws InvalidIPAddressException {
        return new ipAddress(value);
    }

    private void parse(String value) throws InvalidIPAddressException {
        if (value != null && value.matches(IPADDRESS_PATTERN)) {
            int slashPos = value.indexOf("/");
            String addr = null, msk = null;

            if (slashPos >= 0) {
                addr = value.substring(0, slashPos).trim();
                int newStart = slashPos + 1;
                int colonPos = value.indexOf(":", newStart);
                if (colonPos >= 0) {
                    msk = value.substring(newStart, colonPos).trim();
                    portRange = value.substring(colonPos + 1).trim();
                }
                else {
                    msk = value.substring(newStart).trim();
                }
            }
            else {
                int colonPos = value.indexOf(":");
                if (colonPos >= 0) {
                    addr = value.substring(0, colonPos).trim();
                    portRange = value.substring(colonPos + 1).trim();
                }
                else {
                    addr = value.trim();
                }
            }

            try {
                // IPv6 address with preceding "[" and trailing "]"
                if (addr.startsWith("[") && addr.endsWith("]") &&
                   (msk == null || (msk.startsWith("[") && msk.endsWith("]")))) {
                    // potential IPv6, remove the preceding and trailing "[" and "]"
                    addr = addr.substring(1, addr.length() - 1).trim();
                    msk = msk.substring(1, addr.length() - 1).trim();

                    byte[] addrsIPv6 = IPAddressUtil.textToNumericFormatV6(addr);
                    if (addrsIPv6 != null) {
                        if (msk != null) {
                            byte[] msksIPv6 = IPAddressUtil.textToNumericFormatV6(msk);
                            if (msksIPv6 != null) {
                                mask = InetAddress.getByAddress(msksIPv6);
                            }
                            else {
                                throw new InvalidIPAddressException("The mask address doesn't match the format of ip address.");
                            }
                        }
                        address = InetAddress.getByAddress(addrsIPv6);
                    }
                    else {
                        throw new InvalidIPAddressException("The address '" + addr + "' is not an valid IPv6 address.");
                    }
                }
                else {
                    // potential IPv4
                    // FIXME - XACML 2.0 says the ipv4 address may be a host name. I think should it a typo? if not, I
                    // will fix it if anyone use a hostname as address.
                    // make use of sun tool IPAddressUtil
                    byte[] addrsIPv4 = IPAddressUtil.textToNumericFormatV4(addr);
                    if (addrsIPv4 != null) {
                        if (msk != null) {
                            byte[] msksIPv4 = IPAddressUtil.textToNumericFormatV4(msk);
                            if (msksIPv4 != null) {
                                mask = InetAddress.getByAddress(msksIPv4);
                            }
                            else {
                                throw new InvalidIPAddressException("The mask address doesn't match the format of ip address.");
                            }
                        }
                        address = InetAddress.getByAddress(addrsIPv4);
                    }
                    else {
                        throw new InvalidIPAddressException("The address '" + addr + "' is not an valid IPv4 address.");
                    }
                }
            }
            catch (UnknownHostException ex) {
                throw new InvalidIPAddressException("Illegal ip address.", ex);
            }
        }
        else {
            throw new InvalidIPAddressException("The address '" + value + "' is not an valid ip address.");
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == this.getClass()) {
            return toString().equalsIgnoreCase(((ipAddress)o).toString());
        }
        return false;
    }

    public String toString() {
        if (strValue == null) {
            StringBuffer buf = new StringBuffer();
            buf.append(address.getHostAddress());
            if (mask != null) {
                buf.append("/");
                buf.append(mask.getHostAddress());
            }
            if (portRange != null) {
                buf.append(":");
                buf.append(portRange);
            }
            strValue = buf.toString();
        }
        return strValue;
    }

    public int hashCode() {
        return hashCode;
    }
}
