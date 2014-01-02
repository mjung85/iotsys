package an.control;

import java.util.Iterator;

public interface Status {
    public Iterator<String> keys();
    public Object getProperty(String key);
}