package an.datatype;

public class InvalidRFC822NameException extends Exception {
    private static final long serialVersionUID = -6234817419546780795L;

    public InvalidRFC822NameException(String message) {
        super(message);
    }

    public InvalidRFC822NameException(String message, Throwable t) {
        super(message, t);
    }
}