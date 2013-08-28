package an.util;

import java.util.Hashtable;
import java.util.Map;

public class SequenceFactory {
    private static Map<Object, Sequence> sequences = new Hashtable<Object, Sequence>();

    private SequenceFactory() {}

    /**
     * Get a sequence by a given key. In general, the key is the class who will make use of a sequential ID. 
     * @param key
     * @return
     */
    public static synchronized Sequence getSequence(Object key) {
        Sequence sequence = (Sequence)sequences.get(key);
        if (sequence == null) {
            sequence = new Sequence();
            sequences.put(key, sequence);
        }
        return sequence;
    }
}
