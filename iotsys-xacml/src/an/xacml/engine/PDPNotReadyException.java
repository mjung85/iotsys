package an.xacml.engine;

public class PDPNotReadyException extends Exception {
    private static final long serialVersionUID = -5741352755122865709L;

    public PDPNotReadyException(String message) {
        super(message);
    }

    public PDPNotReadyException(String message, Throwable t) {
        super(message, t);
    }
}