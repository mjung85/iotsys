package an.xacml.policy.function;

public class BuiltInFunctionNotImplementedException extends RuntimeException {
    private static final long serialVersionUID = 4437472267272661382L;

    public BuiltInFunctionNotImplementedException(String message) {
        super(message);
    }

    public BuiltInFunctionNotImplementedException(String message, Throwable t) {
        super(message, t);
    }
}