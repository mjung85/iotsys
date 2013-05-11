package an.xacml.adapter.file;

public class PolicyNotFoundException extends Exception {
    private static final long serialVersionUID = 5833716222226384767L;

    public PolicyNotFoundException(String message) {
        super(message);
    }

    public PolicyNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}