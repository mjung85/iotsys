package an.xacml.context;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import an.xacml.DefaultXACMLElement;
import an.xacml.IndeterminateException;

public class StatusDetail extends DefaultXACMLElement {
    // The any object may be an array of MissingAttributeDetail object.
    private final Object any;

    public StatusDetail(IndeterminateException e) {
        any = e.getAttachedObject();
    }

    public StatusDetail(Object detail) {
        any = detail;
    }

    public Object getDetail() {
        return any;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null && (any == null || any.toString().length() == 0)) {
            return true;
        }
        if (o != null && o.getClass() == this.getClass() && any != null) {
            return any.equals(((StatusDetail)o).any);
        }
        return false;
    }

    public int hashCode() {
        return any == null ? 0 : any.hashCode();
    }

    public String toString() {
        if (any instanceof Throwable) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ((Throwable)any).printStackTrace(new PrintWriter(out));
            return out.toString();
        }
        else {
            return any.toString();
        }
    }
}