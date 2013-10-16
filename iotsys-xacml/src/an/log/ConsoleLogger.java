package an.log;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A replacement Logger when configured Logger could not be initialized or has error during constructing.
 */
public class ConsoleLogger extends DefaultLogger {
    public ConsoleLogger(int configLevel) {
        out = new PrintWriter(System.out, true);
        this.configLevel = configLevel;

        try {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            hostName = "**Unknown Host**";
        }
    }

    protected boolean checkLevel(int level) {
        if (ALL == level) {
            return true;
        }
        else if (NONE == level) {
            return false;
        }
        // Not none, not all, compute if this level should be logged.
        else {
            return isCurrentLevelMatchingConfig(level, configLevel, false);
        }
    }

    /**
     * Do nothing because System.out don't need to be rolled over.
     */
    protected void checkRollover(String msg, Throwable t) {}
}