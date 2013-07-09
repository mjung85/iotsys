package an.xacml.engine;

/**
 * All of the method should use read-write lock to do synchronizition.
 */
public interface Cacheable {
    /**
     * Get the last access time, will be used to determine if the Cacheable is expired.
     */
    public void touch();
    /**
     * Determine if this cacheable has been expired.
     * @return
     */
    public boolean isExpired();
    /**
     * Force set current cacheable to expired or not expired.
     * @param expire
     */
    public void setExpired(boolean expire);
    /**
     * Force set current cacheable to expired.
     */
    public void setExpired();
    public long getCreationTime();
    /**
     * Get the last access time.
     * @return
     */
    public long getLastAccessTime();
}