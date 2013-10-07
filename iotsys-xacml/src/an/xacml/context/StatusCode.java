package an.xacml.context;

import java.net.URI;

import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.IndeterminateException;

public class StatusCode extends DefaultXACMLElement {
    public static final StatusCode EVAL_STATUS_OK = new StatusCode(Constants.STATUS_OK, null);
    public static final StatusCode EVAL_STATUS_MISSINGATTRIBUTE = new StatusCode(Constants.STATUS_MISSINGATTRIBUTE, null);
    public static final StatusCode EVAL_STATUS_SYNTAXERROR = new StatusCode(Constants.STATUS_SYNTAXERROR, null);
    public static final StatusCode EVAL_STATUS_PROCESSINGERROR = new StatusCode(Constants.STATUS_PROCESSINGERROR, null);
    public static final StatusCode EVAL_STATUS_SERVERERROR = new StatusCode(Constants.STATUS_SERVERERROR, null);
    public static final StatusCode EVAL_STATUS_UNKNOWNERROR = new StatusCode(Constants.STATUS_UNKNOWNERROR, null);

    private final StatusCode child;
    private final URI value;

    public static StatusCode newStatusCode(IndeterminateException e, StatusCode child) {
        if (e.getStatusCode().equals(Constants.STATUS_MISSINGATTRIBUTE)) {
            return newStatusCodeMissingAttribute(child);
        }
        else if (e.getStatusCode().equals(Constants.STATUS_SYNTAXERROR)) {
            return newStatusCodeSyntaxError(child);
        }
        else if (e.getStatusCode().equals(Constants.STATUS_PROCESSINGERROR)) {
            return newStatusCodeProcessingError(child);
        }
        else if (e.getStatusCode().equals(Constants.STATUS_SERVERERROR)) {
            return newStatusCodeServerError(child);
        }
        else {
            return newStatusCodeUnknownError(child);
        }
    }

    public static StatusCode newStatusCodeOk(StatusCode child) {
        if (child == null) {
            return EVAL_STATUS_OK;
        }
        else {
            return new StatusCode(Constants.STATUS_OK, child);
        }
    }

    public static StatusCode newStatusCodeMissingAttribute(StatusCode child) {
        if (child == null) {
            return EVAL_STATUS_MISSINGATTRIBUTE;
        }
        else {
            return new StatusCode(Constants.STATUS_MISSINGATTRIBUTE, child);
        }
    }

    public static StatusCode newStatusCodeSyntaxError(StatusCode child) {
        if (child == null) {
            return EVAL_STATUS_SYNTAXERROR;
        }
        else {
            return new StatusCode(Constants.STATUS_SYNTAXERROR, child);
        }
    }

    public static StatusCode newStatusCodeProcessingError(StatusCode child) {
        if (child == null) {
            return EVAL_STATUS_PROCESSINGERROR;
        }
        else {
            return new StatusCode(Constants.STATUS_PROCESSINGERROR, child);
        }
    }

    public static StatusCode newStatusCodeServerError(StatusCode child) {
        if (child == null) {
            return EVAL_STATUS_SERVERERROR;
        }
        else {
            return new StatusCode(Constants.STATUS_SERVERERROR, child);
        }
    }

    public static StatusCode newStatusCodeUnknownError(StatusCode child) {
        if (child == null) {
            return EVAL_STATUS_UNKNOWNERROR;
        }
        else {
            return new StatusCode(Constants.STATUS_UNKNOWNERROR, child);
        }
    }

    public StatusCode(URI value, StatusCode child) {
        this.value = value;
        this.child = child;
    }

    public StatusCode getChild() {
        return child;
    }

    public URI getValue() {
        return value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            return compareObject(getChild(), ((StatusCode)o).getChild()) &&
                    compareObject(value, ((StatusCode)o).value);
        }
        return false;
    }

    /**
     * Provides a convenient method to compare status ignore the child status.
     * @param o
     * @return
     */
    public boolean equalsIgnoreChild(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            return this.value.equals(((StatusCode)o).value);
        }
        return false;
    }
}