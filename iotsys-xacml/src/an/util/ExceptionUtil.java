package an.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class ExceptionUtil {
    public static String throwable2String(Throwable t) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        t.printStackTrace(new PrintStream(out));
        return out.toString();
    }
}
