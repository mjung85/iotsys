package org.opencean.core.packets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParameterMap {

    Map<String, Object> map = new HashMap<String, Object>();

    public Object get(String parameterId) {
        return map.get(parameterId);
    }

    public void put(String parameterId, Object value) {
        map.put(parameterId, value);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

}
