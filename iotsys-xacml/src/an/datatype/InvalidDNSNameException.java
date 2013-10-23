package an.datatype;

public class InvalidDNSNameException extends Exception {
    private static final long serialVersionUID = -4664328739174995268L;

    public InvalidDNSNameException(String message) {
        super(message);
    }

    public InvalidDNSNameException(String message, Throwable t) {
        super(message, t);
    }
}