package an.xacml.engine;

import static an.xacml.engine.Cache.CONFIG_CACHE_EXPIRE_TYPE_ACCESS;
import static an.xacml.engine.Cache.CONFIG_CACHE_EXPIRE_TYPE_CREATE;
import an.xacml.DefaultXACMLElement;

/**
 * Provides a default implementation of Cacheable, which will be inherited by PolicySet and Policy.
 * All of the method should use read-write lock to do synchronizition.
 */
public abstract class DefaultCacheable extends DefaultXACMLElement implements Cacheable {
    private long accessTime;
    private long creationTime = 0;
    private boolean expired;
    private Object expLock = new Object();
    private Object accessLock = new Object();
    private Cache owner;
    // This property enables a Cacheable scope expire interval, it will interwork with the one configured
    // on the owner Cache.  Subclass should assign value to it if need the feature.
    protected long selfExpireInterval;

    /**
     * Add current Cacheable to a Cache by given key. This method is designed to invoke from its subclass,
     * such as Policy, PolicySet or Decision.
     * @param key
     * @param cache
     * @throws CacheSizeExceedCapacityException 
     */
    protected void addToCache(Object key, Cache cache) throws CacheSizeExceedCapacityException {
        // Update other attributes before add it to cache.
        // The creationTime is assigned value only for one time.
        if (creationTime == 0) {
            creationTime = System.currentTimeMillis();
        }
        accessTime = System.currentTimeMillis();
        expired = false;
        // Set owner.
        owner = cache;
        // Once the Cacheable is added to cache, it is initialized.
        cache.add(key, this);
    }

    public long getLastAccessTime() {
        synchronized (accessLock) {
            return accessTime;
        }
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean isExpired() {
        String expireType = getConfiguredExpireType();
        long expireInterval = getConfiguredExpireInterval();

        synchronized (expLock) {
            if (expired) {
                return expired;
            }
            else {
                long max = Math.max(expireInterval, selfExpireInterval);
                long min = Math.min(expireInterval, selfExpireInterval);
                long finalExpireInterval = min > 0 ? min : max;

                // If finalExpireInterval is less than 0, then the Cacheable will never expired unless force set it 
                // to expired.
                if (finalExpireInterval <= 0) {
                    expired = false;
                }
                else {
                    if (expireType.equals(CONFIG_CACHE_EXPIRE_TYPE_ACCESS) &&
                        System.currentTimeMillis() - accessTime > finalExpireInterval) {
                        expired = true;
                    }
                    else if (expireType.equals(CONFIG_CACHE_EXPIRE_TYPE_CREATE) &&
                        System.currentTimeMillis() - creationTime > finalExpireInterval) {
                        expired = true;
                    }
                }
                return expired;
            }
        }
    }

    public void setExpired(boolean expire) {
        synchronized (expLock) {
            this.expired = expire;
        }
    }

    public void setExpired() {
        synchronized (expLock) {
            this.expired = true;
        }
    }

    public void touch() {
        synchronized (accessLock) {
            accessTime = System.currentTimeMillis();
        }
    }

    private String getConfiguredExpireType() {
        if (owner.getConfiguredExpireType().equals(CONFIG_CACHE_EXPIRE_TYPE_CREATE)) {
            return CONFIG_CACHE_EXPIRE_TYPE_CREATE;
        }

        return CONFIG_CACHE_EXPIRE_TYPE_ACCESS;
    }

    private long getConfiguredExpireInterval() {
        return owner.getConfiguredExpireInterval();
    }
}