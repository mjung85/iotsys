package an.xacml.engine;

import an.config.ConfigElement;

public class EvaluationResultCache extends Cache {
    public EvaluationResultCache(ConfigElement config) {
        super(config);
    }

    public void invalidateAll() {
        removeAll();
    }
}