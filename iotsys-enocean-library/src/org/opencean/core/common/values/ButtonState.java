package org.opencean.core.common.values;

public enum ButtonState implements Value {
    PRESSED, RELEASED;

    @Override
    public String toString() {
        return getDisplayValue();
    }

    @Override
    public Object getValue() {
        return name();
    }

    @Override
    public String getDisplayValue() {
        return (this.equals(PRESSED) ? "Pressed" : "Released");
    }

}
