package an.xml;

public class XMLDataTypeMappingException extends Exception {
    private static final long serialVersionUID = -8896763048339535895L;

    public XMLDataTypeMappingException(String message) {
        super(message);
    }

    public XMLDataTypeMappingException(Throwable cause) {
        super(cause);
    }

    public XMLDataTypeMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}