package an.util;

public class InvalidCommandLineArgumentException extends Exception {
    private static final long serialVersionUID = -6108490511091402835L;

    public InvalidCommandLineArgumentException(String message) {
        super(message);
    }

    public InvalidCommandLineArgumentException(String message, Throwable t) {
        super(message, t);
    }
}