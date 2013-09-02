package an.log;

import an.config.ConfigElement;

/**
 * The logger employs Log4j as its implementation. It requires log4j.jar in classpath.
 * User who want to use this logger can configure log4j as direct use it. For example, just put a log4j.properties 
 * to classpath, the logger can work.
 */
public class Log4jLogger implements Logger {
    private org.apache.log4j.Logger logger;
    private String tag;

    Log4jLogger(ConfigElement config, String tag) {
        System.out.print("Initialing log4j logger ... ");
        this.tag = tag;
        if (tag != null) {
            logger = org.apache.log4j.Logger.getLogger(tag);
        }

        try {
            LogFactory.registerCleanupMethod(getClass().getMethod("shutdown", new Class[0]));
        }
        catch (Exception e) {
            System.out.println("Could not register the cleanup method to LogFactory, will skip the cleanup.");
            e.printStackTrace(System.out);
        }

        System.out.println("done.");
    }

    public void debug(Object message) {
        getLog4jLogger().debug(message);
    }

    public void debug(Object message, Throwable t) {
        getLog4jLogger().debug(message, t);
    }

    public void error(Object message) {
        getLog4jLogger().error(message);
    }

    public void error(Object message, Throwable t) {
        getLog4jLogger().error(message, t);
    }

    public void fatal(Object message) {
        getLog4jLogger().fatal(message);
    }

    public void fatal(Object message, Throwable t) {
        getLog4jLogger().fatal(message, t);
    }

    public void info(Object message) {
        getLog4jLogger().info(message);
    }

    public void info(Object message, Throwable t) {
        getLog4jLogger().info(message, t);
    }

    public void trace(Object message) {
        getLog4jLogger().trace(message);
    }

    public void trace(Object message, Throwable t) {
        getLog4jLogger().trace(message, t);
    }

    public void warn(Object message) {
        getLog4jLogger().warn(message);
    }

    public void warn(Object message, Throwable t) {
        getLog4jLogger().warn(message, t);
    }

    public String getTag() {
        if (tag == null) {
            Exception ex = new Exception();
            StackTraceElement[] traces = ex.getStackTrace();
            for (int i = 1; i < traces.length; i ++) {
                // Get the first "non-Logger" class name as calling stack's class name.
                if (!traces[i].getClassName().equals(getClass().getName())) {
                    return traces[i].getClassName() + ":" + traces[i].getLineNumber();
                }
            }
            return "**Unknown calling stack**";
        }
        else {
            return tag;
        }
    }

    public boolean isDebugEnabled() {
        return getLog4jLogger().isDebugEnabled();
    }

    @SuppressWarnings("deprecation")
    public static void shutdown() {
        System.out.print("Shutting down log4j logger ... ");
        org.apache.log4j.Logger.shutdown();
        System.out.println("done.");
    }

    protected org.apache.log4j.Logger getLog4jLogger() {
        return (tag == null ? org.apache.log4j.Logger.getLogger(getTag()) : logger);
    }
}