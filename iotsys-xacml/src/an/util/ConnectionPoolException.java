package an.util;

public class ConnectionPoolException extends Exception {
    private static final long serialVersionUID = -4849006683213955054L;

    public ConnectionPoolException(String msg) {
        super(msg);
    }

    public ConnectionPoolException(String msg, Throwable t) {
        super(msg, t);
    }
}