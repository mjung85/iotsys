package an.xacml;

public class PolicySyntaxException extends Exception {
    private static final long serialVersionUID = 1782519751404811287L;

    public PolicySyntaxException(String msg) {
        super(msg);
    }

    public PolicySyntaxException(String msg, Throwable t) {
        super(msg, t);
    }
}