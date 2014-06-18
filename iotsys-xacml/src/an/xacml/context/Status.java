package an.xacml.context;

import an.log.LogFactory;
import an.log.Logger;
import an.xacml.Constants;
import an.xacml.DefaultXACMLElement;
import an.xacml.IndeterminateException;

public class Status extends DefaultXACMLElement {
    private final StatusCode code;
    private String message;
    private StatusDetail detail;

    private int hashCode;
    private Logger logger;

    public Status(IndeterminateException e) {
        logger = LogFactory.getLogger();
        code = StatusCode.newStatusCode(e, null);
        message = e.getMessage();
        // log the status detail since it may not be included in the final response.
        if (logger.isDebugEnabled()) {
            logger.debug("Creating Status ...");
            logger.debug("\tStatusCode   = " + e.getStatusCode());
            logger.debug("\tMessage      = " + e.getMessage());
            logger.debug("\tDetailObject = " + e.getAttachedObject().toString());
        }

        // Only missing attribute will return a StatusDetail object. This is for indicating which attribute is missing.
        if (e.getStatusCode().equals(Constants.STATUS_MISSINGATTRIBUTE)) {
            detail = new StatusDetail(e);
        }
        generateHashCode();
    }

    public Status(StatusCode code) {
        this.code = code;
        generateHashCode();
    }

    /**
     * A copy constructor which is used to create a new Status instance from an existing one.
     * @param status
     */
    public Status(Status status) {
        // Since the StatusCode & StatusDetail are all constant classes, we can directly assign them to the new instances.
        // If we need make them mutable in the future, we should also modify them to use a copy constructor.
        this.code = status.code;
        this.message = status.message;
        this.detail = status.detail;
        this.hashCode = status.hashCode;
        this.logger = status.logger;
    }

    public StatusCode getStatusCode() {
        return code;
    }

    public void setStatusMessage(String msg) {
        this.message = msg;
    }

    public String getStatusMessage() {
        return this.message;
    }

    public void setStatusDetail(StatusDetail detail) {
        this.detail = detail;
    }

    public StatusDetail getStatusDetail() {
        return this.detail;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            Status other = (Status)o;
            return code.equals(other.code) &&
                   // We don't compare the message if one has no message.
                   (message == null || other.message == null) ? true : compareObject(message, other.message) &&
                   compareObject(detail, other.detail);
        }
        return false;
    }

    private void generateHashCode() {
        hashCode = 11;
        hashCode = hashCode * 13 + code.hashCode();
        hashCode = hashCode * 13 + (message == null ? 0 : message.hashCode());
        hashCode = hashCode * 13 + (detail == null ? 0 : detail.hashCode());
    }

    public int hashCode() {
        return hashCode;
    }
}