package an.xml;

public class XMLGeneralException extends Exception {
    private static final long serialVersionUID = -4807278062065045942L;

    public XMLGeneralException(String message) {
        super(message);
    }

    public XMLGeneralException(String message, Throwable t) {
        super(message, t);
    }
}