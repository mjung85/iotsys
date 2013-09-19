package an.util;

public class MissingRequiredArgumentsException extends Exception {
    private static final long serialVersionUID = 9082251489329911784L;

    public MissingRequiredArgumentsException(String message) {
        super(message);
    }

    public MissingRequiredArgumentsException(String message, Throwable t) {
        super(message, t);
    }
}