package an.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provide a utility that can set and get attributes for current thread or thread group. By using
 * this class, user can define global attributes in thread or thread group scope.
 */
public class ThreadContext {
    private static Map<Object, Map<Object, Object>> globalContext = new HashMap<Object, Map<Object, Object>>();

    public static synchronized Object getThreadObject(Object key) {
        Map<Object, Object> threadContext = globalContext.get(Thread.currentThread());
        if (threadContext != null) {
            return threadContext.get(key);
        }
        return null;
    }

    public static synchronized Object getThreadGroupObject(Object key) {
        Map<Object, Object> threadGrpContext = globalContext.get(Thread.currentThread().getThreadGroup());
        if (threadGrpContext != null) {
            return threadGrpContext.get(key);
        }
        return null;
    }

    public static synchronized void setThreadObject(Thread thread, Object key, Object value) {
        Map<Object, Object> threadContext = globalContext.get(thread);
        if (threadContext == null) {
            threadContext = new HashMap<Object, Object>();
            globalContext.put(thread, threadContext);
        }
        threadContext.put(key, value);
    }

    public static synchronized void setThreadGroupObject(ThreadGroup grp, Object key, Object value) {
        Map<Object, Object> threadGrpContext = globalContext.get(grp);
        if (threadGrpContext == null) {
            threadGrpContext = new HashMap<Object, Object>();
            globalContext.put(grp, threadGrpContext);
        }
        threadGrpContext.put(key, value);
    }
}