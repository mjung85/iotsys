package an.log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import an.config.ConfigElement;

/**
 * A default logger we provided. With this logger, you don't need employ any of 3rd logging tool -- either log4j nor 
 * Java logging tool.
 */
public class DefaultLogger implements Logger {
    static final String ATTR_LOGPATH = "path";
    static final String ATTR_ROLLOVER_SIZE = "rolloverSize";
    static final String ATTR_LEVEL = "level";
    static final String ATTR_SINGLELEVEL_MODE = "singleLevelMode";
    static final String ELEM_CLASSNAME_FILTER = "ClassNameFilter";

    static final String FILTER_ATTR_PATTERN = "pattern";
    static final String FILTER_ATTR_LEVEL = "level";

    public static final String[] LEVELS = {"None", "Fatal", "Error", "Warn", "Info", "Debug", "Trace", "All"};
    public static final int NONE  = 0;
    public static final int FATAL = 1;
    public static final int ERROR = 2;
    public static final int WARN  = 3;
    public static final int INFO  = 4;
    public static final int DEBUG = 5;
    public static final int TRACE = 6;
    public static final int ALL   = 7;

    public static final String STR_FORMAT_PATTERM = "%s %7s %s %s %s - %s";

    protected static Map<String, PrintWriter> outputMap = new Hashtable<String, PrintWriter>();
    protected static Formatter msgFormatter = new Formatter(new StringBuffer());

    protected String logFile;
    protected int rolloverSize;
    protected int configLevel;
    protected boolean singleLevelMode;
    protected ConfigElement[] classNameFilters;

    protected PrintWriter out;
    protected String tag;
    protected transient String hostName = "**Unknown Host**";
    protected SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss,SSS zzz");

    /**
     * The constructor is intended to be called from LogFactory.
     * @param config
     * @throws IOException 
     */
    protected DefaultLogger(ConfigElement config, String tag) throws IOException {
        System.out.print("Initialing default logger ... ");
        this.tag = tag;
        loadConfigurations(config);

        synchronized (outputMap) {
            // Each log file has a file writer, we don't create new ones for the same file. We kept the writer
            // in a map.
            out = outputMap.get(logFile);
            if (out == null) {
                // We open the log file with append mode.
                out = new PrintWriter(new FileWriter(new File(logFile), true), true);
                // If there is error occurs, we print the log message to System default output instead.
                outputMap.put(logFile, out);
            }
        }

        try {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            hostName = "**Unknown Host**";
        }

        try {
            LogFactory.registerCleanupMethod(getClass().getMethod("cleanup", new Class[0]));
        }
        catch (Exception e) {
            System.out.println("Could not register the cleanup method to LogFactory, will skip the cleanup.");
            e.printStackTrace(System.out);
        }

        System.out.println("done.");
    }

    protected void loadConfigurations(ConfigElement config) {
        logFile = (String)config.getAttributeValueByName(ATTR_LOGPATH);
        rolloverSize = (Integer)config.getAttributeValueByName(ATTR_ROLLOVER_SIZE);
        configLevel = searchLevel((String)config.getAttributeValueByName(ATTR_LEVEL));
        singleLevelMode = (Boolean)config.getAttributeValueByName(ATTR_SINGLELEVEL_MODE);
        classNameFilters = (ConfigElement[])config.getXMLElementsByName(ELEM_CLASSNAME_FILTER);
    }

    /**
     * Used to be inherited by subclass.
     */
    protected DefaultLogger() {}

    public void debug(Object message) {
        writeLog(DEBUG, message, null);
    }

    public void debug(Object message, Throwable t) {
        writeLog(DEBUG, message, t);
    }

    public void error(Object message) {
        writeLog(ERROR, message, null);
    }

    public void error(Object message, Throwable t) {
        writeLog(ERROR, message, t);
    }

    public void fatal(Object message) {
        writeLog(FATAL, message, null);
    }

    public void fatal(Object message, Throwable t) {
        writeLog(FATAL, message, t);
    }

    public void info(Object message) {
        writeLog(INFO, message, null);
    }

    public void info(Object message, Throwable t) {
        writeLog(INFO, message, t);
    }

    public void trace(Object message) {
        writeLog(TRACE, message, null);
    }

    public void trace(Object message, Throwable t) {
        writeLog(TRACE, message, t);
    }

    public void warn(Object message) {
        writeLog(WARN, message, null);
    }

    public void warn(Object message, Throwable t) {
        writeLog(WARN, message, t);
    }

