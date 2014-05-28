package an.control;

public class OperationFailedException extends Exception {
    private static final long serialVersionUID = -5903130817383667362L;

    public OperationFailedException(String message) {
        super(message);
    }

    public OperationFailedException(String message, Throwable t) {
        super(message, t);
    }
}