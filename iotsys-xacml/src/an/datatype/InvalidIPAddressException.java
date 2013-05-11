package an.datatype;

public class InvalidIPAddressException extends Exception {
    private static final long serialVersionUID = -7558978277276010475L;

    public InvalidIPAddressException(String message) {
        super(message);
    }

    public InvalidIPAddressException(String message, Throwable t) {
        super(message, t);
    }
}