    /**
     * If no tag is set, we get the call stack (Class name and line number) as tag.
     */
    public String getTag() {
        if (tag == null) {
            Exception ex = new Exception();
            StackTraceElement[] traces = ex.getStackTrace();
            for (int i = 1; i < traces.length; i ++) {
                // Get the first "non-Logger" class name as calling stack's class name.
                if (!traces[i].getClassName().startsWith(getClass().getPackage().getName())) {
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
        return checkLevel(DEBUG);
    }

    /**
     * A cleanup method that close all output stream, this method will be called before system is shutting down.
     */
    public static void cleanup() {
        System.out.print("Shutting down default logger ... ");
        synchronized (outputMap) {
            Iterator<PrintWriter> outs = outputMap.values().iterator();
            while (outs.hasNext()) {
                outs.next().close();
            }
        }
        System.out.println("done.");
    }

    protected String getDateTime() {
        return dateFormatter.format(new Date());
    }

    protected String getCurrentThread() {
        return Thread.currentThread().getName();
    }

    protected String getLocalHostName() {
        return hostName;
    }

    protected String formatMessage(int level, Object message) {
        StringBuffer strBuff = (StringBuffer)msgFormatter.out();
        strBuff.delete(0, strBuff.length());
        msgFormatter.format(
                STR_FORMAT_PATTERM,
                "[" + getDateTime() + "]",
                "<" + LEVELS[level] + ">",
                "<" + getLocalHostName() + ">",
                "<" + getCurrentThread() + ">",
                "<" + getTag() + ">",
                message);
        return msgFormatter.toString();
    }

    protected void writeLog(int level, Object message, Throwable t) {
        if (checkLevel(level)) {
            try {
                String logMsg = formatMessage(level, message);
                synchronized (outputMap) {
                    // check if the log file size has exceeded the limit.
                    checkRollover(logMsg, t);
                    out.println(logMsg);
                    if (t != null) {
                        t.printStackTrace(out);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error occurs when writing log: " + 
                        e.getMessage() + ". Will print log to system out.");
                System.out.println(formatMessage(level, message));
                if (t != null) {
                    t.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if the current level should be logged.
     * @param level
     * @return
     */
    protected boolean checkLevel(int level) {
        // Get current class configured log level.
        int classConfigLevel = getFilteredLevel();
        // If current class configured specific log level, we use it instead of the global level.
        if (classConfigLevel >= 0) {
            return isCurrentLevelMatchingConfig(level, classConfigLevel, singleLevelMode);
        }

        if (ALL == level) {
            return true;
        }
        else if (NONE == level) {
            return false;
        }
        // Not none, not all, compute if this level should be logged.
        else {
            return isCurrentLevelMatchingConfig(level, configLevel, singleLevelMode);
        }
    }

    /**
     * If current class has configured specific log level, this method is to get the level.
     * @return
     */
    protected int getFilteredLevel() {
        if (classNameFilters != null && classNameFilters.length > 0) {
            String currentClass = getCurrentClassName();
            for (int i = 0; i < classNameFilters.length; i ++) {
                if (currentClass.matches((String)classNameFilters[i].getAttributeValueByName(FILTER_ATTR_PATTERN))) {
                    return searchLevel((String)classNameFilters[i].getAttributeValueByName(FILTER_ATTR_LEVEL));
                }
            }
        }
        return -1;
    }

    /**
     * Compare current level to configured level, if current level is allowed to be logged, then return true,
     * otherwise, return false.
     * @param level
     * @param configLevel
     * @param single
     * @return
     */
    protected boolean isCurrentLevelMatchingConfig(int level, int configLevel, boolean single) {
        // If single level mode is enabled, we only log the exact level's message.
        // If single level mode is disabled, we log all levels that lower than current level.
        return (single ? configLevel == level : configLevel >= level);
    }

    protected int searchLevel(String level) {
        for (int i = 0; i < LEVELS.length; i ++) {
            if (level.equalsIgnoreCase(LEVELS[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if the current log file size has exceeded the limitation. If exceeded the limitation, rename
     * current log file to add a ".n" trailing, and then create a new one to write log to.
     * @param msg
     * @throws IOException
     */
    protected void checkRollover(String msg, Throwable t) throws IOException {
        if (rolloverSize > 0) {
            File log = new File(logFile);

            // We also need compute the exception's stack traces which we will write to log.
            ByteArrayOutputStream byteOut = null;
            if (t != null) {
                byteOut = new ByteArrayOutputStream();
                t.printStackTrace(new PrintStream(byteOut));
                byteOut.flush();
            }
            // Compute the size include current message which need to be written to current log file.
            if (log.length() + msg.length() + (byteOut == null ? 0 : byteOut.size()) >= rolloverSize * 1024) {
                out.close();
                File renamed = new File(logFile + "." + getRolloverNumber());
                log.renameTo(renamed);
                out = new PrintWriter(new FileWriter(new File(logFile), true), true);
                outputMap.put(logFile, out);
            }
        }
    }

    /**
     * Get the roll over number "n" value of ".n" trailing.
     * @return
     */
    protected int getRolloverNumber() {
        final File log = new File(logFile);
        // Get the file name, not include path.
        final String fileName = log.getName();
        final File directory = log.getParentFile();

        String[] fileNames = directory.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (dir.equals(directory) && name.startsWith(fileName + ".")) {
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        // If no such file exist, we start the index from 1.
        if (fileNames != null && fileNames.length > 0) {
            int max = 0;
            for (int i = 0; i < fileNames.length; i ++) {
                String strNum = fileNames[i].substring(fileName.length() + 1);
                try {
                    int num = Integer.valueOf(strNum);
                    // Get the max number among these file name trailings.
                    if (num > max) {
                        max = num;
                    }
                }
                catch (NumberFormatException e) {}
            }
            return max + 1;
        }
        return 1;
    }

    protected String getCurrentClassName() {
        Exception ex = new Exception();
        StackTraceElement[] traces = ex.getStackTrace();
        for (int i = 1; i < traces.length; i ++) {
            // Get the first "non-Logger" class name as calling stack's class name.
            if (!traces[i].getClassName().equals(this.getClass().getName())) {
                return traces[i].getClassName();
            }
        }
        return null;
    }
}