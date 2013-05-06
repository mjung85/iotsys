package an.xacml.engine;

public class PDPInitializeException extends Exception {
    private static final long serialVersionUID = 3000641797363636826L;

    public PDPInitializeException(String message) {
        super(message);
    }

    public PDPInitializeException(String message, Throwable t) {
        super(message, t);
    }
}