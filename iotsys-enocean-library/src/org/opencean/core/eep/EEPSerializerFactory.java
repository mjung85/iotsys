package org.opencean.core.eep;

import java.util.HashMap;
import java.util.Map;

import org.opencean.core.common.EEPId;

public class EEPSerializerFactory {

    private Map<EEPId, EEPSerializer> parser = new HashMap<EEPId, EEPSerializer>();

    public EEPSerializerFactory() {
    }

    public EEPSerializer getSerializerFor(EEPId profile) {
        return parser.get(profile);
    }

}
