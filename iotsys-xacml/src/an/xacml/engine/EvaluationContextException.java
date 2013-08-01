package an.xacml.engine;

public class EvaluationContextException extends Exception {
    private static final long serialVersionUID = 1461842829958836690L;

    public EvaluationContextException(String message) {
        super(message);
    }

    public EvaluationContextException(String message, Throwable t) {
        super(message, t);
    }
}