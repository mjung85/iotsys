package an.control;

public abstract class AbstractMonitorableAndControllable implements Monitorable, Controllable {
    protected AbstractStatus status;

    public Status getStatus() {
        try {
            return (Status)status.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Object getStatusProperty(String key) {
        return status.getProperty(key);
    }

    public void pause() throws OperationFailedException {
        throw new OperationNotSupportedException("We currenly don't support pause operation.");
    }

    public void resume() throws OperationFailedException {
        throw new OperationNotSupportedException("We currenly don't support resume operation.");
    }
}