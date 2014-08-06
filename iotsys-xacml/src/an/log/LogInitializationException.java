package an.log;

public class LogInitializationException extends Exception {
    private static final long serialVersionUID = -5277034052627946636L;

    public LogInitializationException(String message) {
        super(message);
    }

    public LogInitializationException(String message, Throwable t) {
        super(message, t);
    }
}