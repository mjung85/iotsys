package an.control;

/**
 * Represent a service that could be monitored.
 */
public interface Monitorable {
    /**
     * Get service current Status, which is a map contained several status' properties.
     * @return
     */
    public Status getStatus();
    /**
     * Get a specific status' property from service.
     * @param key
     * @return
     */
    public Object getStatusProperty(String key);
}