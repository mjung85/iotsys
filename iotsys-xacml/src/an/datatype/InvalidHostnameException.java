package an.datatype;

public class InvalidHostnameException extends Exception {
    private static final long serialVersionUID = 3600812310928685082L;

    public InvalidHostnameException(String message) {
        super(message);
    }

    public InvalidHostnameException(String message, Throwable t) {
        super(message, t);
    }
}