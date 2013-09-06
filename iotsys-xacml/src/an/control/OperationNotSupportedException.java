package an.control;

public class OperationNotSupportedException extends RuntimeException {
    private static final long serialVersionUID = -1292868081726280345L;

    public OperationNotSupportedException(String message) {
        super(message);
    }
}
