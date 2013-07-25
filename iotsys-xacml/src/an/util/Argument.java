package an.util;

/**
 * Represent a command line argument.
 */
public class Argument {
    private String name;
    private String value;
    private boolean required;

    public Argument(String name, String value, boolean required) {
        this.name = name;
        this.value = value;
        this.required = required;
    }

    public String getTokenName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isRequired() {
        return required;
    }
}