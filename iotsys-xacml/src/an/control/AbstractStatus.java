package an.control;

import java.util.Hashtable;
import java.util.Iterator;

public abstract class AbstractStatus implements Status {
    protected Hashtable<String, Object> status = new Hashtable<String, Object>();

    @SuppressWarnings("unchecked")
    public Object clone() throws CloneNotSupportedException {
        AbstractStatus cloned = (AbstractStatus)super.clone();
        cloned.status = (Hashtable<String, Object>)status.clone();
        return cloned;
    }

    public Object getProperty(String key) {
        return status.get(key);
    }

    public Iterator<String> keys() {
        return status.keySet().iterator();
    }

    public void updateProperty(String key, Object value) {
        status.put(key, value);
    }
}