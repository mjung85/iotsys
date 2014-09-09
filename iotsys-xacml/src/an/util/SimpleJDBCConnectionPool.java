package an.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * We provide a simple Connection Pool for use. This pool supports a fixed size, a timeout for getting an available
 * connection.
 */
public class SimpleJDBCConnectionPool {
    private Collection<PooledConnection> pool = new HashSet<PooledConnection>();
    // If we can't get an available connection within "timeout", we will throw an exception. This value equal 0 or less
    // than 0 means we will always wait until we get a connection.
    private int timeout;
    private String connURL;
    private String pooled;
    private String pooledSize;

    public SimpleJDBCConnectionPool(String driver, String connectionURL,
            String dbUser, byte[] password, int size, int timeout) throws ConnectionPoolException {
        try {
            this.connURL = connectionURL;
            this.pooled = dbUser;
            this.pooledSize = new String(password);
            this.timeout = timeout;
            DriverManager.registerDriver((Driver)Class.forName(driver).newInstance());
            initialize(connectionURL, dbUser, password, size);
        }
        catch (ConnectionPoolException cpEx) {
            throw cpEx;
        }
        catch (Exception e) {
            throw new ConnectionPoolException("Error occurs while initializing connection pool.", e);
        }
    }

    private void initialize(String connectionURL, String dbUser, byte[] password, int size)
    throws ConnectionPoolException, SQLException {
        if (size <= 0) {
            throw new ConnectionPoolException("The configured size of connection pool is '" + size + "'");
        }

        String test = new String(password);
        for (int i = 0; i < size; i ++) {
            Connection conn = DriverManager.getConnection(connectionURL, dbUser, test);
            PooledConnectionHandler handler = new PooledConnectionHandler(conn);
            // Create the proxy object.
            pool.add((PooledConnection)Proxy.newProxyInstance(
                    PooledConnection.class.getClassLoader(), new Class[] {PooledConnection.class}, handler));
        }
    }

    public void shutdown() {
        Iterator<PooledConnection> i = pool.iterator();
        while (i.hasNext()) {
            i.next().realClose();
        }
        pool.clear();
    }

    /**
     * The main method of the pool. Returns an available connection. If we can't get an available connection within
     * "timeout", we will throw an exception.
     * @return
     * @throws ConnectionPoolException
     */
    public Connection getConnection() throws ConnectionPoolException {
        return getConnection(0);
    }

    /**
     * If the given connection is broken, we'll try to repair it. If we can't repair it, we will throw an exception.
     * @param conn
     */
    public void repairConnection(Connection conn) throws ConnectionPoolException {
        try {
            conn.getMetaData();
        }
        catch (Exception ex) {
            if (!(conn instanceof PooledConnection)) {
                throw new ConnectionPoolException("The connection was not create by this pool, we can't repair it.");
            }
            // Perform reconnect on PooledConnection.
            ((PooledConnection)conn).reconnect();
        }
    }

    /**
     * If we can't get an available connection in a single loop, we will fork another loop, until we get it or timeout.
     * This method is only used to be called from itself.
     * @param timeElapsed Time elapsed till now.
     * @return
     * @throws ConnectionPoolException
     */
    private Connection getConnection(long timeElapsed) throws ConnectionPoolException {
        long start = System.currentTimeMillis() + timeElapsed;
        Iterator<PooledConnection> i = pool.iterator();
        while (i.hasNext()) {
            if (timeout > 0 && (System.currentTimeMillis() - start) > timeout) {
                throw new ConnectionPoolException("No connection got within " + timeout + " milliseconds.");
            }

            PooledConnection pooled = i.next();
            synchronized (pooled) {
                if (!pooled.inUse()) {
                    pooled.useIt();
                    return pooled;
                }
            }
        }

        // If we haven't get yet, we will re-loop to get it. Before we re-loop, we will take a rest for 100ms or 10% of
        // the "timeout".
        try {
            Thread.sleep(timeout > 0 ? timeout / 10 : 100);
        } catch (InterruptedException e) {}

        return getConnection(System.currentTimeMillis() - start);
    }

    /**
     * The pooled connection we actually returns to caller.
     */
    interface PooledConnection extends Connection {
        /**
         * Returns true if current connection is in use. Otherwise return false.
         * @return
         */
        public boolean inUse();
        /**
         * Set this connection to "in using"
         */
        public void useIt();
        /**
         * If current connection is broken, try to re-connect it.
         */
        public void reconnect();
        /**
         * Close this connection really.
         */
        public void realClose();
    }

    /**
     * The Proxy used this handler to call the target methods.
     */
    class PooledConnectionHandler implements InvocationHandler {
        private Connection conn;
        private boolean inUse = false;

        /**
         * We will cache a real Connection to the handler. While method call coming to the handler, we can make the call
         * to the real Connection. 
         * @param conn
         */
        PooledConnectionHandler(Connection conn) {
            this.conn = conn;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // We prevent caller to call close method on Connection object.
            if (method.getName().equals("close")) {
                // close method should be called by Connection user after they accomplish their tasks. We should
                // synchronize it with useIt and isInUse methods.
                synchronized (proxy) {
                    inUse = false;
                    return null;
                }
            }
            if (method.getName().equals("useIt")) {
                inUse = true;
                return null;
            }
            if (method.getName().equals("inUse")) {
                return inUse;
            }
            if (method.getName().equals("reconnect")) {
                synchronized (proxy) {
                    this.conn = DriverManager.getConnection(connURL, pooled, pooledSize);
                    return null;
                }
            }
            if (method.getName().equals("realClose")) {
                conn.close();
                return null;
            }
            // We passed all other methods to the Connection object.
            Method delgMethod = conn.getClass().getMethod(method.getName(), method.getParameterTypes());
            return delgMethod.invoke(conn, args);
        }
    }
}