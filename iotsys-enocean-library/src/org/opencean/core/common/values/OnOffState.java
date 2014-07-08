package org.opencean.core.common.values;

public enum OnOffState implements Value {
    ON, OFF;

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
        return (this.equals(ON) ? "On" : "Off");
    }

}
