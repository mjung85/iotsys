package an.xacml.engine;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import an.config.ConfigElement;
import an.log.LogFactory;
import an.log.Logger;

public abstract class Cache {
    public static final String ATTR_CACHE_SIZE = "size";
    public static final String ATTR_CACHE_EXPIREINTERVAL = "expireInterval";
    public static final String ATTR_CACHE_EXPIRETYPE = "expireType";

    public static final String CONFIG_CACHE_EXPIRE_TYPE_ACCESS = "access";
    public static final String CONFIG_CACHE_EXPIRE_TYPE_CREATE = "create";

    private int configSize;
    private long configExpInterval;
    private String configExpType;

    private Boolean running;
    protected int size;
    protected ReadWriteLock cacheLock = new ReentrantReadWriteLock(true);
    protected final int preparedNum = 10;
    // Use a non-synchronized version of Map, I will use lock to control the access.
    protected Map<Object, List<Cacheable>> cache = new HashMap<Object, List<Cacheable>>();
    protected Logger logger;

    public Cache(ConfigElement config) {
        loadConfigurations(config);
        logger = LogFactory.getLogger();
    }

    protected void loadConfigurations(ConfigElement config) {
        configSize = (Integer)config.getAttributeValueByName(ATTR_CACHE_SIZE);
        configExpInterval = (Long)config.getAttributeValueByName(ATTR_CACHE_EXPIREINTERVAL);
        configExpType = (String)config.getAttributeValueByName(ATTR_CACHE_EXPIRETYPE);
    }

    /**
     * Should use read-write lock to do synchronization. This method is designed for called from Cacheable's
     * implementation.
     * 
     * The cache supports add multiple cacheable objects with same key, in this case, it will create a list to keep all
     * of these same key objects, then put the list to cache. This feature is to support multiple policies match the
     * same request(target).
     */
    protected void add(Object key, Cacheable o) throws CacheSizeExceedCapacityException {
        try {
            writeLock();
            ensureCapacity();
            addInternal(key, o);
        }
        finally {
            writeUnlock();
        }
    }

    /**
     * Get the cacheable, and then check if it is expired, if expired, remove it from cache, and then return null.
     * @return
     */
    public List<Cacheable> get(Object key) {
        try {
            readLock();

            List<Cacheable> o = cache.get(key);
            if (o == null) {
                return null;
            }

            // If got an array, check each item for expiration.
            if (hasExpirationsInList(o)) {
                try {
                    writeLock();
                    return removeExpirationsInList(o);
                }
                finally {
                    writeUnlock();
                }
            }
            return o;
        }
        finally {
            readUnlock();
        }
    }

    /**
     * Get all cacheables from the cache. Check each cacheable if there is expired. It should take a certain long time
     * because it will cleanup all expirations. Did not find who need to call it, so make it protected.
     * @return
     */
    protected List<Cacheable> getAll() {
        List<Cacheable> all = new ArrayList<Cacheable>();
        try {
            readLock();

            Collection<List<Cacheable>> lists = cache.values();
            for (List<Cacheable> each : lists) {
                // check each item for expiration.
                if (hasExpirationsInList(each)) {
                    try {
                        writeLock();
                        removeExpirationsInList(each);
                    }
                    finally {
                        writeUnlock();
                    }
                }
                for (Cacheable cacheable : each) {
                    if (!all.contains(cacheable)) {
                        all.add(cacheable);
                    }
                }
            }
            return all;
        }
        finally {
            readUnlock();
        }
    }

    protected void removeAll() {
        try {
            writeLock();
            cache.clear();
            size = 0;
        }
        finally {
            writeUnlock();
        }
    }

    protected List<Cacheable> remove(Object key) {
        try {
            writeLock();
            return removeInternal(key);
        }
        finally {
            writeUnlock();
        }
    }

    protected List<Cacheable> removeInternal(Object key) {
        List<Cacheable> removed = cache.remove(key);
        if (removed != null) {
            size -= removed.size();
        }
        return removed;
    }

    public int size() {
        try {
            readLock();
            return size;
        }
        finally {
            readUnlock();
        }
    }

