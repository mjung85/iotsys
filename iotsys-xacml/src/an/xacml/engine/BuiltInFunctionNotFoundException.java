package an.xacml.engine;

public class BuiltInFunctionNotFoundException extends Exception {
    private static final long serialVersionUID = -7217264936068447769L;

    public BuiltInFunctionNotFoundException(String message) {
        super(message);
    }

    public BuiltInFunctionNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}