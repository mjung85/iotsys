package an.control;

/**
 * Represent a service that need to be controlled. Currently PDP implements the interface.
 */
public interface Controllable {
    public void start() throws OperationFailedException;
    /**
     * Waiting current processes to be complete, then shutdown the service.
     */
    public void shutdown() throws OperationFailedException;
    /**
     * Shutdown the service immediately, don't waiting processes to be complete.
     */
    public void shutdownForce() throws OperationFailedException;
    public void pause() throws OperationFailedException;
    public void resume() throws OperationFailedException;
}