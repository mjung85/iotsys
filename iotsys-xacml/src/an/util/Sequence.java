package an.util;

public class Sequence {
    private long sequence = 0L;

    Sequence() {}

    public synchronized Long getNextLong() {
        return ++ sequence;
    }

    public synchronized String getNextString() {
        return String.valueOf(++ sequence);
    }
}