    /**
     * Dump all Cacheables to the given output stream. This is for debug.
     */
    protected void dump(OutputStream out) {
        try {
            readLock();

            PrintWriter writer = new PrintWriter(out);
            Iterator<Object> keys = cache.keySet().iterator();
            while (keys.hasNext()) {
                writer.println();
                Object key = keys.next();
                List<Cacheable> value = cache.get(key);
                writer.print(key.toString() + " (" + value.size() + ") : [");
                if (value.size() > 0) {
                    writer.print(value.get(0).toString());
                    for (int i = 1; i < value.size(); i ++) {
                        writer.print(", " + value.get(i).toString());
                    }
                }
                writer.print("]");
            }
            writer.flush();
        }
        finally {
            readUnlock();
        }
    }

    public long getConfiguredCacheSize() {
        return configSize;
    }

    public long getConfiguredExpireInterval() {
        return configExpInterval;
    }

    public String getConfiguredExpireType() {
        return configExpType;
    }

    /**
     * Add a Cacheable to internal cache map. No lock operation, for internal use only.
     * @param key
     * @param o
     * @throws CacheSizeExceedCapacityException
     */
    protected void addInternal(Object key, Cacheable o) {
        // Check expired when add cacheable.
        if (o.isExpired()) {
            return;
        }
        // Check if the instance exists.
        List<Cacheable> origin = cache.get(key);
        if (origin == null) {
            List<Cacheable> toBeCached = new ArrayList<Cacheable>();
            toBeCached.add(o);
            cache.put(key, toBeCached);
        }
        else {
            for (int i = 0; i < origin.size(); i ++) {
                Cacheable item = origin.get(i);
                // equals should compare Cacheable's id(policy id, policySet id or decision's serials no.)
                if (o.equals(item)) {
                    // Force update the cacheable.
                    origin.set(i, o);
                    return;
                }
            }
            origin.add(o);
        }
        size ++;
    }

    protected void ensureCapacity() throws CacheSizeExceedCapacityException {
        if (size >= configSize) {
            throw new CacheSizeExceedCapacityException(
                    "Current size(" + size + ") has exceeded the capacity(" + configSize + ") of cache.");
        }

        if (size + preparedNum >= configSize) {
            cleanupExpired();
        }
    }

    /**
     * Clean up expirations in cache. The method will fork a new thread to do clean up. it is invoked internally.
     */
    protected void cleanupExpired() {
        synchronized (running) {
            if (running) {
                return;
            }

            running = true;
            Thread cleanThread = new Thread() {
                public void run() {
                    removeSomeExpirations();
                    running = false;
                }

                /**
                 * Remove some expired cacheables, and then return the keys.
                 * @return
                 */
                private void removeSomeExpirations() {
                    int loop = 0;
                    // We only remove some of expired cacheables for 1 calling. This is intend to let those 
                    // add operations in. Otherwise, the add operation will wait for a long time untill all
                    // expired have been removed.
                    Iterator<Object> keys = cache.keySet().iterator();
                    while (loop < preparedNum && keys.hasNext()) {
                        Object key = keys.next();
                        List<Cacheable> o = cache.get(key);
                        int originalSize = o.size();
                        // has expired?
                        if (hasExpirationsInList(o)) {
                            try {
                                writeLock();
                                removeExpirationsInList(o);
                            }
                            finally {
                                writeUnlock();
                            }
                        }
                        loop += o.size() - originalSize;
                    }
                }
            };
            cleanThread.start();
        }
    }

    protected boolean hasExpirationsInList(List<Cacheable> o) {
        for (Cacheable item : o) {
            if (item.isExpired()) {
                return true;
            }
        }
        return false;
    }

    protected List<Cacheable> removeExpirationsInList(List<Cacheable> o) {
        // Re-get it from cache since it may be changed.
        if (o != null) {
            int index = 0;
            while (o.size() > index) {
                Cacheable item = o.get(index);
                if (item.isExpired()) {
                    o.remove(index);
                    size --;
                }
                else {
                    index ++;
                }
            }
        }
        return o;
    }

    /**
     * If the cacheLock is null, we don't do synchronization. The sub-classes may need this feature.
     */
    protected void readLock() {
        // Wait untill acquire the lock.
        cacheLock.readLock().lock();
    }

    protected void readUnlock() {
        cacheLock.readLock().unlock();
    }

    protected void writeLock() {
        // Wait untill acquire the lock.
        cacheLock.writeLock().lock();
    }

    protected void writeUnlock() {
        cacheLock.writeLock().unlock();
    }
}