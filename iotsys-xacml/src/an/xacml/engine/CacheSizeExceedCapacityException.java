package an.xacml.engine;

public class CacheSizeExceedCapacityException extends Exception {
    private static final long serialVersionUID = -3769339105453117759L;

    public CacheSizeExceedCapacityException(String message) {
        super(message);
    }

    public CacheSizeExceedCapacityException(String message, Throwable t) {
        super(message, t);
    }
}