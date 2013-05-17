package an.log;

/**
 * Represent a logger tool, which could be implemented by customers.
 */
public interface Logger {
    public static final String ELEMTYPE_LOG = "LogType";

    public void info(Object message);
    public void info(Object message, Throwable t);
    public void warn(Object message);
    public void warn(Object message, Throwable t);
    public void error(Object message);
    public void error(Object message, Throwable t);
    public void fatal(Object message);
    public void fatal(Object message, Throwable t);
    public void trace(Object message);
    public void trace(Object message, Throwable t);
    public void debug(Object message);
    public void debug(Object message, Throwable t);
    /**
     * Each logger may have a tag, which will printed to every log record. If this tag isn't set, system will use 
     * current stack's class name and line number as default tag.
     * @param tag
     */
    public String getTag();

    /**
     * We may first check if debug enabled, and then perform operations to generate the log message. This may
     * improve the performance.
     * @return
     */
    public boolean isDebugEnabled();
}