package an.xacml.engine;

public class CacheableExpiredException extends Exception {
    private static final long serialVersionUID = -2438650418697943063L;

    public CacheableExpiredException(String message) {
        super(message);
    }

    public CacheableExpiredException(String message, Throwable t) {
        super(message, t);
    }
}