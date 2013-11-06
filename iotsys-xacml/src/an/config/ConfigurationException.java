package an.config;

public class ConfigurationException extends Exception {
    private static final long serialVersionUID = 8064979342998158518L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable t) {
        super(message, t);
    }
}