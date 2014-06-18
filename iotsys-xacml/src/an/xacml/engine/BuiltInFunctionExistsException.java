package an.xacml.engine;

public class BuiltInFunctionExistsException extends Exception {
    private static final long serialVersionUID = 1331906462441476187L;

    public BuiltInFunctionExistsException(String message) {
        super(message);
    }

    public BuiltInFunctionExistsException(String message, Throwable t) {
        super(message, t);
    }
}