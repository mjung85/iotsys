package an.xacml.engine;

import an.control.AbstractStatus;

public class PDPStatus extends AbstractStatus {
    public static final String KEY_RUN_STATUS = "Status";
    public static final String KEY_WORKERTHREADS_TOTAL = "TotalThreads";
    public static final String KEY_WORKERTHREADS_IDLES = "IdleThreads";
    // TODO other keys

    public static final String STATUS_RUN_NOTRUN = "NotRun";
    public static final String STATUS_RUN_RUNING = "Running";
    public static final String STATUS_RUN_INITIALIZED = "Initialized";
    public static final String STATUS_RUN_RELOADPOLICY = "ReloadingPolicies";
    // TODO other status

    public PDPStatus() {
        status.put(KEY_RUN_STATUS, STATUS_RUN_NOTRUN);
    }
}