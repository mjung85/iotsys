package an.datatype;

public class InvalidHexBinaryException extends Exception {
    private static final long serialVersionUID = -3402921696886886464L;

    public InvalidHexBinaryException(String message) {
        super(message);
    }

    public InvalidHexBinaryException(String message, Throwable t) {
        super(message, t);
    }
}