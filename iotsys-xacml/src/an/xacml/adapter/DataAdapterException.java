package an.xacml.adapter;

public class DataAdapterException extends Exception {
    private static final long serialVersionUID = -5980952682699380887L;

    public DataAdapterException(String message) {
        super(message);
    }

    public DataAdapterException(String message, Throwable t) {
        super(message, t);
    }
}