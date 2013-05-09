package an.log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import an.config.ConfigElement;
import an.config.ConfigurationException;

/**
 * The LogFactory is used to obtain a Logger. User should first pass an LogConfigElement to initialize the factory, 
 * then call getLogger() with default tag or call getLogger(String) to provide a custom tag for the logger.
 * 
 * Currently, we provided 2 types of logger, one is an internal logger, the other is a log4j logger. To provide such 
 * a log system is intend to avoid libraries conflict in employer system.
 * 
 * Users may provide their custom implementation of interface Logger, they should provide the implementation's class 
 * name(configuration attribute an.log.Logger's value) with such a constructor: 
 * CustomLogger(CustomLoggerConfigElement config, String tag). Users also need provide a corresponding 
 * CustomLoggerConfigElement class and configurations in config file.
 */
public class LogFactory {
    /**
     * Loggers are categorized by their tag. The logger's tag could not be changed after the logger is instantiated.
     */
    private static Map<String, Logger> loggerByTag = new Hashtable<String, Logger>();
    private static Set<Method> cleanupRegistry = new HashSet<Method>();

    static final String ATTR_LOGGER_CLASSNAME = "an.log.Logger";
    /**
     * The logger with default tag(class name & line number).
     */
    private static Logger defaultLogger;
    private static ConfigElement config;
    private static boolean hasInitialized = false;

    /**
     * Initialize the factory. Currently, the factory doesn't support re-initialize, if does so, will cause an 
     * exception.
     * @param config
     * @throws LogInitializationException 
     */
    public static synchronized void initialize(ConfigElement config) throws LogInitializationException {
        System.out.print("Initialing logging factory ... ");
        if (hasInitialized) {
            throw new LogInitializationException("LogFactory has already been initialized.");
        }
        else {
            LogFactory.config = config;
            hasInitialized = true;
        }
        System.out.println("done.");
    }

    /**
     * Get a Logger by a given tag. The tag could be an arbitrary string, and this tag will be added to the log.
     * In common case, the tag should be the class name which make use of the Logger.
     * @param tag
     * @return
     */
    public static synchronized Logger getLogger(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Logger's tag could not be null, " +
                    "if you want to get a Logger with default tag, please use getLogger() instead.");
        }

        Logger logger = (Logger)loggerByTag.get(tag);
        try {
            if (logger != null) {
                return logger;
            }
            else {
                logger = createLogger(tag);

                loggerByTag.put(tag, logger);
                return logger;
            }
        }
        catch (Exception e) {
            System.out.println("Error occurs during initialize Logger, use System.out instead.");
            e.printStackTrace(System.out);
            logger = new ConsoleLogger(DefaultLogger.WARN);

            loggerByTag.put(tag, logger);
            return logger;
        }
    }

    /**
     * Get a logger with default tag. The default tag is composited with calling class name and line number.
     * @return
     */
    public static synchronized Logger getLogger() {
        defaultLogger = new ConsoleLogger(DefaultLogger.ALL);
        return defaultLogger;

//        try {
//            if (defaultLogger != null) {
//                return defaultLogger;
//            }
//            else {
//                defaultLogger = createLogger(null);
//                return defaultLogger;
//            }
//        }
//        catch (Exception e) {
//            System.out.println("Error occurs during initialize Logger, use System.out instead.");
//            e.printStackTrace(System.out);
//            defaultLogger = new ConsoleLogger(DefaultLogger.WARN);
//            return defaultLogger;
//        }
    }

    public static synchronized void registerCleanupMethod(Method cleanup) {
        cleanupRegistry.add(cleanup);
    }

    public static synchronized void shutdown() {
        Iterator<Method> methods = cleanupRegistry.iterator();
        while (methods.hasNext()) {
            try {
                methods.next().invoke(null, new Object[0]);
            }
            catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }

    private static Logger createLogger(String tag) throws ConfigurationException, LogInitializationException {
        if (!hasInitialized) {
            throw new LogInitializationException("LogFactory has NOT been initialized.");
        }

        try {
            String loggerClassName = (String)config.getAttributeValueByName(ATTR_LOGGER_CLASSNAME);
            Class<?> loggerClass = Class.forName(loggerClassName);
            Constructor<?> loggerCons = loggerClass.getDeclaredConstructor(new Class[]{config.getClass(), String.class});
            Logger logger = (Logger)loggerCons.newInstance(config, tag);

            return logger;
        }
        catch (Exception e) {
            throw new ConfigurationException("Error occurs when initialize the Logger.", e);
        }
    }
}